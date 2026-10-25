import exception.*;
import model.*;
import model.annotation.*;
import service.*;
import util.*;

import java.util.Scanner;

// Requirement 2.5: Use one static import
import static util.Validator.isValidMobile;

public class MiniBank {

    public static void main(String[] args) {

        // Requirement 1.6: place objects of the three types in an Account[] array, loop through it and call interestRate() on each
        Account[] accounts = new Account[3];
        accounts[0] = new SavingsAccount(101, "Alice", 5000, 1000);
        accounts[1] = new CurrentAccount(102, "Bob", 2000, 5000);
        accounts[2] = new FixedDepositAccount(103, "Charlie", 10000);

        System.out.println("----- Account Array Polymorphism -----");
        for (Account acc : accounts) {
            System.out.println(acc.getOwnerName() + " interest rate: " + acc.interestRate());
            
            // pattern instanceof check to handle one type specially
            if (acc instanceof SavingsAccount sa) {
                System.out.println("  -> This is a Savings Account!");
            } else if (acc instanceof CurrentAccount ca) {
                System.out.println("  -> This is a Current Account!");
            } else if (acc instanceof FixedDepositAccount fda) {
                System.out.println("  -> This is a Fixed Deposit Account!");
            }
        }

        // Requirement 2.3: Use WithdrawRule two ways in main: first with an anonymous class, then with a lambda expression
        System.out.println("\n----- WithdrawRule Testing -----");
        
        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };
        System.out.println("Anonymous class rule allow 1000 from Alice? " + rule1.allow(accounts[0], 1000));

        WithdrawRule rule2 = (account, amount) -> account.canWithdraw(amount);
        System.out.println("Lambda expression rule allow 15000 from Bob? " + rule2.allow(accounts[1], 15000));

        // Requirement 3.5: In main, create an Account with a negative balance and run AnnotationValidator.validate() to print the error.
        System.out.println("\n----- Annotation Validator Testing -----");
        Account negativeAcc = new SavingsAccount(104, "Dave", -500, 0); 
        String[] errors = AnnotationValidator.validate(negativeAcc);
        for (String error : errors) {
            System.out.println("Validation Error: " + error);
        }

        System.out.println("\nStatic Import Test - isValidMobile: " + isValidMobile("9876543210"));

        // ===== Week 8: Exception Handling & Fault-Tolerance =====
        System.out.println("\n----- Week 8: Exception Handling & Fault-Tolerance -----");

        // Try-with-resources with AutoCloseable BankTransactionSession
        try (BankTransactionSession session = new BankTransactionSession("Txn-Session-01")) {
            session.log("Initiating operations for Alice (Acc 101) and Bob (Acc 102)");

            // 1. Successful Deposit & Withdrawal
            try {
                accounts[0].deposit(1000);
                session.log("Deposited 1000 into Alice. New balance: " + accounts[0].getBalance());

                accounts[0].withdraw(2000);
                session.log("Withdrew 2000 from Alice. New balance: " + accounts[0].getBalance());
            } catch (BankException e) {
                System.out.println("Operation failed: " + e.getMessage());
            } finally {
                System.out.println("Operation block 1 finalized.");
            }

            // 2. InvalidAmountException demonstration (non-positive amount)
            try {
                session.log("Attempting negative deposit into Alice...");
                accounts[0].deposit(-200);
            } catch (InvalidAmountException e) {
                System.out.println("Caught Expected InvalidAmountException: " + e.getMessage());
            } finally {
                System.out.println("Deposit validation check finalized.");
            }

            // 3. InsufficientFundsException demonstration (with shortfall)
            try {
                session.log("Attempting excessive withdrawal from Alice...");
                accounts[0].withdraw(50000);
            } catch (InsufficientFundsException e) {
                System.out.println("Caught Expected InsufficientFundsException: " + e.getMessage() +
                                   " [Shortfall: " + e.getShortfall() + "]");
            } catch (BankException e) {
                System.out.println("BankException: " + e.getMessage());
            } finally {
                System.out.println("Withdrawal funds check finalized.");
            }

            // 4. AccountNotFoundException demonstration
            try {
                session.log("Attempting transfer to null account...");
                accounts[0].transfer(null, 500);
            } catch (AccountNotFoundException e) {
                System.out.println("Caught Expected AccountNotFoundException: " + e.getMessage());
            } catch (BankException e) {
                System.out.println("BankException: " + e.getMessage());
            } finally {
                System.out.println("Null account transfer check finalized.");
            }

            // 5. Successful Transfer demonstration
            try {
                session.log("Transferring 500 from Alice (101) to Bob (102)...");
                accounts[0].transfer(accounts[1], 500);
                session.log("Transfer successful! Alice Balance: " + accounts[0].getBalance() +
                            ", Bob Balance: " + accounts[1].getBalance());
            } catch (BankException e) {
                System.out.println("Transfer failed: " + e.getMessage());
            } finally {
                System.out.println("Valid transfer check finalized.");
            }
        } // AutoCloseable session closed here

        System.out.println("\n----- MiniBank Menu -----");
        Scanner sc = new Scanner(System.in);
        BankInfo bank = new BankInfo("MiniBank", "Main Branch");

        System.out.println("Welcome to " + bank.name() + ", " + bank.branch());

        int choice;
        do {
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) break;
            
            choice = sc.nextInt();

            String message = switch (choice) {
                case 1 -> "Open Account - To be implemented in later lab.";
                case 2 -> "Deposit - To be implemented in later lab.";
                case 3 -> "Withdraw - To be implemented in later lab.";
                case 4 -> "Transfer - To be implemented in later lab.";
                case 5 -> "Thank you for using MiniBank.";
                default -> "Invalid Choice.";
            };

            System.out.println(message);

        } while (choice != 5);
        sc.close();
    }
}
