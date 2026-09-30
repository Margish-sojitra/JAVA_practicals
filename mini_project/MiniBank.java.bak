import java.util.Scanner;
import java.util.regex.Pattern;

public class MiniBank {

    record BankInfo(String name, String branch) {
    }

    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    }

    record Command(TransactionType type, String accountNumber, long amount) {
    }

    public static class Validator {

        private static final Pattern MOBILE =
                Pattern.compile("^[6-9][0-9]{9}$");

        private static final Pattern EMAIL =
                Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        private static final Pattern PAN =
                Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

        private static final Pattern IFSC =
                Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

        public static boolean isValidMobile(String mobile) {
            return mobile != null && MOBILE.matcher(mobile).matches();
        }

        public static boolean isValidEmail(String email) {
            return email != null && EMAIL.matcher(email).matches();
        }

        public static boolean isValidPan(String pan) {
            return pan != null && PAN.matcher(pan).matches();
        }

        public static boolean isValidIfsc(String ifsc) {
            return ifsc != null && IFSC.matcher(ifsc).matches();
        }
    }

    public static class CommandParser {

        public static Command parse(String line) {

            String[] parts = line.trim().split("\\s+");

            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Command must contain type, account number and amount.");
            }

            TransactionType type =
                    TransactionType.valueOf(parts[0].toUpperCase());

            String accountNumber = parts[1];

            long amount = Long.parseLong(parts[2]);

            return new Command(type, accountNumber, amount);
        }
    }

    public static class StatementFormatter {

        public static String buildStatement(Account account) {

            StringBuilder statement = new StringBuilder();

            statement.append("========== ACCOUNT STATEMENT ==========\n");
            statement.append("Account Number : ")
                    .append(account.getAccountNumber())
                    .append("\n");

            statement.append("Owner Name     : ")
                    .append(account.getOwnerName())
                    .append("\n");

            statement.append("Balance        : ")
                    .append(account.getBalance())
                    .append("\n");

           

            return statement.toString();
        }
    }

    public static class Customer implements Cloneable {

        private String name;
        private Address address;

        public Customer(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public Address getAddress() {
            return address;
        }

        public static class Address {

            private String line;
            private String city;
            private String pincode;

            public Address(String line, String city, String pincode) {
                this.line = line;
                this.city = city;
                this.pincode = pincode;
            }

            public String getLine() {
                return line;
            }

            public String getCity() {
                return city;
            }

            public String getPincode() {
                return pincode;
            }
        }

        @Override
        public Customer clone() {
            try {
                return (Customer) super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    public static class Account {

        private int accountNumber;
        private String ownerName;
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

        public double getBalance() {
            return balance;
        }

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

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo("MiniBank", "Main Branch");

        
        System.out.println(bank.name());
        System.out.println(bank.branch());
        

        System.out.println("\n----- Validator Testing -----");

        System.out.println("Valid Mobile: "
                + Validator.isValidMobile("9876543210"));

        System.out.println("Invalid Mobile: "
                + Validator.isValidMobile("1234567890"));

        System.out.println("Valid Email: "
                + Validator.isValidEmail("user@gmail.com"));

        System.out.println("Invalid Email: "
                + Validator.isValidEmail("user@gmail"));

        System.out.println("Valid PAN: "
                + Validator.isValidPan("ABCDE1234F"));

        System.out.println("Invalid PAN: "
                + Validator.isValidPan("ABC12345"));

        System.out.println("Valid IFSC: "
                + Validator.isValidIfsc("SBIN0001234"));

        System.out.println("Invalid IFSC: "
                + Validator.isValidIfsc("SB123456"));

        System.out.println("\n----- Command Parser Testing -----");

        String input = "DEPOSIT AC0001 500";

        Command command = CommandParser.parse(input);

        System.out.println("Command Type    : " + command.type());
        System.out.println("Account Number  : " + command.accountNumber());
        System.out.println("Amount          : " + command.amount());

        Account account1 = new Account(
                101,
                "Margish",
                5000
        );

        Account account2 = new Account(
                101,
                "Rahul",
                8000
        );

        Account account3 = new Account(
                102,
                "Amit",
                7000
        );

        System.out.println("\n----- Account Testing -----");

        System.out.println(account1);
        System.out.println(account3);

        System.out.println("\nAccount1 equals Account2: "
                + account1.equals(account2));

        System.out.println("Account1 equals Account3: "
                + account1.equals(account3));

        System.out.println("\n----- Statement -----");

        System.out.println(
                StatementFormatter.buildStatement(account1)
        );

        System.out.println("----- instanceof Testing -----");

        if (account1 instanceof Account) {
            System.out.println("account1 is an Account object.");
        }

        int choice;

        do {

            System.out.println("\n----- MiniBank Menu -----");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

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