public interface Discountable {
    public void applyFlatDiscount() throws InvalidDiscountException;
    public void applyDiscount() throws InvalidDiscountException;
    public double getPrice();
}
