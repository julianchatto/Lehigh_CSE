#include <atomic>
#include <condition_variable>
#include <functional>
#include <iostream>
#include <queue>
#include <thread>
#include <unistd.h>

#include "pool.h"

using namespace std;

class my_pool : public thread_pool {
	// Hint: the reference solution uses an atomic variable, a queue, a mutex, a
	// condition variable, two function pointers, and a vector.  You probably
	// can't implement a pool with less.

	// Hint: you might want to add additional private methods to this class.  For
	// example, in the reference solution, one of the methods of this class is the
	// function that each thread runs.

private:
    vector<thread> threads;  // pool of worker threads
    queue<int> task_queue;   // queue of socket descriptors
    mutex queue_mutex;       // protects access to task_queue
    condition_variable cv;   // for thread synchronization
    atomic<bool> active;     // tracks if pool is active
    function<bool(int)> handler;  // function to handle connections
    function<void()> shutdown_handler;  // shutdown handler function

    // running thread function
    void thread_runner() {
        while (true) {
            unique_lock<mutex> lock(queue_mutex);
            // wait until there is a task or pool becomes inactive
			while (task_queue.empty() && active) {
				cv.wait(lock);
			}

            // if there are no tasks and the pool is inactive, exit the loop
            if (task_queue.empty() && !active) {
				cout << "Server terminated" << endl;
                break;
            }

            // process a task if available
            if (!task_queue.empty()) {
                int sd = task_queue.front();
                task_queue.pop();
                lock.unlock(); // unlock the mutex before handling the task

                // process the task using the handler function
                bool done = handler(sd);

                // if the handler signals shutdown, mark the pool as inactive
                if (done) {
                    {
                        lock_guard<mutex> shutdown_lock(queue_mutex);
                        active = false;
						cv.notify_all(); // notify all threads to exit
                    }
					shutdown_handler();
                }
				close(sd); // close socket after processing
            }
        }
        cout << "Server terminated" << endl;
    }

public:
	/// construct a thread pool by providing a size and the function to run on
	/// each element that arrives in the queue
	///
	/// @param size    The number of threads in the pool
	/// @param handler The code to run whenever something arrives in the pool
	my_pool(int size, function<bool(int)> handler) {
		active = true;          // sets pool active status
        this->handler = handler;  // assign the handler function

        // create the running threads and add them to the vector
        for (int i = 0; i < size; ++i) {
            threads.emplace_back(thread(&my_pool::thread_runner, this));
        }
	}

	/// destruct a thread pool
	///
	/// Hint: If you do things right, you probably won't need to write a
	/// destructor
	// virtual ~my_pool() = default;
	virtual ~my_pool() {
		await_shutdown();
	}

	/// Allow a user of the pool to provide some code to run when the pool decides
	/// it needs to shut down.
	///
	/// @param func The code that should be run when the pool shuts down
	virtual void set_shutdown_handler(function<void()> func) {
		shutdown_handler = func;
	}

	/// Allow a user of the pool to see if the pool has been shut down
	virtual bool check_active() {
		return active;
	}

	/// Shutting down the pool can take some time.  await_shutdown() lets a user
	/// of the pool wait until the threads are all done servicing clients.
	virtual void await_shutdown() {
		// join all threads to ensure they finish processing
		for (auto& t : threads) {
			if (t.joinable()) {
				t.join();  // wait for thread to finish
			}
		}
    	cout << "Server terminated" << endl;
	}

	/// When a new connection arrives at the server, it calls this to pass the
	/// connection to the pool for processing.
	///
	/// @param sd The socket descriptor for the new connection
	virtual void service_connection(int sd) {
		{
			lock_guard<mutex> lock(queue_mutex);
			task_queue.push(sd);  // push the socket descriptor to the queue
		}
		// Notify one waiting thread to process the new task
		cv.notify_one();
	}
};

/// Create a thread_pool object.
///
/// We use a factory pattern (with private constructor) to ensure that anyone
thread_pool *pool_factory(int size, function<bool(int)> handler) {
	// Hint: Don't change this function!
	return new my_pool(size, handler);
}
