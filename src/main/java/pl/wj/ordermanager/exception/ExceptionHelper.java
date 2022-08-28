package pl.wj.ordermanager.exception;

public class ExceptionHelper {
    public static String createResourceNotFoundExceptionMessage(String resource) {
        if (resource == null || resource.isBlank()) return "Resource not found";
        return String.format("%s not found", capitalizeFirstLetter(resource));
    }

    public static String createResourceExistsExceptionMessage(String resource) {
        if (resource == null || resource.isBlank()) return "Resource already exists";
        return String.format("%s already exists", capitalizeFirstLetter(resource));
    }

    public static String createResourceExistsExceptionMessage(String resource, String violationFields) {
        if (resource == null || resource.isBlank()) return "Resource already exists";
        if (violationFields == null || violationFields.isBlank()) return createResourceExistsExceptionMessage(resource);
        violationFields = violationFields.trim().toLowerCase();
        return String.format("%s with given %s already exists", capitalizeFirstLetter(resource), violationFields);
    }

    public static String createResourceInvalidVersionExceptionMessage(String resource) {
        if (resource == null || resource.isBlank()) return "Resource has invalid version";
        return String.format("%s has invalid version", capitalizeFirstLetter(resource));
    }

    private static String capitalizeFirstLetter(String word) {
        word = word.trim().toLowerCase();
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }
}
