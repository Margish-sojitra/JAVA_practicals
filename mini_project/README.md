# MiniBank — Object-Oriented Banking System & Capstone

A complete, clean, production-grade Java application demonstrating modern Object-Oriented Programming, generic data access with reflection, multithreading, concurrency control, deadlock prevention, file persistence, reflective auditing, and automated unit testing.

---

## Architecture Overview

```
 [ Layer 1: Front Desk (UI) ]    --->  MiniBank.java (Interactive Console Menu)
               │
               ▼
 [ Layer 2: Manager (Logic) ]    --->  BankService.java (Validates rules & coordinates)
               │
               ▼
 [ Layer 3: The Vault (Storage) ]--->  Repository<T> (Generic storage with @Id reflection)
```

---

## Key Features by Practical

### Practical 13: Generic Data Access Layer (`Repository<T>`)
- **Generic Store**: [`Repository<T>`](file:///d:/JAVA_OOP/JAVA_practicals/mini_project/src/service/Repository.java) holds an internal `HashMap<String, T>` with CRUD operations: `save`, `findById`, `findAll`, `delete`.
- **Reflection on `@Id`**: Inside `save()`, the entity's class hierarchy is scanned via reflection to find the field annotated with [`@Id`](file:///d:/JAVA_OOP/JAVA_practicals/mini_project/src/model/annotation/Id.java), reading its value as the primary map key.
- **Safe Varargs**: `@SafeVarargs public final ArrayList<T> saveAll(T... items)`.
- **Bounded Generic Method**: `public static <N extends Number> double average(N[] values)`.

### Practical 14: BankService Capstone & Assembly
- **Business Layer**: [`BankService`](file:///d:/JAVA_OOP/JAVA_practicals/mini_project/src/service/BankService.java) coordinates `Repository<Account>`, `Validator`, and domain exceptions:
  - `openAccount`, `deposit`, `withdraw`, `transfer`, `statement`, `listAccounts`.
- **Reflective Auditing**: [`@Audit`](file:///d:/JAVA_OOP/JAVA_practicals/mini_project/src/model/annotation/Audit.java) annotation and [`AuditLogger`](file:///d:/JAVA_OOP/JAVA_practicals/mini_project/src/service/AuditLogger.java) that inspects methods at runtime to record timestamped activity to console and `logs/audit.log`.
- **Connected Practical 1 Menu**: Interactive console with 11 options, loading accounts from `data/accounts.ser` on startup and saving on exit.
- **Automated Tests**: [`BankServiceTest`](file:///d:/JAVA_OOP/JAVA_practicals/mini_project/src/BankServiceTest.java) verifying deposits, withdrawal limits, concurrent deposit thread-safety, safe transfers, and reflection.

---

## Project Structure

```
mini_project/
├── data/
│   └── accounts.ser                       # Serialized accounts binary store
├── logs/
│   ├── audit.log                          # Reflective audit logs
│   └── daily_transactions.log             # Transaction execution log
├── reports/
│   └── daily_summary_report.txt           # Generated summary report
├── out/                                   # Compiled bytecode (.class)
├── src/
│   ├── exception/                         # Custom banking exceptions
│   │   ├── AccountNotFoundException.java
│   │   ├── BankException.java
│   │   ├── InsufficientFundsException.java
│   │   └── InvalidAmountException.java
│   ├── model/                             # Domain models & entities
│   │   ├── annotation/
│   │   │   ├── Audit.java                 # @Audit annotation
│   │   │   ├── Id.java                    # @Id annotation
│   │   │   ├── MaxLength.java
│   │   │   ├── Positive.java
│   │   │   └── Test.java                  # @Test annotation
│   │   ├── Account.java                   # Abstract base account
│   │   ├── CurrentAccount.java
│   │   ├── Customer.java
│   │   ├── FixedDepositAccount.java
│   │   └── SavingsAccount.java
│   ├── service/                           # Business logic & repository
│   │   ├── AuditLogger.java               # Reflective audit logger
│   │   ├── BankService.java               # Core banking service
│   │   ├── ReportGenerator.java           # Daily report generator
│   │   ├── Repository.java                # Generic repository with reflection
│   │   ├── StatePersister.java            # Object serialization
│   │   ├── TransactionLog.java            # File log appender
│   │   └── TransactionProcessor.java      # Thread pool batch processor
│   ├── util/
│   │   ├── AnnotationValidator.java       # Reflective validator
│   │   └── Validator.java                 # Regex input validator
│   ├── BankServiceTest.java               # Automated @Test suite (Zero dependencies)
│   └── MiniBank.java                      # Main runnable entry point & menu
├── manifest.txt                           # JAR manifest
├── MiniBank.jar                           # Final executable runnable JAR
└── README.md                              # Technical documentation
```

---

## How to Build and Run

### 1. Compile the Project
```powershell
javac -d mini_project/out (Get-ChildItem -Path mini_project/src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

### 2. Run the Automated Tests (@Test)
```powershell
java -cp mini_project/out BankServiceTest
```

### 3. Build the Runnable Executable JAR
```powershell
cd mini_project
jar cvfm MiniBank.jar manifest.txt -C out .
cd ..
```

### 4. Run the MiniBank Application
```powershell
java -jar mini_project/MiniBank.jar
```
