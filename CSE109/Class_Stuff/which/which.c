#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// Get the PATH variable into our program

// Split the PATH variable into its constituent directories

// Concat path name with directory

bool file_exists(char *path_to_file) {
  if (access(path_to_file, X_OK) == 0) {
    return true;
  } else {
    return false;
  }
}

int main(int argc, char **argv) {
  if (argc == 1) {
    return 1;
  }
  bool a_flag = false;
  for (int i = 1; i < argc; i++) {
    if (strcmp(argv[i],"-a") == 0) {
      a_flag = true;
    } else if (argv[i][0] == '-') {  // it's not -a
      return 2;
    }
  }

  int return_value = 0;
  
  char *path_env = getenv("PATH");
  for (int i = 1; i < argc; i++) {
    bool found = false;
    char path_env_cpy[1000000];
    strcpy(path_env_cpy, path_env);
    char *exe_name = argv[i];
    char *pch;
    pch = strtok(path_env_cpy, ":");
    while (pch != NULL) {
      char full_path[4096];
      strcpy(full_path, pch);
      strcat(full_path, "/");
      strcat(full_path, exe_name);
      if (file_exists(full_path)) {
        found = true;
        printf("%s\n", full_path);
        if (!a_flag) {
          break;  
        }
      }
      pch = strtok(NULL, ":");
      if (found == false) {
        return_value = 1;
      }
    }
  }
  return return_value;
}