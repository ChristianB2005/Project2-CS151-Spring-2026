package restaurant.model;

public class Kitchen {
    private Order[] activeOrders;
    private int activeOrderCount;
    private Chef[] staff;
    private int staffCount;
    private int maxCapacity;
    private int totalOrdersCompleted;
    private static int instanceCount = 0;

    public Kitchen(int maxCapacity) {
        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new IllegalStateException("Maximum number of restaurant.model.Kitchen instances reached.");
        }

        this.activeOrders = new Order[Constants.MAXIMUM_INSTANCES];
        this.activeOrderCount = 0;
        this.staff = new Chef[Constants.MAXIMUM_INSTANCES];
        this.staffCount = 0;
        this.maxCapacity = maxCapacity;
        this.totalOrdersCompleted = 0;
        instanceCount++;
    }

    public Order[] getActiveOrders() {
        return activeOrders;
    }

    public int getActiveOrderCount() {
        return activeOrderCount;
    }

    public Chef[] getStaff() {
        return staff;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getTotalOrdersCompleted() {
        return totalOrdersCompleted;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void addChef(Chef chef) {
        // TODO:
        // 1. validate chef is not null
        // 2. add chef to staff if space exists
    }

    public void addOrder(Order order) throws KitchenAtCapacityException {
        // TODO:
        // 1. validate order is not null
        // 2. check if kitchen is at maxCapacity
        // 3. throw KitchenAtCapacityException if full
        // 4. add order to activeOrders
        // 5. optionally assign to an available chef
    }

    public void removeOrder(Order order) {
        // TODO:
        // 1. locate order in activeOrders
        // 2. remove it from the array
        // 3. shift remaining orders left
        // 4. decrement activeOrderCount
    }

    public Chef getAvailableChef() {
        // TODO:
        // return the first chef who is on duty
        // and is able to take another order
        return null;
    }

    public String listActiveOrders() {
        // TODO: return a formatted String of all active orders
        return "";
    }

    public void incrementCompletedOrders() {
        totalOrdersCompleted++;
    }

    public String getDailySummary() {
        // TODO: return summary info such as:
        // active orders, chefs on duty, total completed
        return "";
    }

    @Override
    public String toString() {
        return "restaurant.model.Kitchen{" +
                "activeOrderCount=" + activeOrderCount +
                ", staffCount=" + staffCount +
                ", maxCapacity=" + maxCapacity +
                ", totalOrdersCompleted=" + totalOrdersCompleted +
                '}';
    }
}