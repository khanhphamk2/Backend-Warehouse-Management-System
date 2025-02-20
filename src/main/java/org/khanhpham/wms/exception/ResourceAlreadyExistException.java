package org.khanhpham.wms.exception;

public class ResourceAlreadyExistException extends RuntimeException {
    public ResourceAlreadyExistException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s: %s already exist", resourceName, fieldName, fieldValue));
    }

    @Override
    public String toString() {
        return String.format("ResourceAlreadyExistException: %s", getMessage());
    }
}
