package org.redhat.exceptions;

public class MalformedNoteException extends Exception {
    /**
     * generated serial version UUID
     */
    private static final long serialVersionUID = -4963871740639334006L;

    public MalformedNoteException() {
    }

    public MalformedNoteException(String message) {
        super(message);
    }
    
    public MalformedNoteException(Throwable cause) {
        super(cause);
    }

    public MalformedNoteException(String message, Throwable cause) {
        super(message, cause);
    }
}