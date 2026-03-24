package customer;

import table.Table;

public class Customer {
    private int customerID;
    private String name;
    private int partySize;
    private int loyaltyPoints;
    private double bill;
    private boolean isSeated;
    private Table reservedTable;

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

    public boolean reserveTable(Table table) {
        if (table.getIsReserved()) return false;
        reservedTable = table;
        return true;
    }
}
