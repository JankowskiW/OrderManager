package pl.wj.ordermanager.exception;

import javax.persistence.EntityExistsException;

public class ResourceExistsException extends EntityExistsException {
    public ResourceExistsException(String resource) {
        super(ExceptionHelper.createResourceExistsExceptionMessage(resource));
    }

    public ResourceExistsException(String resource, String violationFields) {
        super(ExceptionHelper.createResourceExistsExceptionMessage(resource, violationFields));
    }
}
