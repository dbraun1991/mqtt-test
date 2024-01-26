

package de.application.infrastructure.messagebroker.mqtt;

import de.application.domain.exception.MessageNotSentException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttServiceImpl implements MqttService, InitializingBean {

    private static final Logger logger =
            LoggerFactory.getLogger(MqttServiceImpl.class);

    @Value("${mqtt.broker.url}")
    private String mqttUrl;

    private MqttClient client;
    private final String messageSeparator = ";";


    @Override
    public void afterPropertiesSet() {
        logger.info(String.format(
                "Executing afterPropertiesSet-Constructor on bean '%s' with hash code: %s",
                this.getClass(),
                this.hashCode()));

        connectToBroker();
    }


    private void connectToBroker() {

        // Set the custom persistence directory
        String persistenceDirectory = "endpoint/infrastructure/mqtt";

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(15);

        try {
            // Set up file persistence for the client
            MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(persistenceDirectory);

            logger.info("Startup - ENV - mqttUrl - About to connect to    ==>   " + mqttUrl);

            this.client = new MqttClient(mqttUrl, MqttClient.generateClientId(), persistence);
            client.connect(options);

            logger.info("Startup - ENV - mqttUrl    ==>   CONNECTED");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // This method is called when the connection to the broker is lost.
                    logger.info("===> MQTT - Connection Lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // This method is called when a message arrives from the broker.
                    String receivedMessage = new String(message.getPayload());
                    logger.info(String.format("===> Message received on topic   ==>   %s", topic));
                    logger.info(String.format("===> Complete message   ==>   %s",  receivedMessage));
                    // Add your reaction to the message here
                    String[] splitReceivedMessage = receivedMessage.split(messageSeparator);
                    for (String part:splitReceivedMessage) {
                        logger.info(part);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // This method is called when a message has been successfully delivered.
                }
            });

        } catch (Exception e) {
            // Handle exceptions
            logger.error("Exception in 'connectToBroker' in 'MqttServiceImpl'", e);
        }
    }

    @Override
    public void publishMessage(
            String message
    ) throws MessageNotSentException {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            this.client.publish("defaultTopic", mqttMessage);
        } catch (MqttException e) {
            // Handle exceptions
            logger.error("MqttException in 'publishMessage' in 'MqttService'", e);
            throw new MessageNotSentException();
        }
    }
}
