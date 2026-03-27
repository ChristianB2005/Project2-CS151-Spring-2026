package exceptions;

public class TooManyInstancesException extends Exception{
    public TooManyInstancesException(String message){
        super(message);
    }
}
