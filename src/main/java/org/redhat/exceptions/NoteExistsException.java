package org.redhat.exceptions;

public class NoteExistsException extends Exception {
    /**
     * generated serial version UUID
     */
    private static final long serialVersionUID = -6211899990639763472L;

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