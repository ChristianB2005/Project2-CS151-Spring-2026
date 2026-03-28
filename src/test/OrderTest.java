package test;

import exceptions.InvalidDiscountException;
import exceptions.InvalidOrderState;
import exceptions.TooManyInstancesException;
import model.Customer;
import model.MenuItem;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.OrderStatus;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Order.class, "numOrders");
        resetStatic(Customer.class, "instances");
        MenuItem.resetInstanceCount();
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    private Order makeOrder() throws TooManyInstancesException {
        return new Order();
    }

    private Customer makeCustomer(String name) throws TooManyInstancesException {
        return new Customer(name, 1);
    }

    private MenuItem makeItem(String id, String name, double price)
            throws TooManyInstancesException {
        return new MenuItem(id, name, price, "Food", 10);
    }

    // ---------------------------------------------------------------
    // Constructor tests
    // ---------------------------------------------------------------

    @Test
    void constructor_initializesCorrectly() throws TooManyInstancesException {
        Order o = makeOrder();
        assertEquals(OrderStatus.TAKING_ORDER, o.getOrderStatus());
        assertEquals(0.0, o.getPrice());
        assertTrue(o.getOrderContents().isEmpty());
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Order();
        }
        assertThrows(TooManyInstancesException.class, Order::new);
    }

    // ---------------------------------------------------------------
    // addOrder tests
    // ---------------------------------------------------------------

    @Test
    void addOrder_addsItemAndUpdatesTotalPrice()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        MenuItem item = makeItem("M1", "Burger", 9.99);
        o.addOrder(c, item);
        assertEquals(9.99, o.getPrice(), 0.001);
        assertEquals(1, o.getOrderContents().size());
    }

    @Test
    void addOrder_multipleCustomers_sumsPricesCorrectly()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c1 = makeCustomer("Alice");
        Customer c2 = makeCustomer("Bob");
        o.addOrder(c1, makeItem("M1", "Burger", 10.00));
        o.addOrder(c2, makeItem("M2", "Salad", 7.00));
        assertEquals(17.00, o.getPrice(), 0.001);
    }

    @Test
    void addOrder_replacesExistingItemForSameCustomer()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 10.00));
        o.addOrder(c, makeItem("M2", "Salad", 6.00)); // replaces burger
        assertEquals(6.00, o.getPrice(), 0.001);
        assertEquals(1, o.getOrderContents().size());
    }

    @Test
    void addOrder_throwsWhenStatusIsInKitchen()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 9.99));
        o.setOrderStatus(OrderStatus.IN_KITCHEN);
        assertThrows(InvalidOrderState.class,
                () -> o.addOrder(c, makeItem("M2", "Salad", 6.00)));
    }

    @Test
    void addOrder_throwsWhenStatusIsReady()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.setOrderStatus(OrderStatus.READY);
        assertThrows(InvalidOrderState.class,
                () -> o.addOrder(c, makeItem("M1", "Burger", 9.99)));
    }

    // ---------------------------------------------------------------
    // removeOrder tests
    // ---------------------------------------------------------------

    @Test
    void removeOrder_removesItemAndUpdatesPrice()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        MenuItem item = makeItem("M1", "Burger", 9.99);
        o.addOrder(c, item);
        o.removeOrder(c);
        assertEquals(0.0, o.getPrice(), 0.001);
        assertTrue(o.getOrderContents().isEmpty());
    }

    @Test
    void removeOrder_onlyRemovesCorrectCustomer()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c1 = makeCustomer("Alice");
        Customer c2 = makeCustomer("Bob");
        o.addOrder(c1, makeItem("M1", "Burger", 10.00));
        o.addOrder(c2, makeItem("M2", "Salad", 7.00));
        o.removeOrder(c1);
        assertEquals(7.00, o.getPrice(), 0.001);
        assertEquals(1, o.getOrderContents().size());
    }

    @Test
    void removeOrder_throwsWhenStatusIsInKitchen()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 9.99));
        o.setOrderStatus(OrderStatus.IN_KITCHEN);
        assertThrows(InvalidOrderState.class, () -> o.removeOrder(c));
    }

    @Test
    void removeOrder_throwsWhenStatusIsReady()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 9.99));
        o.setOrderStatus(OrderStatus.READY);
        assertThrows(InvalidOrderState.class, () -> o.removeOrder(c));
    }

    // ---------------------------------------------------------------
    // getOrderContents tests
    // ---------------------------------------------------------------

    @Test
    void getOrderContents_returnsEmptyListInitially() throws TooManyInstancesException {
        Order o = makeOrder();
        assertTrue(o.getOrderContents().isEmpty());
    }

    @Test
    void getOrderContents_returnsCorrectItems()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        MenuItem item = makeItem("M1", "Burger", 9.99);
        o.addOrder(c, item);
        assertEquals(1, o.getOrderContents().size());
        assertEquals("Burger", o.getOrderContents().get(0).getName());
    }

    // ---------------------------------------------------------------
    // setOrderStatus tests
    // ---------------------------------------------------------------

    @Test
    void setOrderStatus_changesStatusCorrectly() throws TooManyInstancesException {
        Order o = makeOrder();
        o.setOrderStatus(OrderStatus.IN_KITCHEN);
        assertEquals(OrderStatus.IN_KITCHEN, o.getOrderStatus());
        o.setOrderStatus(OrderStatus.READY);
        assertEquals(OrderStatus.READY, o.getOrderStatus());
    }

    // ---------------------------------------------------------------
    // applyDiscount tests
    // ---------------------------------------------------------------

    @Test
    void applyDiscount_reducesTotalPrice()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 10.00));
        o.applyDiscount(0.10); // 10% off
        assertEquals(9.00, o.getPrice(), 0.001);
    }

    @Test
    void applyDiscount_zeroPercentage_noChange()
            throws TooManyInstancesException, InvalidDiscountException {
        Order o = makeOrder();
        o.applyDiscount(0.0);
        assertEquals(0.0, o.getPrice(), 0.001);
    }

    @Test
    void applyDiscount_fullDiscount_setsToZero()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 10.00));
        o.applyDiscount(1.0);
        assertEquals(0.0, o.getPrice(), 0.001);
    }

    @Test
    void applyDiscount_throwsForNegativePercentage() throws TooManyInstancesException {
        Order o = makeOrder();
        assertThrows(InvalidDiscountException.class, () -> o.applyDiscount(-0.1));
    }

    @Test
    void applyDiscount_throwsForPercentageOverOne() throws TooManyInstancesException {
        Order o = makeOrder();
        assertThrows(InvalidDiscountException.class, () -> o.applyDiscount(1.5));
    }

    // ---------------------------------------------------------------
    // applyFlatDiscount tests
    // ---------------------------------------------------------------

    @Test
    void applyFlatDiscount_reducesTotalPrice()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 10.00));
        o.applyFlatDiscount(3.00);
        assertEquals(7.00, o.getPrice(), 0.001);
    }

    @Test
    void applyFlatDiscount_throwsForNegativeAmount() throws TooManyInstancesException {
        Order o = makeOrder();
        assertThrows(InvalidDiscountException.class, () -> o.applyFlatDiscount(-1.00));
    }

    @Test
    void applyFlatDiscount_throwsWhenMakesPriceNegative()
            throws TooManyInstancesException, InvalidOrderState {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 5.00));
        assertThrows(InvalidDiscountException.class, () -> o.applyFlatDiscount(10.00));
    }

    @Test
    void applyFlatDiscount_exactAmountAllowed()
            throws TooManyInstancesException, InvalidOrderState, InvalidDiscountException {
        Order o = makeOrder();
        Customer c = makeCustomer("Alice");
        o.addOrder(c, makeItem("M1", "Burger", 5.00));
        o.applyFlatDiscount(5.00); // exact — reduces to exactly 0
        assertEquals(0.0, o.getPrice(), 0.001);
    }
}
