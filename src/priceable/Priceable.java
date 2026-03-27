package priceable;

public interface Priceable {
    double calculateTotal();
    double applyDiscount(double percent);
    double getBasePrice();
}
