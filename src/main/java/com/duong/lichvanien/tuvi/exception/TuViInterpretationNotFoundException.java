package com.duong.lichvanien.tuvi.exception;

/**
 * Exception thrown when Tu Vi interpretation is not found in database.
 * This occurs when the requested chart interpretation has not been seeded yet.
 */
public class TuViInterpretationNotFoundException extends RuntimeException {

    public TuViInterpretationNotFoundException(String message) {
        super(message);
    }

    public TuViInterpretationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create exception with standard message indicating data needs to be seeded.
     */
    public static TuViInterpretationNotFoundException forChart(String chartHash, String gender) {
        return new TuViInterpretationNotFoundException(
            String.format("Interpretation not found for chart (hash: %s, gender: %s). " +
                         "Data needs to be seeded first. Please contact admin to generate interpretation data.",
                         chartHash, gender)
        );
    }
}
