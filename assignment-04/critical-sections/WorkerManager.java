public class WorkerManager {
    public static void main(String[] args) {
        String amqpUrl = "amqps://metfivxb:pJd2_Nqv3HwjlyMQq1s9Q9mxaWvnSWHY@cow.rmq2.cloudamqp.com/metfivxb";
        DistributedLockManager lockManager = new DistributedLockManager(amqpUrl);

        String queueName = "distributed_mutex_queue";
        int numWorkers = 5;

        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i;
            DistributedLock workerLock = lockManager.getNewLock(queueName);

            new Thread(() -> {
                runWorker(workerId, workerLock);
            }).start();
        }
    }

    private static void runWorker(int id, DistributedLock lock) {
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