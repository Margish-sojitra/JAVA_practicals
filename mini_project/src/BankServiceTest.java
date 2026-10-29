import exception.*;
import model.*;
import model.annotation.*;
import service.*;
import util.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Unit Test Suite for MiniBank (Practical 14 Requirement 4).
 * Completely self-contained: runs in any IDE without external JARs or dependencies.
 */
public class BankServiceTest {

    private Repository<Account> repository;
    private BankService bankService;

    public void setUp() {
        repository = new Repository<>();
        bankService = new BankService(repository);
    }

    /**
     * Requirement 14.4 Test 1: A deposit increases the balance.
     */
    @Test
    public void testDepositIncreasesBalance() throws BankException {
        SavingsAccount account = new SavingsAccount(1001, "AliceTest", 1000.0, 500);
        bankService.openAccount(account);

        Account updated = bankService.deposit("1001", 750);

        assertEquals(1750.0, updated.getBalance(), "Balance must increase by 750");
        assertEquals(1750.0, repository.findById("1001").getBalance(), "Repository must reflect new balance");
    }

    /**
     * Requirement 14.4 Test 2: A withdrawal over the limit throws an exception.
     */
    @Test
    public void testWithdrawalOverLimitThrowsException() throws BankException {
        // Balance 1000 with minBalance 500 -> withdrawing 700 exceeds allowed limit
        SavingsAccount savings = new SavingsAccount(1002, "BobTest", 1000.0, 500);
        bankService.openAccount(savings);

        boolean threwException = false;
        try {
            bankService.withdraw("1002", 700);
        } catch (InsufficientFundsException e) {
            threwException = true;
            assertTrue(e.getShortfall() > 0, "Shortfall must be greater than zero");
        }

        assertTrue(threwException, "Withdrawing over limit must throw InsufficientFundsException");
        assertEquals(1000.0, savings.getBalance(), "Failed withdrawal must not change balance");
    }

    /**
     * Requirement 14.4 Test 3: Many concurrent deposits keep the balance correct (Thread Safety).
     */
    @Test
    public void testConcurrentDepositsKeepBalanceCorrect() throws Exception {
        double initialBalance = 1000.0;
        SavingsAccount shared = new SavingsAccount(1003, "ConcurrentHolder", initialBalance, 0);
        bankService.openAccount(shared);

        int numThreads = 10;
        int depositsPerThread = 50;
        long amountPerDeposit = 20;
        double expectedFinal = initialBalance + ((double) numThreads * depositsPerThread * amountPerDeposit);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < depositsPerThread; j++) {
                        bankService.deposit("1003", amountPerDeposit);
                    }
                } catch (Exception e) {
                    fail("Unexpected error during concurrent deposit: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean finished = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(finished, "All deposit threads must finish within timeout");
        assertEquals(expectedFinal, shared.getBalance(), "Final balance must exactly match expected total");
    }

    /**
     * Requirement 14.4 Test 4: Safe transfer between accounts.
     */
    @Test
    public void testTransferBetweenAccounts() throws BankException {
        SavingsAccount from = new SavingsAccount(2001, "Sender", 3000.0, 500);
        CurrentAccount to = new CurrentAccount(2002, "Receiver", 1000.0, 2000);
        bankService.openAccount(from);
        bankService.openAccount(to);

        bankService.transfer("2001", "2002", 1200);

        assertEquals(1800.0, from.getBalance(), "Sender balance must decrease by 1200");
        assertEquals(2200.0, to.getBalance(), "Receiver balance must increase by 1200");
    }

    /**
     * Requirement 13: Generic Repository extracts @Id via reflection.
     */
    @Test
    public void testGenericRepositoryReflection() {
        Repository<Account> customRepo = new Repository<>();
        SavingsAccount acc = new SavingsAccount(3001, "ReflectUser", 5500.0, 1000);

        // Reflection finds @Id (accountNumber) and uses "3001" as key
        customRepo.save(acc);

        Account retrieved = customRepo.findById("3001");
        assertTrue(retrieved != null, "Retrieved account must not be null");
        assertEquals(5500.0, retrieved.getBalance(), "Retrieved account balance must match");

        ArrayList<Account> all = customRepo.findAll();
        assertEquals(1, all.size(), "Repository should contain 1 account");
    }

    /**
     * Requirement 13: Bounded generic method average(N[] values).
     */
    @Test
    public void testGenericBoundedAverage() {
        Double[] doubleValues = new Double[]{100.0, 200.0, 300.0, 400.0};
        assertEquals(250.0, Repository.average(doubleValues), "Average must be 250.0");
    }

    // --- Simple Assertion Helpers (Zero Dependencies) ---
    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.001) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * Standalone Test Runner that inspects and runs all @Test methods reflectively.
     */
    public static void runAllTests() {
        System.out.println("==========================================================");
        System.out.println("            MINIBANK AUTOMATED TESTS (@Test)              ");
        System.out.println("==========================================================");

        BankServiceTest suite = new BankServiceTest();
        Method[] methods = BankServiceTest.class.getDeclaredMethods();
        int passed = 0;
        int failed = 0;

        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                try {
                    suite.setUp();
                    m.invoke(suite);
                    System.out.println("  [PASS] " + m.getName());
                    passed++;
                } catch (Throwable t) {
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    System.err.println("  [FAIL] " + m.getName() + " -> " + cause.getMessage());
                    failed++;
                }
            }
        }

        System.out.println("----------------------------------------------------------");
        System.out.println("  Total Tests: " + (passed + failed) + " | Passed: " + passed + " | Failed: " + failed);
        System.out.println("==========================================================");
    }

    public static void main(String[] args) {
        runAllTests();
    }
}

