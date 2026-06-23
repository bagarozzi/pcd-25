public class WorkerManager {
    public static void main(String[] args) {
        int numWorkers = 5;



        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i;
            new Thread(() -> {
                runWorker(workerId);
            }).start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static void runWorker(int id) {
        String queueName = "distributed_mutex_queue";
        DistributedLock lock = new DistributedLock(queueName);

        try {
            try{
                lock.init();
                //System.out.println("Worker-" + id + " pronto.");
                // Lavoro fuori sezione critica
                lock.acquire();

                System.out.println("Worker-" + id + " sta lavorando nella sezione critica.");
                Thread.sleep(1000); // Lavoro nella sezione critica
            } finally {
                lock.release();
                lock.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private static void runFailedWorker(int id) {
        String queueName = "distributed_mutex_queue";
        DistributedLock lock = new DistributedLock(queueName);

        try {
            try {
                lock.init();
                System.out.println("Worker-" + id + " pronto.");
                // Lavoro fuori sezione critica
                Thread.sleep(1000);

                lock.acquire();

                System.out.println("Worker-" + id + " esce senza rilasciare la lock");
                Thread.sleep(1000);
                return;
            }
            finally {
                System.out.println("Worker" + id + " rilascia la lock anche se è fallito");
                lock.release();
                lock.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}