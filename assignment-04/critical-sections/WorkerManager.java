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
            System.out.println("Worker-" + id + " pronto.");

            for (int j = 0; j < 3; j++) {
                // Lavoro fuori sezione critica
                Thread.sleep(1000);

                lock.acquire();

                System.out.println("Worker-" + id + " sta lavorando nella sezione critica.");
                Thread.sleep(1000); // Lavoro nella sezione critica

                lock.release();
            }
            lock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}