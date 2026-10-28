package service;

import exception.BankException;
import model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Week 10 & 12 MiniBank - TransactionProcessor
 * Manages thread pool execution with ExecutorService and thread-safe shared accounts
 * using ConcurrentHashMap<String, Account>.
 */
public class TransactionProcessor {
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> sharedAccounts;

    public TransactionProcessor() {
        this(new ConcurrentHashMap<>());
    }

    public TransactionProcessor(ConcurrentHashMap<String, Account> sharedAccounts) {
        this.executor = Executors.newFixedThreadPool(4);
        this.sharedAccounts = sharedAccounts != null ? sharedAccounts : new ConcurrentHashMap<>();
    }

    /**
     * Submits a custom Runnable task to the thread pool.
     */
    public void submit(Runnable task) {
        executor.execute(task);
    }

    /**
     * Registers an account into the thread-safe ConcurrentHashMap.
     */
    public void addAccount(Account account) {
        if (account != null) {
            sharedAccounts.put(String.valueOf(account.getAccountNumber()), account);
        }
    }

    /**
     * Retrieves an account from the thread-safe ConcurrentHashMap.
     */
    public Account getAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    /**
     * Returns the underlying thread-safe ConcurrentHashMap.
     */
    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }

    /**
     * Submits a thread-safe deposit operation using ConcurrentHashMap.
     */
    public void processDeposit(String accountNumber, long amount) {
        submit(() -> {
            Account account = sharedAccounts.get(accountNumber);
            if (account != null) {
                try {
                    account.deposit(amount);
                    System.out.printf("[%s] Deposited $%d to Acc #%s. New Balance: $%.2f%n",
                            Thread.currentThread().getName(), amount, accountNumber, account.getBalance());
                } catch (BankException e) {
                    System.err.printf("[%s] Deposit error on Acc #%s: %s%n",
                            Thread.currentThread().getName(), accountNumber, e.getMessage());
                }
            } else {
                System.err.printf("[%s] Account #%s not found in ConcurrentHashMap%n",
                        Thread.currentThread().getName(), accountNumber);
            }
        });
    }

    /**
     * Submits a thread-safe withdrawal operation using ConcurrentHashMap.
     */
    public void processWithdraw(String accountNumber, long amount) {
        submit(() -> {
            Account account = sharedAccounts.get(accountNumber);
            if (account != null) {
                try {
                    account.withdraw(amount);
                    System.out.printf("[%s] Withdrew $%d from Acc #%s. New Balance: $%.2f%n",
                            Thread.currentThread().getName(), amount, accountNumber, account.getBalance());
                } catch (BankException e) {
                    System.err.printf("[%s] Withdrawal error on Acc #%s: %s%n",
                            Thread.currentThread().getName(), accountNumber, e.getMessage());
                }
            } else {
                System.err.printf("[%s] Account #%s not found in ConcurrentHashMap%n",
                        Thread.currentThread().getName(), accountNumber);
            }
        });
    }

    /**
     * Gracefully terminates the executor service.
     */
    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
