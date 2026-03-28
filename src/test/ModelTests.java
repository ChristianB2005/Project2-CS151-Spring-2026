package test;

import exceptions.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.OrderStatus;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for all model classes.
 *
 * SETUP NOTE: Because each class tracks a static instance count, tests
 * must reset those counters before each test to avoid false
 * TooManyInstancesException failures. The resetStatic() helper below
 * uses reflection to do this — no changes to production code needed.
 *
 * To run: add JUnit 5 (junit-jupiter) to your project dependencies.
 * In IntelliJ: File > Project Structure > Libraries > Add JUnit 5.
 * In VS Code: add the JUnit extension and junit-jupiter jar.
 */
public class ModelTests {

    // ---------------------------------------------------------------
    // Static counter reset — runs before every test
    // ---------------------------------------------------------------

    @BeforeEach
    void resetAllInstanceCounts() throws Exception {
        resetStatic(Customer.class, "instances");
        resetStatic(Reservation.class, "instances");
        resetStatic(Table.class, "numTables");
        resetStatic(Order.class, "numOrders");
        resetStatic(Chef.class, "instanceCount");
        resetStatic(Kitchen.class, "instanceCount");
        resetStatic(Server.class, "instanceCount");
        MenuItem.resetInstanceCount(); // MenuItem already provides this method
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    // ---------------------------------------------------------------
    // Helper factories — keeps each test concise
    // ---------------------------------------------------------------

    private MenuItem makeItem(String id, String name, double price, int stock)
            throws TooManyInstancesException {
        return new MenuItem(id, name, price, "Food", stock);
    }

    private Customer makeCustomer(String name, int partySize)
            throws TooManyInstancesException {
        return new Customer(name, partySize);
    }

    private Table makeTable(int capacity) throws TooManyInstancesException {
        return new Table(capacity);
    }

    private Order makeOrder() throws TooManyInstancesException {
        return new Order();
    }

    private Chef makeChef(String id, String name) throws TooManyInstancesException {
        return new Chef(id, name, "Grill");
    }

    private Kitchen makeKitchen(int capacity) throws TooManyInstancesException {
        return new Kitchen(capacity);
    }

    private Server makeServer(String id, String name) throws TooManyInstancesException {
        return new Server(id, name, "Section A");
    }

    // ===============================================================
    // MenuItem tests
    // ===============================================================

    @Test
    void menuItem_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 10);
        assertEquals("M1", item.getItemId());
        assertEquals("Burger", item.getName());
        assertEquals(9.99, item.getPrice());
        assertTrue(item.isAvailable());
        assertEquals(10, item.getStockCount());
    }

    @Test
    void menuItem_zeroStock_marksUnavailable() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 0);
        assertFalse(item.isAvailable());
    }

    @Test
    void menuItem_decrementStock_reducesCount() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 3);
        item.decrementStock();
        assertEquals(2, item.getStockCount());
    }

    @Test
    void menuItem_decrementStock_marksUnavailableAtZero() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 1);
        item.decrementStock();
        assertFalse(item.isAvailable());
        assertEquals(0, item.getStockCount());
    }

    @Test
    void menuItem_decrementStock_throwsWhenOutOfStock() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 0);
        assertThrows(IllegalStateException.class, item::decrementStock);
    }

    @Test
    void menuItem_restock_increasesCountAndMarksAvailable() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 0);
        item.restock(5);
        assertEquals(5, item.getStockCount());
        assertTrue(item.isAvailable());
    }

    @Test
    void menuItem_restock_throwsForNonPositiveQuantity() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.restock(0));
        assertThrows(IllegalArgumentException.class, () -> item.restock(-1));
    }

    @Test
    void menuItem_markUnavailable_setsFlag() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.markUnavailable();
        assertFalse(item.isAvailable());
    }

    @Test
    void menuItem_updatePrice_setsNewPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.updatePrice(12.50);
        assertEquals(12.50, item.getPrice());
    }

    @Test
    void menuItem_updatePrice_throwsForNegative() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.updatePrice(-1));
    }

    @Test
    void menuItem_applyDiscount_reducesPrice() throws TooManyInstancesException, InvalidDiscountException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        item.applyDiscount(0.10); // 10% off
        assertEquals(9.00, item.getPrice(), 0.001);
    }

    @Test
    void menuItem_applyDiscount_throwsForOutOfRange() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        assertThrows(InvalidDiscountException.class, () -> item.applyDiscount(-0.1));
        assertThrows(InvalidDiscountException.class, () -> item.applyDiscount(1.5));
    }

    @Test
    void menuItem_applyFlatDiscount_reducesPrice() throws TooManyInstancesException, InvalidDiscountException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        item.applyFlatDiscount(2.00);
        assertEquals(8.00, item.getPrice(), 0.001);
    }

    @Test
    void menuItem_applyFlatDiscount_throwsWhenExceedsPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 5.00, 5);
        assertThrows(InvalidDiscountException.class, () -> item.applyFlatDiscount(10.00));
    }

    @Test
    void menuItem_applyModifier_returnsAdjustedPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        double modified = item.applyModifier(2.50);
        assertEquals(12.50, modified, 0.001);
    }

    @Test
    void menuItem_constructor_throwsForNullOrEmptyFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem(null, "Burger", 9.99, "Food", 5));
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "", 9.99, "Food", 5));
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "Burger", -1, "Food", 5));
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "Burger", 9.99, "Food", -1));
    }

    @Test
    void menuItem_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new MenuItem("M" + i, "Item" + i, 1.00, "Food", 1);
        }
        assertThrows(TooManyInstancesException.class,
                () -> new MenuItem("M101", "Extra", 1.00, "Food", 1));
    }

    // ===============================================================
    // Customer tests
    // ===============================================================

    @Test
    void customer_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertEquals("Alice", c.getName());
        assertEquals(2, c.getPartySize());
        assertEquals(0, c.getLoyaltyPoints());
        assertEquals(0.0, c.getBill());
        assertFalse(c.getIsSeated());
    }

    @Test
    void customer_constructor_throwsForInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> makeCustomer("", 2));
        assertThrows(IllegalArgumentException.class, () -> makeCustomer("Alice", 0));
        assertThrows(IllegalArgumentException.class, () -> makeCustomer("Alice", -1));
    }

    @Test
    void customer_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Customer("Customer" + i, 1);
        }
        assertThrows(TooManyInstancesException.class, () -> new Customer("Extra", 1));
    }

    @Test
    void customer_setPartySize_throwsForNonPositive() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setPartySize(0));
        assertThrows(IllegalArgumentException.class, () -> c.setPartySize(-1));
    }

    @Test
    void customer_setBill_throwsForNegative() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.setBill(-5.0));
    }

    @Test
    void customer_payBill_clearsAndEarnsPoints() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(50.0);
        Order order = makeOrder();
        order.setOrderStatus(OrderStatus.READY);
        boolean result = c.payBill(order);
        assertTrue(result);
        assertEquals(0.0, c.getBill());
        assertEquals(50, c.getLoyaltyPoints());
    }

    @Test
    void customer_payBill_returnsFalseIfOrderNotReady() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(50.0);
        Order order = makeOrder();
        // Order status defaults to TAKING_ORDER
        boolean result = c.payBill(order);
        assertFalse(result);
    }

    @Test
    void customer_payBill_throwsForNullOrder() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.payBill(null));
    }

    @Test
    void customer_applyDiscount_reducesBill() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(100.0);
        boolean result = c.applyDiscount(0.10); // 10% off
        assertTrue(result);
        assertEquals(90.0, c.getBill(), 0.001);
    }

    @Test
    void customer_applyDiscount_returnsFalseForInvalidRange() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        c.setBill(100.0);
        assertFalse(c.applyDiscount(-0.1));
        assertFalse(c.applyDiscount(1.5));
    }

    @Test
    void customer_applyDiscount_returnsFalseForZeroBill() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertFalse(c.applyDiscount(0.10));
    }

    @Test
    void customer_cancelReservation_throwsForWrongCustomer()
            throws TooManyInstancesException {
        Customer alice = makeCustomer("Alice", 2);
        Reservation reservation = new Reservation("Bob", 2, "Table1");
        assertThrows(RuntimeException.class, () -> alice.cancelReservation(reservation));
    }

    @Test
    void customer_cancelReservation_throwsForNullReservation() throws TooManyInstancesException {
        Customer c = makeCustomer("Alice", 2);
        assertThrows(IllegalArgumentException.class, () -> c.cancelReservation(null));
    }

    // ===============================================================
    // Reservation tests
    // ===============================================================

    @Test
    void reservation_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        assertEquals("Alice", r.getCustomerName());
        assertEquals(2, r.getPartySize());
        assertEquals("Table1", r.getTableId());
        assertEquals("Pending", r.getStatus());
    }

    @Test
    void reservation_constructor_throwsForInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(null, 2, "Table1"));
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation("Alice", 0, "Table1"));
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation("Alice", 2, ""));
    }

    @Test
    void reservation_confirmReservation_changesStatus() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        assertTrue(r.confirmReservation());
        assertEquals("Confirmed", r.getStatus());
    }

    @Test
    void reservation_confirmReservation_returnsFalseIfNotPending() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        r.confirmReservation();
        assertFalse(r.confirmReservation()); // already confirmed
    }

    @Test
    void reservation_cancelReservation_changesStatus() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        assertTrue(r.cancelReservation());
        assertEquals("Cancelled", r.getStatus());
    }

    @Test
    void reservation_cancelReservation_returnsFalseIfAlreadyCancelled() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        r.cancelReservation();
        assertFalse(r.cancelReservation());
    }

    @Test
    void reservation_updatePartySize_updatesSuccessfully() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        Table table = makeTable(6);
        assertTrue(r.updatePartySize(4, table));
        assertEquals(4, r.getPartySize());
    }

    @Test
    void reservation_updatePartySize_returnsFalseIfExceedsCapacity() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        Table table = makeTable(3);
        assertFalse(r.updatePartySize(5, table));
    }

    @Test
    void reservation_updatePartySize_returnsFalseIfCancelled() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        Table table = makeTable(6);
        r.cancelReservation();
        assertFalse(r.updatePartySize(3, table));
    }

    @Test
    void reservation_isActive_returnsFalseWhenCancelled() throws TooManyInstancesException {
        Reservation r = new Reservation("Alice", 2, "Table1");
        assertTrue(r.isActive());
        r.cancelReservation();
        assertFalse(r.isActive());
    }

    @Test
    void reservation_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Reservation("Alice", 1, "Table" + i);
        }
        assertThrows(TooManyInstancesException.class,
                () -> new Reservation("Alice", 1, "TableExtra"));
    }

    // ===============================================================
    // Table tests
    // ===============================================================

    @Test
    void table_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertEquals(4, t.getMaxCapacity());
        assertFalse(t.isOccupied());
        assertFalse(t.isReserved());
        assertEquals(0, t.getCustomerCount());
    }

    @Test
    void table_constructor_throwsForNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Table(0));
        assertThrows(IllegalArgumentException.class, () -> new Table(-1));
    }

    @Test
    void table_addCustomer_seatsCustomerAndSetsOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 2);
        t.addCustomer(c);
        assertTrue(t.isOccupied());
        assertEquals(1, t.getCustomerCount());
    }

    @Test
    void table_addCustomer_throwsWhenAtCapacity() throws TooManyInstancesException {
        Table t = makeTable(1);
        Customer c1 = makeCustomer("Alice", 1);
        Customer c2 = makeCustomer("Bob", 1);
        t.addCustomer(c1);
        assertThrows(IllegalStateException.class, () -> t.addCustomer(c2));
    }

    @Test
    void table_addCustomer_throwsForDuplicateCustomer() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        assertThrows(IllegalStateException.class, () -> t.addCustomer(c));
    }

    @Test
    void table_removeCustomer_removesAndClearsOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        t.removeCustomer(c);
        assertFalse(t.isOccupied());
        assertEquals(0, t.getCustomerCount());
    }

    @Test
    void table_removeCustomer_throwsForCustomerNotAtTable() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        assertThrows(IllegalStateException.class, () -> t.removeCustomer(c));
    }

    @Test
    void table_reserveTable_setsReserved() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        assertTrue(t.isReserved());
        assertEquals("Alice", t.getReservedForName());
    }

    @Test
    void table_reserveTable_throwsWhenAlreadyReserved() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        assertThrows(IllegalStateException.class, () -> t.reserveTable("Bob"));
    }

    @Test
    void table_reserveTable_throwsWhenOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        assertThrows(IllegalStateException.class, () -> t.reserveTable("Bob"));
    }

    @Test
    void table_cancelTableReservation_clearsReservation() throws TooManyInstancesException {
        Table t = makeTable(4);
        t.reserveTable("Alice");
        t.cancelTableReservation();
        assertFalse(t.isReserved());
        assertNull(t.getReservedForName());
    }

    @Test
    void table_clearOccupancy_resetsAllState() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        t.clearOccupancy();
        assertFalse(t.isOccupied());
        assertFalse(t.isReserved());
        assertEquals(0, t.getCustomerCount());
    }

    @Test
    void table_canSeatParty_returnsTrueForValidParty() throws TooManyInstancesException {
        Table t = makeTable(4);
        assertTrue(t.canSeatParty(3));
    }

    @Test
    void table_canSeatParty_returnsFalseWhenOccupied() throws TooManyInstancesException {
        Table t = makeTable(4);
        Customer c = makeCustomer("Alice", 1);
        t.addCustomer(c);
        assertFalse(t.canSeatParty(2));
    }

    @Test
    void table_canSeatParty_returnsFalseWhenExceedsCapacity() throws TooManyInstancesException {
        Table t = makeTable(2);
        assertFalse(t.canSeatParty(5));
    }

    @Test
    void table_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Table(4);
        }
        assertThrows(TooManyInstancesException.class, () -> new Table(4));
    }

    // ===============================================================
    // Order tests
    // ===============================================================

    @Test
    void order_constructor_initializesCorrectly() throws TooManyInstancesException {
        Order o = makeOrder();
        assertEquals(OrderStatus.TAKING_ORDER, o.getOrderStatus());
        assertEquals(0.0, o.getPrice());
    }

    @Test
    void order_addOrder_addsItemAndUpdatesTotalPrice()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        o.addOrder(c, item);
        assertEquals(9.99, o.getPrice(), 0.001);
    }

    @Test
    void order_addOrder_replacesExistingItemForSameCustomer()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item1 = makeItem("M1", "Burger", 9.99, 5);
        MenuItem item2 = makeItem("M2", "Salad", 6.99, 5);
        o.addOrder(c, item1);
        o.addOrder(c, item2); // replaces burger with salad
        assertEquals(6.99, o.getPrice(), 0.001);
    }

    @Test
    void order_addOrder_throwsIfNotTakingOrder()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        o.addOrder(c, item);
        o.setOrderStatus(OrderStatus.IN_KITCHEN);
        MenuItem item2 = makeItem("M2", "Salad", 6.99, 5);
        assertThrows(InvalidOrderState.class, () -> o.addOrder(c, item2));
    }

    @Test
    void order_removeOrder_removesItemAndUpdatesPrice()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        o.addOrder(c, item);
        o.removeOrder(c);
        assertEquals(0.0, o.getPrice(), 0.001);
    }

    @Test
    void order_removeOrder_throwsIfNotTakingOrder()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        o.addOrder(c, item);
        o.setOrderStatus(OrderStatus.IN_KITCHEN);
        assertThrows(InvalidOrderState.class, () -> o.removeOrder(c));
    }

    @Test
    void order_applyDiscount_reducesTotalPrice()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        o.addOrder(c, item);
        o.applyDiscount(0.10); // 10% off
        assertEquals(9.00, o.getPrice(), 0.001);
    }

    @Test
    void order_applyDiscount_throwsForOutOfRange()
            throws TooManyInstancesException {
        Order o = makeOrder();
        assertThrows(InvalidDiscountException.class, () -> o.applyDiscount(-0.1));
        assertThrows(InvalidDiscountException.class, () -> o.applyDiscount(1.5));
    }

    @Test
    void order_applyFlatDiscount_reducesTotalPrice()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        o.addOrder(c, item);
        o.applyFlatDiscount(3.00);
        assertEquals(7.00, o.getPrice(), 0.001);
    }

    @Test
    void order_applyFlatDiscount_throwsWhenMakesPriceNegative()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 5.00, 5);
        o.addOrder(c, item);
        assertThrows(InvalidDiscountException.class, () -> o.applyFlatDiscount(10.00));
    }

    @Test
    void order_getOrderContents_returnsCorrectItems()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        o.addOrder(c, item);
        assertEquals(1, o.getOrderContents().size());
        assertEquals("Burger", o.getOrderContents().get(0).getName());
    }

    @Test
    void order_setOrderStatus_changesStatus() throws TooManyInstancesException {
        Order o = makeOrder();
        o.setOrderStatus(OrderStatus.IN_KITCHEN);
        assertEquals(OrderStatus.IN_KITCHEN, o.getOrderStatus());
    }

    @Test
    void order_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Order();
        }
        assertThrows(TooManyInstancesException.class, Order::new);
    }

    // ===============================================================
    // Chef tests
    // ===============================================================

    @Test
    void chef_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertEquals("E1", chef.getEmployeeId());
        assertEquals("Gordon", chef.getName());
        assertEquals("Grill", chef.getSpecialty());
        assertFalse(chef.isOnDuty());
        assertEquals(0, chef.getOrderCount());
    }

    @Test
    void chef_constructor_throwsForNullSpecialty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Chef("E1", "Gordon", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Chef("E1", "Gordon", ""));
    }

    @Test
    void chef_clockIn_setsOnDutyAndStatus() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        assertTrue(chef.isOnDuty());
        assertEquals("Available", chef.getChefStatus());
    }

    @Test
    void chef_clockOut_clearsOnDutyAndStatus() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.clockOut();
        assertFalse(chef.isOnDuty());
        assertEquals("Off Duty", chef.getChefStatus());
    }

    @Test
    void chef_clockOut_throwsWithActiveOrders() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertThrows(IllegalStateException.class, chef::clockOut);
    }

    @Test
    void chef_acceptOrder_addsOrderAndSetsBusy() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertEquals(1, chef.getOrderCount());
        assertEquals("Busy", chef.getChefStatus());
        assertEquals(OrderStatus.IN_KITCHEN, order.getOrderStatus());
    }

    @Test
    void chef_acceptOrder_throwsWhenOffDuty() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        Order order = makeOrder();
        assertThrows(IllegalStateException.class, () -> chef.acceptOrder(order));
    }

    @Test
    void chef_acceptOrder_throwsForDuplicateOrder() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertThrows(IllegalStateException.class, () -> chef.acceptOrder(order));
    }

    @Test
    void chef_completeOrder_removesOrderAndSetsReady() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        chef.completeOrder(order);
        assertEquals(0, chef.getOrderCount());
        assertEquals(OrderStatus.READY, order.getOrderStatus());
        assertEquals("Available", chef.getChefStatus());
    }

    @Test
    void chef_completeOrder_throwsForOrderNotInQueue() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        assertThrows(IllegalStateException.class, () -> chef.completeOrder(order));
    }

    @Test
    void chef_transferOrder_movesOrderToOtherChef() throws TooManyInstancesException {
        Chef chef1 = makeChef("E1", "Gordon");
        Chef chef2 = makeChef("E2", "Julia");
        chef1.clockIn();
        chef2.clockIn();
        Order order = makeOrder();
        chef1.acceptOrder(order);
        chef1.transferOrder(order, chef2);
        assertEquals(0, chef1.getOrderCount());
        assertEquals(1, chef2.getOrderCount());
    }

    @Test
    void chef_transferOrder_throwsWhenTransferringToSelf() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertThrows(IllegalArgumentException.class, () -> chef.transferOrder(order, chef));
    }

    @Test
    void chef_viewQueue_returnsNoOrdersMessage() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertTrue(chef.viewQueue().contains("No current orders"));
    }

    @Test
    void chef_updateStatus_changesStatusCorrectly() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.updateStatus("Busy");
        assertEquals("Busy", chef.getChefStatus());
    }

    @Test
    void chef_updateStatus_throwsForInvalidStatus() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertThrows(IllegalArgumentException.class, () -> chef.updateStatus("Sleeping"));
    }

    @Test
    void chef_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Chef("E" + i, "Chef" + i, "Grill");
        }
        assertThrows(TooManyInstancesException.class,
                () -> new Chef("E101", "Extra", "Grill"));
    }

    // ===============================================================
    // Kitchen tests
    // ===============================================================

    @Test
    void kitchen_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertEquals(5, k.getMaxCapacity());
        assertEquals(0, k.getActiveOrderCount());
        assertEquals(0, k.getStaffCount());
        assertEquals(0, k.getTotalOrdersCompleted());
    }

    @Test
    void kitchen_constructor_throwsForNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Kitchen(0));
    }

    @Test
    void kitchen_addOrder_addsOrderToQueue() throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        k.addOrder(order);
        assertEquals(1, k.getActiveOrderCount());
    }

    @Test
    void kitchen_addOrder_throwsWhenAtCapacity() throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(2);
        k.addOrder(makeOrder());
        k.addOrder(makeOrder());
        assertThrows(KitchenAtCapacityException.class, () -> k.addOrder(makeOrder()));
    }

    @Test
    void kitchen_addOrder_throwsForDuplicateOrder() throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        k.addOrder(order);
        assertThrows(IllegalStateException.class, () -> k.addOrder(order));
    }

    @Test
    void kitchen_addOrder_assignsToAvailableChef()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        k.addChef(chef);
        Order order = makeOrder();
        k.addOrder(order);
        assertEquals(1, chef.getOrderCount());
    }

    @Test
    void kitchen_removeOrder_removesAndIncrementsTotalCompleted()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        k.addOrder(order);
        k.removeOrder(order);
        assertEquals(0, k.getActiveOrderCount());
        assertEquals(1, k.getTotalOrdersCompleted());
    }

    @Test
    void kitchen_removeOrder_throwsForOrderNotInQueue() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        assertThrows(IllegalStateException.class, () -> k.removeOrder(order));
    }

    @Test
    void kitchen_addChef_throwsForDuplicateChef() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        Chef chef = makeChef("E1", "Gordon");
        k.addChef(chef);
        assertThrows(IllegalStateException.class, () -> k.addChef(chef));
    }

    @Test
    void kitchen_getAvailableChef_returnsChefWithLeastOrders()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(10);
        Chef chef1 = makeChef("E1", "Gordon");
        Chef chef2 = makeChef("E2", "Julia");
        chef1.clockIn();
        chef2.clockIn();
        k.addChef(chef1);
        k.addChef(chef2);

        // Give chef1 one order, chef2 should be preferred for the next
        Order first = makeOrder();
        chef1.acceptOrder(first);

        Chef available = k.getAvailableChef();
        assertEquals(chef2, available);
    }

    @Test
    void kitchen_getAvailableChef_returnsNullWhenNoneOnDuty() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        Chef chef = makeChef("E1", "Gordon");
        k.addChef(chef); // chef is off duty
        assertNull(k.getAvailableChef());
    }

    @Test
    void kitchen_getDailySummary_containsKeyInfo() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        String summary = k.getDailySummary();
        assertTrue(summary.contains("Active Orders"));
        assertTrue(summary.contains("Total Orders Completed"));
        assertTrue(summary.contains("Chefs On Duty"));
    }

    @Test
    void kitchen_listActiveOrders_returnsMessageWhenEmpty() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertTrue(k.listActiveOrders().contains("no active orders"));
    }

    @Test
    void kitchen_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Kitchen(5);
        }
        assertThrows(TooManyInstancesException.class, () -> new Kitchen(5));
    }

    // ===============================================================
    // Server tests
    // ===============================================================

    @Test
    void server_constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        assertEquals("S1", s.getEmployeeId());
        assertEquals("Jake", s.getName());
        assertEquals("Section A", s.getSection());
        assertFalse(s.isOnDuty());
        assertEquals(0, s.getTableCount());
    }

    @Test
    void server_clockIn_setsOnDutyAndStatus() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        assertTrue(s.isOnDuty());
        assertEquals("Available", s.getServerStatus());
    }

    @Test
    void server_clockOut_throwsWithAssignedTables() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        assertThrows(IllegalStateException.class, s::clockOut);
    }

    @Test
    void server_assignTable_addsTableAndSetsBusy() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        assertEquals(1, s.getTableCount());
        assertEquals("Busy", s.getServerStatus());
    }

    @Test
    void server_assignTable_throwsWhenOffDuty() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        Table t = makeTable(4);
        assertThrows(IllegalStateException.class, () -> s.assignTable(t));
    }

    @Test
    void server_assignTable_throwsForDuplicateTable() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        assertThrows(IllegalStateException.class, () -> s.assignTable(t));
    }

    @Test
    void server_seatCustomer_seatsCustomerAtTable() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        assertTrue(t.isOccupied());
        assertTrue(c.getIsSeated());
    }

    @Test
    void server_seatCustomer_throwsWhenTableOccupied() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c1 = makeCustomer("Alice", 2);
        Customer c2 = makeCustomer("Bob", 1);
        s.seatCustomer(c1, t);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(c2, t));
    }

    @Test
    void server_seatCustomer_throwsWhenPartySizeExceedsCapacity() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(2);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 5);
        assertThrows(IllegalStateException.class, () -> s.seatCustomer(c, t));
    }

    @Test
    void server_takeCustomerOrder_addsItemToOrder()
            throws TooManyInstancesException, InvalidOrderState {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        s.takeCustomerOrder(order, c, item);
        assertEquals(9.99, order.getPrice(), 0.001);
        assertEquals(4, item.getStockCount()); // decremented
    }

    @Test
    void server_takeCustomerOrder_throwsForUnavailableItem() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 2);
        s.seatCustomer(c, t);
        Order order = makeOrder();
        MenuItem item = makeItem("M1", "Burger", 9.99, 0); // out of stock
        assertThrows(IllegalStateException.class, () -> s.takeCustomerOrder(order, c, item));
    }

    @Test
    void server_applyDiscountToOrder_appliesSuccessfully()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Order order = makeOrder();
        Customer c = makeCustomer("Alice", 1);
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        order.addOrder(c, item);
        s.applyDiscountToOrder(order, 0.10);
        assertEquals(9.00, order.getPrice(), 0.001);
    }

    @Test
    void server_removeTable_removesSuccessfully() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        s.removeTable(t);
        assertEquals(0, s.getTableCount());
    }

    @Test
    void server_removeTable_throwsForOccupiedTable() throws TooManyInstancesException {
        Server s = makeServer("S1", "Jake");
        s.clockIn();
        Table t = makeTable(4);
        s.assignTable(t);
        Customer c = makeCustomer("Alice", 1);
        s.seatCustomer(c, t);
        assertThrows(IllegalStateException.class, () -> s.removeTable(t));
    }

    @Test
    void server_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Server("S" + i, "Server" + i, "Section A");
        }
        assertThrows(TooManyInstancesException.class,
                () -> new Server("S101", "Extra", "Section A"));
    }
}
