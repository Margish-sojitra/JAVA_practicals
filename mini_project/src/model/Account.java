package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing {

    @Id
    private int accountNumber;
    private String ownerName;
    @Positive
    private double balance;

    public Account(int accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero. Received: " + amount);
        }
        this.balance += amount;
    }

    @Override
    public boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero. Received: " + amount);
        }
        if (!canWithdraw(amount)) {
            long available = getAvailableBalance();
            long shortfall = amount > available ? (amount - available) : 1;
            throw new InsufficientFundsException(
                "Insufficient funds in Account " + accountNumber + " (" + ownerName + "). Shortfall: " + shortfall,
                shortfall
            );
        }
        this.balance -= amount;
        return true;
    }

    public long getAvailableBalance() {
        return (long) Math.max(0, balance);
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Target account cannot be null.");
        }
        boolean withdrawn = false;
        try {
            this.withdraw(amount);
            withdrawn = true;
            to.deposit(amount);
        } catch (BankException e) {
            if (withdrawn) {
                try {
                    this.deposit(amount);
                } catch (InvalidAmountException ignored) {}
            }
            throw e;
        } finally {
            System.out.println("[Audit] Transfer attempt from Account " + this.accountNumber +
                               " to Account " + to.getAccountNumber() + " completed.");
        }
    }

    public abstract boolean canWithdraw(long amount);

    @Override
    public abstract double interestRate();

    @Override
    public String toString() {
        return "Account Number: " + accountNumber +
                ", Owner Name: " + ownerName +
                ", Balance: " + balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Account))
            return false;
        Account a = (Account) o;
        return accountNumber == a.accountNumber;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(accountNumber);
    }
}
