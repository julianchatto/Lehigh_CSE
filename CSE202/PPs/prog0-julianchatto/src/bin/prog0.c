#include <stdio.h>
#include <stdlib.h>
#include <string.h> 
#include <unistd.h>
#include <stdbool.h>
#include "user.h"

/**
 * Prints the menu options
 * @return void
*/
void printMenu() {
    printf("\nSelect an operation: \n");
    printf("1: Add a new user\n");
    printf("2: Reset the password of an existing user\n");
    printf("3: Logout\n");
}
/**
 * Method that frees all allocated memory
 * @param users the array of users
 * @param size the size of the array
 * @return void
*/
void free_everything(user_t* users, int size) {
    for (int i = 0; i < size; i++) {
        free(users[i].password);
        free(users[i].username);
    }
    free(users);
}

int main(int argc, char** argv) {
    user_t* users = (user_t*) malloc(65536); // allocate memory for users

    if (argc < 2) { // make sure filename was passed
        printf("No arguments passed\n");
        exit(1);
    }

    char* fileName = argv[1]; // get the fileName

    int size;
    if ((size = read_users(users, fileName)) == -1) { // read the users and assign the number of users to size, makes sure reading was successful
        printf("reading users failed for filename \"%s\"\n", fileName);
        exit(1);
    } else {
        printf("File \"%s\" loaded successfully\n", fileName); 
    }
    
    int index;
    for (char i = 0; i < 3; i++) { // loops 3 times for 3 tries
        char username[6]; // username is 6 chars long
        char password[11];  // password is at most 10 chars long

        // read in the usernames
        printf("\nEnter login credentials: \n");
        printf("username: ");
        scanf("%s", username);
        printf("password: ");
        scanf("%s", password);
        
        if ((index = find_user(users, username, password, size)) != -1) { // check if users is found
            break;
        }
        if (i == 2) { // out of tries, print a message and quit
            printf("Access denied after three trials.\n");
            exit(1);
        }
        printf("\nInvalid credentials. Try again.\n");
    } 

    if (users[index].privilege == USER) { // make sure user has ADMIN privilege
        printf("You do not have administrator access rights. You cannot access the file.\n\n");
        exit(1);
    }
    
    int choice;
    bool case1or2Used = false; // holds the value to determine if we should save the users
    while (true) { // loop until user logs off
        printMenu();
        scanf("%d", &choice);
        if (choice == 1) { // add a new user
            char new_username[7];
            char privilege[2]; 

            printf("Enter username: ");
            scanf("%s", new_username);
            printf("Enter user privileges (1 for admin, 0 for user): ");
            scanf("%s", privilege);

            if (find_user(users, new_username, NULL, size) == -1) { // check if username is already taken
                User newUser;
                newUser.username = (char*) malloc(7); // allocate memory for new username
                strcpy(newUser.username, new_username); // copy new username into newUser

                if (strcmp(privilege, "0") == 0)  { // if '0' then user, else admin
                    newUser.privilege = USER;
                } else {
                    newUser.privilege = ADMIN;
                }

                new_password(&newUser); // assign new password 

                users[size] = newUser; // add new user to users

                size++; // increment size because new user
                case1or2Used = true; // set to true because we added a new user
                printf("User added successfully: %s\n", new_username);
            } else {
                printf("\nUsername already taken\n");
            }
        } else if (choice == 2) { // reset the password of an existing user  
            char new_username[7];

            printf("Enter username: ");
            scanf("%s", new_username);

            int found;
            if ((found = find_user(users, new_username, NULL, size)) != -1) { // check if username is found
                new_password(&users[found]); // modify password

                case1or2Used = true; // set to true because we modified a password

                printf("password reset successfully.\n");
            } else {
                printf("\nNo username found\n");
            }
        } else if (choice == 3)  { // log off
            if (case1or2Used) { // check if we need to save the users
                printf("Updating the file users.txt ...\n");
                save_users(users, fileName, size); // save the users
            }

            printf("Logout completed.\n");
            
            free_everything(users, size); // free allocated memory
            exit(0);
        } else {
            printf("NOT AN OPTION. TRY AGAIN.\n");
        }
    }
    return 0;
}