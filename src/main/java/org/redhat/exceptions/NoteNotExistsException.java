package org.redhat.exceptions;

public class NoteNotExistsException extends Exception {
    public NoteNotExistsException() {
    }

    public NoteNotExistsException(String message) {
        super(message);
    }

    public NoteNotExistsException(Throwable cause) {
        super(cause);
    }

    public NoteNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}