package de.application;

import de.application.domain.exception.MessageNotSentException;
import de.application.infrastructure.messagebroker.mqtt.MqttService;
import org.junit.ClassRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=integrationtest"
)
@Testcontainers
class applicationTest {

    private static final Logger logger =
            LoggerFactory.getLogger(applicationTest.class);

    @Autowired
    MqttService mqttService;

    // Create a common network for all containers
    @ClassRule
    public static Network network = Network.newNetwork();

    // Define the mosquitto MQTT message broker container
    @ClassRule
    public static GenericContainer<?> mosquittoContainer =
            new GenericContainer<>(
                    DockerImageName.parse("eclipse-mosquitto:2.0.18"))
                    .withExposedPorts(1883) // Default MQTT port
                    .withNetwork(network)
                    .withNetworkAliases("mosquitto")
                    .withFileSystemBind(
                            "src/test/resources/mosquitto.conf",
                            "/mosquitto/config/mosquitto.conf",
                            BindMode.READ_ONLY)
                    .withCommand("/usr/sbin/mosquitto -c /mosquitto/config/mosquitto.conf")
                    .waitingFor(Wait.forListeningPort());

    static {
        // Start the container
        mosquittoContainer.start();

        // === Set system properties ===
        // --> MessageQueue
        String mqttURL = String.format("tcp://%s:%s",
                mosquittoContainer.getHost(),
                mosquittoContainer.getMappedPort(1883));
        System.setProperty("MQTT_URL", mqttURL);
    }



    @Test
    @DisplayName("Roundtrip-Test von 'Anlage des Users' über 'Token-Generierung' bis zum 'Löschen des Users'.")
    public void ConnectionTest() throws MessageNotSentException {

        mqttService.publishMessage("If published, succeeded");

        logger.info("connected");
    }



}