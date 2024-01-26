package de.application.domain.cron;

import de.application.domain.exception.MessageNotSentException;
import de.application.infrastructure.messagebroker.mqtt.MqttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 */
@Service
@Order(5)
@EnableScheduling
public class MessageCron {

    private static final Logger logger =
            LoggerFactory.getLogger(MessageCron.class);


    @Autowired
    private MqttService mqttService;

    private final int fixedInterval = 1000 * 10;             // One day
    private final int initialIntervalDelay = 1000;    // 10 Seconds


    @Scheduled(
            fixedRate = fixedInterval,
            initialDelay = initialIntervalDelay)
    public void scheduledCRON()
            throws MessageNotSentException {
        logger.info("Scheduled CRON - publishing a new random UUID to topic: 'defaultTopic'");

        mqttService.publishMessage(
                UUID.randomUUID().toString()
        );
    }

}
