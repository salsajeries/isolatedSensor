package org.example;

public class DHT22_Declares {
    protected final static int BEGIN_READING_MILLS = 20;        // Time to wait before expecting start signal, 20 milliseconds

    // Signal the sensor is ready to send data
    protected final static int PREPARE_DATA_LOW_PULSE_MICS = 80;    // 80 microseconds
    protected final static int PREPARE_DATA_HIGH_PULSE_MICS = 80;   // 80 microseconds

    // Data signals 0 or 1 by pulse length
    protected final static int ZERO_PULSE_MICS = 27;    // Zero bit
    protected final static int ONE_PULSE_MICS = 70;     // One bit

    // Bit number constants
    protected final static int TOTAL_NUM_BITS = 40;
    protected final static int RH_NUM_BITS = 16;        // Humidity
    protected final static int T_NUM_BITS = 16;         // Temperature
    protected final static int CKSUM_NUM_BITS = 8;      // Checksum
}
