#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h> 
#include <unistd.h>
#include "user.h"

/**
 * Method to create a new random password of length 8-10
 * @param u the user to assign the password to
 * @return void
*/
void new_password(user_t *u) {
    const char* options = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM,./;'[]-=<>?:{}_+()&*%^#$!@~1234567890`";
    srand(time(NULL)); // initilize random

    char password_length = rand() % 3 + 8; // find password length rand() % 3 gives in range 0-2 inclusive
    char password[password_length + 1];
    
    for (char i = 0; i < password_length; i++) {
        password[i] = options[rand() % 91];
    }

    password[password_length] = '\0'; // add a null terminator at the end
    u->password = (char*) malloc(11); // allocate memory for the password
    strcpy(u->password, password);
}


/**
 * Method to read users from a file
 * @param user_list the list of users
 * @param filename the filename to be read
 * @return the number of users read, -1 if failed
*/
int read_users(user_t *user_list, char* filename)  {
    FILE *file = fopen(filename, "r");
    if (!file) { // make sure file exists
        return -1;
    }

    int numRead = 0; 
    char *buffer = NULL;   
  	size_t length = 0;   
	while (getline(&buffer, &length, file) != -1) { // loop untill file has been read
        // create and allocate newUser
        User newUser;
        char privilege[2];
        char username[7];
        char password[11];
        newUser.username = (char*) malloc(7);
        newUser.password = (char*) malloc(11);

        sscanf(buffer, "%s %s %s", username, password, privilege); // assign line to user

        // copy into new user
        strcpy(newUser.username, username); 
        strcpy(newUser.password, password);
        if (strcmp(privilege, "0") == 0)  { // assign privilege
            newUser.privilege = USER;
        } else {
            newUser.privilege = ADMIN;
        }
        user_list[numRead] = newUser; // add new user to user_list
        numRead++; // increment numRead because new user
    }
    free(buffer); // free buffer
    fclose(file); // close the file
    return numRead;
}

/**
 * Method to save users to a file
 * @param user_list the list of users
 * @param filename the filename to be saved to
 * @param size the number of users in the list
 * @return 0 if successful, -1 if not
*/
int save_users(user_t *user_list, char* filename, int size) {
    FILE *file = fopen(filename, "w");
    if (!file) { // make sure file exists
        return -1;
    }

    for (int i = 0; i < size; i++) { // loop through user_list and print to file
        if (user_list[i].privilege == USER) {
            fprintf(file, "%s %s %s", user_list[i].username, user_list[i].password, "0\n");
        } else {
            fprintf(file, "%s %s %s", user_list[i].username, user_list[i].password, "1\n");
        }
    }
    
    fclose(file); // close file
    return 0;
}

/**
 * Method to find a user in a list of users
 * @param user_list the list of users
 * @param username the username to be found
 * @param password the password to be found
 * @param size the number of users in the list
 * @return the index of the user if found, -1 if not
 */
int find_user(user_t *user_list, char* username, char* password, int size) {
    for (int i = 0; i < size; i++) { // loop through file 
        if (strcmp(user_list[i].username, username) == 0) { // check if username is found
            if (password == NULL || strcmp(user_list[i].password, password) == 0) { // check if password is match or password is NULL
                return i;
            }
        }
    }
    return -1;
}