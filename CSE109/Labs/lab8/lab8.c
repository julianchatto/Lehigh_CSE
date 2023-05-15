#include <stdio.h>       /* for fprintf */
#include <string.h>      /* for strlen, strerror */
/* you will need to add some includes here for nanosleep, getpid, and getppid */

int main(void) {
  int     pfd[2];                             /* for pipe file descriptors (pfd) */
  pid_t   pid;                                /* for PID from fork() */
  char    string[] = "Mind over mattress!\n"; /* for testing */
  char    buffer[80];                         /* hold string read from pipe */
  ssize_t numBytes;                           /* count of bytes written or read */
  /* you will need to declare some variables needed by nanosleep */

  /* in this program, you will write to file descriptor pfd[1] and read from file descriptor pfd[0] */

  /* call your wrapped pipe() command to create a pipe to communicate from child to parent */

  if (      ) {  /* call wrapped fork() command, use return value to determine if executing in child */

    /* close the unneeded "read" end of the pipe */

    /* use fprintf call to print to stdout the process ID of the child and the process ID of its parent */

    /* replace the following 6 lines of code with 2 lines: a call to Write() and the fprintf call in the else */
    if ((numBytes = write(pfd[1], string, (strlen(string)+1))) == -1) { /* send "string" down the pipe */
      fprintf(stderr, "CHILD: write error (%d): %s\n", errno, strerror(errno));
      exit(errno);
    } else {
      fprintf(stdout, "CHILD: I wrote %ld bytes on file descriptor %d\n", numBytes, pfd[1]);
    }

    /* close the "read" end of the pipe */

  } else {  /* parent code in here */

    /* close the unneeded "read" end of the pipe */

    /* use fprintf call to print to stdout the process ID of the child, its own process ID, and the process ID of its parent */

    /* replace the following 6 lines of code with 2 lines: a call to Read() and the fprintf call in the else */
    if ((numBytes = read(pfd[0], buffer, sizeof(buffer))) == 0) { /* read in a string from the pipe */
      fprintf(stderr, "PARENT: read error (%d): %s\n", errno, strerror(errno));
      exit(errno);
    } else {
      fprintf(stdout, "PARENT: I read %ld bytes from file descriptor %d\n", numBytes, pfd[0]);
    }

    fprintf(stdout, "PARENT: I received a message: %s", buffer);

    /* close the "write" end of the pipe */
    
  }

  /* set your variables for the upcoming call to Nanosleep and call Nanosleep() */

  return 0;
}

/*
 *  Q0: Describe what the pipe() function does?
 *  A0: 
 *
 *  Q1: What is the main functional difference between the pipe you are using in this lab versus the socketpair
 *      you used in a recent lab? 
 *  A1: 
 *  
 *  A2: What did you decide about wrapping the getpid, getppid, and getpgrpid functions? Why?
 *  Q2: 
 *
 *  Q3: What did you observe when you executed "pstree $$" on the command line while your program was running?
 *  A3: 
 *  
 *  Q4: Why are we specifying "strlen(string) + 1" in the write function call? I.e., why not just "strlen(string)"?
 *  A4: 
 *  
 *  Q5: What was the real amount of time your program ran? (If your real time is not greater than or equal to 9.5 seconds,
 *      you need to change the values you set for nanosleep.)
 *  A5: 
 *  
 *  Q6: What is user time and sys time? Why do you think these do not sum up to the real time?
 *  A6: 
 *
 *  Q7: If a function, such as close() or pipe(), only returns 0 or -1, do you really need to store its return code 
 *      in your Stevens-style wrapper OR could you just hardcode "return 0", knowing that a check for -1 would result
 *      in an exit from the wrapper?
 *  A7: 
 *
 *  Q8: If you Stevens-style wrap a function, such as close() or pipe(), that returns 0 when successful, do you need to
 *      store its return value in the code in which you called Close() or Pipe()?
 *  A8: 
 */
