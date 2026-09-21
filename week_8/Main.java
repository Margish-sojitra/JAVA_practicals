class OutOfStockException extends Exception {
    private int shortfall;

    public OutOfStockException(String item, int requested, int available) {
        super("Out of stock: " + item);
        shortfall = requested - available;
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
    private String[] items;
    private int[] stock;
    private int size;

    public Warehouse(int capacity) {
        items = new String[capacity];
        stock = new int[capacity];
        size = 0;
    }

    public void addItem(String item, int qty) {
        items[size] = item;
        stock[size] = qty;
        size++;
    }

    public void issue(String item, int qty)
            throws OutOfStockException, InvalidQuantityException {

        if (qty <= 0) {
            throw new InvalidQuantityException(
                "Quantity must be greater than 0"
            );
        }

        int index = -1;

        for (int i = 0; i < size; i++) {
            if (items[i].equalsIgnoreCase(item)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new OutOfStockException(item, qty, 0);
        }

        if (stock[index] < qty) {
            throw new OutOfStockException(item, qty, stock[index]);
        }

        stock[index] -= qty;
    }

    public void displayStock() {
        System.out.println("\nCurrent Stock:");
        for (int i = 0; i < size; i++) {
            System.out.println(items[i] + " : " + stock[i]);
        }
    }
}

class Request {
    String item;
    int qty;

    public Request(String item, int qty) {
        this.item = item;
        this.qty = qty;
    }
}

public class Main {
    public static void main(String[] args) {

        Warehouse warehouse = new Warehouse(10);

        warehouse.addItem("Pen", 50);
        warehouse.addItem("Notebook", 30);
        warehouse.addItem("Pencil", 20);
        warehouse.addItem("Eraser", 10);

        Request[] requests = {
            new Request("Pen", 10),
            new Request("Notebook", 40),
            new Request("Pencil", 0),
            new Request("Marker", 5),
            new Request("Eraser", 5),
            new Request("Pen", -3),
            new Request("Pencil", 15)
        };

        System.out.println("Processing Requests:");

        for (int i = 0; i < requests.length; i++) {
            Request r = requests[i];

            try {
                warehouse.issue(r.item, r.qty);
                System.out.println(
                    "Request " + (i + 1) + ": Issued " +
                    r.qty + " " + r.item
                );

            } catch (OutOfStockException e) {
                System.out.println(
                    "Request " + (i + 1) + " Failed: " +
                    e.getMessage()
                );
                System.out.println(
                    "Shortfall: " + e.getShortfall()
                );

            } catch (InvalidQuantityException e) {
                System.out.println(
                    "Request " + (i + 1) + " Failed: " +
                    e.getMessage()
                );
            }
        }

        warehouse.displayStock();
    }
}