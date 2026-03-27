public class Order implements Discountable {
    @Override
    public double calculateTotal() {
        return 0;
    }

    @Override
    public double applyDiscount(double percent) {
        return 0;
    }

    @Override
    public double getPrice() {
        return 0;
    }

    public String getStatus() {
        return null;
    }
}
