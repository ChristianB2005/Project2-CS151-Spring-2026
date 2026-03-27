package model;
import util.Constants;
import core.Discountable;
import exceptions.InvalidDiscountException;
import exceptions.TooManyInstancesException;

public class MenuItem implements Discountable {

    private static int instanceCount = 0;

    private String itemId;
    private String name;
    private double price;
    private String category;
    private boolean isAvailable;
    private int stockCount;

    public MenuItem(String itemId, String name, double price, String category, int stockCount) throws TooManyInstancesException{
        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new TooManyInstancesException("Maximum number of MenuItem instances reached.");
        }
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stockCount = stockCount;
        this.isAvailable = stockCount > 0;
        instanceCount++;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public int getStockCount() { return stockCount; }
    public static int getInstanceCount() { return instanceCount; }

    // Setters
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.price = price;
    }

    public void setStockCount(int stockCount) {
        if (stockCount < 0) {
            throw new IllegalArgumentException("Stock count cannot be negative.");
        }
        this.stockCount = stockCount;
        if (this.stockCount == 0) {
            this.isAvailable = false;
        }
    }

    // Discountable interface methods — matches interface exactly
    public void applyFlatDiscount(double discountAmount) throws InvalidDiscountException {
        if (price <= 0) {
            throw new InvalidDiscountException("Cannot apply flat discount to item with invalid price.");
        }
        this.price -= 1.00; // flat $1.00 discount — adjust as needed
        if (this.price <= 0) {
            throw new InvalidDiscountException("Discount cannot reduce price to zero or below.");
        }
    }

    public void applyDiscount(double percentage) throws InvalidDiscountException {
        if (price <= 0) {
            throw new InvalidDiscountException("Cannot apply discount to item with invalid price.");
        }
        this.price -= (this.price * 0.10); // 10% discount — adjust as needed
        if (this.price <= 0) {
            throw new InvalidDiscountException("Discount cannot reduce price to zero or below.");
        }
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    // Other MenuItem methods
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.price = newPrice;
    }

    public void markUnavailable() {
        this.isAvailable = false;
    }

    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero.");
        }
        this.stockCount += quantity;
        this.isAvailable = true;
    }

    public void decrementStock() {
        if (stockCount <= 0) {
            throw new IllegalStateException("Item is out of stock.");
        }
        this.stockCount--;
        if (this.stockCount == 0) {
            markUnavailable();
        }
    }

    public double applyModifier(double modifierAmount) {
        double modifiedPrice = this.price + modifierAmount;
        if (modifiedPrice <= 0) {
            throw new IllegalArgumentException("Modified price cannot be zero or negative.");
        }
        return modifiedPrice;
    }

    public static void resetInstanceCount() {
        instanceCount = 0;
    }

    @Override
    public String toString() {
        return "MenuItem[id=" + itemId + ", name=" + name + ", price=$" + price
                + ", category=" + category + ", available=" + isAvailable
                + ", stock=" + stockCount + "]";
    }
}