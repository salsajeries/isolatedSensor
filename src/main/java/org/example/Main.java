package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting reading...");

        double[] result = new double[2];
        boolean success = false;

        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "/home/pi/Desktop/mydht22.py");
            Process process = pb.start();
            System.out.println("Process started.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine(); // Expecting: "24.3, 60.2"

            if (line != null && !line.trim().equalsIgnoreCase("Error reading.")) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2) {
                    result[0] = Double.parseDouble(parts[0]); // Temperature
                    result[1] = Double.parseDouble(parts[1]); // Humidity
                    success = true;
                }
            } else {
                System.out.println("ERROR from script: " + line);
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            System.out.printf("Temperature: %.1f°C (%.1f°F)%n", result[0], (result[0] * 1.8 + 32));
            System.out.printf("Humidity: %.1f%%%n", result[1]);
        } else {
            System.out.println("Failed to read valid sensor data.");
        }
    }
}