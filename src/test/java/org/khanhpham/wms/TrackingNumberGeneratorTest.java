package org.khanhpham.wms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.khanhpham.wms.utils.TrackingNumberGenerator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

class TrackingNumberGeneratorTest {
    private static final Pattern PO_PATTERN = Pattern.compile("^PO\\d{8}\\d{6}[A-Z0-9]{6}$");
    private static final Pattern SO_PATTERN = Pattern.compile("^SO\\d{8}\\d{5}[A-Z0-9]{6}$");
    private static final Pattern RANDOM_STRING_PATTERN = Pattern.compile("^[A-Z0-9]+$");

    @BeforeEach
    void setUp() {
        // Reset counters before each test to ensure predictable results
        TrackingNumberGenerator.resetCounters();
    }

    @Test
    void testGeneratePurchaseOrderNumber() {
        String poNumber = TrackingNumberGenerator.generatePurchaseOrderNumber();
        assertNotNull(poNumber);
        assertTrue(PO_PATTERN.matcher(poNumber).matches(), "Invalid PO number format: " + poNumber);
    }

    @Test
    void testGenerateSalesOrderNumber() {
        String soNumber = TrackingNumberGenerator.generateSalesOrderNumber();
        assertNotNull(soNumber);
        assertTrue(SO_PATTERN.matcher(soNumber).matches(), "Invalid SO number format: " + soNumber);
    }

    @Test
    void testSequentialNumberIncrement() {
        String firstPO = TrackingNumberGenerator.generatePurchaseOrderNumber();
        String secondPO = TrackingNumberGenerator.generatePurchaseOrderNumber();

        // Extract sequential numbers
        int firstNumber = Integer.parseInt(firstPO.substring(10, 16));
        int secondNumber = Integer.parseInt(secondPO.substring(10, 16));

        assertEquals(firstNumber + 1, secondNumber, "PO sequential number did not increment correctly.");
    }

    @Test
    void testResetCounters() {
        TrackingNumberGenerator.generatePurchaseOrderNumber();
        TrackingNumberGenerator.resetCounters();
        String resetPO = TrackingNumberGenerator.generatePurchaseOrderNumber();

        // Ensure it starts again from 1
        int resetNumber = Integer.parseInt(resetPO.substring(10, 16));
        assertEquals(1, resetNumber, "PO counter did not reset correctly.");
    }

    @Test
    void testGenerateRandomString() throws Exception {
        java.lang.reflect.Method method = TrackingNumberGenerator.class.getDeclaredMethod("generateRandomString", int.class);
        method.setAccessible(true);

        String randomString = (String) method.invoke(null, 6);
        assertNotNull(randomString);
        assertEquals(6, randomString.length());
        assertTrue(RANDOM_STRING_PATTERN.matcher(randomString).matches(), "Random string contains invalid characters.");
    }
}
