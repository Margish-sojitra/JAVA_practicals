package model;

public class CurrentAccount extends Account {
    private long overdraftLimit;

    public CurrentAccount(int accountNumber, String ownerName, double balance, long overdraftLimit) {
        super(accountNumber, ownerName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }
}
