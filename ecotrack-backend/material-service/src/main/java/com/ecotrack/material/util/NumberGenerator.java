package com.ecotrack.material.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class NumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private NumberGenerator() {
    }

    /** Example: ORD-20260803-4821 */
    public static String generateOrderNumber() {
        return "ORD-" + LocalDate.now().format(DATE_FORMAT) + "-" + randomSuffix();
    }

    /** Example: TXN-20260803-4821 */
    public static String generateTransactionNumber() {
        return "TXN-" + LocalDate.now().format(DATE_FORMAT) + "-" + randomSuffix();
    }

    private static int randomSuffix() {
        return 1000 + RANDOM.nextInt(9000);
    }
}
