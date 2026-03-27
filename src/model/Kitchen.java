package model;

import exceptions.KitchenAtCapacityException;
import exceptions.TooManyInstancesException;
import util.Constants;

public class Kitchen {
    private Order[] activeOrders;
    private int activeOrderCount;
    private Chef[] staff;
    private int staffCount;
    private int maxCapacity;
    private int totalOrdersCompleted;
    private static int instanceCount = 0;

    public Kitchen(int maxCapacity) throws TooManyInstancesException {
        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new TooManyInstancesException("Maximum number of Kitchen instances reached.");
        }

        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Kitchen capacity must be greater than zero.");
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
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Kitchen capacity must be greater than zero.");
        }
        this.maxCapacity = maxCapacity;
    }

    public void addChef(Chef chef) {
        if (chef == null) {
            throw new IllegalArgumentException("Chef cannot be null.");
        }

        if (staffCount >= staff.length) {
            throw new IllegalStateException("Kitchen staff roster is full.");
        }

        for (int i = 0; i < staffCount; i++) {
            if (staff[i] == chef) {
                throw new IllegalStateException("Chef is already assigned to this kitchen.");
            }
        }

        staff[staffCount] = chef;
        staffCount++;
    }

    public void addOrder(Order order) throws KitchenAtCapacityException {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (activeOrderCount >= maxCapacity) {
            throw new KitchenAtCapacityException("Kitchen is at maximum order capacity.");
        }

        if (activeOrderCount >= activeOrders.length) {
            throw new IllegalStateException("Active order storage is full.");
        }

        for (int i = 0; i < activeOrderCount; i++) {
            if (activeOrders[i] == order) {
                throw new IllegalStateException("Order is already in the kitchen.");
            }
        }

        activeOrders[activeOrderCount] = order;
        activeOrderCount++;

        Chef availableChef = getAvailableChef();
        if (availableChef != null) {
            availableChef.acceptOrder(order);
        }
    }

    public void removeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        int orderIndex = -1;

        for (int i = 0; i < activeOrderCount; i++) {
            if (activeOrders[i] == order) {
                orderIndex = i;
                break;
            }
        }

        if (orderIndex == -1) {
            throw new IllegalStateException("Order not found in kitchen.");
        }

        for (int i = orderIndex; i < activeOrderCount - 1; i++) {
            activeOrders[i] = activeOrders[i + 1];
        }

        activeOrders[activeOrderCount - 1] = null;
        activeOrderCount--;

        totalOrdersCompleted++;
    }

    public Chef getAvailableChef() {
        Chef bestChef = null;

        for (int i = 0; i < staffCount; i++) {
            Chef currentChef = staff[i];

            if (currentChef != null && currentChef.isOnDuty()) {
                if (bestChef == null || currentChef.getOrderCount() < bestChef.getOrderCount()) {
                    bestChef = currentChef;
                }
            }
        }

        return bestChef;
    }

    public String listActiveOrders() {
        if (activeOrderCount == 0) {
            return "There are no active orders in the kitchen.";
        }

        String result = "Active kitchen orders:\n";

        for (int i = 0; i < activeOrderCount; i++) {
            result += (i + 1) + ". " + activeOrders[i] + "\n";
        }

        return result;
    }

    public void incrementCompletedOrders() {
        totalOrdersCompleted++;
    }

    public String getDailySummary() {
        int chefsOnDuty = 0;

        for (int i = 0; i < staffCount; i++) {
            if (staff[i] != null && staff[i].isOnDuty()) {
                chefsOnDuty++;
            }
        }

        return "Kitchen Daily Summary:\n" +
                "Active Orders: " + activeOrderCount + "\n" +
                "Total Orders Completed: " + totalOrdersCompleted + "\n" +
                "Total Staff: " + staffCount + "\n" +
                "Chefs On Duty: " + chefsOnDuty + "\n" +
                "Max Capacity: " + maxCapacity;
    }

    @Override
    public String toString() {
        return "Kitchen{" +
                "activeOrderCount=" + activeOrderCount +
                ", staffCount=" + staffCount +
                ", maxCapacity=" + maxCapacity +
                ", totalOrdersCompleted=" + totalOrdersCompleted +
                '}';
    }
}