package model;

import util.Constants;
import util.OrderStatus;

public class Customer {
    private String customerID;
    private String name;
    private int partySize;
    private int loyaltyPoints;
    private double bill;
    private boolean isSeated;
    private Table reservedTable;
    private Reservation activeReservation;
    private static int instances = 0;

    public Customer(String name, int partySize) {
        if (instances >= Constants.MAXIMUM_INSTANCES) {
            throw new RuntimeException("Maximum number of Customer instances reached.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be greater than zero.");
        }

        this.customerID = "Customer" + instances;
        this.name = name.trim();
        this.partySize = partySize;
        this.loyaltyPoints = 0;
        this.bill = 0.0;
        this.isSeated = false;
        this.reservedTable = null;
        this.activeReservation = null;
        instances++;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public int getPartySize() {
        return partySize;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public double getBill() {
        return bill;
    }

    public boolean getIsSeated() {
        return isSeated;
    }

    public Table getReservedTable() {
        return reservedTable;
    }

    public Reservation getActiveReservation() {
        return activeReservation;
    }

    public void setCustomerID(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty.");
        }
        customerID = id.trim();
    }

    public void setName(String nameInput) {
        if (nameInput == null || nameInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
        name = nameInput.trim();
    }

    public void setPartySize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Party size must be greater than zero.");
        }
        partySize = size;
    }

    public void setLoyaltyPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Loyalty points cannot be negative.");
        }
        loyaltyPoints = points;
    }

    public void setBill(double billInput) {
        if (billInput < 0) {
            throw new IllegalArgumentException("Bill cannot be negative.");
        }
        bill = billInput;
    }

    public void setIsSeated(boolean input) {
        isSeated = input;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID='" + customerID + '\'' +
                ", name='" + name + '\'' +
                ", partySize=" + partySize +
                ", loyaltyPoints=" + loyaltyPoints +
                ", bill=" + bill +
                ", isSeated=" + isSeated +
                ", hasReservation=" + (activeReservation != null) +
                '}';
    }

    public boolean makeReservation(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }
        if (activeReservation != null && !activeReservation.getStatus().equals(Constants.RESERVATION_STATUS_CANCELLED)) {
            return false;
        }
        if (table.isOccupied() || table.isReserved()) {
            return false;
        }
        if (this.partySize > table.getMaxCapacity()) {
            return false;
        }
        if (Reservation.getInstances() >= Constants.MAXIMUM_INSTANCES) {
            return false;
        }

        Reservation reservation = new Reservation(this.name, this.partySize, table.getTableID());
        if (!reservation.confirmReservation()) {
            return false;
        }

        this.activeReservation = reservation;
        this.reservedTable = table;
        this.isSeated = false;
        table.reserveTable(this.name);

        return true;
    }

    public boolean cancelReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        if (!reservation.getCustomerName().equals(this.name)) {
            throw new RuntimeException("Cancelling wrong reservation.");
        }
        if (reservation.getStatus().equals(Constants.RESERVATION_STATUS_CANCELLED)) {
            return false;
        }

        boolean cancelled = reservation.cancelReservation();
        if (!cancelled) {
            return false;
        }

        if (reservedTable != null && reservedTable.getTableID().equals(reservation.getTableId())) {
            reservedTable.cancelTableReservation();
        }

        this.activeReservation = null;
        this.reservedTable = null;
        this.isSeated = false;
        return true;
    }

    public boolean payBill(Order order) {
        if (order == null) {
            throw new RuntimeException("No order provided for Customer to pay bill.");
        }
        if (!order.getOrderStatus().equals(OrderStatus.READY)) {
            return false;
        }

        double amount = order.getPrice();
        if (amount <= 0) {
            this.bill = 0.0;
            return true;
        }

        this.bill = 0.0;
        earnPoints(amount);
        return true;
    }

    public boolean applyDiscount(double percent) {
        if (percent <= 0 || percent > 100) {
            return false;
        }
        if (this.bill <= 0) {
            return false;
        }

        double discountAmount = this.bill * (percent / 100.0);
        this.bill -= discountAmount;
        return true;
    }

    private void earnPoints(double amountPaid) {
        int pointsEarned = (int) amountPaid;
        this.loyaltyPoints += pointsEarned;
    }
}