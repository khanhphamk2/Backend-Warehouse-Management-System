package org.khanhpham.wms.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class TrackingNumberGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String PO_PREFIX  = "PO";
    private static final String SO_PREFIX  = "SO";
    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Sequential counters for PO and SO tracking numbers
    private final AtomicLong poCounter = new AtomicLong(1);
    private final AtomicLong soCounter = new AtomicLong(1);

    /**
     * Generates a unique tracking number for a Purchase Order (PO).
     * Format: PO + Date (yyyyMMdd) + Sequential Number (6 digits) + Random Alphanumeric String (6 characters).
     *
     * @return A unique PO tracking number.
     */
    public String generatePurchaseOrderNumber() {
        return PO_PREFIX + getCurrentDate() + String.format("%06d", poCounter.getAndIncrement()) + generateRandomString();
    }

    /**
     * Generates a unique tracking number for a Sales Order (SO).
     * Format: SO + Date (yyyyMMdd) + Sequential Number (5 digits) + Random Alphanumeric String (6 characters).
     *
     * @return A unique SO tracking number.
     */
    public String generateSalesOrderNumber() {
        return SO_PREFIX + getCurrentDate() + String.format("%05d", soCounter.getAndIncrement()) + generateRandomString();
    }

    /**
     * Retrieves the current date as a string formatted as "yyyyMMdd".
     *
     * @return The formatted current date string.
     */
    private String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * Resets the sequential counters to 1 (mainly for testing purposes).
     */
    public void resetCounters() {
        poCounter.set(1);
        soCounter.set(1);
    }

    /**
     * Generates a random alphanumeric string using uppercase letters (A-Z) and digits (0-9).
     *
     * @return A randomly generated alphanumeric string of the specified length.
     */
    private String generateRandomString() {
        StringBuilder trackingNumber = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            trackingNumber.append(CHARACTERS.charAt(index));
        }

        return trackingNumber.toString();
    }
}
