#include <stdio.h>
#include <sys/types.h> 
#include <sys/stat.h>
#include <unistd.h> 
#include <stdlib.h>
#include <signal.h>
#include <wait.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <limits.h>
#include <time.h>

typedef void handler_t;
int avg = 0; /* Global variables to hold the final average */
volatile int p_sum = 0; /* Global Varaible to hold the sum */
const unsigned width = N / P; /* How many items will be read for each child process */

void initialize(int *);
void unix_error(const char *msg);
pid_t Fork();
pid_t Wait(int *status);
pid_t Waitpid(pid_t pid, int *status, int options);
int Sigqueue(pid_t pid, int signum, union sigval value);
int Sigemptyset(sigset_t *set);
int Sigfillset(sigset_t *set);
int Sigaction(int signum, const struct sigaction *new_act, struct sigaction *old_act);
int Sigprocmask(int how, const sigset_t *set, sigset_t *oldset);
ssize_t Write(int d, const void *buffer, size_t nbytes);
int Sigdelset(sigset_t *set, int signo);
handler_t *Signal(int signum, handler_t *handler);
void sigusr1_handler(int sig, siginfo_t *value, void *ucontext);


int main(){
	int A[N];
	initialize(A);
	unsigned start, end; // used to determine bounds for child processes
	struct sigaction action, old_action;

	/* install a portable handler for SIGUSR1 using the wrapper function Signal */
    action.sa_flags = SA_SIGINFO | SA_RESTART;
    action.sa_sigaction = sigusr1_handler;
    Sigfillset(&action.sa_mask);
	Sigaction(SIGUSR1, &action, &old_action);

	/* print the message for installing SIGUSR1 handler*/
	fprintf(stderr, "Parent process %d installing SIGUSR1 handler\n", getpid());
	
	/* create (P child processes) to calculate a partial average and send the signal SIGUSR1 to the parent*/
	for (int i = 0; i < P; i++) {
		if (Fork() == 0) {
			/* Determine Bounds */
			start = width * i;
            end = start + width;

			/* Print child average */
			fprintf(stderr, "Child process %d finding the average from %d to %d\n", getpid(), start, end - 1);
			
			/* Calculate partial sum */
            int partial_avg = 0;
            for (unsigned j = start; j < end; j++) {
                partial_avg += A[j];
            }

            /* Set sigval union to send to child */
			union sigval value;
            value.sival_int = partial_avg / P; 

			/* Print sending */
			fprintf(stderr, "Child process %d sending SIGUSR1 to parent process with the partial average %d\n", getpid(), value.sival_int);
	
			/* Send signal to parent process */
			sleep(i);
			Sigqueue(getppid(), SIGUSR1, value);
			
			/* Child process exits */
			exit(0); 
		}
	}
	
	/* reap the (P) children */
	int status;
	while (Wait(&status) > 0) {
		/* Print normally */
		if (WIFEXITED(status)) {
			fprintf(stderr, "Child process %d terminated normally with exit status %d\n", getpid(), status);
		} else {
			fprintf(stderr, "Child process %d terminated abnormally with exit status %d\n", getpid(), status);
		}

	}

	/* print the array A if the macro TEST is defined */
#ifdef TEST
	printf("A = {");
	for(int i=0; i<N-1; i++){
		printf("%d, ", A[i]);
	}
	printf("%d}\n", A[N-1]);
#endif
	
	/* print the final average */
	avg =  p_sum / P;
	fprintf(stderr, "Final Average = %d\n", avg);

	exit(0);
}

/**
 * Initializes an array of integers with random values
 * @param M for the array to be initialized
 */
void initialize(int M[N]){
    int i;
    srand(time(NULL));
    for(i=0; i<N; i++){
        M[i] = rand() % N;
    }
}

/**
 * Handler for SIGUSR1
 * @param sig for the signal number
 * @param value for the data 
 * @param ucontext for any aditional information
*/
void sigusr1_handler(int sig, siginfo_t *value, void *ucontext) {
	/* Store current errno */
	int olderrno = errno;

	/* Set mask */
	sigset_t mask, oldMask;
	Sigfillset(&mask);
	Sigdelset(&mask, SIGUSR1);
	
	/* Block Signals */
	Sigprocmask(SIG_BLOCK, &mask, &oldMask);

	/* Add value sent by children*/
	p_sum += value->si_value.sival_int;

	/* Write caught */	
	char out[64];
	sprintf(out, "Parent process caught SIGUSR1 with partial average: %d\n", value->si_value.sival_int);
	Write(2, out, strlen(out));

	/* Restore old mask */ 
	Sigprocmask(SIG_SETMASK, &oldMask, NULL);

	/* Restore old errno */
	errno = olderrno;

	/*
		Guidlines for handlers followed: Handler kept as simple as possible. All signals (except SIGUSR1) are blocked to protect 
		access to shared global data. Only safe and async-signal-safe functions are called (no use of printf). 
		Declared global variables with prefix volatile.
	*/
}

