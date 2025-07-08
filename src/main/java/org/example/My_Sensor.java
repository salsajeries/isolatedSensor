package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class My_Sensor {

    public static double[] readDHTFromPython() {
        double[] result = new double[2];

        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "/home/pi/Desktop/salwa/mydht22.py");
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine(); // Expecting one line: "24.3, 60.2"

            if (line != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2) {
                    result[0] = Double.parseDouble(parts[0]); // Temperature
                    result[1] = Double.parseDouble(parts[1]); // Humidity
                }
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}