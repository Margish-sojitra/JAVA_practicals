import exception.*;
import model.*;
import model.annotation.*;
import service.*;
import util.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static util.Validator.isValidMobile;

/**
 * MiniBank Capstone Entry Point.
 * Practical 13 & 14 Final Deliverable.
 */
public class MiniBank {

    private static final Path ACCOUNTS_FILE = Paths.get("mini_project", "data", "accounts.ser");
    private static final Path LOGS_DIR = Paths.get("mini_project", "logs");
    private static final Path REPORT_FILE = Paths.get("mini_project", "reports", "daily_summary_report.txt");

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("       MINIBANK CAPSTONE PROJECT (MILESTONE 3)            ");
        System.out.println("==========================================================");

        // --- Practical 13: Generic Repository & Reflection Demo ---
        System.out.println("\n[Practical 13 Demo] Generic Repository & @Id Reflection:");
        Repository<Account> demoRepo = new Repository<>();
        Account a1 = new SavingsAccount(101, "Alice", 5000, 1000);
        Account a2 = new CurrentAccount(102, "Bob", 3000, 2000);
        Account a3 = new FixedDepositAccount(103, "Charlie", 10000);

        // Save using @SafeVarargs saveAll
        demoRepo.saveAll(a1, a2, a3);
        System.out.println("  * Saved 3 accounts via generic Repository.saveAll()");

        // Fetch one by ID (extracted via @Id reflection)
        Account fetched = demoRepo.findById("102");
        System.out.println("  * Fetched by ID '102' using reflection: " + fetched);

        // Bounded generic average <N extends Number>
        Double[] balances = new Double[]{a1.getBalance(), a2.getBalance(), a3.getBalance()};
        System.out.println("  * Bounded generic Repository.average(): " + Repository.average(balances));

        // --- Practical 14: BankService Capstone Setup ---
        BankService bankService = new BankService();

        // Load existing accounts on startup (Practical 11 & 14)
        boolean hasLoaded = bankService.loadSavedAccounts(ACCOUNTS_FILE);
        if (!hasLoaded || bankService.getRepository().size() == 0) {
            try {
                bankService.openAccount(new SavingsAccount(101, "Alice", 5000, 1000));
                bankService.openAccount(new CurrentAccount(102, "Bob", 3000, 2000));
                bankService.openAccount(new FixedDepositAccount(103, "Charlie", 10000));
            } catch (BankException ignored) {}
        }

        // JVM shutdown hook ensures accounts are always saved to file
        Runtime.getRuntime().addShutdownHook(new Thread(() -> bankService.saveAccounts(ACCOUNTS_FILE)));

