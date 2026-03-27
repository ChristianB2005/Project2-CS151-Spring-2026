package exceptions;

public class InvalidDiscountException extends Exception {
    public InvalidDiscountException(String message){
        super(message);
    }
}