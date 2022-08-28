package pl.wj.ordermanager.exception;

public class ResourceInvalidVersionException extends RuntimeException {
    public ResourceInvalidVersionException(String resource) {
        super(ExceptionHelper.createResourceInvalidVersionExceptionMessage(resource));
    }
}
