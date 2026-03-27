package restaurant.model;

public class Chef extends Employee {
    private String specialty;
    private Order[] currentOrders;
    private int orderCount;
    private static int instanceCount = 0;

    public Chef(String employeeId, String name, String specialty) {
        super(employeeId, name);

        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new IllegalStateException("Maximum number of restaurant.model.Chef instances reached.");
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
            throw new IllegalStateException("restaurant.model.Chef must be on duty to accept an order.");
        }

        if (orderCount >= currentOrders.length) {
            throw new IllegalStateException("restaurant.model.Chef order queue is full.");
        }

        currentOrders[orderCount] = order;
        orderCount++;

        order.setOrderStatus(OrderStatus.IN_KITCHEN);
    }

    public void completeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        int orderIndex = -1;

        for (int i = 0; i < orderCount; i++) {
            if (currentOrders[i] == order) {
                orderIndex = i;
                break;
            }
        }

        if (orderIndex == -1) {
            throw new IllegalStateException("Order not found in this chef's queue.");
        }

        for (int i = orderIndex; i < orderCount - 1; i++) {
            currentOrders[i] = currentOrders[i + 1];
        }

        currentOrders[orderCount - 1] = null;
        orderCount--;

        order.setOrderStatus(OrderStatus.READY);
    }

    @Override
    public void updateStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        if (status.equalsIgnoreCase("Available") || status.equalsIgnoreCase("Busy")) {
            this.isOnDuty = true;
        } else if (status.equalsIgnoreCase("Off Duty")) {
            this.isOnDuty = false;
        } else {
            throw new IllegalArgumentException("Invalid chef status.");
        }
    }

    public String viewQueue() {
        if (orderCount == 0) {
            return "No current orders assigned to chef " + name + ".";
        }

        String result = "Current orders for chef " + name + ":\n";

        for (int i = 0; i < orderCount; i++) {
            result += (i + 1) + ". " + currentOrders[i] + "\n";
        }

        return result;
    }

    @Override
    public String toString() {
        return "restaurant.model.Chef{" +
                "employeeId='" + getEmployeeId() + '\'' +
                ", name='" + getName() + '\'' +
                ", specialty='" + specialty + '\'' +
                ", isOnDuty=" + isOnDuty() +
                ", orderCount=" + orderCount +
                '}';
    }
}