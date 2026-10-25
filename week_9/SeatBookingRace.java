public class SeatBookingRace {

    static class BookingSystem {
        int seatsLeft = 5;
        int successfulBookings = 0;

        public boolean bookSeatUnsync(String customerName) {
            if (seatsLeft > 0) {
                System.out.println(customerName + " sees " + seatsLeft + " seats available. Booking...");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                seatsLeft--;
                successfulBookings++;
                System.out.println(customerName + " successfully booked a seat. Seats left: " + seatsLeft);
                return true;
            } else {
                System.out.println(customerName + " failed to book. No seats left.");
                return false;
            }
        }

        public synchronized boolean bookSeatSync(String customerName) {
            if (seatsLeft > 0) {
                System.out.println(customerName + " sees " + seatsLeft + " seats available. Booking...");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                seatsLeft--;
                successfulBookings++;
                System.out.println(customerName + " successfully booked a seat. Seats left: " + seatsLeft);
                return true;
            } else {
                System.out.println(customerName + " failed to book. No seats left.");
                return false;
            }
        }
        
        public int getSeatsLeft() {
            return seatsLeft;
        }

        public int getSuccessfulBookings() {
            return successfulBookings;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- 2. Seat Booking Race ---");
        
        System.out.println("\n--- Unsynchronized Booking ---");
        BookingSystem unsyncSystem = new BookingSystem();
        runBookingThreads(unsyncSystem, false);
        System.out.println("\nTotal successful bookings (Unsynchronized): " + unsyncSystem.getSuccessfulBookings());
        if (unsyncSystem.getSuccessfulBookings() > 5) {
            System.out.println("As expected, more than 5 seats were sold (oversell) due to race conditions!");
        }

        System.out.println("\n============================================\n");

        System.out.println("--- Synchronized Booking ---");
        BookingSystem syncSystem = new BookingSystem();
        runBookingThreads(syncSystem, true);
        System.out.println("\nTotal successful bookings (Synchronized): " + syncSystem.getSuccessfulBookings());
        if (syncSystem.getSuccessfulBookings() == 5) {
            System.out.println("As expected, exactly 5 seats were sold when synchronized.");
        }
    }

    private static void runBookingThreads(BookingSystem system, boolean useSync) throws InterruptedException {
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final String customerName = "Customer-" + (i + 1);
            threads[i] = new Thread(() -> {
                if (useSync) {
                    system.bookSeatSync(customerName);
                } else {
                    system.bookSeatUnsync(customerName);
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
}
