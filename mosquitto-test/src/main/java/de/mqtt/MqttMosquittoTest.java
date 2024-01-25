package de.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MqttMosquittoTest {

    public static void main(String[] args) {
        SpringApplication.run(MqttMosquittoTest.class, args);
    }
}
