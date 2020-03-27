package org.redhat.exceptions;

public class NoteExistsException extends Exception {
    public NoteExistsException() {
    }

    public NoteExistsException(String message) {
        super(message);
    }
    
    public NoteExistsException(Throwable cause) {
        super(cause);
    }

    public NoteExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}