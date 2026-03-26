package reservation;

import constants.Constants;
import table.Table;

public class Reservation {
    private String reservationId;
    private String name;
    private int partySize;
    private String tableId;
    private String status;
    private static int instances = 0;

    public Reservation(String customerName, int partySize, String tableId) {
        if (instances >= Constants.MAXIMUM_INSTANCES)
            throw new RuntimeException("Maximum number of Reservation instances reached.");
        this.name = customerName;
        this.partySize = partySize;
        this.tableId = tableId;
        this.reservationId = "Reservation" + instances;
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
        return "Reservation:" + name + ":" + reservationId;
    }

    public void confirmReservation() {

    }

    public void cancelReservation() {

    }

    public void updatePartySize(int newSize, Table table) {

    }

    public String getReservationDetails() {
        return null;
    }
}
