import com.rabbitmq.client.*;
import java.io.IOException;
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
        while (true) {
            GetResponse response = channel.basicGet(queueName, false); // autoAck = false per sicurezza
            if (response != null) {
                //token aquired
                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
                System.out.println("[" + Thread.currentThread().getName() + "] Lock acquisito con successo.");
                return;
            }
            // Se la coda è vuota, attendiamo tot ms prima di riprovare
            Thread.sleep(100);
        }
    }

    public void release() throws IOException {
        String tokenMessage = "TOKEN";
        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, tokenMessage.getBytes());
        System.out.println("[" + Thread.currentThread().getName() + "] Lock rilasciato.");
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null) channel.close();
        if (connection != null) connection.close();
    }
}