package core;

public interface Discountable {
    double applyFlatDiscount();
    double applyDiscount(double percent);
    double getPrice();
}
