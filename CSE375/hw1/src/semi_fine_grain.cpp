#include <map>
#include <mutex>
#include <chrono>
#include <iostream>
#include <vector>
#include <random>
#include <thread>
#include <atomic>

using namespace std;

// Global variables
atomic<int> num_balances(0); // atomic int for the number of balances waiting 
map<int, float> accounts; // Map of all the accounts in the bank
map<int, unique_ptr<mutex>> account_locks; // Map of locks for each account "bucket"
int NUM_ACCOUNTS = 10;
int MAX_LOCKS = 2; // The maximum number of locks
const float MAX_BALANCE = 100000; // The maximum total balance amongst all accounts 
vector<int> thread_times; // Vector to store the time taken by each thread. Each index holds the time taken by the corresponding thread

/**
 * @brief Hashes the account number to a lock
 * 
 * @param account The account number
 * @return int The lock number
 */
int hash_account_num(int account) {
    return account % MAX_LOCKS;
}

/**
 * @brief Inserts the initial balance into the accounts
 */
void insert() {
    float balance = MAX_BALANCE / NUM_ACCOUNTS; // Determine the balance for each account
    for (int i = 1; i <= NUM_ACCOUNTS; i++) {
        accounts[i] = balance;
    }
    accounts[1] += MAX_BALANCE - (balance * NUM_ACCOUNTS); // add the remainder to the first account
    
    // Create each lock bucket
    for (int i = 0; i < MAX_LOCKS; i++) {
        account_locks.emplace(i, make_unique<mutex>());
    }
}

/**
 * @brief Generates a random integer between min and max
 * 
 * @param min The minimum value
 * @param max The maximum value
 * @return int The random integer
 */
int getRanInt(int min, int max) {
    static thread_local random_device rd;
    static thread_local mt19937 gen(rd());
    uniform_int_distribution<int> distrib(min, max);
    return distrib(gen);
}

/**
 * @brief Helper method that modifies the balance of two accounts
 * 
 * @param account1 The first account
 * @param account2 The second account
 */
void modifyBalance(int account1, int account2) {
    float v1 = accounts[account1], v2 = accounts[account2];

    float amount = getRanInt(0, static_cast<int>(v1)); // Number between 0 and account[account1]

    v1 -= amount;
    v2 += amount;
    accounts[account1] = v1;
    accounts[account2] = v2;
}

/**
 * @brief Deposits a random amount from one account to another
 */
void deposit() {
    // If the number of balances waiting is greater than 5, yield the thread
    while (num_balances.load() > 5) {
        this_thread::yield();
    }

    int account1 = getRanInt(1, NUM_ACCOUNTS), account2 = account1;
    do {
        account2 = getRanInt(1, NUM_ACCOUNTS);
    } while (account2 == account1);

    // get the hashes for each account
    int hash1 = hash_account_num(account1), hash2 = hash_account_num(account2);
    
    // lock the accounts in ascending order to avoid deadlock
    if (hash1 > hash2) {
        swap(hash1, hash2);
    }

    if (hash1 == hash2) { // the case where the both accounts hash to the same bucket
        lock_guard<mutex> lock(*account_locks[hash1]);
        modifyBalance(account1, account2);
    } else {
        lock_guard<mutex> lock1(*account_locks[hash1]);
        lock_guard<mutex> lock2(*account_locks[hash2]);
        modifyBalance(account1, account2);
    }
}

/**
 * @brief Sums The balance of all accounts
 * 
 * @return float The total balance
 */
float balance() {
    // increment the number of balances waiting
    num_balances.fetch_add(1);

    // Acquire the all the buckets
    vector<unique_lock<mutex>> locks;
    locks.reserve(MAX_LOCKS); // pre-reserve the space for the locks
    for (int i = 0; i < MAX_LOCKS; i++) {
        locks.emplace_back(*account_locks[i]);
    }

    float balance = 0.0;
    for (int i = 1; i <= NUM_ACCOUNTS; i++) {
        balance += accounts[i];
    }
    
    // decrement the number of balances waiting
    num_balances.fetch_sub(1);
    return balance;
}

/**
 * @brief Does the work of depositing and checking the balance
 * @cite https://stackoverflow.com/questions/22387586/measuring-execution-time-of-a-function-in-c
 * 
 * @param iterations The number of iterations to run
 */
void do_work(int iterations, int thread_num) {
    auto start = chrono::high_resolution_clock::now();
    for (int i = 0; i < iterations; i++) {
        if (getRanInt(0, 100) <= 95) { // 95% chance of depositing
            deposit();
        } else {
            float bal = balance();
            if (bal != MAX_BALANCE) {
                cout << "ERROR: balance returned " << bal << endl;
            }
        }
    }
    auto end = chrono::high_resolution_clock::now();
    auto duration = chrono::duration_cast<chrono::milliseconds>(end - start);
    thread_times[thread_num] = duration.count();
}

/**
 * @brief Cleans up the accounts and lock table
 */
void cleanup() {
    accounts.clear();
}

/**
 * @brief The main function
 * 
 * @param argc The number of arguments
 * @param argv The arguments
 * @return int The exit code
 */
int main(int argc, char* argv[]) {
    if (argc != 3 && argc != 4) { // Check if the number of arguments is correct
        cout << "Usage: <num_threads> <num_iterations> <(OPTIONAL)num_accounts>" << endl;
        return 1;
    }

    const int NUM_THREADS = atoi(argv[1]);
    if (NUM_THREADS < 1) { // Check if the number of threads is correct
        cout << "Number of threads must be greater than 0" << endl;
        return 1;
    }
    MAX_LOCKS = NUM_THREADS * 3;


    const int NUM_ITERATIONS = atoi(argv[2]); 
    if (NUM_ITERATIONS < 1) { // Check if the number of iterations is correct
        cout << "Number of iterations must be greater than 0" << endl;
        return 1;
    }

    if (argc == 4) { // Check if the number of accounts is provided
        NUM_ACCOUNTS = atoi(argv[3]);
        if (NUM_ACCOUNTS < 2) { // Check if the number of accounts is correct
            cout << "Number of accounts must be greater than 1" << endl;
            return 1;
        }
    }

    // Ensure the thread_times vector is the correct size
    thread_times.resize(NUM_THREADS, 0);

    insert();

    // create the threads and call do_work
    vector<thread> threads;
    threads.reserve(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
        try {
            threads.push_back(thread(do_work, NUM_ITERATIONS, i));
        } catch (const exception& e) {
            cout << "Error creating thread " << i << ": " << e.what() << endl;
        }
    }
    
    for (auto& t : threads) {
        if (t.joinable()) {
            t.join();
        } else {
            cout << "Thread not joinable" << endl;
        }
    }

    // get the max duration
    int max_duration = 0;
    for (int i = 0; i < NUM_THREADS; i++) {
        max_duration = max(max_duration, thread_times[i]);
    }

    #ifdef TEST
        cout << "Max duration for " << NUM_THREADS << " threads, " << NUM_ITERATIONS << " iterations, and " <<  NUM_ACCOUNTS << " accounts: " << max_duration << "ms" << endl;
        cout << "Ending balance for " << NUM_THREADS << " threads: " << balance() << endl;
    #endif

    #ifndef TEST
        cout << NUM_THREADS << " " << NUM_ITERATIONS << " " << NUM_ACCOUNTS << " " << max_duration << endl;
    #endif

    cleanup();

    return 0;
}
