import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class DistributedLock {
    private final String queueName;
    private Connection connection;
    private Channel channel;

    public DistributedLock(String queueName) {
        this.queueName = queueName;
    }

    public void init() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //user/host: metfivxb
        //password: pJd2_Nqv3HwjlyMQq1s9Q9mxaWvnSWHY
        String amqpUrl = "amqps://metfivxb:pJd2_Nqv3HwjlyMQq1s9Q9mxaWvnSWHY@cow.rmq2.cloudamqp.com/metfivxb";
        factory.setUri(amqpUrl);

        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();

        // Dichiariamo una coda persistente per garantire che il token non vada perso
        this.channel.queueDeclare(queueName, true, false, false, null);
    }

    public void acquire() throws IOException, InterruptedException {
        CompletableFuture<Void> request = new CompletableFuture<>(); 

        DeliverCallback dcb = (consumerTag, delivery) -> {
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            request.complete(null);
        };

        CancelCallback ccb = (consumerTagInfo) -> request.completeExceptionally(new RuntimeException());

        String consumerTag = channel.basicConsume(queueName, false, dcb, ccb);
        try {
            request.get(); 
        } catch (ExecutionException e) {
            throw new RuntimeException("Errore durante l'acquisizione del lock", e.getCause());
        } finally {
            channel.basicCancel(consumerTag);
        }
    }

    public void release() throws IOException {
        String tokenMessage = "TOKEN";
        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, tokenMessage.getBytes());
        System.out.println("Lock rilasciato.");
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null) channel.close();
        if (connection != null) connection.close();
    }
}