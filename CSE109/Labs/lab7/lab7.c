/*
 * File: lab7.c
 * UserID: Ivanna B. Kewl
 */

#include <stdio.h>       /* for fprintf */
#include <string.h>      /* for strcpy */
#include <sys/socket.h>  /* PF_FILE, SOCK_STREAM */
#include "syscalls.h"    /* for wrapped system calls */

#define msgLen 64

/* global variable (don't modify) */
static char const key[msgLen] = "91AcZ4l0nX39)e.xm2l'gu20wsx87s4K7&-*C1Y%>?11niO0=~bzN,}x[$vZ2/5q"; /* pretend both processes have this secret key */

/* prototype (don't modify) */
void crypto(char *);

/* main - only need to make 4 edits here; replace C++-style comments (i.e., "//") with function calls */
int main(void) {
  pid_t pid;
  int sockd[2]; /* the pair of socket descriptors */

  Socketpair(PF_FILE, SOCK_STREAM, 0, sockd);  /* creates an unnamed pair of connected sockets: sockd[0] and sockd[1] */

  if ((pid = Fork()) != 0) {  /* parent */
    Close(sockd[0]);  /* we only need one end of the socket in the parent, so we will choose to close sockd[0] */
    char message[msgLen];
    // add a call to Read() to read msgLen bytes from sockd[1] into message array
    Read(sockd[1], message, msgLen);
    fprintf(stdout, "parent: received encrypted message => '%s'\n", message);
    crypto(message);  /* decrypt message from parent */
    fprintf(stdout, "\nparent: decrypted message          => '%s'\n\n", message);
    strcpy(message, "That doesn't surprise me!"); 
    crypto(message);    /* encrypt reply to child */
    // add a call to Write() to write the msgLen bytes in message array to sockd[1]
    Write(sockd[1], message, msgLen);

    Wait(NULL); /* call Wait to wait for child to die (sounds sad, but it's not) */
    Close(sockd[1]);
  } else {                    /* child */
    Close(sockd[1]);  /* we only need one end of the socket in the child (the parent is using sockd[1]), so we will close sockd[1] */
    char message[msgLen] = "Professor Erle has a \"MOM\" tattoo and wears a wig.";
    crypto(message);  /* encrypt message before sending to child */
    // add a call to Write() to write msgLen bytes in message array to sockd[0]
    Write(sockd[0], message, msgLen);
    // add a call to Read() to read msgLen bytes from sockd[0] into message array 
    Read(sockd[0], message, msgLen);
    fprintf(stdout, "child:  received encrypted reply   => '%s'\n", message);
    crypto(message);  /* decrypt reply from parent */
    fprintf(stdout, "\nchild:  decrypted reply            => '%s'\n\n", message);
    Close(sockd[0]);
  }

  return 0;
}

/* add functionality to crypto function here */
void crypto(char *buffer) {
  for (int i = 0; i < msgLen; i++) {
    buffer[i] ^= key[i];
  }
}


/*
 * Q0: What are two attributes of socketpair()? This is called a "gimme", when someone gives you the answer.
 * A0: 1) a socketpair is bidirectional
 *     2) socketpairs can be datagram-oriented (some communication channels are only stream-oriented)
 *
 * Q1: Creating sockets with socketpair() is SO much easier than the whole socket()/bind()/listen()/connect()/accept() process. 
 *     Why don't we always just use socketpair() for all communications? Hint: read 'man 3p socketpair'... which refers you to 'man 2 socketpair'.
 * A1: socketpair() requires the use of Fork(), therefore, the operaitons need to start at the same time inorder for socketpair to work. Additionally, socketpair doens't work with over the interent(only in unix)
 *
 * Q2: What is the purpose of the wait system call? Hint: read 'man 3p wait'... which refers you to 'man 2 wait'.
 * A2: The wait system call pauses the parent process untill the child process finishes running. This ensures that there are no issues with the messages
 *     
 * Q3: Wait a minute, why are there man pages for these system calls in chapter 3p and chapter 2? In fact, check out 'man -a socket'... all the chapters
 *     with pages on "socket" will be displayed one after the other. (Just hit 'q' to move on to the next man page.) When do we use which chapter? Hint:
 *     'man man' may help with some of this answer; but you'll probably want to access the ol' Internet.
 * A3: There are man pages in both chapters becasue the system calls apply to both chapters and therefore needs man pages for system and library calls chapters. You use chapter 2 for system calls and chapter 3 for library calls.
 *
 * Q4: Why is the encrypted message received by the parent so short? And why is the encrypted message received by the child split over two lines?
 * A4: The encrypted message recieved by the child likely split over two lines because the crypto() fuction changed the character in the message to a newline character. The encrypted message recieved by the parent is short because it is shorter.
 *
 * Q5: In the parent (line 33), why didn't we simply assign "That doesn't surprise me!" to the message array variable? I.e., why not do the following?    
 *     message = "That doesn't surprise me!";                                                                                                             
 * A5: you need to use strcpy because crypto would change the values of the message. Using strcpy will copy the values of the message into the array, and only change those values
 *
 * Q6: Why is it that we can use the same crypto() function to both encrypt and decrypt?
 * A6: The XOR operation that crypto() message uses is a reversible operation. Therefore, if you XOR a message with a key, you can XOR the same message with the same key to get the original message back.
 */
