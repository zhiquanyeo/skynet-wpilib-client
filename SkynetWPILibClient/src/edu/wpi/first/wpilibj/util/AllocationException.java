package edu.wpi.first.wpilibj.util;

/**
 * Exception indicating that the resource is already allocated
 * @author dtjones
 */
public class AllocationException extends RuntimeException {

    /**
     * Create a new AllocationException
     * @param msg the message to attach to the exception
     */
    public AllocationException(String msg) {
        super(msg);
    }
}
