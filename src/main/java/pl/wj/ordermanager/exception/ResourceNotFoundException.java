package pl.wj.ordermanager.exception;

import javax.persistence.EntityNotFoundException;

import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceNotFoundExceptionMessage;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(String resource) {
        super(createResourceNotFoundExceptionMessage(resource));
    }
}
