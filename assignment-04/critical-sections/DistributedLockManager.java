public class DistributedLockManager {
    private final String amqpUrl;

    public DistributedLockManager(String amqpUrl) {
        this.amqpUrl = amqpUrl;
    }

    public DistributedLock getNewLock(String queueName) {
        return new DistributedLock(queueName, this.amqpUrl);
    }
}