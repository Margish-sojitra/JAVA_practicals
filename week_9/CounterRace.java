public class CounterRace {

    static class Counter {
        int count = 0;

        public void incrementUnsync() {
            count++;
        }

        public synchronized void incrementSync() {
            count++;
        }
        
        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- 1. Counter Race ---");
        
        Counter unsyncCounter = new Counter();
        runThreads(unsyncCounter, false);
        System.out.println("Expected count: 20000");
        System.out.println("Actual count (Unsynchronized): " + unsyncCounter.getCount());
        if (unsyncCounter.getCount() < 20000) {
            System.out.println("As expected, the unsynchronized count is too low due to race conditions.");
        }
        
        System.out.println();

        Counter syncCounter = new Counter();
        runThreads(syncCounter, true);
        System.out.println("Expected count: 20000");
        System.out.println("Actual count (Synchronized): " + syncCounter.getCount());
        if (syncCounter.getCount() == 20000) {
            System.out.println("As expected, the synchronized count is exactly right.");
        }
    }

    private static void runThreads(Counter counter, boolean useSync) throws InterruptedException {
        int numThreads = 20;
        int incrementsPerThread = 1000;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    if (useSync) {
                        counter.incrementSync();
                    } else {
                        counter.incrementUnsync();
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
