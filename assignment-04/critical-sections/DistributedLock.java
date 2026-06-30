import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class DistributedLock {
    private final String queueName;
    private final String amqpUrl;
    private Connection connection;
    private Channel channel;

    private String consumerTag;
    private long currentDeliveryTag = -1;

    public DistributedLock(String queueName, String amqpUrl) {
        this.queueName = queueName;
        this.amqpUrl = amqpUrl;
    }

    public void init() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(this.amqpUrl);

        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();

        boolean queueExists = true;
        try {
            Channel testChannel = connection.createChannel();
            testChannel.queueDeclarePassive(queueName);
            testChannel.close();
        } catch (IOException e) {
            queueExists = false;
        }

        this.channel.queueDeclare(queueName, true, false, false, null);

        if (!queueExists) {
            try {
                Channel initChannel = connection.createChannel();
                initChannel.queueDeclare(queueName + "_init_lock", false, true, false, null);

                String tokenMessage = "TOKEN";
                this.channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, tokenMessage.getBytes());
                System.out.println("Token iniziale inserito correttamente dal sistema!");

                initChannel.close();
            } catch (IOException e) {
            }
        }
    }

    public void acquire() throws IOException, InterruptedException {
        CompletableFuture<Long> request = new CompletableFuture<>();

        channel.basicQos(1);

        DeliverCallback dcb = (cTag, delivery) -> {
            request.complete(delivery.getEnvelope().getDeliveryTag());
        };

        CancelCallback ccb = (cTag) -> request.completeExceptionally(new RuntimeException("Consumer cancellato"));

        this.consumerTag = channel.basicConsume(queueName, false, dcb, ccb);

        try {
            this.currentDeliveryTag = request.get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Errore durante l'acquisizione del lock", e.getCause());
        }
    }

    public void release() throws IOException {
        if (this.currentDeliveryTag != -1) {
            channel.basicCancel(this.consumerTag);
            channel.basicReject(this.currentDeliveryTag, true);
            this.currentDeliveryTag = -1;
            System.out.println("Lock rilasciato.");
        }
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) channel.close();
        if (connection != null && connection.isOpen()) connection.close();
    }
}