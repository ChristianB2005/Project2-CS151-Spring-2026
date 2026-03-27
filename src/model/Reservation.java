package model;

import util.Constants;

public class Reservation {
    private String reservationId;
    private String name;
    private int partySize;
    private String tableId;
    private String status;
    private static int instances = 0;

    public Reservation(String customerName, int partySize, String tableId) {
        if (instances >= Constants.MAXIMUM_INSTANCES) {
            throw new RuntimeException("Maximum number of Reservation instances reached.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be greater than zero.");
        }
        if (tableId == null || tableId.trim().isEmpty()) {
            throw new IllegalArgumentException("Table ID cannot be null or empty.");
        }

        this.name = customerName.trim();
        this.partySize = partySize;
        this.tableId = tableId.trim();
        this.reservationId = "Reservation" + instances;
        this.status = Constants.RESERVATION_STATUS_PENDING;
        instances++;
    }

    public static int getInstances() {
        return instances;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getCustomerName() {
        return name;
    }

    public int getPartySize() {
        return partySize;
    }

    public String getTableId() {
        return tableId;
    }

    public String getStatus() {
        return status;
    }

    public void setReservationId(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be null or empty.");
        }
        this.reservationId = reservationId.trim();
    }

    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
        this.name = customerName.trim();
    }

    public void setPartySize(int partySize) {
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be greater than zero.");
        }
        this.partySize = partySize;
    }

    public void setTableId(String tableId) {
        if (tableId == null || tableId.trim().isEmpty()) {
            throw new IllegalArgumentException("Table ID cannot be null or empty.");
        }
        this.tableId = tableId.trim();
    }

    public void setStatus(String status) {
        if (!status.equals(Constants.RESERVATION_STATUS_PENDING) &&
                !status.equals(Constants.RESERVATION_STATUS_CONFIRMED) &&
                !status.equals(Constants.RESERVATION_STATUS_CANCELLED)) {
            throw new IllegalArgumentException("Invalid reservation status.");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", name='" + name + '\'' +
                ", partySize=" + partySize +
                ", tableId='" + tableId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public boolean confirmReservation() {
        if (!this.status.equals(Constants.RESERVATION_STATUS_PENDING)) {
            return false;
        }
        this.status = Constants.RESERVATION_STATUS_CONFIRMED;
        return true;
    }

    public boolean cancelReservation() {
        if (this.status.equals(Constants.RESERVATION_STATUS_CANCELLED)) {
            return false;
        }
        this.status = Constants.RESERVATION_STATUS_CANCELLED;
        return true;
    }

    public boolean updatePartySize(int newSize, Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null.");
        }
        if (newSize <= 0) {
            return false;
        }
        if (newSize > table.getMaxCapacity()) {
            return false;
        }
        if (this.status.equals(Constants.RESERVATION_STATUS_CANCELLED)) {
            return false;
        }

        this.partySize = newSize;
        return true;
    }

    public boolean isActive() {
        return !status.equals(Constants.RESERVATION_STATUS_CANCELLED);
    }

    public String getReservationDetails() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", name='" + name + '\'' +
                ", partySize=" + partySize +
                ", tableId='" + tableId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}