package model;

import core.Employee;
import exceptions.InvalidDiscountException;
import util.Constants;
import util.OrderStatus;

public class Server extends Employee {
    private String section;
    private String serverStatus;
    private final Table[] assignedTables;
    private int tableCount;
    private static int instanceCount = 0;

    public Server(String employeeId, String name, String section) {
        super(employeeId, name);

        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new IllegalStateException("Maximum number of Server instances reached.");
        }

        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Server section cannot be null or empty.");
        }

        this.section = section.trim();
        this.serverStatus = "Off Duty";
        this.assignedTables = new Table[Constants.MAXIMUM_INSTANCES];
        this.tableCount = 0;
        instanceCount++;
    }

    public String getSection() {
        return section;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public Table[] getAssignedTables() {
        return assignedTables;
    }

    public int getTableCount() {
        return tableCount;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public void setSection(String section) {
        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Server section cannot be null or empty.");
        }
        this.section = section.trim();
    }

    @Override
    public void clockIn() {
        super.clockIn();
        this.serverStatus = "Available";
    }

    @Override
    public void clockOut() {
        if (tableCount > 0) {
            throw new IllegalStateException("Server cannot clock out while still assigned to tables.");
        }

        super.clockOut();
        this.serverStatus = "Off Duty";
    }

    public void assignTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }

        if (!isOnDuty()) {
            throw new IllegalStateException("Server must be on duty to be assigned a table.");
        }

        if (tableCount >= assignedTables.length) {
            throw new IllegalStateException("Server table list is full.");
        }

        if (findTableIndex(table) != -1) {
            throw new IllegalStateException("Table is already assigned to this server.");
        }

        assignedTables[tableCount] = table;
        tableCount++;
        table.assignServer(this);

        refreshServerStatus();
    }

    public void removeTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }

        int tableIndex = findTableIndex(table);

        if (tableIndex == -1) {
            throw new IllegalStateException("Table is not assigned to this server.");
        }

        if (table.isOccupied()) {
            throw new IllegalStateException("Cannot remove an occupied table from a server.");
        }

        for (int i = tableIndex; i < tableCount - 1; i++) {
            assignedTables[i] = assignedTables[i + 1];
        }

        assignedTables[tableCount - 1] = null;
        tableCount--;

        table.clearOccupancy();
        table.setOccupation(false);

        refreshServerStatus();
    }

    public void seatCustomer(Customer customer, Table table) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }

        if (!isOnDuty()) {
            throw new IllegalStateException("Server must be on duty to seat a customer.");
        }

        if (findTableIndex(table) == -1) {
            throw new IllegalStateException("Table is not assigned to this server.");
        }

        if (table.isOccupied()) {
            throw new IllegalStateException("Table is already occupied.");
        }

        if (customer.getPartySize() > table.getMaxCapacity()) {
            throw new IllegalStateException("Customer party size exceeds the table capacity.");
        }

        table.addCustomer(customer);
        table.setOccupation(true);
        table.assignServer(this);
        customer.setIsSeated(true);

        refreshServerStatus();
    }

    public void takeCustomerOrder(Order order, Customer customer, MenuItem item) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        if (item == null) {
            throw new IllegalArgumentException("Menu item cannot be null.");
        }

        if (!isOnDuty()) {
            throw new IllegalStateException("Server must be on duty to take an order.");
        }

        if (!customer.getIsSeated()) {
            throw new IllegalStateException("Customer must be seated before placing an order.");
        }

        if (!item.isAvailable() || item.getStockCount() <= 0) {
            throw new IllegalStateException("Menu item is unavailable or out of stock.");
        }

        if (order.getOrderStatus() != OrderStatus.TAKING_ORDER) {
            throw new IllegalStateException("Order is no longer accepting items.");
        }

        order.addOrder(customer, item);
        item.decrementStock();

        refreshServerStatus();
    }

    public void applyDiscountToOrder(Order order, double percentage) throws InvalidDiscountException {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (!isOnDuty()) {
            throw new IllegalStateException("Server must be on duty to apply a discount.");
        }

        order.applyDiscount(percentage);
    }

    public void clearTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }

        if (findTableIndex(table) == -1) {
            throw new IllegalStateException("Table is not assigned to this server.");
        }

        table.clearOccupancy();
        table.setOccupation(false);
        table.assignServer(this);

        refreshServerStatus();
    }

    public boolean hasAssignedTables() {
        return tableCount > 0;
    }

    public String viewAssignedTables() {
        if (tableCount == 0) {
            return "No tables are currently assigned to server " + getName() + ".";
        }

        StringBuilder result = new StringBuilder();
        result.append("Tables assigned to server ").append(getName()).append(":\n");

        for (int i = 0; i < tableCount; i++) {
            result.append(i + 1)
                    .append(". ")
                    .append(assignedTables[i].getTableID())
                    .append(" (occupied=")
                    .append(assignedTables[i].isOccupied())
                    .append(")\n");
        }

        return result.toString();
    }

    @Override
    public void updateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty.");
        }

        if (status.equalsIgnoreCase("Available")) {
            this.serverStatus = "Available";
            setOnDuty(true);
        } else if (status.equalsIgnoreCase("Busy")) {
            this.serverStatus = "Busy";
            setOnDuty(true);
        } else if (status.equalsIgnoreCase("Off Duty")) {
            if (tableCount > 0) {
                throw new IllegalStateException("Server cannot be marked off duty while still assigned to tables.");
            }
            this.serverStatus = "Off Duty";
            setOnDuty(false);
        } else {
            throw new IllegalArgumentException("Invalid server status.");
        }
    }

    private int findTableIndex(Table table) {
        for (int i = 0; i < tableCount; i++) {
            if (assignedTables[i] == table) {
                return i;
            }
        }
        return -1;
    }

    private void refreshServerStatus() {
        if (!isOnDuty()) {
            serverStatus = "Off Duty";
        } else if (tableCount == 0) {
            serverStatus = "Available";
        } else {
            serverStatus = "Busy";
        }
    }

    @Override
    public String toString() {
        return "Server{" +
                "employeeId='" + getEmployeeId() + '\'' +
                ", name='" + getName() + '\'' +
                ", section='" + section + '\'' +
                ", serverStatus='" + serverStatus + '\'' +
                ", isOnDuty=" + isOnDuty() +
                ", tableCount=" + tableCount +
                '}';
    }
}