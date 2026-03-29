package core;

import exceptions.InvalidDiscountException;

public interface Discountable {
    public void applyFlatDiscount(double discountAmount) throws InvalidDiscountException;

    public void applyDiscount(double percentage) throws InvalidDiscountException;

    public double getPrice();
}