/**
 * Prints an error message
 * @param msg for the message to be printed
*/
void unix_error(const char *msg) {
	fprintf(stderr, "%s: %s\n", msg, strerror(errno));
	exit(1);
}

/**
 * Steven-style wrapper for the function fork
 * @return PID of the child process is returned in the parent, and 0 is returned in the child. On failure, -1
*/
pid_t Fork() {
	pid_t f;
	if ((f = fork()) == -1) {
		unix_error("Fork error");     
	}
	return f;
}

/**
 * Steven-style wrapper for the funciton wait
 * @param status the status to wait for
 * @return PID of the terminated child; on failure, -1 is returned
*/
pid_t Wait(int *status) {
	pid_t w;
	if ((w = wait(status)) == -1) {
		return w;
	}
	return w;
}

/**
 * Steven-style wrapper for the function waitpid
 * @param pid for the process id
 * @param status for the staus to wair for
 * @param options for the options
 * @return PID of the terminated child; on failure, -1 is returned
*/
pid_t Waitpid(pid_t pid, int *status, int options) {
	pid_t wp;
	if ((wp = waitpid(pid, status, options)) == -1) {
		unix_error("Waitpid error");
	}
	return wp;
}

/**
 * Steven-style wrapper for the function sigqueue
 * @param pid for the process id
 * @param signum for the signal number
 * @param value for the value to be sent
 * @return returns 0, indicating that the signal was successfully queued to the receiving process. Otherwise, -1 
*/
int Sigqueue(pid_t pid, int signum, union sigval value) {
	int sq;
	if ((sq = sigqueue(pid, signum, value)) < 0) {
		unix_error("Sigqueue error");
	}
	return sq;
}

/**
 * Steven-style wrapper for the function sigemptyset
 * @param set for the sigset_t to be intialized
 * @return 0 on success, -1 otherwise
*/
int Sigemptyset(sigset_t *set) {
	int ses;
	if ((ses = sigemptyset(set)) == -1) {
		unix_error("Sigemptyset error");
	}
	return ses;
}

/**
 * Steven-style wrapper for the function sigfillset
 * @param set for the sigset_t to be intialized
 * @return 0 on success, -1 otherwise
*/
int Sigfillset(sigset_t *set) {
	int sfs;
	if ((sfs = sigfillset(set)) == -1) {
		unix_error("Sigfillset error");
	}
	return sfs;
}

/**
 * Steven-style wrapper for the function sigdelset
 * @param set for the sigset_t to be intialized
 * @param signo for the signal number
 * @return 0 on success, -1 otherwise
*/
int Sigdelset(sigset_t *set, int signo) {
	int sds;
	if ((sds = sigdelset(set, signo)) == -1) {
		unix_error("Sigdelset error");
	}
	return sds;
}

/**
 * Steven-style wrapper for the function sigaction
 * @param signum for the signal number to be sent
 * @param new_act for the sigaction to be set
 * @param old_act for the sigaction to store current state
 * @return 0 on success, -1 otherwise
*/
int Sigaction(int signum, const struct sigaction *new_act, struct sigaction *old_act) {
	int sa;
	if ((sa = sigaction(signum, new_act, old_act)) == -1) {
		unix_error("Sigaction error");
	}
	return sa;
}

/**
 * Steven-style wrapper for the function sigprocmask
 * @param how for what should be masket
 * @param set the set to be set
 * @param old_act for the set to store current state
 * @return 0 on success, -1 otherwise
*/
int Sigprocmask(int how, const sigset_t *set, sigset_t *oldset) {
	int spm;
	if ((spm = sigprocmask(how, set, oldset)) == -1) {
		unix_error("Sigprocmask error");
	}
	return spm;
}

/**
 * Steven-style wrapper for the function write
 * @param d for the file descriptor
 * @param buffer for the location of the buffer
 * @param nbytes for the number of bytes to write
 * @return 
*/
ssize_t Write(int d, const void *buffer, size_t nbytes) {
	ssize_t w;
	if ((w = write(d, buffer, nbytes)) == -1) {
		unix_error("Write error");
	}
	return w;
}

/**
 * Steven-style wrapper for the function signal
 * @param signum for the signal number
 * @param handler for the handler function
 * @return returns the previous value of the signal handler. On failure, returns SIG_ERR
*/
handler_t *Signal(int signum, handler_t *handler) {
	if (signal(signum, handler) == SIG_ERR) {
		unix_error("Signal error");
	}
	return handler;
}