package com.ecotrack.pickup.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class PickupNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private PickupNumberGenerator() {
    }

    /** Example: PKP-20260803-4821 */
    public static String generate() {
        int suffix = 1000 + RANDOM.nextInt(9000);
        return "PKP-" + LocalDate.now().format(DATE_FORMAT) + "-" + suffix;
    }
}
