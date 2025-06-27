package org.example;

// PI4J DEPENDENCIES
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

public class My_Sensor {
    // DECLARE CLASS VARIABLES
    private final Context pi4j;
    private final DigitalInput input;

    // CONSTRUCTOR
    public My_Sensor() {
        System.out.println("Creating Sensor...");

        // Initialize Pi4J context
        this.pi4j = Pi4J.newAutoContext();

        // Configure GPIO pin (physical pin 16 = BCM pin 23)
        DigitalInputConfig DINConfig = DigitalInput.newConfigBuilder(pi4j) // DIN = Digital INput
                .id("sensor")
                .name("mySensor")
                .address(23)
                .build();

        this.input = pi4j.create(DINConfig);

        // ADD LISTENER TO READ SENSOR DYNAMICALLY
        this.input.addListener(e -> {
            System.out.println(e.state() == DigitalState.LOW);
        });
    }
}
