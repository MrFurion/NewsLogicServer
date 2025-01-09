package by.clevertec.util;

public final class ExtracterErrorMessage {
    private ExtracterErrorMessage() {}

    public static String extractErrorMessage(String errorMessage) {
        if (errorMessage.contains("violates")) {
            String[] parts = errorMessage.split("violates");
            return parts[1].split("constraint")[0].trim();
        }
        return "Unknown data integrity violation.";
    }
}
