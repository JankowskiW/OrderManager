package pl.wj.ordermanager.exception;

public class ExceptionHelper {
    public static String createResourceNotFoundMessage(String resource) {
        if (resource.isEmpty()) return "Resource not found";
        return String.format("%s%s not found", resource.substring(0,1).toUpperCase(), resource.substring(1).toLowerCase());
    }
}
