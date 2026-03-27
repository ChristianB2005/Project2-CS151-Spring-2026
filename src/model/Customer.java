package model;

import util.Constants;

public class Customer {
    private String customerID;
    private String name;
    private int partySize;
    private int loyaltyPoints;
    private double bill;
    private boolean isSeated;
    private Table reservedTable;
    private static int instances = 0;

//    public model.Customer(String name, int partySize, int loyaltyPoints, double bill, boolean isSeated, model.Table reservedTable) {
//        if (instances >= util.Constants.MAXIMUM_INSTANCES)
//            throw new RuntimeException("Maximum number of model.Customer instances reached.");
//        this.customerID = "model.Customer" + instances;
//        this.name = name;
//        this.partySize = partySize;
//        this.loyaltyPoints = loyaltyPoints;
//        this.bill = bill;
//        this.isSeated = isSeated;
//        this.reservedTable = reservedTable;
//        instances++;
//    }

    public Customer(String name, int partySize) {
        if (instances >= Constants.MAXIMUM_INSTANCES)
            throw new RuntimeException("Maximum number of model.Customer instances reached.");
        this.customerID = "model.Customer" + instances;
        this.name = name;
        this.partySize = partySize;
        this.loyaltyPoints = 0;
        this.bill = 0;
        instances++;
    }

    public String getCustomerID() { return customerID; }
    public String getName() { return name; }
    public int getPartySize() { return partySize; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public double getBill() { return bill; }
    public boolean getIsSeated() { return isSeated; }
    public Table getReservedTable() { return reservedTable; }

    public void setCustomerID(String id) { customerID = id; }
    public void setName(String nameInput) { name = nameInput; }
    public void setPartySize(int size) { partySize = size; }
    public void setLoyaltyPoints(int points) { loyaltyPoints = points; }
    public void setBill(double billInput) { bill = billInput; }
    public void setIsSeated(boolean input) { isSeated = input; }

    @Override
    public String toString() {
        return "model.Customer:" + name + ":" + customerID;
    }

    public boolean makeReservation(Table table) {
        if (table.isOccupied())
            return false;
        if (this.partySize > table.getCapacity())
            return false;
        if (Reservation.getInstances() >= Constants.MAXIMUM_INSTANCES)
            return false;

        Reservation reservation = new Reservation(this.name, this.partySize, table.getTableId());
        reservation.confirmReservation();
        table.seatCustomer(this);
        return true;
    }

    public boolean cancelReservation(Reservation reservation) {
        if (!reservation.getCustomerName().equals(this.name)) {
            throw new RuntimeException("Cancelling wrong reservation.");
        }
        if (reservation.getStatus().equals(Constants.RESERVATION_STATUS_CANCELLED)) {
            return false;
        }
        reservation.cancelReservation();
        return true;
    }

    public boolean payBill(Order order) {
        if (order == null) {
            throw new RuntimeException("No order provided for model.Customer to pay bill");
        }
        if (!order.getStatus().equals(Constants.ORDER_READY)) {
            return false;
        }
        double amount = order.getPrice();
        if (amount <= 0)
            return true;
        double amountPaid = this.bill;
        this.bill = 0.0;
        earnPoints(amountPaid);
        return true;
    }

    public boolean applyDiscount(double percent) {
        if (percent <= 0 || percent > 100)
            return false;
        if (this.bill <= 0)
            return false;
        double discountAmount = this.bill * (percent / 100);
        this.bill -= discountAmount;
        return true;
    }

    private void earnPoints(double amountPaid) {
        int pointsEarned = (int) amountPaid;
        this.loyaltyPoints += pointsEarned;
    }
}
