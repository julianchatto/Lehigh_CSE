#include <iostream>
#include <string>
#include <sys/wait.h>
#include <unistd.h>
#include <vector>
#include <cstring>

#include <linux/seccomp.h>
#include <sys/prctl.h>

#include "../common/constants.h"
#include "../common/contextmanager.h"

#include "functable.h"
#include "map.h"
#include "storage.h"

using namespace std;

// helper function to set up seccomp for sandboxing
bool setup_seccomp() {
    if (prctl(PR_SET_NO_NEW_PRIVS, 1, 0, 0, 0) != 0) {
        return false;
    }

    if (prctl(PR_SET_SECCOMP, SECCOMP_MODE_STRICT) != 0) {
        return false;
    }

    return true;
}

/// helper function for safe writing to a file descriptor
bool safe_write(int fd, const void *buffer, size_t size) {
    ssize_t written = write(fd, buffer, size);
    if (written < 0) {
        return false;
    }
    if (static_cast<size_t>(written) != size) {
        return false;
    }
    return true;
}

/// child process helper function for map/reduce
void child_mr(int read_fd, int write_fd, map_func map_function, reduce_func reduce_function) {
    if (!setup_seccomp()) {
        exit(EXIT_FAILURE);
    }

    vector<vector<uint8_t>> map_results;
    while (true) {
        uint32_t key_size;
        if (read(read_fd, &key_size, sizeof(key_size)) <= 0 || key_size == 0) break;

        string key(key_size, '\0');
        if (read(read_fd, &key[0], key_size) != static_cast<ssize_t>(key_size)) {
            exit(EXIT_FAILURE);
        }

        uint32_t value_size;
        if (read(read_fd, &value_size, sizeof(value_size)) <= 0) break;

        vector<uint8_t> value(value_size);
        if (read(read_fd, value.data(), value_size) != static_cast<ssize_t>(value_size)) {
            exit(EXIT_FAILURE);
        }

        map_results.push_back(map_function(key, value));
    }

    vector<uint8_t> reduce_result = reduce_function(map_results);
    size_t result_size = reduce_result.size();

    if (!safe_write(write_fd, &result_size, sizeof(result_size)) || !safe_write(write_fd, reduce_result.data(), result_size)) {
        exit(EXIT_FAILURE);
    }

    close(write_fd);
    exit(EXIT_SUCCESS);
}

/// Register a .so with the function table
///
/// @param user       The name of the user who made the request
/// @param pass       The password for the user, used to authenticate
/// @param mrname     The name to use for the registration
/// @param so         The .so file contents to register
/// @param admin_name The name of the admin user
/// @param funcs      A pointer to the function table
///
/// @return A result tuple, as described in storage.h
result_t my_register_mr(const string &user, const string &mrname, const vector<uint8_t> &so, const string &admin_name, FuncTable *funcs) {
	if (user != admin_name) {
        return {false, RES_ERR_LOGIN, {}};
    }

    string result = funcs->register_mr(mrname, so);
    if (result != RES_OK) {
        return {false, result, {}};
    }

    return {true, RES_OK, {}};
};

/// Run a map/reduce on all the key/value tuples of the kv_store
///
/// @param user       The name of the user who made the request
/// @param mrname     The name of the map/reduce functions to use
/// @param admin_name The name of the admin user
/// @param funcs      A pointer to the function table
/// @param kv_store   A pointer to the Map holding the key/value store
///
/// @return A result tuple, as described in storage.h
result_t my_invoke_mr(const string &user, const string &mrname, const string &admin_name, FuncTable *funcs, Map<string, vector<uint8_t>> *kv_store) {
	// Retrieve map and reduce functions
    auto [map_function, reduce_function] = funcs->get_mr(mrname);
    if (!map_function || !reduce_function) {
        return {false, RES_ERR_FUNC, {}};
    }

    // Create pipes for inter-process communication
    int pipe_to_child[2], pipe_from_child[2];
    if (pipe(pipe_to_child) < 0 || pipe(pipe_from_child) < 0) {
        return {false, RES_ERR_SERVER, {}};
    }

    // Fork to create child process
    pid_t pid = fork();
    if (pid < 0) {
        return {false, RES_ERR_SERVER, {}};
    }

    if (pid == 0) {
        // Child process
        close(pipe_to_child[1]); // Close unused write end
        close(pipe_from_child[0]); // Close unused read end

        // Execute child map/reduce logic
        child_mr(pipe_to_child[0], pipe_from_child[1], map_function, reduce_function);

        // If child_mr fails, exit immediately
        exit(EXIT_FAILURE);
    }

    // Parent process
    close(pipe_to_child[0]); // Close unused read end
    close(pipe_from_child[1]); // Close unused write end

    ContextManager cleanup([&]() {
        close(pipe_to_child[1]);
        close(pipe_from_child[0]);
        waitpid(pid, nullptr, 0); // Reap child process
    });

    // Send keys and values to the child
    bool write_success = true;
    kv_store->do_all_readonly(
        [&](const string &key, const vector<uint8_t> &value) {
            uint32_t key_size = key.size();
            uint32_t value_size = value.size();
            write_success &= safe_write(pipe_to_child[1], &key_size, sizeof(key_size));
            write_success &= safe_write(pipe_to_child[1], key.data(), key_size);
            write_success &= safe_write(pipe_to_child[1], &value_size, sizeof(value_size));
            write_success &= safe_write(pipe_to_child[1], value.data(), value_size);
        },
        []() {});

    // Signal EOF to the child process
    uint32_t zero = 0;
    write_success &= safe_write(pipe_to_child[1], &zero, sizeof(zero));
    close(pipe_to_child[1]); // Close write end

    if (!write_success) {
        return {false, RES_ERR_SERVER, {}};
    }

    // Read the result from the child process
    size_t result_size;
    vector<uint8_t> result;
    if (read(pipe_from_child[0], &result_size, sizeof(result_size)) <= 0) {
        return {false, RES_ERR_SERVER, {}};
    }

    result.resize(result_size);
    if (read(pipe_from_child[0], result.data(), result_size) != static_cast<ssize_t>(result_size)) {
        return {false, RES_ERR_SERVER, {}};
    }

    close(pipe_from_child[0]); // Close read end

    return {true, RES_OK, result};
}
