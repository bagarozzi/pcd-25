public class WorkerManager {
    public static void main(String[] args) {
        int numWorkers = 5;

        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i;
            new Thread(() -> {
                runWorker(workerId);
            }).start();
        }
    }

    private static void runWorker(int id) {
        String queueName = "distributed_mutex_queue";
        DistributedLock lock = new DistributedLock(queueName);

        try {
            lock.init();
            System.out.println("Worker-" + id + " pronto. Attende il lock...");

            lock.acquire();
            try {
                System.out.println("Worker-" + id + " HA ACQUISITO IL LOCK e lavora nella sezione critica.");
                Thread.sleep(1000);
            } finally {
                lock.release();
                lock.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}