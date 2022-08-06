package pl.wj.ordermanager.exception;

import javax.persistence.EntityNotFoundException;

import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceNotFoundMessage;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(String resource) {
        super(createResourceNotFoundMessage(resource));
    }
}
