package model;

import util.Constants;
import java.util.ArrayList;

public class Table {
    private static int numTables = 0;
    private int maxCapacity;
    private Server server;
    private boolean isOccupied;
    private String tableID;
    private final ArrayList<Customer> customersAtTable;

    public Table(int maxCapacity) {
        if (numTables >= Constants.MAXIMUM_INSTANCES) {
            throw new RuntimeException("Maximum number of Table instances reached.");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Table capacity must be greater than zero.");
        }

        numTables++;
        this.maxCapacity = maxCapacity;
        this.tableID = "Table" + numTables;
        this.server = null;
        this.isOccupied = false;
        this.customersAtTable = new ArrayList<Customer>();
    }

    public String getTableID() {
        return tableID;
    }

    public void setTableID(String tableID) {
        if (tableID == null || tableID.trim().isEmpty()) {
            throw new IllegalArgumentException("Table ID cannot be null or empty.");
        }
        this.tableID = tableID.trim();
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Table capacity must be greater than zero.");
        }
        if (customersAtTable.size() > maxCapacity) {
            throw new IllegalArgumentException("New capacity cannot be smaller than current party size.");
        }
        this.maxCapacity = maxCapacity;
    }

    public Server getServer() {
        return server;
    }

    public void assignServer(Server newServer) {
        this.server = newServer;
    }

    public boolean hasAssignedServer() {
        return server != null;
    }

    public boolean isOccupied() {
        return this.isOccupied;
    }

    public void setOccupation(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public ArrayList<Customer> getCustomersAtTable() {
        return customersAtTable;
    }

    public int getCustomerCount() {
        return customersAtTable.size();
    }

    public boolean canSeatParty(int partySize) {
        return partySize > 0 && partySize <= maxCapacity && !isOccupied;
    }

    public void clearOccupancy() {
        this.server = null;
        this.isOccupied = false;
        customersAtTable.clear();
    }

    public void addCustomer(Customer newCustomer) {
        if (newCustomer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        if (customersAtTable.size() >= maxCapacity) {
            throw new IllegalStateException("Table is already at maximum capacity.");
        }
        if (customersAtTable.contains(newCustomer)) {
            throw new IllegalStateException("Customer is already seated at this table.");
        }

        customersAtTable.add(newCustomer);
    }

    public void removeCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        if (!customersAtTable.remove(customer)) {
            throw new IllegalStateException("Customer is not seated at this table.");
        }

        if (customersAtTable.isEmpty()) {
            this.isOccupied = false;
        }
    }

    public String getTableDetails() {
        return "Table{" +
                "tableID='" + tableID + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", occupied=" + isOccupied +
                ", customerCount=" + customersAtTable.size() +
                ", serverAssigned=" + (server != null) +
                '}';
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableID='" + tableID + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", occupied=" + isOccupied +
                ", customerCount=" + customersAtTable.size() +
                '}';
    }
}