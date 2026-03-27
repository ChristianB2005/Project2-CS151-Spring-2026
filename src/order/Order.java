package order;

import priceable.Priceable;

public class Order implements Priceable {
    @Override
    public double calculateTotal() {
        return 0;
    }

    @Override
    public double applyDiscount(double percent) {
        return 0;
    }

    @Override
    public double getBasePrice() {
        return 0;
    }

    public String getStatus() {
        return null;
    }
}
