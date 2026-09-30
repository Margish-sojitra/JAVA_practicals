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
