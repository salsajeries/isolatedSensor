package org.example;

// PI4J DEPENDENCIES
import com.pi4j.Pi4J;
//import com.pi4j.context.Context;
//import com.pi4j.io.gpio.digital.DigitalInput;
//import com.pi4j.io.gpio.digital.DigitalInputConfig;
//import com.pi4j.io.gpio.digital.DigitalState;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.gpio.digital.Digital;
import com.pi4j.util.Console;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class My_Sensor {

    // Pi4J variables
    private final Console console = new Console();          // For debugging purposes
    private final Context pi4j;             // Container for Pi4J session
    private DigitalOutput dataOut = null;   // Used to control start signal for reading
    private DigitalInput dataIn = null;     // Used to read sensor data
    private int dataPinNum = 4;                 // Pin used for sending data
    private boolean awaitingHigh;           // True = Waiting for Low->High, False = Waiting for High->Low
    private DigitalOutputConfigBuilder outputConfig;    // Output config builder
    private DigitalInputConfigBuilder inputConfig;      // Input config builder


    /**
     * Constructor for My_Sensor
     */
    public My_Sensor() {
        System.out.println("Creating sensor...");

        // Initialize Pi4J context
        this.pi4j = Pi4J.newAutoContext();
        System.out.println("Pi4J context created successfully. |");

        // Clean Pi4J registry if needed
        if (pi4j.registry().exists("Data_Out")) {
            pi4j.registry().remove("Data_Out");
        }
        if (pi4j.registry().exists("Data_In")) {
            pi4j.registry().remove("Data_In");
        }

        this.awaitingHigh = true;
        this.init();

        System.out.println("Sensor created successfully.");
    }

    private void init() {
        System.out.println(">>> Enter: init");

        outputConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("Data_Out")
                .name("Data_Out")
                .address(this.dataPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("gpiod-digital-output");
        System.out.println("outputConfig created successfully.");

        inputConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("Data_In")
                .name("Data_Out")
                .address(this.dataPinNum)
                .pull(PullResistance.OFF)
                .provider("gpiod-digital-input");
        System.out.println("inputConfig created successfully.");

        System.out.println("<<< Exit: init");
    }

    private void createOutputPin() {
        dataOut = pi4j.create(outputConfig);
        //System.out.println("dataOut created successfully.");
    }

    private void createInputPin() {
        dataIn = pi4j.create(inputConfig);
        //System.out.println("dataIn created successfully.");
    }

    private void idleOutputPin() {
        this.pi4j.shutdown(dataOut.id());
        //System.out.println("dataOut shutdown/idled successfully.");
    }

    private void idleInputPin() {
        this.pi4j.shutdown(dataIn.id());
        //System.out.println("dataIn shutdown/idled successfully.");
    }

    public void readAndDisplayData() throws InterruptedException {
        System.out.println(">>> Enter: readAndDisplayData");

        double temperature, humidity;
        double[] res;

        for (int i = 0; i < DHT22_Declares.TOTAL_NUM_BITS; i++) {
            if ((res = read()) != null) {
                temperature = res[0];
                humidity = res[1];
                String sign = "";

                if (((long) temperature & 0x8000) > 0) {
                    sign = "-";
                }

                // Log sensor data
                // Converts temperature C->F
                System.out.println("\tRH: " + humidity + "\tT: " + sign + ((temperature * 1.8) + 32));
                break;
            } else try {
                Thread.sleep(300);
            } catch (Exception e) {
                System.out.println("ERROR in readAndDisplayData:");
                System.out.println(e);
            }
        }
        System.out.println("<<< Exit: readAndDisplayData");
    }

    private double[] read() throws InterruptedException {
        //System.out.println(">>> Enter: read");

        // Start signal
        createOutputPin();
        dataOut.state(DigitalState.LOW);
        Thread.sleep(2);
        dataOut.state(DigitalState.HIGH);
        idleOutputPin();

        long now = System.nanoTime();

        // Switch to input pin
        createInputPin();
        now = System.nanoTime();
        DigitalState state = dataIn.state();

        long val = 0;
        long lastHi = now;
        int read = 0;

        // DEBUGGING PURPOSES
        int transitions = 0;

        while (read < 40 && System.nanoTime() - now < 10000000) {
            DigitalState next = dataIn.state();
            if (state != next) {
                //transitions++;  // COUNT SIGNAL EDGES
                if (next == DigitalState.HIGH) {
                    lastHi = System.nanoTime();
                } else {
                    val = (val << 1);
                    read++;
                    if ((System.nanoTime() - lastHi) / 1000 > 48) {
                        val++;  // Long duration high = 1
                    }
                }
                state = next;
            }
        }

        idleInputPin();

        System.out.println("Transitions observed: " + read);

        double[] rval = null;
        double temperature = 0.0;
        double humidity = 0.0;

        if (read >= 38) {
            int hi = (int) ((val & 0xff00000000L) >> 32), hd = (int) ((val & 0xff000000L) >> 24),
                    ti = (int) ((val & 0xff0000) >> 16), td = (int) ((val & 0xff00) >> 8),
                    cs = (int) (val & 0xff);

            // Checksum validation
            if (cs == ((hi + hd + ti + td) & 0xff)) {
                temperature = ((((ti & 0x7f) << 8) + td) / 10.) * ((ti & 0x80) != 0 ? -1 : 1);  // Check if sign bit is set (-), multiply by -1
                humidity = ((hi << 8) + hd) / 10;
                rval = new double[]{temperature, humidity};

                System.out.println("Decoded values \tT: " + temperature + "/" + ((temperature * 1.8) + 32) + "\tRH: " + humidity);
            } else {
                System.out.println("ERROR checksum failed val: " + val);
            }
        } else {
            //System.out.println("Not enough bits read: " + read);
        }
        //System.out.println("<<< Exit: read");
        return rval;

    }

    public void close() {
        System.out.println("Shutting down Pi4J context...");
        pi4j.shutdown();
    }

}