package customer;

import constants.Constants;
import table.Table;

public class Customer {
    private int customerID;
    private String name;
    private int partySize;
    private int loyaltyPoints;
    private double bill;
    private boolean isSeated;
    private Table reservedTable;
    private static int instances = 0;

    public Customer(String name, int partySize, int loyaltyPoints, double bill, boolean isSeated, Table reservedTable) {
        if (instances >= Constants.MAXIMUM_INSTANCES)
            throw new RuntimeException("Maximum number of Customer instances reached.");
        this.customerID = instances;
        this.name = name;
        this.partySize = partySize;
        this.loyaltyPoints = loyaltyPoints;
        this.bill = bill;
        this.isSeated = isSeated;
        this.reservedTable = reservedTable;
        instances++;
    }

    public Customer(String name, int partySize) {
        if (instances >= Constants.MAXIMUM_INSTANCES)
            throw new RuntimeException("Maximum number of Customer instances reached.");
        this.customerID = instances;
        this.name = name;
        this.partySize = partySize;
        this.loyaltyPoints = 0;
        this.bill = 0;
        instances++;
    }

    public int getCustomerID() { return customerID; }
    public String getName() { return name; }
    public int getPartySize() { return partySize; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public double getBill() { return bill; }
    public boolean getIsSeated() { return isSeated; }
    public Table getReservedTable() { return reservedTable; }

    public void setCustomerID(int id) { customerID = id; }
    public void setName(String nameInput) { name = nameInput; }
    public void setPartySize(int size) { partySize = size; }
    public void setLoyaltyPoints(int points) { loyaltyPoints = points; }
    public void setBill(double billInput) { bill = billInput; }
    public void setIsSeated(boolean input) { isSeated = input; }

    @Override
    public String toString() {
        return "Customer:" + name + ":" + customerID;
    }

    public boolean reserveTable(Table table) {
        if (table.getIsReserved()) return false;
        reservedTable = table;
        return true;
    }
}
