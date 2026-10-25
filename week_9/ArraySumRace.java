public class ArraySumRace {

    static class SharedTotal {
        long total = 0;

        public void addUnsync(long value) {
            total += value;
        }

        public synchronized void addSync(long value) {
            total += value;
        }

        public long getTotal() {
            return total;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- 3. Array Sum Race ---");
        
        int arraySize = 10_000_000;
        int[] numbers = new int[arraySize];
        long expectedTotal = 0;
        
        for (int i = 0; i < arraySize; i++) {
            numbers[i] = 1;
            expectedTotal += numbers[i];
        }

        System.out.println("Expected Total: " + expectedTotal);

        SharedTotal unsyncTotal = new SharedTotal();
        runSumThreads(numbers, unsyncTotal, false);
        System.out.println("Actual Total (Unsynchronized): " + unsyncTotal.getTotal());
        if (unsyncTotal.getTotal() != expectedTotal) {
            System.out.println("As expected, the unsynchronized sum is WRONG.");
        } else {
            System.out.println("Surprisingly, the unsynchronized sum is correct on this run (run it again!).");
        }

        System.out.println();

        SharedTotal syncTotal = new SharedTotal();
        runSumThreads(numbers, syncTotal, true);
        System.out.println("Actual Total (Synchronized): " + syncTotal.getTotal());
        if (syncTotal.getTotal() == expectedTotal) {
            System.out.println("As expected, the synchronized sum is EXACTLY RIGHT.");
        }
    }

    private static void runSumThreads(int[] numbers, SharedTotal sharedTotal, boolean useSync) throws InterruptedException {
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        int chunkSize = numbers.length / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numThreads - 1) ? numbers.length : (i + 1) * chunkSize;

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    if (useSync) {
                        sharedTotal.addSync(numbers[j]);
                    } else {
                        sharedTotal.addUnsync(numbers[j]);
                    }
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
}
