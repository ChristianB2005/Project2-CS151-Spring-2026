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
        if (instances >= Constants.MAXIMUM_INSTANCES)
            throw new RuntimeException("Maximum number of model.Reservation instances reached.");
        this.name = customerName;
        this.partySize = partySize;
        this.tableId = tableId;
        this.reservationId = "model.Reservation" + instances;
        this.status = Constants.RESERVATION_STATUS_PENDING;
        instances++;
    }

    public static int getInstances() { return instances; }
    public String getReservationId() { return reservationId; }
    public String getCustomerName() { return name; }
    public int getPartySize() { return partySize; }
    public String getTableId() { return tableId; }
    public String getStatus() { return status; }

    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public void setCustomerName(String customerName) { this.name = customerName; }
    public void setPartySize(int partySize) { this.partySize = partySize; }
    public void setTableId(String tableId) { this.tableId = tableId; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "model.Reservation:" + name + ":" + reservationId;
    }

    public boolean confirmReservation() {
        if (!this.status.equals(Constants.RESERVATION_STATUS_PENDING))
            return false;
        this.status = Constants.RESERVATION_STATUS_CONFIRMED;
        return true;
    }

    public boolean cancelReservation() {
        if (this.status.equals(Constants.RESERVATION_STATUS_CANCELLED))
            return false;
        this.status = Constants.RESERVATION_STATUS_CANCELLED;
        return true;
    }

    public boolean updatePartySize(int newSize, Table table) {
        if (newSize <= 0) {
            return false;
        }
        if (newSize > table.getCapacity()) {
            return false;
        }
        if (this.status.equals("CANCELLED")) {
            return false;
        }
        this.partySize = newSize;
        return true;
    }

    public String getReservationDetails() {
        return "model.Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", name='" + name + '\'' +
                ", partySize=" + partySize +
                ", tableId='" + tableId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