        // Practical 1 & 14 Interactive Banking Menu
        runInteractiveMenu(bankService);
    }

    private static void runInteractiveMenu(BankService bankService) {
        Scanner sc = new Scanner(System.in);
        BankInfo bankInfo = new BankInfo("MiniBank", "Main Branch");
        int choice = 0;

        do {
            System.out.println("\n---------------- " + bankInfo.name() + " (" + bankInfo.branch() + ") ----------------");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Account Statement");
            System.out.println("6. List All Accounts");
            System.out.println("7. Safe Batch Processing (TransactionProcessor)");
            System.out.println("8. Generate Daily Report");
            System.out.println("9. Run Automated Unit Tests (Practical 14 @Test)");
            System.out.println("10. Run Previous Milestone Demos (Weeks 1-12)");
            System.out.println("11. Exit");
            System.out.print("Select an option (1-11): ");

            if (!sc.hasNextInt()) {
                System.out.println("\nExiting session.");
                break;
            }

            choice = sc.nextInt();
            sc.nextLine(); // clear newline

            switch (choice) {
                case 1 -> {
                    System.out.println("\n[Open New Account]");
                    System.out.println("Type: 1. Savings | 2. Current | 3. Fixed Deposit");
                    System.out.print("Enter choice (1-3): ");
                    int type = sc.hasNextInt() ? sc.nextInt() : 1;
                    sc.nextLine();

                    System.out.print("Account Number (digits): ");
                    int accNo = sc.hasNextInt() ? sc.nextInt() : 0;
                    sc.nextLine();

                    System.out.print("Owner Name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Initial Balance: ");
                    double initBal = sc.hasNextDouble() ? sc.nextDouble() : 0.0;
                    sc.nextLine();

                    long extra = 0;
                    if (type == 1) {
                        System.out.print("Minimum Balance: ");
                        extra = sc.hasNextLong() ? sc.nextLong() : 500;
                        sc.nextLine();
                    } else if (type == 2) {
                        System.out.print("Overdraft Limit: ");
                        extra = sc.hasNextLong() ? sc.nextLong() : 2000;
                        sc.nextLine();
                    }

                    System.out.print("Mobile (10 digits, optional): ");
                    String mobile = sc.nextLine().trim();

                    System.out.print("Email (optional): ");
                    String email = sc.nextLine().trim();

                    try {
                        Account created = bankService.openAccount(type, accNo, name, initBal, extra, mobile, email);
                        System.out.println("SUCCESS! Created: " + created);
                    } catch (BankException e) {
                        System.err.println("FAILED: " + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.print("\nEnter Account Number: ");
                    String accNo = sc.nextLine().trim();
                    System.out.print("Enter Amount to Deposit: ");
                    long amount = sc.hasNextLong() ? sc.nextLong() : 0;
                    sc.nextLine();

                    try {
                        Account acc = bankService.deposit(accNo, amount);
                        System.out.println("SUCCESS! New Balance: " + acc.getBalance());
                    } catch (BankException e) {
                        System.err.println("FAILED: " + e.getMessage());
                    }
                }
                case 3 -> {
                    System.out.print("\nEnter Account Number: ");
                    String accNo = sc.nextLine().trim();
                    System.out.print("Enter Amount to Withdraw: ");
                    long amount = sc.hasNextLong() ? sc.nextLong() : 0;
                    sc.nextLine();

                    try {
                        Account acc = bankService.withdraw(accNo, amount);
                        System.out.println("SUCCESS! New Balance: " + acc.getBalance());
                    } catch (InsufficientFundsException e) {
                        System.err.println("FAILED: Insufficient funds (Shortfall: " + e.getShortfall() + ")");
                    } catch (BankException e) {
                        System.err.println("FAILED: " + e.getMessage());
                    }
                }
                case 4 -> {
                    System.out.print("\nFrom Account Number: ");
                    String from = sc.nextLine().trim();
                    System.out.print("To Account Number: ");
                    String to = sc.nextLine().trim();
                    System.out.print("Amount to Transfer: ");
                    long amount = sc.hasNextLong() ? sc.nextLong() : 0;
                    sc.nextLine();

                    try {
                        bankService.transfer(from, to, amount);
                        System.out.println("SUCCESS! Transferred " + amount + " from " + from + " to " + to);
                    } catch (BankException e) {
                        System.err.println("FAILED: " + e.getMessage());
                    }
                }
                case 5 -> {
                    System.out.print("\nEnter Account Number: ");
                    String accNo = sc.nextLine().trim();
                    try {
                        String stmt = bankService.statement(accNo);
                        System.out.println("\n" + stmt);
                    } catch (BankException e) {
                        System.err.println("FAILED: " + e.getMessage());
                    }
                }
                case 6 -> {
                    System.out.println("\n--- All Registered Accounts ---");
                    ArrayList<Account> list = bankService.listAccounts();
                    if (list.isEmpty()) {
                        System.out.println("No accounts found.");
                    } else {
                        for (Account a : list) {
                            System.out.println("  " + a);
                        }
                    }
                }
                case 7 -> {
                    System.out.println("\n--- Running Safe Batch Processing ---");
                    List<Command> batch = Arrays.asList(
                            new Command(TransactionType.DEPOSIT, "101", 500),
                            new Command(TransactionType.WITHDRAW, "102", 200),
                            new Command(TransactionType.DEPOSIT, "103", 1000)
                    );
                    bankService.processBatch(batch);
                }
                case 8 -> {
                    System.out.println("\n--- Daily Summary Report ---");
                    try {
                        String rpt = bankService.generateReport(LOGS_DIR, REPORT_FILE);
                        System.out.println(rpt);
                    } catch (Exception e) {
                        System.err.println("Error generating report: " + e.getMessage());
                    }
                }
                case 9 -> {
                    BankServiceTest.runAllTests();
                }
                case 10 -> {
                    runMilestoneDemos();
                }
                case 11 -> {
                    System.out.println("Saving accounts and exiting...");
                    bankService.saveAccounts(ACCOUNTS_FILE);
                    System.out.println("Thank you for using MiniBank!");
                }
                default -> System.out.println("Invalid selection. Please choose 1-11.");
            }

        } while (choice != 11);

        bankService.saveAccounts(ACCOUNTS_FILE);
    }

    /**
     * Preserves all demonstration exercises from Weeks 1 through 12.
     */
    public static void runMilestoneDemos() {
        System.out.println("\n==========================================================");
        System.out.println("         WEEKS 1 - 12 MILESTONE DEMONSTRATIONS            ");
        System.out.println("==========================================================");

        Account[] demoAccounts = new Account[]{
                new SavingsAccount(201, "Alice", 5000, 1000),
                new CurrentAccount(202, "Bob", 2000, 5000),
                new FixedDepositAccount(203, "Charlie", 10000)
        };

        System.out.println("\n1. Polymorphism (interestRate):");
        for (Account a : demoAccounts) {
            System.out.println("  " + a.getOwnerName() + " interest: " + a.interestRate() + "%");
        }

        System.out.println("\n2. Lambda WithdrawRule:");
        WithdrawRule rule = (account, amount) -> account.canWithdraw(amount);
        System.out.println("  Can Alice withdraw 1000? " + rule.allow(demoAccounts[0], 1000));

        System.out.println("\n3. Annotation Validator (@Positive):");
        Account neg = new SavingsAccount(204, "Invalid", -100, 0);
        String[] errs = AnnotationValidator.validate(neg);
        for (String err : errs) {
            System.out.println("  Validation Error: " + err);
        }

        System.out.println("\n4. Concurrency & Synchronization:");
        Account syncAcc = new SavingsAccount(205, "SyncHolder", 0, 0);
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new AccountWorker(syncAcc, 20, 10, false));
            threads[i].start();
        }
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        System.out.println("  Synchronized balance (expected 1000): " + syncAcc.getBalance());

        System.out.println("\n5. Deadlock Prevention (Lock Ordering):");
        Account aA = new SavingsAccount(10, "UserA", 5000, 100);
        Account aB = new SavingsAccount(20, "UserB", 5000, 100);
        try {
            Account.transferSafe(aA, aB, 100);
            System.out.println("  Deadlock-safe transfer completed successfully.");
        } catch (BankException ignored) {}

        System.out.println("==========================================================\n");
    }
}
