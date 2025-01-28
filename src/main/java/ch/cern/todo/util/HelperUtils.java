package ch.cern.todo.util;

import ch.cern.todo.exceptions.BadRequestException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HelperUtils {

    // Private constructor to prevent instantiation
    private HelperUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void validateNotEmpty(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException("Incomplete data", "Field cannot be null or empty");
        }
    }

    public static LocalDate formatData(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }
}
