#include <map>
#include <chrono>
#include <iostream>
#include <vector>
#include <random>

using namespace std;

// Global variables
map<int, float> accounts; // Map of all the accounts in the bank
int NUM_ACCOUNTS = 10;
const float MAX_BALANCE = 100000; // The maximum total balance amongst all accounts 

/**
 * @brief Inserts the initial balance into the accounts
 */
void insert() {
    float balance = MAX_BALANCE / NUM_ACCOUNTS; // Determine the balance for each account
    for (int i = 1; i <= NUM_ACCOUNTS; i++) {
        accounts[i] = balance;
    }
    accounts[1] += MAX_BALANCE - (balance * NUM_ACCOUNTS); // add the remainder to ensure MAX_BALANCE is correct
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
 * @brief Sums The balance of all accounts
 * 
 * @return float The total balance
 */
float balance() {
    float balance = 0.0;
    for (int i = 1; i <= NUM_ACCOUNTS; i++) {
        balance += accounts[i];
    }
    return balance;
}

/**
 * @brief Deposits a random amount from one account to another
 */
void deposit() {
    int account1 = getRanInt(1, NUM_ACCOUNTS), account2 = account1;
    do {
        account2 = getRanInt(1, NUM_ACCOUNTS);
    } while (account2 == account1);

    float v1 = accounts[account1], v2 = accounts[account2];

    float amount = getRanInt(0, static_cast<int>(v1)); // Number between 0 and account[account1]

    v1 -= amount;
    v2 += amount;
    accounts[account1] = v1;
    accounts[account2] = v2;
}

/**
 * @brief Does the work of depositing and checking the balance
 * 
 * @param iterations The number of iterations to run
 */
void do_work(int iterations) {
    for (int i = 0; i < iterations; i++) {
        if (getRanInt(0, 100) <= 95) { // 95% chance of depositing
            deposit();
        } else {
            float bal = balance();
            if (bal != MAX_BALANCE) { // Check if the balance is correct
                cout << "ERROR: balance returned " << bal << endl;
            }
        }
    }
}

/**
 * @brief Cleans up the accounts map
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

    const int NUM_THREADS = 1;
    if (NUM_THREADS < 1) { // Check if the number of threads is correct
        cout << "Number of threads must be greater than 0" << endl;
        return 1;
    }

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

    insert();

    auto start = chrono::high_resolution_clock::now();
    do_work(NUM_THREADS * NUM_ITERATIONS);
    auto end = chrono::high_resolution_clock::now();
    auto duration = chrono::duration_cast<chrono::milliseconds>(end - start);

    #ifdef TEST
        cout << "Duration for single threaded: " << duration.count() << "ms" << endl;
        cout << "Ending balance for single thread: " << balance() << endl;
    #endif

    #ifndef TEST
        cout << NUM_THREADS << " " << NUM_ITERATIONS << " " << NUM_ACCOUNTS  << " "  << duration.count() << endl;
    #endif

    cleanup();

    return 0;
}