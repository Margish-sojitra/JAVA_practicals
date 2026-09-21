package discount;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Practical6_3 {

    @FunctionalInterface
    interface DiscountRule {
        double apply(double price);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        List<Double> prices = Arrays.asList(
            1000.0,
            2500.0,
            5000.0,
            8000.0
        );

        System.out.println("Prices:");

        for (double price : prices) {
            System.out.println(price);
        }

        System.out.println("\nChoose discount rule:");
        System.out.println("1. 10% discount");
        System.out.println("2. 20% discount");
        System.out.println("3. Flat Rs. 500 discount");

        int choice = sc.nextInt();

        DiscountRule rule;

        if (choice == 1) {
            rule = price -> price * 0.90;
        } else if (choice == 2) {
            rule = price -> price * 0.80;
        } else if (choice == 3) {
            rule = price -> price - 500;
        } else {
            System.out.println("Invalid choice.");
            sc.close();
            return;
        }

        System.out.println("\nPrices after discount:");

        for (double price : prices) {
            double finalPrice = rule.apply(price);

            if (finalPrice < 0) {
                finalPrice = 0;
            }

            System.out.println(
                "Original: Rs." + price +
                " -> Final: Rs." + finalPrice
            );
        }

        sc.close();
    }
}
