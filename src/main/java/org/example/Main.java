package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Press Ctrl+C to exit.");
        // CREATE SENSOR INSTANCE
        My_Sensor sensor = new My_Sensor();

        // Read data
        sensor.readAndDisplayData();

        // Clean up Pi4J
        sensor.close();
    }
}