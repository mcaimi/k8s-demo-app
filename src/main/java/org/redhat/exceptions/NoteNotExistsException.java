package org.redhat.exceptions;

public class NoteNotExistsException extends Exception {
    /**
     * generated serial version UUID
     */
    private static final long serialVersionUID = -2968812072817733049L;

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