public class LockSetup {
    public static void main(String[] args) {
        DistributedLock lock = new DistributedLock("distributed_mutex_queue");

        try {
            lock.init();
            lock.release();
            System.out.println("Token iniziale inserito correttamente!");
            //senza questo va in deadlock perchè non trova il primo messaggio nella coda
            lock.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}