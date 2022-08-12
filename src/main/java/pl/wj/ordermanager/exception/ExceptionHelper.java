package pl.wj.ordermanager.exception;

public class ExceptionHelper {
    public static String createResourceNotFoundExceptionMessage(String resource) {
        if (resource.isEmpty()) return "Resource not found";
        return String.format("%s%s not found", resource.substring(0,1).toUpperCase(), resource.substring(1).toLowerCase());
    }

    public static String createResourceExistsExceptionMessage(String resource) {
        if (resource.isEmpty()) return "Resource already exists";
        return String.format("$s%s already exists", capitalizeFirstLetter(resource));
    }

    public static String createResourceExistsExceptionMessage(String resource, String violationFields) {
        if (resource.isEmpty()) return "Resource already exists";
        if (violationFields.isEmpty()) return createResourceExistsExceptionMessage(resource);
        violationFields = violationFields.trim().toLowerCase();
        return String.format("$s%s with given %s already exists", capitalizeFirstLetter(resource), violationFields);
    }

    private static String capitalizeFirstLetter(String word) {
        word = word.trim().toLowerCase();
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }
}
