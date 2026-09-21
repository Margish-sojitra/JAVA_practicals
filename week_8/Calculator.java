import java.util.Scanner;

class DivideByZeroException extends Exception {
    public DivideByZeroException(String message) {
        super(message);
    }
}

class InvalidNumberException extends Exception {
    public InvalidNumberException(String message) {
        super(message);
    }
}

public class Calculator {

    public static double divide(double a, double b) throws DivideByZeroException {
        if (b == 0) {
            throw new DivideByZeroException("Error: Division by zero is not allowed.");
        }
        return a / b;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number: ");
        double a = scanner.nextDouble();

        System.out.print("Enter second number: ");
        double b = scanner.nextDouble();

        scanner.nextLine();

        System.out.print("Enter operation (+, -, *, /): ");
        String operator = scanner.nextLine();

        try {
            switch (operator) {

                case "+":
                    System.out.println("Result: " + (a + b));
                    break;

                case "-":
                    System.out.println("Result: " + (a - b));
                    break;

                case "*":
                    System.out.println("Result: " + (a * b));
                    break;

                case "/":
                    double result = divide(a, b);
                    System.out.println("Result: " + result);
                    break;

                default:
                    throw new InvalidNumberException(
                        "Error: Invalid operation. Please use +, -, *, or /."
                    );
            }

        } catch (DivideByZeroException e) {
            System.out.println(e.getMessage());

        } catch (InvalidNumberException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Code Completed.");
            scanner.close();
        }
    }
}