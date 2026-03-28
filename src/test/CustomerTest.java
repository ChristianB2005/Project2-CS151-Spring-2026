package test;

import exceptions.ReservationException;
import exceptions.TooManyInstancesException;
import model.Customer;
import model.Order;
import model.Reservation;
import model.MenuItem;
import model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.OrderStatus;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Customer.class, "instances");
        resetStatic(Order.class, "numOrders");
        resetStatic(Reservation.class, "instances");
        resetStatic(Table.class, "numTables");
        model.MenuItem.resetInstanceCount();
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    private Customer makeCustomer(String name, int partySize) throws TooManyInstancesException {
        return new Customer(name, partySize);
    }

    private Order makeOrder() throws TooManyInstancesException {
        return new Order();
    }

    // ---------------------------------------------------------------
    // Constructor tests
    // ---------------------------------------------------------------

    @Test
    void constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertEquals("Alice", c.getName());
        assertEquals(2, c.getPartySize());
        assertEquals(0, c.getLoyaltyPoints());
        assertEquals(0.0, c.getBill());
        assertFalse(c.getIsSeated());
        assertNull(c.getReservedTable());
        assertNull(c.getActiveReservation());
    }

    @Test
    void constructor_throwsForNullName() {
        assertThrows(IllegalArgumentException.class, () -> makeCustomer(null, 2));
    }

    @Test
    void constructor_throwsForEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> makeCustomer("", 2));
    }

    @Test
    void constructor_throwsForZeroPartySize() {
        assertThrows(IllegalArgumentException.class, () -> makeCustomer("Alice", 0));
    }

    @Test
    void constructor_throwsForNegativePartySize() {
        assertThrows(IllegalArgumentException.class, () -> makeCustomer("Alice", -1));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Customer("Customer" + i, 1);
        }
        assertThrows(TooManyInstancesException.class, () -> new Customer("Extra", 1));
    }

    // ---------------------------------------------------------------
    // Setter validation tests
    // ---------------------------------------------------------------

    @Test
    void setPartySize_throwsForZero() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setPartySize(0));
    }

    @Test
    void setPartySize_throwsForNegative() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setPartySize(-1));
    }

    @Test
    void setBill_throwsForNegative() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setBill(-5.0));
    }

    @Test
    void setBill_allowsZero() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(0.0);
        assertEquals(0.0, c.getBill());
    }

    @Test
    void setLoyaltyPoints_throwsForNegative() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setLoyaltyPoints(-1));
    }

    @Test
    void setName_throwsForNullOrEmpty() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setName(null));
        assertThrows(IllegalArgumentException.class, () -> c.setName(""));
    }

    // ---------------------------------------------------------------
    // payBill tests
    // ---------------------------------------------------------------

    @Test
    void payBill_clearsAndEarnsPoints() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(50.0);
        Order order = makeOrder();
        order.setOrderStatus(OrderStatus.READY);
        assertTrue(c.payBill(order));
        assertEquals(0.0, c.getBill());
        assertEquals(50, c.getLoyaltyPoints());
    }

    @Test
    void payBill_returnsFalseWhenOrderNotReady() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(50.0);
        Order order = makeOrder();
        // Default status is TAKING_ORDER
        assertFalse(c.payBill(order));
        assertEquals(50.0, c.getBill()); // bill unchanged
    }

    @Test
    void payBill_returnsFalseWhenOrderInKitchen() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(50.0);
        Order order = makeOrder();
        order.setOrderStatus(OrderStatus.IN_KITCHEN);
        assertFalse(c.payBill(order));
    }

    @Test
    void payBill_throwsForNullOrder() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.payBill(null));
    }

    @Test
    void payBill_zeroBill_earnsZeroPoints() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        Order order = makeOrder();
        order.setOrderStatus(OrderStatus.READY);
        c.payBill(order);
        assertEquals(0, c.getLoyaltyPoints());
    }

    // ---------------------------------------------------------------
    // applyDiscount tests
    // ---------------------------------------------------------------

    @Test
    void applyDiscount_reducesBillByPercentage() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(100.0);
        assertTrue(c.applyDiscount(0.10));
        assertEquals(90.0, c.getBill(), 0.001);
    }

    @Test
    void applyDiscount_fullDiscount_setsBillToZero() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(100.0);
        assertTrue(c.applyDiscount(1.0));
        assertEquals(0.0, c.getBill(), 0.001);
    }

    @Test
    void applyDiscount_returnsFalseForNegativePercentage() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(100.0);
        assertFalse(c.applyDiscount(-0.1));
        assertEquals(100.0, c.getBill()); // bill unchanged
    }

    @Test
    void applyDiscount_returnsFalseForPercentageOverOne() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(100.0);
        assertFalse(c.applyDiscount(1.5));
    }

    @Test
    void applyDiscount_returnsFalseForZeroBill() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertFalse(c.applyDiscount(0.10));
    }

    // ---------------------------------------------------------------
    // cancelReservation tests
    // ---------------------------------------------------------------

    @Test
    void cancelReservation_throwsForNullReservation() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.cancelReservation(null));
    }

    @Test
    void cancelReservation_throwsForWrongCustomer() throws TooManyInstancesException {
        Customer alice = makeCustomer("Alice", 2);
        Reservation reservation = new Reservation("Bob", 2, "Table1");
        assertThrows(RuntimeException.class, () -> alice.cancelReservation(reservation));
    }

    @Test
    void cancelReservation_returnsTrueIfAlreadyCancelled() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        Reservation reservation = new Reservation("Alice", 2, "Table1");
        reservation.cancelReservation();
        assertTrue(c.cancelReservation(reservation));
    }

    @Test
    void cancelReservation_cancelsActiveReservation() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        Reservation reservation = new Reservation("Alice", 2, "Table1");
        reservation.confirmReservation();
        assertTrue(c.cancelReservation(reservation));
        assertEquals("Cancelled", reservation.getStatus());
    }

    // ---------------------------------------------------------------
    // makeReservation tests
    // ---------------------------------------------------------------

    @Test
    void makeReservation_throwsForNullTable() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class,
                () -> c.makeReservation(null));
    }

    @Test
    void makeReservation_returnsFalseWhenTableOccupied()
            throws TooManyInstancesException, ReservationException {
        Customer c = makeCustomer("Alice", 2);
        Table t = new Table(4);
        Customer other = makeCustomer("Bob", 1);
        t.addCustomer(other);
        assertFalse(c.makeReservation(t));
    }

    @Test
    void makeReservation_returnsFalseWhenPartySizeExceedsCapacity()
            throws TooManyInstancesException, ReservationException {
        Customer c = makeCustomer("Alice", 5);
        Table t = new Table(2);
        assertFalse(c.makeReservation(t));
    }
}
