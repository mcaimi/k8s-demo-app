package org.redhat.exceptions;

public class MalformedNoteException extends Exception {
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