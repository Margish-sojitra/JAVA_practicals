package service;

import model.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Week 12 MiniBank - AccountStore (Milestone 2 Checkpoint)
 * Replaces array-based storage with Collections Framework:
 * - HashMap<String, Account> keyed by account number for O(1) lookups
 * - ArrayList<String> for appendable transaction audit history
 * - sortedByBalance() returning an ArrayList<Account> sorted with a Comparator
 */
public class AccountStore {

    // 1. HashMap keeping accounts keyed by account number
    private final HashMap<String, Account> accounts = new HashMap<>();

    // 2. ArrayList keeping transaction history
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    /**
     * Adds an account to the store using its account number as the key.
     *
     * @param account the account to add
     */
    public void add(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        String key = String.valueOf(account.getAccountNumber());
        accounts.put(key, account);
        recordTransaction("Added Account: #" + key + " (" + account.getOwnerName() + ")");
    }

    /**
     * Retrieves an account by its account number string.
     *
     * @param accountNumber account number key
     * @return the matching Account, or null if not found
     */
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Convenience overload to retrieve an account by integer account number.
     *
     * @param accountNumber numeric account number
     * @return the matching Account, or null if not found
     */
    public Account get(int accountNumber) {
        return accounts.get(String.valueOf(accountNumber));
    }

    /**
     * Returns all accounts stored in the store.
     *
     * @return an ArrayList containing all accounts
     */
    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * Records a transaction entry in the transaction history.
     *
     * @param entry description of the transaction
     */
    public void recordTransaction(String entry) {
        transactionHistory.add(entry);
    }

    /**
     * Alias for recordTransaction to add a transaction entry.
     *
     * @param entry transaction description
     */
    public void addTransaction(String entry) {
        recordTransaction(entry);
    }

    /**
     * Returns a copy of the transaction history list.
     *
     * @return ArrayList of recorded transaction strings
     */
    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    /**
     * Returns an ArrayList of accounts sorted by balance using a Comparator.
     * Sorts descending (highest balance first), breaking ties by natural order (account number).
     *
     * @return sorted list of accounts
     */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sortedList = new ArrayList<>(accounts.values());
        Comparator<Account> balanceComparator = Comparator
                .comparingDouble(Account::getBalance)
                .reversed()
                .thenComparing(Comparator.naturalOrder());
        sortedList.sort(balanceComparator);
        return sortedList;
    }

    /**
     * Returns total number of accounts in the store.
     *
     * @return account count
     */
    public int size() {
        return accounts.size();
    }
}
