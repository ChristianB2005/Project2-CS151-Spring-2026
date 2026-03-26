public class Chef extends Employee {
    private String specialty;
    private Order[] currentOrders;
    private int orderCount;
    private static int instanceCount = 0;

    public Chef(String employeeId, String name, String specialty) {
        super(employeeId, name);

        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new IllegalStateException("Maximum number of Chef instances reached.");
        }

        this.specialty = specialty;
        this.currentOrders = new Order[Constants.MAXIMUM_INSTANCES];
        this.orderCount = 0;
        instanceCount++;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Order[] getCurrentOrders() {
        return currentOrders;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public void acceptOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (!isOnDuty) {
            throw new IllegalStateException("Chef must be on duty to accept an order.");
        }

        if (orderCount >= currentOrders.length) {
            throw new IllegalStateException("Chef order queue is full.");
        }

        currentOrders[orderCount] = order;
        orderCount++;

        order.setOrderStatus(OrderStatus.IN_PROGRESS);
    }

    public void completeOrder(Order order) {
        // TODO:
        // 1. locate the order in currentOrders
        // 2. remove it from the array
        // 3. shift remaining orders left
        // 4. decrement orderCount
        // 5. mark order as ready/completed
    }

    @Override
    public void updateStatus(String status) {
        // TODO:
        // define what "status" means for Chef in project
        // examples: "Available", "Busy", "Off Duty"
    }

    public String viewQueue() {
        // TODO: return a formatted String of current orders
        return "";
    }

    @Override
    public String toString() {
        return "Chef{" +
                "employeeId='" + getEmployeeId() + '\'' +
                ", name='" + getName() + '\'' +
                ", specialty='" + specialty + '\'' +
                ", isOnDuty=" + isOnDuty() +
                ", orderCount=" + orderCount +
                '}';
    }
}