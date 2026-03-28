package model;

import core.Employee;
import exceptions.TooManyInstancesException;
import util.Constants;
import util.OrderStatus;

public class Chef extends Employee {
    private String specialty;
    private String chefStatus;
    private Order[] currentOrders;
    private int orderCount;
    private static int instanceCount = 0;

    public Chef(String employeeId, String name, String specialty) throws TooManyInstancesException { 
        super(employeeId, name);

        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new TooManyInstancesException("Maximum number of Chef instances reached.");
        }

        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Chef specialty cannot be null or empty.");
        }

        this.specialty = specialty;
        this.chefStatus = "Off Duty";
        this.currentOrders = new Order[Constants.MAXIMUM_INSTANCES];
        this.orderCount = 0;
        instanceCount++;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Chef specialty cannot be null or empty.");
        }
        this.specialty = specialty;
    }

    public String getChefStatus() {
        return chefStatus;
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

    @Override
    public void clockIn() {
        super.clockIn();
        this.chefStatus = "Available";
    }

    @Override
    public void clockOut() {
        if (orderCount > 0) {
            throw new IllegalStateException("Chef cannot clock out while orders are still assigned.");
        }

        super.clockOut();
        this.chefStatus = "Off Duty";
    }

    public void acceptOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (!isOnDuty()) {
            throw new IllegalStateException("Chef must be on duty to accept an order.");
        }

        if (orderCount >= currentOrders.length) {
            throw new IllegalStateException("Chef order queue is full.");
        }

        for (int i = 0; i < orderCount; i++) {
            if (currentOrders[i] == order) {
                throw new IllegalStateException("Order is already assigned to this chef.");
            }
        }

        currentOrders[orderCount] = order;
        orderCount++;

        order.setOrderStatus(OrderStatus.IN_KITCHEN);

        if (orderCount > 0) {
            chefStatus = "Busy";
        }
    }

    public void completeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        int orderIndex = findOrderIndex(order);

        if (orderIndex == -1) {
            throw new IllegalStateException("Order not found in this chef's queue.");
        }

        for (int i = orderIndex; i < orderCount - 1; i++) {
            currentOrders[i] = currentOrders[i + 1];
        }

        currentOrders[orderCount - 1] = null;
        orderCount--;

        order.setOrderStatus(OrderStatus.READY);

        if (!isOnDuty()) {
            chefStatus = "Off Duty";
        } else if (orderCount == 0) {
            chefStatus = "Available";
        } else {
            chefStatus = "Busy";
        }
    }

    public void transferOrder(Order order, Chef otherChef) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (otherChef == null) {
            throw new IllegalArgumentException("Receiving chef cannot be null.");
        }

        if (otherChef == this) {
            throw new IllegalArgumentException("Chef cannot transfer an order to themselves.");
        }

        int orderIndex = findOrderIndex(order);

        if (orderIndex == -1) {
            throw new IllegalStateException("Order not found in this chef's queue.");
        }

        otherChef.acceptOrder(order);

        for (int i = orderIndex; i < orderCount - 1; i++) {
            currentOrders[i] = currentOrders[i + 1];
        }

        currentOrders[orderCount - 1] = null;
        orderCount--;

        if (!isOnDuty()) {
            chefStatus = "Off Duty";
        } else if (orderCount == 0) {
            chefStatus = "Available";
        } else {
            chefStatus = "Busy";
        }
    }

    public boolean hasActiveOrders() {
        return orderCount > 0;
    }

    @Override
    public void updateStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        if (status.equalsIgnoreCase("Available")) {
            this.chefStatus = "Available";
            setOnDuty(true);
        } else if (status.equalsIgnoreCase("Busy")) {
            this.chefStatus = "Busy";
            setOnDuty(true);
        } else if (status.equalsIgnoreCase("Off Duty")) {
            if (orderCount > 0) {
                throw new IllegalStateException("Chef cannot be marked off duty while orders are still assigned.");
            }
            this.chefStatus = "Off Duty";
            setOnDuty(false);
        } else {
            throw new IllegalArgumentException("Invalid chef status.");
        }
    }

    public String viewQueue() {
        if (orderCount == 0) {
            return "No current orders assigned to chef " + getName() + ".";
        }

        StringBuilder result = new StringBuilder();
        result.append("Current orders for chef ").append(getName()).append(":\n");

        for (int i = 0; i < orderCount; i++) {
            result.append(i + 1)
                    .append(". ")
                    .append(currentOrders[i])
                    .append("\n");
        }

        return result.toString();
    }

    private int findOrderIndex(Order order) {
        for (int i = 0; i < orderCount; i++) {
            if (currentOrders[i] == order) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Chef{" +
                "employeeId='" + getEmployeeId() + '\'' +
                ", name='" + getName() + '\'' +
                ", specialty='" + specialty + '\'' +
                ", chefStatus='" + chefStatus + '\'' +
                ", isOnDuty=" + isOnDuty() +
                ", orderCount=" + orderCount +
                '}';
    }
}