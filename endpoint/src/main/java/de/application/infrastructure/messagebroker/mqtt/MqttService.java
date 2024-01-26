package de.application.infrastructure.messagebroker.mqtt;

import de.application.domain.exception.MessageNotSentException;

/**
 * @author dbr
 * @Description: Managing the MQTT connection(s)
 */
public interface MqttService {

    /**
     * @Description: Publish a SecurityEvent
     */
    void publishMessage(
            String message
    ) throws MessageNotSentException;

}
