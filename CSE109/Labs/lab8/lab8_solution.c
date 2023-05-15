#include <stdio.h>       /* for printf */
#include <string.h>      /* for strlen */
#include <time.h>        /* for timespec struct, nanosleep */
#include <sys/types.h>   /* for getpid, getppid */
#include <unistd.h>      /* for getpid, getppid */
#include "syscalls.h"

int main(void) {
  int     pfd[2];                             /* for pipe file descriptors (pfd) */
  pid_t   pid;                                /* for PID from fork() */
  char    string[] = "Mind over mattress!\n"; /* for testing */
  char    buffer[80];                         /* hold string read from pipe */
  ssize_t numBytes;                           /* count of bytes written or read */
  struct  timespec req, rem;                  /* for values needed by nanosleep() */

  /* in this program, you will write to file descriptor pfd[1] and read from file descriptor pfd[0] */

  Pipe(pfd);  /* create pipe */
  if ((pid = Fork()) == 0) {   /* create process; in child */
    Close(pfd[0]);                                           /* close unneeded "read" end of pipe */
    fprintf(stdout, "CHILD: my process id is %d and my parent process is %d\n", getpid(), getppid());
    numBytes = Write(pfd[1], string, (strlen(string)+1));    /* send "string" down the pipe */
    fprintf(stdout, "CHILD: wrote %ld bytes on file descriptor %d\n", numBytes, pfd[1]);
    Close(pfd[1]);                                           /* close "write" end of pipe */
  } else {          /* pid != 0  in parent */
    Close(pfd[1]);                                           /* close unneeded "write" end of pipe */
    fprintf(stdout, "PARENT: my child process id is %d, my process id is %d, and my parent process is %d\n", pid, getpid(), getppid());
    numBytes = Read(pfd[0], buffer, sizeof(buffer));         /* read in a string from the pipe */
    fprintf(stdout, "PARENT: read %ld bytes from file descriptor %d\n", numBytes, pfd[0]);
    fprintf(stdout, "PARENT: Received string: %s", buffer);
    Close(pfd[0]);                                           /* close "read" end of pipe */
  }

  req.tv_sec = 9;
  req.tv_nsec = 500000000L; /* using L since tv_nsec is a long int */
  Nanosleep(&req, &rem);

  return 0;
}

/*
 * Q0: Describe what the pipe() function does?
 * A0: The pipe function creates an unnamed pipe through which processes can communicate. Data written to one end of the pipe
 *    can be read from the other end of the pipe on a first-in-first-out basis.
 *
 * Q1: What is the main functional difference between the pipe you are using in this lab versus the socketpair
 *     you used in a recent lab?
 * A1: The pipe is unidirectional while the socketpair is bidirectional.
 *
 * A2: What did you decide about wrapping the getpid and getppid? Why?
 * Q2: These do not need to be wrapped since they are guaranteed to work (i.e., no error checking and handling is required).
 *
 * Q3: What did you observe when you executed "pstree $$" on the command line while your program was running?
 * A3: A tree-like structure is printed that shows "bash -- bash -- lab8 -- lab8" (and the pstree) as the process hierarchy.
 *
 * Q4: Why are we specifying "strlen(string) + 1" in the write function call? I.e., why not just "strlen(string)"?
 * A4: Because we want the string terminator written as well, so when we print the string, we will not print beyond the message.
 *
 * Q5: What was the real amount of time your program ran? (If your real time is not greater than or equal to 9.5 seconds,
 *     you need to change the values you set for nanosleep.)
 * A5: 9.503 (or something simimlar is acceptable)
 *
 * Q6: What is user time and sys time? Why do you think these do not sum up to the real time?
 * A6: The user time is the amount of time the code ran with the permissions of the user. The sys time is the amount
 *     of time the code ran with the permission of the kernel... these are your system calls. For most of the time, 
 *     the process is paused waiting for the timer to expire. Also, there are other processes running on the machine
 *     at the same time is our processes.
 *
 * Q7: If a function, such as close() or pipe(), only returns 0 or -1, do you really need to store its return code
 *     in your Stevens-style wrapper OR could you just hardcode "return 0", knowing that a check for -1 would result
 *     in an exit from the wrapper?
 * A7: Yes, you could avoid saving the return code value from the system call; just compare the return code against -1.
 *
 * Q8: If you Stevens-style wrap a function, such as close() or pipe(), that returns 0 when successful, do you need to
 *     store the return value in the code in which you called Close() or Pipe()?
 * A8: No, you usually will have no interest in the return value from your wrapped function.
 */
