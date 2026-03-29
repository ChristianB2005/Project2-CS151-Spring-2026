package test;

import exceptions.TooManyInstancesException;
import model.Reservation;
import model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Constants;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Reservation.class, "instances");
        resetStatic(Table.class, "numTables");
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    private Reservation makeReservation(String name, int partySize, String tableId)
            throws TooManyInstancesException {
        return new Reservation(name, partySize, tableId);
    }

    // ---------------------------------------------------------------
    // Constructor tests
    // ---------------------------------------------------------------

    @Test
    void constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertEquals("Alice", r.getCustomerName());
        assertEquals(2, r.getPartySize());
        assertEquals("Table1", r.getTableId());
        assertEquals(Constants.RESERVATION_STATUS_PENDING, r.getStatus());
    }

    @Test
    void constructor_throwsForNullCustomerName() {
        assertThrows(IllegalArgumentException.class,
                () -> makeReservation(null, 2, "Table1"));
    }

    @Test
    void constructor_throwsForEmptyCustomerName() {
        assertThrows(IllegalArgumentException.class,
                () -> makeReservation("", 2, "Table1"));
    }

    @Test
    void constructor_throwsForZeroPartySize() {
        assertThrows(IllegalArgumentException.class,
                () -> makeReservation("Alice", 0, "Table1"));
    }

    @Test
    void constructor_throwsForNegativePartySize() {
        assertThrows(IllegalArgumentException.class,
                () -> makeReservation("Alice", -1, "Table1"));
    }

    @Test
    void constructor_throwsForNullTableId() {
        assertThrows(IllegalArgumentException.class,
                () -> makeReservation("Alice", 2, null));
    }

    @Test
    void constructor_throwsForEmptyTableId() {
        assertThrows(IllegalArgumentException.class,
                () -> makeReservation("Alice", 2, ""));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Reservation("Alice", 1, "Table" + i);
        }
        assertThrows(TooManyInstancesException.class,
                () -> makeReservation("Alice", 1, "TableExtra"));
    }

    // ---------------------------------------------------------------
    // confirmReservation tests
    // ---------------------------------------------------------------

    @Test
    void confirmReservation_changeStatusToConfirmed() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertTrue(r.confirmReservation());
        assertEquals(Constants.RESERVATION_STATUS_CONFIRMED, r.getStatus());
    }

    @Test
    void confirmReservation_returnsFalseIfAlreadyConfirmed() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.confirmReservation();
        assertFalse(r.confirmReservation());
    }

    @Test
    void confirmReservation_returnsFalseIfCancelled() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.cancelReservation();
        assertFalse(r.confirmReservation());
    }

    // ---------------------------------------------------------------
    // cancelReservation tests
    // ---------------------------------------------------------------

    @Test
    void cancelReservation_changesStatusToCancelled() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertTrue(r.cancelReservation());
        assertEquals(Constants.RESERVATION_STATUS_CANCELLED, r.getStatus());
    }

    @Test
    void cancelReservation_returnsFalseIfAlreadyCancelled() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.cancelReservation();
        assertFalse(r.cancelReservation());
    }

    @Test
    void cancelReservation_worksFromConfirmedStatus() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.confirmReservation();
        assertTrue(r.cancelReservation());
        assertEquals(Constants.RESERVATION_STATUS_CANCELLED, r.getStatus());
    }

    // ---------------------------------------------------------------
    // updatePartySize tests
    // ---------------------------------------------------------------

    @Test
    void updatePartySize_updatesSuccessfully() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        Table t = new Table(6);
        assertTrue(r.updatePartySize(4, t));
        assertEquals(4, r.getPartySize());
    }

    @Test
    void updatePartySize_returnsFalseIfExceedsCapacity() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        Table t = new Table(3);
        assertFalse(r.updatePartySize(5, t));
        assertEquals(2, r.getPartySize()); // unchanged
    }

    @Test
    void updatePartySize_returnsFalseIfZeroOrNegative() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        Table t = new Table(6);
        assertFalse(r.updatePartySize(0, t));
        assertFalse(r.updatePartySize(-1, t));
    }

    @Test
    void updatePartySize_returnsFalseIfCancelled() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        Table t = new Table(6);
        r.cancelReservation();
        assertFalse(r.updatePartySize(3, t));
        assertEquals(2, r.getPartySize()); // unchanged
    }

    @Test
    void updatePartySize_throwsForNullTable() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertThrows(IllegalArgumentException.class, () -> r.updatePartySize(3, null));
    }

    // ---------------------------------------------------------------
    // isActive tests
    // ---------------------------------------------------------------

    @Test
    void isActive_returnsTrueWhenPending() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertTrue(r.isActive());
    }

    @Test
    void isActive_returnsTrueWhenConfirmed() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.confirmReservation();
        assertTrue(r.isActive());
    }

    @Test
    void isActive_returnsFalseWhenCancelled() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.cancelReservation();
        assertFalse(r.isActive());
    }

    // ---------------------------------------------------------------
    // Setter validation tests
    // ---------------------------------------------------------------

    @Test
    void setCustomerName_throwsForNullOrEmpty() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertThrows(IllegalArgumentException.class, () -> r.setCustomerName(null));
        assertThrows(IllegalArgumentException.class, () -> r.setCustomerName(""));
    }

    @Test
    void setPartySize_throwsForZeroOrNegative() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertThrows(IllegalArgumentException.class, () -> r.setPartySize(0));
        assertThrows(IllegalArgumentException.class, () -> r.setPartySize(-1));
    }

    @Test
    void setTableId_throwsForNullOrEmpty() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertThrows(IllegalArgumentException.class, () -> r.setTableId(null));
        assertThrows(IllegalArgumentException.class, () -> r.setTableId(""));
    }

    @Test
    void setStatus_throwsForInvalidStatus() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        assertThrows(IllegalArgumentException.class, () -> r.setStatus("UNKNOWN"));
    }

    @Test
    void setStatus_acceptsValidStatuses() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        r.setStatus(Constants.RESERVATION_STATUS_CONFIRMED);
        assertEquals(Constants.RESERVATION_STATUS_CONFIRMED, r.getStatus());
        r.setStatus(Constants.RESERVATION_STATUS_CANCELLED);
        assertEquals(Constants.RESERVATION_STATUS_CANCELLED, r.getStatus());
    }

    // ---------------------------------------------------------------
    // getReservationDetails tests
    // ---------------------------------------------------------------

    @Test
    void getReservationDetails_containsAllKeyInfo() throws TooManyInstancesException {
        Reservation r = makeReservation("Alice", 2, "Table1");
        String details = r.getReservationDetails();
        assertTrue(details.contains("Alice"));
        assertTrue(details.contains("Table1"));
        assertTrue(details.contains("Pending"));
    }
}
