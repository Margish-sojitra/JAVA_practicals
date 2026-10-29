package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.TransactionType;
import model.annotation.Audit;
import util.AnnotationValidator;
import util.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BankService (Practical 14 Requirement 1):
 * Wires together Repository<Account>, Validator, and Exception classes.
 */
public class BankService {

    private final Repository<Account> repository;
    private final Path dailyLogFile = Paths.get("mini_project", "logs", "daily_transactions.log");

    public BankService() {
        this(new Repository<>());
    }

    public BankService(Repository<Account> repository) {
        this.repository = repository != null ? repository : new Repository<>();
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    /**
     * 1. Open Account with validation
     */
    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(Account account) throws BankException {
        AuditLogger.log(BankService.class, "openAccount", account);

        if (account == null) {
            throw new BankException("Account cannot be null.");
        }

        String id = String.valueOf(account.getAccountNumber());
        if (repository.findById(id) != null) {
            throw new BankException("Account number " + id + " already exists.");
        }

        // Validate @Positive and @MaxLength annotations
        String[] errors = AnnotationValidator.validate(account);
        if (errors != null && errors.length > 0) {
            throw new BankException("Validation error: " + String.join(", ", errors));
        }

        repository.save(account);
        logTransaction("OPEN " + id + " " + (long) account.getBalance());
        return account;
    }

    /**
     * Helper to open account by prompting for simple parameters
     */
    public Account openAccount(int typeChoice, int accNum, String name, double initialBalance,
                               long extraParam, String mobile, String email) throws BankException {
        // Validate contact details using Validator
        if (mobile != null && !mobile.isBlank() && !Validator.isValidMobile(mobile)) {
            throw new BankException("Invalid Indian mobile number: " + mobile);
        }
        if (email != null && !email.isBlank() && !Validator.isValidEmail(email)) {
            throw new BankException("Invalid email format: " + email);
        }

        Account acc = switch (typeChoice) {
            case 1 -> new SavingsAccount(accNum, name, initialBalance, extraParam);
            case 2 -> new CurrentAccount(accNum, name, initialBalance, extraParam);
            case 3 -> new FixedDepositAccount(accNum, name, initialBalance);
            default -> throw new BankException("Invalid account type selection: " + typeChoice);
        };

        return openAccount(acc);
    }

    /**
     * 2. Deposit money
     */
    @Audit(action = "DEPOSIT")
    public Account deposit(String accountNumber, long amount) throws BankException {
        AuditLogger.log(BankService.class, "deposit", accountNumber, amount);

        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive. Received: " + amount);
        }

        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found.");
        }

        account.deposit(amount);
        logTransaction("DEPOSIT " + accountNumber + " " + amount);
        return account;
    }

    /**
     * 3. Withdraw money
     */
    @Audit(action = "WITHDRAW")
    public Account withdraw(String accountNumber, long amount) throws BankException {
        AuditLogger.log(BankService.class, "withdraw", accountNumber, amount);

        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Received: " + amount);
        }

        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found.");
        }

        account.withdraw(amount);
        logTransaction("WITHDRAW " + accountNumber + " " + amount);
        return account;
    }

    /**
     * 4. Deadlock-safe Transfer between accounts
     */
    @Audit(action = "TRANSFER")
    public void transfer(String fromAcc, String toAcc, long amount) throws BankException {
        AuditLogger.log(BankService.class, "transfer", fromAcc, toAcc, amount);

        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive. Received: " + amount);
        }

        Account from = repository.findById(fromAcc);
        if (from == null) {
            throw new AccountNotFoundException("Source account " + fromAcc + " not found.");
        }

        Account to = repository.findById(toAcc);
        if (to == null) {
            throw new AccountNotFoundException("Destination account " + toAcc + " not found.");
        }

        // Safe transfer ordering locks by smaller account number first
        Account.transferSafe(from, to, amount);
        logTransaction("WITHDRAW " + fromAcc + " " + amount);
        logTransaction("DEPOSIT " + toAcc + " " + amount);
    }

    /**
     * 5. Generate Statement
     */
    @Audit(action = "STATEMENT")
    public String statement(String accountNumber) throws BankException {
        AuditLogger.log(BankService.class, "statement", accountNumber);

        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found.");
        }
        return StatementFormatter.buildStatement(account);
    }

    /**
     * 6. List all registered accounts
     */
    @Audit(action = "LIST_ACCOUNTS")
    public ArrayList<Account> listAccounts() {
        AuditLogger.log(BankService.class, "listAccounts");
        return repository.findAll();
    }

    /**
     * Practical 14 Requirement 3: Process a batch of transactions using TransactionProcessor
     */
    @Audit(action = "PROCESS_BATCH")
    public void processBatch(List<Command> commands) {
        AuditLogger.log(BankService.class, "processBatch", commands.size() + " commands");

        ConcurrentHashMap<String, Account> threadSafeMap = new ConcurrentHashMap<>();
        for (Account acc : repository.findAll()) {
            threadSafeMap.put(String.valueOf(acc.getAccountNumber()), acc);
        }

        TransactionProcessor processor = new TransactionProcessor(threadSafeMap);

        for (Command cmd : commands) {
            if (cmd.type() == TransactionType.DEPOSIT) {
                processor.processDeposit(cmd.accountNumber(), cmd.amount());
                logTransaction("DEPOSIT " + cmd.accountNumber() + " " + cmd.amount());
            } else if (cmd.type() == TransactionType.WITHDRAW) {
                processor.processWithdraw(cmd.accountNumber(), cmd.amount());
                logTransaction("WITHDRAW " + cmd.accountNumber() + " " + cmd.amount());
            }
        }

        processor.stop();
        System.out.println("Batch transaction processing completed successfully.");
    }

    /**
     * Practical 14 Requirement 2 & Practical 11: Load saved accounts from file on startup
     */
    public boolean loadSavedAccounts(Path accountsFile) {
        try {
            if (Files.exists(accountsFile)) {
                Account[] loaded = StatePersister.load(accountsFile);
                if (loaded != null && loaded.length > 0) {
                    repository.saveAll(loaded);
                    System.out.println("Loaded " + loaded.length + " accounts from " + accountsFile.getFileName());
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load existing accounts: " + e.getMessage());
        }
        return false;
    }

    /**
     * Practical 14 Requirement 2 & Practical 11: Save all accounts to file on exit
     */
    public boolean saveAccounts(Path accountsFile) {
        try {
            ArrayList<Account> accounts = repository.findAll();
            StatePersister.save(accounts.toArray(new Account[0]), accountsFile);
            System.out.println("Saved " + accounts.size() + " accounts to " + accountsFile.getFileName());
            return true;
        } catch (Exception e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            return false;
        }
    }

    /**
     * Practical 14 Requirement 3 & Practical 11: Generate report from logs
     */
    public String generateReport(Path logsFolder, Path reportFile) throws IOException {
        return ReportGenerator.generateReport(logsFolder, reportFile);
    }

    private void logTransaction(String entry) {
        try {
            TransactionLog.append(dailyLogFile, entry);
        } catch (IOException ignored) {}
    }
}
