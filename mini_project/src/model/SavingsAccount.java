package model;

public class SavingsAccount extends Account {
    private long minBalance;

    public SavingsAccount(int accountNumber, String ownerName, double balance, long minBalance) {
        super(accountNumber, ownerName, balance);
        this.minBalance = minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }
}
