package org.example.meetingapp.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final Long id;

    public ResourceNotFoundException(Long id) {
        super("Möte med id " + id + " hittades inte");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}