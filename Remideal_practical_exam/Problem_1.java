import java.util.Scanner;

class Product {
    private final String name;
    private final int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public static Product findProduct(char code) {
        return switch (code) {
            case 'N' -> new Product("Notebook", 45);
            case 'P' -> new Product("Pen", 10);
            case 'M' -> new Product("Marker", 25);
            case 'F' -> new Product("File", 30);
            default -> null;
        };
    }
}

class Purchase {
    private int receiptNo;
    private Product product;
    private int quantity;

    private static int nextReceipt = 201;
    private static int totalPurchases = 0;
    private static int totalRevenue = 0;

    public Purchase(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.receiptNo = nextReceipt++;

        totalPurchases++;
        totalRevenue += getAmount();
    }

    public int getAmount() {
        return product.getPrice() * quantity;
    }

    public static int getTotalPurchases() {
        return totalPurchases;
    }

    public static int getTotalRevenue() {
        return totalRevenue;
    }

    @Override
    public String toString() {
        return "Receipt " + receiptNo + ": "
                + product.getName() + " x " + quantity
                + " = " + getAmount();
    }
}

public class Problem_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            char code = sc.next().charAt(0);
            int quantity = sc.nextInt();

            Product product = Product.findProduct(code);

            if (product == null || quantity <= 0) {
                System.out.println(
                    "Invalid item: " + code + " " + quantity
                );
            } else {
                Purchase purchase = new Purchase(product, quantity);
                System.out.println(purchase);
            }
        }

        System.out.println(
            "Total receipts: " + Purchase.getTotalPurchases()
            + ", Total revenue: " + Purchase.getTotalRevenue()
        );

        sc.close();
    }
}