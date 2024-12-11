# Name

Julian Chattopadhyay (juc226)

## Assumptions

- To pay off a debt (i.e. loans, credit cards) you may only use savings accounts
- Creating new customers, accounts, loans, cards, etc... can only be done using the manager interface
- debit cards are linked to checking accounts, so the max you can spend is the amount in the linked checking account
- savings accounts will continue to apply the penalty fee as long as the balance is below the min_balance. So if the min_balance is $50 and the current balance is $30 and you then withdraw $10, the penalty will be applied again. This continues until the balance rises above the min_balance
- creating a savings account automatically sets the balance to min_balance
- For credit cards, payments can only be made on balance due and not running balance. The assumption here is the balance due is the amount due from the month prior
- Savings accounts can have negative balances, but checking cannot
- deposit money comes from thin air, while withdraws must exist in an account

## Interfaces

- I implemented interfaces 2, 3, 7, 4, 6, and the 'new' portion of 5
- Manager (logged in as a manger)
  - Creating a new customer
  - Creating a new Account (savings, checking, or investment)
  - Creating a new loan
  - Creating a new card (credit or debit)
- Customer (logged in as a user)
  - Make a deposit or withdraw (into a checking or savings account)
  - Pay down a debt on a loan or credit card
  - Make a purchase using a credit or debit card
  - View all accounts associated with a user

## Trigger

- I have created a trigger that automatically updates the date a checking account was last used. When the balance of the account is changed (via a withdraw, deposit, or debit card) the trigger automatically sets the last used date to the system time. You can test this by modifying the balance of a checking account and then using the view all accounts feature to see the update reflected. Just make sure that the date was not already the date you are testing

## Data Generation

- All data was generated using Mockaroo or created manually

## Testing

- I would not recommend using customer #1 because I used this account to test my code. Any of the other customers are largely untouched
- To see updates made to user accounts, type 4 in the main user interface to view all of the accounts associated with the user
- The 'Exit' option in the user/manager interface brings you back to the login page, while the 'Quit' option ends the program
- Feel free to create a new user if you'd like to start from scratch
- Account/user information is displayed when necessary, don't worry about having to search for it
- If you'd like to recompile the jar file, the run.sh file compiles and automatically starts running the program. This file was generated using chatgpt  

## Code Structure

- I am particularly proud of the way I've structured some of the classes. For example, I have created a static ErrorHandler class that handles all errors caused by inputs or exceptions. This way all errors look the same. There is also an InputHandler class that handles all inputs and ensures that they are within specified values and of the correct data type.
