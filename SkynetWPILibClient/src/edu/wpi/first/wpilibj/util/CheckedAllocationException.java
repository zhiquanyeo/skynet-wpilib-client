package edu.wpi.first.wpilibj.util;

/**
 * Exception indicating that the resource is already allocated
 * This is meant to be thrown by the resource class
 * @author dtjones
 */
public class CheckedAllocationException extends Exception {

    /**
     * Create a new CheckedAllocationException
     * @param msg the message to attach to the exception
     */
    public CheckedAllocationException(String msg) {
        super(msg);
    }
}
