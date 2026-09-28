
import java.util.*;

class OutOfStockException extends Exception {
    private int shortfall;

    public OutOfStockException(int shortfall) {
        super("Insufficient stock. Shortfall: " + shortfall);
        this.shortfall = shortfall;
    }

    public int getShortfall() {
        return shortfall;
    }
}

class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String message) {
        super(message);
    }
}

class Warehouse {
    private Map<String, Integer> stock = new HashMap<>();

    public void addItem(String item, int qty) {
        stock.put(item, qty);
    }

    public void issue(String item, int qty)
            throws OutOfStockException, InvalidQuantityException {

        if (qty <= 0) {
            throw new InvalidQuantityException(
                    "Quantity must be greater than 0."
            );
        }

        int available = stock.getOrDefault(item, 0);

        if (qty > available) {
            throw new OutOfStockException(qty - available);
        }

        stock.put(item, available - qty);
        System.out.println("Issued " + qty + " units of " + item);
    }
}

public class StockIssue {

    public static void main(String[] args) {

        Warehouse warehouse = new Warehouse();

        warehouse.addItem("Laptop", 10);
        warehouse.addItem("Mouse", 20);
        warehouse.addItem("Keyboard", 5);

        String[][] requests = {
            {"Laptop", "4"},
            {"Mouse", "25"},
            {"Keyboard", "0"},
            {"Keyboard", "3"},
            {"Monitor", "2"}
        };

        for (String[] request : requests) {

            String item = request[0];
            int qty = Integer.parseInt(request[1]);

            try {
                warehouse.issue(item, qty);

            } catch (OutOfStockException e) {
                System.out.println(
                    "Failed: " + item +
                    " - " + e.getMessage()
                );

            } catch (InvalidQuantityException e) {
                System.out.println(
                    "Failed: " + item +
                    " - " + e.getMessage()
                );
            }
        }

        System.out.println("All requests processed.");
    }
}

