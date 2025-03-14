package org.khanhpham.wms.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    @Override
    public String toString() {
        return String.format("ResourceNotFoundException: %s", getMessage());
    }
}
