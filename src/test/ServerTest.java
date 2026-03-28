package test;

import exceptions.InvalidDiscountException;
import exceptions.InvalidOrderState;
import exceptions.TooManyInstancesException;
import model.Customer;
import model.MenuItem;
import model.Order;
import model.Server;
import model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.OrderStatus;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Server.class, "instanceCount");
        resetStatic(Customer.class, "instances");
        resetStatic(Table.class, "numTables");
        resetStatic(Order.class, "numOrders");
        MenuItem.resetInstanceCount();
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    private Server makeServer() throws TooManyInstancesException {
        return new Server("S1", "Jake", "Section A");
    }

    private Table makeTable(int capacity) throws TooManyInstancesException {
        return new Table(capacity);
    }

    private Customer makeCustomer(String name, int partySize) throws TooManyInstancesException {
        return new Customer(name, partySize);
    }

    private Order makeOrder() throws TooManyInstancesException {
        return new Order();
    }

    private MenuItem makeItem(String id, String name, double price, int stock)
            throws TooManyInstancesException {
        return new MenuItem(id, name, price, "Food", stock);
    }

    // ---------------------------------------------------------------
    // Constructor tests
    // ---------------------------------------------------------------

    @Test
    void constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Server s = makeServer();
        assertEquals("S1", s.getEmployeeId());
        assertEquals("Jake", s.getName());
        assertEquals("Section A", s.getSection());
        assertFalse(s.isOnDuty());
        assertEquals("Off Duty", s.getServerStatus());
        assertEquals(0, s.getTableCount());
    }

    @Test
    void constructor_throwsForNullSection() {
        assertThrows(IllegalArgumentException.class,
                () -> new Server("S1", "Jake", null));
    }

    @Test
    void constructor_throwsForEmptySection() {
        assertThrows(IllegalArgumentException.class,
                () -> new Server("S1", "Jake", ""));
    }

    @Test
    void constructor_throwsForNullEmployeeId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Server(null, "Jake", "Section A"));
    }

    @Test
    void constructor_throwsForNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Server("S1", null, "Section A"));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Server("S" + i, "Server" + i, "Section A");
        }
        assertThrows(TooManyInstancesException.class,
                () -> new Server("S101", "Extra", "Section A"));
    }

    // ---------------------------------------------------------------
    // clockIn tests
    // ---------------------------------------------------------------

    @Test
    void clockIn_setsOnDutyTrue() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        assertTrue(s.isOnDuty());
    }

    @Test
    void clockIn_setsStatusToAvailable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        assertEquals("Available", s.getServerStatus());
    }

    // ---------------------------------------------------------------
    // clockOut tests
    // ---------------------------------------------------------------

    @Test
    void clockOut_setsOnDutyFalse() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.clockOut();
        assertFalse(s.isOnDuty());
    }

    @Test
    void clockOut_setsStatusToOffDuty() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.clockOut();
        assertEquals("Off Duty", s.getServerStatus());
    }

    @Test
    void clockOut_throwsWithAssignedTables() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.assignTable(makeTable(4));
        assertThrows(IllegalStateException.class, s::clockOut);
    }

    // ---------------------------------------------------------------
    // assignTable tests
    // ---------------------------------------------------------------

    @Test
    void assignTable_addsTableAndSetsBusy() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.assignTable(makeTable(4));
        assertEquals(1, s.getTableCount());
        assertEquals("Busy", s.getServerStatus());
    }

    @Test
    void assignTable_allowsMultipleTables() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.assignTable(makeTable(4));
        s.assignTable(makeTable(2));
        assertEquals(2, s.getTableCount());
    }

    @Test
    void assignTable_throwsWhenOffDuty() throws TooManyInstancesException {
        Server s = makeServer();
        assertThrows(IllegalStateException.class, () -> s.assignTable(makeTable(4)));
    }

    @Test
    void assignTable_throwsForNullTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        assertThrows(IllegalArgumentException.class, () -> s.assignTable(null));
    }

    @Test
    void assignTable_throwsForDuplicateTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        assertThrows(IllegalStateException.class, () -> s.assignTable(t));
    }

    // ---------------------------------------------------------------
    // removeTable tests
    // ---------------------------------------------------------------

    @Test
    void removeTable_removesTableSuccessfully() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        s.removeTable(t);
        assertEquals(0, s.getTableCount());
    }

    @Test
    void removeTable_setsStatusToAvailableWhenNoTables() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        s.removeTable(t);
        assertEquals("Available", s.getServerStatus());
    }

    @Test
    void removeTable_throwsForOccupiedTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 1);
        s.seatCustomer(c, t);
        assertThrows(IllegalStateException.class, () -> s.removeTable(t));
    }

    @Test
    void removeTable_throwsForReservedTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        t.reserveTable("Alice");
        assertThrows(IllegalStateException.class, () -> s.removeTable(t));
    }

    @Test
    void removeTable_throwsForUnassignedTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        assertThrows(IllegalStateException.class, () -> s.removeTable(t));
    }

    // ---------------------------------------------------------------
    // seatCustomer tests
    // ---------------------------------------------------------------

    @Test
    void seatCustomer_seatsCustomerAndSetsOccupied() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        assertTrue(t.isOccupied());
        assertTrue(c.getIsSeated());
    }

    @Test
    void seatCustomer_throwsWhenOffDuty() throws TooManyInstancesException {
        Server s = makeServer();
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(c, t));
    }

    @Test
    void seatCustomer_throwsForUnassignedTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(c, t));
    }

    @Test
    void seatCustomer_throwsWhenTableOccupied() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c1 = makeCustomer("Alice", 2);
        Customer c2 = makeCustomer("Bob", 1);
        s.seatCustomer(c1, t);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(c2, t));
    }

    @Test
    void seatCustomer_throwsWhenPartySizeExceedsCapacity() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(2);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 5);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(c, t));
    }

    @Test
    void seatCustomer_throwsForNullCustomer() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        assertThrows(IllegalArgumentException.class, () -> s.seatCustomer(null, t));
    }

    @Test
    void seatCustomer_throwsForNullTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> s.seatCustomer(c, null));
    }

    @Test
    void seatCustomer_throwsWhenTableReservedForSomeoneElse() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        t.reserveTable("Bob");
        Customer alice = makeCustomer("Alice", 2);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(alice, t));
    }

    @Test
    void seatCustomer_allowsReservedCustomerToSit() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        t.reserveTable("Alice");
        Customer alice = makeCustomer("Alice", 2);
        assertDoesNotThrow(() -> s.seatCustomer(alice, t));
    }

    // ---------------------------------------------------------------
    // takeCustomerOrder tests
    // ---------------------------------------------------------------

    @Test
    void takeCustomerOrder_addsItemToOrder()
            throws TooManyInstancesException, InvalidOrderState {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        s.takeCustomerOrder(order, c, item);
        assertEquals(9.99, order.getPrice(), 0.001);
    }

    @Test
    void takeCustomerOrder_decrementsItemStock()
            throws TooManyInstancesException, InvalidOrderState {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        s.takeCustomerOrder(order, c, item);
        assertEquals(4, item.getStockCount());
    }

    @Test
    void takeCustomerOrder_throwsForUnavailableItem() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 0); // out of stock
        assertThrows(IllegalStateException.class,
                () -> s.takeCustomerOrder(order, c, item));
    }

    @Test
    void takeCustomerOrder_throwsWhenCustomerNotSeated() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2); // not seated
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalStateException.class,
                () -> s.takeCustomerOrder(order, c, item));
    }

    @Test
    void takeCustomerOrder_throwsWhenOffDuty() throws TooManyInstancesException {
        Server s = makeServer();
        Customer c = makeCustomer("Alice", 2);
        c.setIsSeated(true);
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalStateException.class,
                () -> s.takeCustomerOrder(order, c, item));
    }

    @Test
    void takeCustomerOrder_throwsForNullOrder() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Customer c = makeCustomer("Alice", 2);
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class,
                () -> s.takeCustomerOrder(null, c, item));
    }

    // ---------------------------------------------------------------
    // applyDiscountToOrder tests
    // ---------------------------------------------------------------

    @Test
    void applyDiscountToOrder_appliesSuccessfully()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Server s = makeServer();
        s.clockIn();
        Order order = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        order.addOrder(c, makeItem("M1", "Burger", 10.00, 5));
        s.applyDiscountToOrder(order, 0.10);
        assertEquals(9.00, order.getPrice(), 0.001);
    }

    @Test
    void applyDiscountToOrder_throwsForNullOrder() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        assertThrows(IllegalArgumentException.class,
                () -> s.applyDiscountToOrder(null, 0.10));
    }

    @Test
    void applyDiscountToOrder_throwsWhenOffDuty() throws TooManyInstancesException {
        Server s = makeServer();
        Order order = makeOrder();
        assertThrows(IllegalStateException.class,
                () -> s.applyDiscountToOrder(order, 0.10));
    }

    // ---------------------------------------------------------------
    // clearTable tests
    // ---------------------------------------------------------------

    @Test
    void clearTable_clearsOccupancy() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 1);
        s.seatCustomer(c, t);
        s.clearTable(t);
        assertFalse(t.isOccupied());
        assertEquals(0, t.getCustomerCount());
    }

    @Test
    void clearTable_throwsForUnassignedTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        Table t = makeTable(4);
        assertThrows(IllegalStateException.class, () -> s.clearTable(t));
    }

    @Test
    void clearTable_throwsForNullTable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        assertThrows(IllegalArgumentException.class, () -> s.clearTable(null));
    }

    // ---------------------------------------------------------------
    // viewAssignedTables tests
    // ---------------------------------------------------------------

    @Test
    void viewAssignedTables_returnsNoTablesMessageWhenEmpty() throws TooManyInstancesException {
        Server s = makeServer();
        assertTrue(s.viewAssignedTables().contains("No tables"));
    }

    @Test
    void viewAssignedTables_listsAssignedTables() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.assignTable(makeTable(4));
        String result = s.viewAssignedTables();
        assertTrue(result.contains("Jake"));
        assertTrue(result.contains("1."));
    }

    // ---------------------------------------------------------------
    // updateStatus tests
    // ---------------------------------------------------------------

    @Test
    void updateStatus_setsAvailable() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.updateStatus("Available");
        assertEquals("Available", s.getServerStatus());
    }

    @Test
    void updateStatus_setsBusy() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.updateStatus("Busy");
        assertEquals("Busy", s.getServerStatus());
    }

    @Test
    void updateStatus_setsOffDutyWithNoTables() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.updateStatus("Off Duty");
        assertEquals("Off Duty", s.getServerStatus());
        assertFalse(s.isOnDuty());
    }

    @Test
    void updateStatus_throwsOffDutyWithAssignedTables() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.assignTable(makeTable(4));
        assertThrows(IllegalStateException.class, () -> s.updateStatus("Off Duty"));
    }

    @Test
    void updateStatus_throwsForInvalidStatus() throws TooManyInstancesException {
        Server s = makeServer();
        assertThrows(IllegalArgumentException.class, () -> s.updateStatus("Napping"));
    }

    @Test
    void updateStatus_throwsForNullOrEmptyStatus() throws TooManyInstancesException {
        Server s = makeServer();
        assertThrows(IllegalArgumentException.class, () -> s.updateStatus(null));
        assertThrows(IllegalArgumentException.class, () -> s.updateStatus(""));
    }

    // ---------------------------------------------------------------
    // hasAssignedTables tests
    // ---------------------------------------------------------------

    @Test
    void hasAssignedTables_returnsFalseInitially() throws TooManyInstancesException {
        Server s = makeServer();
        assertFalse(s.hasAssignedTables());
    }

    @Test
    void hasAssignedTables_returnsTrueAfterAssignment() throws TooManyInstancesException {
        Server s = makeServer();
        s.clockIn();
        s.assignTable(makeTable(4));
        assertTrue(s.hasAssignedTables());
    }
}
