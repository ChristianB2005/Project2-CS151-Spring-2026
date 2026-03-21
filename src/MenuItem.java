public class MenuItem {

    private static int instanceCount = 0;

    private String itemId;
    private String name;
    private double price;
    private String category;
    private boolean isAvailable;
    private int stockCount;

    public MenuItem(String itemId, String name, double price, String category, int stockCount) {
        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new RuntimeException("Maximum number of MenuItem instances reached.");
        }
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stockCount = stockCount;
        this.isAvailable = stockCount > 0;
        instanceCount++;
    }

    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public int getStockCount() { return stockCount; }
    public static int getInstanceCount() { return instanceCount; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }

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

    @Override
    public String toString() {
        return "MenuItem[id=" + itemId + ", name=" + name + ", price=$" + price
                + ", category=" + category + ", available=" + isAvailable
                + ", stock=" + stockCount + "]";
    }
}