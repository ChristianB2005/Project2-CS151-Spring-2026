package test;

import exceptions.TooManyInstancesException;
import model.Customer;
import model.Server;
import model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Table.class, "numTables");
        resetStatic(Customer.class, "instances");
        resetStatic(Server.class, "instanceCount");
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    private Table makeTable(int capacity) throws TooManyInstancesException {
        return new Table(capacity);
    }

    private Customer makeCustomer(String name, int partySize) throws TooManyInstancesException {
        return new Customer(name, partySize);
    }

    // ---------------------------------------------------------------
    // Constructor tests
    // ---------------------------------------------------------------

    @Test
    void constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertEquals(4, t.getMaxCapacity());
        assertFalse(t.isOccupied());
        assertFalse(t.isReserved());
        assertNull(t.getReservedForName());
        assertEquals(0, t.getCustomerCount());
        assertNull(t.getServer());
    }

    @Test
    void constructor_assignsUniqueTableIds() throws TooManyInstancesException {
        Table t1 = makeTable(4);
        Table t2 = makeTable(4);
        assertNotEquals(t1.getTableID(), t2.getTableID());
    }

    @Test
    void constructor_throwsForZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Table(0));
    }

    @Test
    void constructor_throwsForNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Table(-1));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Table(4);
        }
        assertThrows(TooManyInstancesException.class, () -> new Table(4));
    }

    // ---------------------------------------------------------------
    // addCustomer tests
    // ---------------------------------------------------------------

    @Test
    void addCustomer_seatsCustomerAndSetsOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 2);
        t.addCustomer(c);
        assertTrue(t.isOccupied());
        assertEquals(1, t.getCustomerCount());
    }

    @Test
    void addCustomer_allowsMultipleCustomersUpToCapacity() throws TooManyInstancesException {
        Table t = makeTable(3);
        t.addCustomer(makeCustomer("Alice", 1));
        t.addCustomer(makeCustomer("Bob", 1));
        t.addCustomer(makeCustomer("Carol", 1));
        assertEquals(3, t.getCustomerCount());
    }

    @Test
    void addCustomer_throwsWhenAtCapacity() throws TooManyInstancesException {
        Table t = makeTable(1);
        t.addCustomer(makeCustomer("Alice", 1));
        assertThrows(IllegalStateException.class,
                () -> t.addCustomer(makeCustomer("Bob", 1)));
    }

    @Test
    void addCustomer_throwsForDuplicateCustomer() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        assertThrows(IllegalStateException.class, () -> t.addCustomer(c));
    }

    @Test
    void addCustomer_throwsForNullCustomer() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertThrows(IllegalArgumentException.class, () -> t.addCustomer(null));
    }

    @Test
    void addCustomer_clearsReservationWhenReservedCustomerArrives() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        assertFalse(t.isReserved());
        assertNull(t.getReservedForName());
    }

    // ---------------------------------------------------------------
    // removeCustomer tests
    // ---------------------------------------------------------------

    @Test
    void removeCustomer_removesCustomerSuccessfully() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        t.removeCustomer(c);
        assertEquals(0, t.getCustomerCount());
    }

    @Test
    void removeCustomer_setsUnoccupiedWhenEmpty() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        t.removeCustomer(c);
        assertFalse(t.isOccupied());
    }

    @Test
    void removeCustomer_tableRemainsOccupiedWithOtherCustomers() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c1 = makeCustomer("Alice", 1);
        Customer c2 = makeCustomer("Bob", 1);
        t.addCustomer(c1);
        t.addCustomer(c2);
        t.removeCustomer(c1);
        assertTrue(t.isOccupied());
        assertEquals(1, t.getCustomerCount());
    }

    @Test
    void removeCustomer_throwsForCustomerNotAtTable() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        assertThrows(IllegalStateException.class, () -> t.removeCustomer(c));
    }

    @Test
    void removeCustomer_throwsForNullCustomer() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertThrows(IllegalArgumentException.class, () -> t.removeCustomer(null));
    }

    // ---------------------------------------------------------------
    // reserveTable tests
    // ---------------------------------------------------------------

    @Test
    void reserveTable_setsReservedAndName() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        assertTrue(t.isReserved());
        assertEquals("Alice", t.getReservedForName());
    }

    @Test
    void reserveTable_throwsWhenAlreadyReserved() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        assertThrows(IllegalStateException.class, () -> t.reserveTable("Bob"));
    }

    @Test
    void reserveTable_throwsWhenOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.addCustomer(makeCustomer("Alice", 1));
        assertThrows(IllegalStateException.class, () -> t.reserveTable("Bob"));
    }

    @Test
    void reserveTable_throwsForNullOrEmptyName() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertThrows(IllegalArgumentException.class, () -> t.reserveTable(null));
        assertThrows(IllegalArgumentException.class, () -> t.reserveTable(""));
    }

    // ---------------------------------------------------------------
    // cancelTableReservation tests
    // ---------------------------------------------------------------

    @Test
    void cancelTableReservation_clearsReservation() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        t.cancelTableReservation();
        assertFalse(t.isReserved());
        assertNull(t.getReservedForName());
    }

    // ---------------------------------------------------------------
    // clearOccupancy tests
    // ---------------------------------------------------------------

    @Test
    void clearOccupancy_resetsAllState() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        t.reserveTable("Bob");
        t.clearOccupancy();
        assertFalse(t.isOccupied());
        assertFalse(t.isReserved());
        assertNull(t.getReservedForName());
        assertEquals(0, t.getCustomerCount());
        assertNull(t.getServer());
    }

    // ---------------------------------------------------------------
    // canSeatParty tests
    // ---------------------------------------------------------------

    @Test
    void canSeatParty_returnsTrueForValidParty() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertTrue(t.canSeatParty(3));
    }

    @Test
    void canSeatParty_returnsFalseWhenOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.addCustomer(makeCustomer("Alice", 1));
        assertFalse(t.canSeatParty(2));
    }

    @Test
    void canSeatParty_returnsFalseWhenReserved() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        assertFalse(t.canSeatParty(2));
    }

    @Test
    void canSeatParty_returnsFalseWhenExceedsCapacity() throws TooManyInstancesException {
        Table t = makeTable(2);
        assertFalse(t.canSeatParty(5));
    }

    @Test
    void canSeatParty_returnsFalseForZeroOrNegativePartySize() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertFalse(t.canSeatParty(0));
        assertFalse(t.canSeatParty(-1));
    }

    // ---------------------------------------------------------------
    // assignServer tests
    // ---------------------------------------------------------------

    @Test
    void assignServer_setsServer() throws TooManyInstancesException {
        Table t = makeTable(4);
        Server s = new Server("S1", "Jake", "Section A");
        t.assignServer(s);
        assertEquals(s, t.getServer());
        assertTrue(t.hasAssignedServer());
    }

    @Test
    void assignServer_allowsNull_clearsServer() throws TooManyInstancesException {
        Table t = makeTable(4);
        Server s = new Server("S1", "Jake", "Section A");
        t.assignServer(s);
        t.assignServer(null);
        assertFalse(t.hasAssignedServer());
    }

    // ---------------------------------------------------------------
    // setMaxCapacity tests
    // ---------------------------------------------------------------

    @Test
    void setMaxCapacity_updatesCapacity() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.setMaxCapacity(6);
        assertEquals(6, t.getMaxCapacity());
    }

    @Test
    void setMaxCapacity_throwsForZeroOrNegative() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertThrows(IllegalArgumentException.class, () -> t.setMaxCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> t.setMaxCapacity(-1));
    }

    @Test
    void setMaxCapacity_throwsWhenSmallerThanCurrentPartySize() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.addCustomer(makeCustomer("Alice", 1));
        t.addCustomer(makeCustomer("Bob", 1));
        t.addCustomer(makeCustomer("Carol", 1));
        assertThrows(IllegalArgumentException.class, () -> t.setMaxCapacity(2));
    }
}
