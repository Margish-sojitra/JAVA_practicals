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

        // ===== Week 9: Concurrency, Race Conditions & Synchronization =====
        System.out.println("\n----- Week 9: Concurrency, Race Conditions & Synchronization -----");

        // Part 1: Unsynchronized deposits across 10 threads (demonstrating Race Condition)
        System.out.println("-> Test 1: Unsynchronized Deposits (Demonstrating Race Condition)");
        Account raceAccount = new SavingsAccount(201, "RaceTest", 0, 0);
        int numThreads = 10;
        int depositsPerThread = 50;
        long depositAmount = 10;
        double expectedTotal = (double) numThreads * depositsPerThread * depositAmount;

        Thread[] unsyncThreads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            unsyncThreads[i] = new Thread(
                new AccountWorker(raceAccount, depositsPerThread, depositAmount, true),
                "Worker-Unsync-" + (i + 1)
            );
            unsyncThreads[i].start();
        }

        for (Thread t : unsyncThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Expected Balance: " + expectedTotal);
        System.out.println("Actual Balance (Unsynchronized): " + raceAccount.getBalance());
        if (raceAccount.getBalance() < expectedTotal) {
            System.out.println("Observed Race Condition: Lost updates occurred due to lack of synchronization.");
        } else {
            System.out.println("Note: No lost updates observed in this run.");
        }

        // Part 2: Synchronized deposits across 10 threads (Thread-safe)
        System.out.println("\n-> Test 2: Synchronized Deposits (Thread-Safe with Synchronization)");
        Account syncAccount = new SavingsAccount(202, "SyncTest", 0, 0);
        Thread[] syncThreads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            syncThreads[i] = new Thread(
                new AccountWorker(syncAccount, depositsPerThread, depositAmount, false),
                "Worker-Sync-" + (i + 1)
            );
            syncThreads[i].start();
        }

        for (Thread t : syncThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Expected Balance: " + expectedTotal);
        System.out.println("Actual Balance (Synchronized): " + syncAccount.getBalance());
        if (syncAccount.getBalance() == expectedTotal) {
            System.out.println("Synchronization Success: Balance is exactly correct! All 10 threads synchronized safely.");
        }

        // ===== Week 10: Thread Pools, Producer-Consumer & Deadlock Prevention =====
        System.out.println("\n----- Week 10: Thread Pools, Producer-Consumer & Deadlock Prevention -----");

        // 1 & 2. TransactionProcessor with ExecutorService (FixedThreadPool of 4)
        System.out.println("-> Part 1 & 2: Batch Transaction Processing via TransactionProcessor (FixedThreadPool-4)");
        TransactionProcessor processor = new TransactionProcessor();
        Account poolAccount = new SavingsAccount(301, "PoolUser", 1000, 500);

        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            if (taskId % 2 != 0) {
                processor.submit(() -> {
                    try {
                        poolAccount.deposit(200);
                        System.out.println("[PoolTask-" + taskId + "] Deposited 200. Balance: " + poolAccount.getBalance());
                    } catch (BankException e) {
                        System.err.println("[PoolTask-" + taskId + "] Error: " + e.getMessage());
                    }
                });
            } else {
                processor.submit(() -> {
                    try {
                        poolAccount.withdraw(100);
                        System.out.println("[PoolTask-" + taskId + "] Withdrew 100. Balance: " + poolAccount.getBalance());
                    } catch (BankException e) {
                        System.err.println("[PoolTask-" + taskId + "] Error: " + e.getMessage());
                    }
                });
            }
        }
        processor.stop();
        System.out.println("All thread pool transactions completed. Final Balance: " + poolAccount.getBalance());

        // 3. Producer-Consumer using wait() & notify() with TransactionBuffer
        System.out.println("\n-> Part 3: Producer-Consumer Coordination with wait() & notify()");
        TransactionBuffer buffer = new TransactionBuffer(3);
        Thread producer = new Thread(() -> {
            Command[] testCmds = {
                new Command(TransactionType.DEPOSIT, "301", 500),
                new Command(TransactionType.WITHDRAW, "301", 200),
                new Command(TransactionType.TRANSFER, "301", 300),
                new Command(TransactionType.DEPOSIT, "301", 1000)
            };
            for (Command cmd : testCmds) {
                try {
                    buffer.produce(cmd);
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Txn-Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 4; i++) {
                try {
                    buffer.consume();
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Txn-Consumer");

        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Producer-Consumer coordination completed successfully.");

        // 4. Deadlock Creation and Resolution
        System.out.println("\n-> Part 4: Deadlock Creation and Lock-Ordering Resolution");
        Account accA = new SavingsAccount(10, "UserA", 5000, 100);
        Account accB = new SavingsAccount(20, "UserB", 5000, 100);

        System.out.println("Step 4a: Demonstrating Deadlock (Opposite Lock Ordering)...");
        Thread deadlockT1 = new Thread(() -> {
            try {
                Account.transferUnsafe(accA, accB, 100);
            } catch (BankException ignored) {}
        }, "Deadlock-Thread-AtoB");

        Thread deadlockT2 = new Thread(() -> {
            try {
                Account.transferUnsafe(accB, accA, 100);
            } catch (BankException ignored) {}
        }, "Deadlock-Thread-BtoA");

        deadlockT1.setDaemon(true);
        deadlockT2.setDaemon(true);
        deadlockT1.start();
        deadlockT2.start();

        try {
            deadlockT1.join(300);
            deadlockT2.join(300);
        } catch (InterruptedException ignored) {}

        if (deadlockT1.isAlive() && deadlockT2.isAlive()) {
            System.out.println(">>> DEADLOCK DETECTED! Both threads are stuck in circular wait waiting for each other's locks.");
        }

        System.out.println("\nStep 4b: Resolving Deadlock (Consistent Lock Ordering: smaller ID first)...");
        Account safeAccA = new SavingsAccount(10, "SafeUserA", 5000, 100);
        Account safeAccB = new SavingsAccount(20, "SafeUserB", 5000, 100);

        Thread safeT1 = new Thread(() -> {
            try {
                Account.transferSafe(safeAccA, safeAccB, 100);
            } catch (BankException e) {
                System.err.println(e.getMessage());
            }
        }, "Safe-Thread-AtoB");

        Thread safeT2 = new Thread(() -> {
            try {
                Account.transferSafe(safeAccB, safeAccA, 100);
            } catch (BankException e) {
                System.err.println(e.getMessage());
            }
        }, "Safe-Thread-BtoA");

        safeT1.start();
        safeT2.start();

        try {
            safeT1.join();
            safeT2.join();
        } catch (InterruptedException ignored) {}

        System.out.println(">>> DEADLOCK PREVENTED! Transfers executed successfully without blocking.");
        System.out.println("Final AccA Balance: " + accA.getBalance() + ", AccB Balance: " + accB.getBalance());

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
