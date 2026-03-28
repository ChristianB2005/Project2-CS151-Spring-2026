package test;

import exceptions.KitchenAtCapacityException;
import exceptions.TooManyInstancesException;
import model.Chef;
import model.Kitchen;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class KitchenTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Kitchen.class, "instanceCount");
        resetStatic(Chef.class, "instanceCount");
        resetStatic(Order.class, "numOrders");
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
    }

    private Kitchen makeKitchen(int capacity) throws TooManyInstancesException {
        return new Kitchen(capacity);
    }

    private Chef makeChef(String id, String name) throws TooManyInstancesException {
        return new Chef(id, name, "Grill");
    }

    private Order makeOrder() throws TooManyInstancesException {
        return new Order();
    }

    // ---------------------------------------------------------------
    // Constructor tests
    // ---------------------------------------------------------------

    @Test
    void constructor_setsFieldsCorrectly() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertEquals(5, k.getMaxCapacity());
        assertEquals(0, k.getActiveOrderCount());
        assertEquals(0, k.getStaffCount());
        assertEquals(0, k.getTotalOrdersCompleted());
    }

    @Test
    void constructor_throwsForZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Kitchen(0));
    }

    @Test
    void constructor_throwsForNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Kitchen(-1));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Kitchen(5);
        }
        assertThrows(TooManyInstancesException.class, () -> new Kitchen(5));
    }

    // ---------------------------------------------------------------
    // addOrder tests
    // ---------------------------------------------------------------

    @Test
    void addOrder_addsOrderToQueue()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        k.addOrder(makeOrder());
        assertEquals(1, k.getActiveOrderCount());
    }

    @Test
    void addOrder_throwsWhenAtCapacity()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(2);
        k.addOrder(makeOrder());
        k.addOrder(makeOrder());
        assertThrows(KitchenAtCapacityException.class, () -> k.addOrder(makeOrder()));
    }

    @Test
    void addOrder_throwsForNullOrder() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertThrows(IllegalArgumentException.class, () -> k.addOrder(null));
    }

    @Test
    void addOrder_throwsForDuplicateOrder()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        k.addOrder(order);
        assertThrows(IllegalStateException.class, () -> k.addOrder(order));
    }

    @Test
    void addOrder_automaticallyAssignsToAvailableChef()
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
    void addOrder_doesNotAssignWhenNoChefOnDuty()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Chef chef = makeChef("E1", "Gordon");
        k.addChef(chef); // chef not clocked in
        Order order = makeOrder();
        k.addOrder(order);
        assertEquals(0, chef.getOrderCount()); // not assigned
        assertEquals(1, k.getActiveOrderCount()); // still in kitchen queue
    }

    // ---------------------------------------------------------------
    // removeOrder tests
    // ---------------------------------------------------------------

    @Test
    void removeOrder_removesOrderFromQueue()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        k.addOrder(order);
        k.removeOrder(order);
        assertEquals(0, k.getActiveOrderCount());
    }

    @Test
    void removeOrder_incrementsTotalOrdersCompleted()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order order = makeOrder();
        k.addOrder(order);
        k.removeOrder(order);
        assertEquals(1, k.getTotalOrdersCompleted());
    }

    @Test
    void removeOrder_multipleOrders_removesCorrectOne()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        Order o1 = makeOrder();
        Order o2 = makeOrder();
        Order o3 = makeOrder();
        k.addOrder(o1);
        k.addOrder(o2);
        k.addOrder(o3);
        k.removeOrder(o2);
        assertEquals(2, k.getActiveOrderCount());
    }

    @Test
    void removeOrder_throwsForOrderNotInQueue() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertThrows(IllegalStateException.class, () -> k.removeOrder(makeOrder()));
    }

    @Test
    void removeOrder_throwsForNullOrder() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertThrows(IllegalArgumentException.class, () -> k.removeOrder(null));
    }

    // ---------------------------------------------------------------
    // addChef tests
    // ---------------------------------------------------------------

    @Test
    void addChef_increasesStaffCount() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        k.addChef(makeChef("E1", "Gordon"));
        assertEquals(1, k.getStaffCount());
    }

    @Test
    void addChef_throwsForNullChef() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertThrows(IllegalArgumentException.class, () -> k.addChef(null));
    }

    @Test
    void addChef_throwsForDuplicateChef() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        Chef chef = makeChef("E1", "Gordon");
        k.addChef(chef);
        assertThrows(IllegalStateException.class, () -> k.addChef(chef));
    }

    // ---------------------------------------------------------------
    // getAvailableChef tests
    // ---------------------------------------------------------------

    @Test
    void getAvailableChef_returnsNullWhenNoChefs() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertNull(k.getAvailableChef());
    }

    @Test
    void getAvailableChef_returnsNullWhenAllOffDuty() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        k.addChef(makeChef("E1", "Gordon")); // not clocked in
        assertNull(k.getAvailableChef());
    }

    @Test
    void getAvailableChef_returnsChefOnDuty() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        k.addChef(chef);
        assertEquals(chef, k.getAvailableChef());
    }

    @Test
    void getAvailableChef_returnsChefWithFewestOrders()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(10);
        Chef chef1 = makeChef("E1", "Gordon");
        Chef chef2 = makeChef("E2", "Julia");
        chef1.clockIn();
        chef2.clockIn();
        k.addChef(chef1);
        k.addChef(chef2);
        chef1.acceptOrder(makeOrder()); // give chef1 one order manually
        Chef available = k.getAvailableChef();
        assertEquals(chef2, available); // chef2 has fewer orders
    }

    // ---------------------------------------------------------------
    // listActiveOrders tests
    // ---------------------------------------------------------------

    @Test
    void listActiveOrders_returnsMessageWhenNoOrders() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertTrue(k.listActiveOrders().toLowerCase().contains("no active orders"));
    }

    @Test
    void listActiveOrders_listsPresentOrders()
            throws TooManyInstancesException, KitchenAtCapacityException {
        Kitchen k = makeKitchen(5);
        k.addOrder(makeOrder());
        k.addOrder(makeOrder());
        String result = k.listActiveOrders();
        assertTrue(result.contains("1."));
        assertTrue(result.contains("2."));
    }

    // ---------------------------------------------------------------
    // getDailySummary tests
    // ---------------------------------------------------------------

    @Test
    void getDailySummary_containsActiveOrders() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertTrue(k.getDailySummary().contains("Active Orders"));
    }

    @Test
    void getDailySummary_containsTotalCompleted() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertTrue(k.getDailySummary().contains("Total Orders Completed"));
    }

    @Test
    void getDailySummary_containsChefsOnDuty() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertTrue(k.getDailySummary().contains("Chefs On Duty"));
    }

    @Test
    void getDailySummary_reflectsCorrectChefsOnDuty() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        Chef chef1 = makeChef("E1", "Gordon");
        Chef chef2 = makeChef("E2", "Julia");
        chef1.clockIn();
        k.addChef(chef1);
        k.addChef(chef2); // off duty
        String summary = k.getDailySummary();
        assertTrue(summary.contains("1")); // 1 chef on duty
    }

    // ---------------------------------------------------------------
    // setMaxCapacity tests
    // ---------------------------------------------------------------

    @Test
    void setMaxCapacity_updatesCapacity() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        k.setMaxCapacity(10);
        assertEquals(10, k.getMaxCapacity());
    }

    @Test
    void setMaxCapacity_throwsForZeroOrNegative() throws TooManyInstancesException {
        Kitchen k = makeKitchen(5);
        assertThrows(IllegalArgumentException.class, () -> k.setMaxCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> k.setMaxCapacity(-1));
    }
}
