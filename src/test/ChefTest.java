package test;

import exceptions.TooManyInstancesException;
import model.Chef;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.OrderStatus;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ChefTest {

    @BeforeEach
    void resetInstanceCounts() throws Exception {
        resetStatic(Chef.class, "instanceCount");
        resetStatic(Order.class, "numOrders");
    }

    private void resetStatic(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(null, 0);
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
        Chef chef = makeChef("E1", "Gordon");
        assertEquals("E1", chef.getEmployeeId());
        assertEquals("Gordon", chef.getName());
        assertEquals("Grill", chef.getSpecialty());
        assertFalse(chef.isOnDuty());
        assertEquals("Off Duty", chef.getChefStatus());
        assertEquals(0, chef.getOrderCount());
    }

    @Test
    void constructor_throwsForNullSpecialty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Chef("E1", "Gordon", null));
    }

    @Test
    void constructor_throwsForEmptySpecialty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Chef("E1", "Gordon", ""));
    }

    @Test
    void constructor_throwsForNullEmployeeId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Chef(null, "Gordon", "Grill"));
    }

    @Test
    void constructor_throwsForNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Chef("E1", null, "Grill"));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new Chef("E" + i, "Chef" + i, "Grill");
        }
        assertThrows(TooManyInstancesException.class,
                () -> new Chef("E101", "Extra", "Grill"));
    }

    // ---------------------------------------------------------------
    // clockIn tests
    // ---------------------------------------------------------------

    @Test
    void clockIn_setsOnDutyTrue() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        assertTrue(chef.isOnDuty());
    }

    @Test
    void clockIn_setsStatusToAvailable() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        assertEquals("Available", chef.getChefStatus());
    }

    // ---------------------------------------------------------------
    // clockOut tests
    // ---------------------------------------------------------------

    @Test
    void clockOut_setsOnDutyFalse() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.clockOut();
        assertFalse(chef.isOnDuty());
    }

    @Test
    void clockOut_setsStatusToOffDuty() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.clockOut();
        assertEquals("Off Duty", chef.getChefStatus());
    }

    @Test
    void clockOut_throwsWithActiveOrders() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.acceptOrder(makeOrder());
        assertThrows(IllegalStateException.class, chef::clockOut);
    }

    // ---------------------------------------------------------------
    // acceptOrder tests
    // ---------------------------------------------------------------

    @Test
    void acceptOrder_addsOrderToQueue() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.acceptOrder(makeOrder());
        assertEquals(1, chef.getOrderCount());
    }

    @Test
    void acceptOrder_setsOrderStatusToInKitchen() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertEquals(OrderStatus.IN_KITCHEN, order.getOrderStatus());
    }

    @Test
    void acceptOrder_setsChefStatusToBusy() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.acceptOrder(makeOrder());
        assertEquals("Busy", chef.getChefStatus());
    }

    @Test
    void acceptOrder_throwsWhenOffDuty() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertThrows(IllegalStateException.class, () -> chef.acceptOrder(makeOrder()));
    }

    @Test
    void acceptOrder_throwsForNullOrder() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        assertThrows(IllegalArgumentException.class, () -> chef.acceptOrder(null));
    }

    @Test
    void acceptOrder_throwsForDuplicateOrder() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertThrows(IllegalStateException.class, () -> chef.acceptOrder(order));
    }

    // ---------------------------------------------------------------
    // completeOrder tests
    // ---------------------------------------------------------------

    @Test
    void completeOrder_removesOrderFromQueue() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        chef.completeOrder(order);
        assertEquals(0, chef.getOrderCount());
    }

    @Test
    void completeOrder_setsOrderStatusToReady() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        chef.completeOrder(order);
        assertEquals(OrderStatus.READY, order.getOrderStatus());
    }

    @Test
    void completeOrder_setsStatusBackToAvailable() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        chef.completeOrder(order);
        assertEquals("Available", chef.getChefStatus());
    }

    @Test
    void completeOrder_remainsBusyWithOtherActiveOrders() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order1 = makeOrder();
        Order order2 = makeOrder();
        chef.acceptOrder(order1);
        chef.acceptOrder(order2);
        chef.completeOrder(order1);
        assertEquals("Busy", chef.getChefStatus());
        assertEquals(1, chef.getOrderCount());
    }

    @Test
    void completeOrder_throwsForOrderNotInQueue() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        assertThrows(IllegalStateException.class, () -> chef.completeOrder(makeOrder()));
    }

    @Test
    void completeOrder_throwsForNullOrder() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        assertThrows(IllegalArgumentException.class, () -> chef.completeOrder(null));
    }

    // ---------------------------------------------------------------
    // transferOrder tests
    // ---------------------------------------------------------------

    @Test
    void transferOrder_movesOrderToOtherChef() throws TooManyInstancesException {
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
    void transferOrder_throwsWhenTransferringToSelf() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertThrows(IllegalArgumentException.class, () -> chef.transferOrder(order, chef));
    }

    @Test
    void transferOrder_throwsForNullOrder() throws TooManyInstancesException {
        Chef chef1 = makeChef("E1", "Gordon");
        Chef chef2 = makeChef("E2", "Julia");
        chef1.clockIn();
        assertThrows(IllegalArgumentException.class, () -> chef1.transferOrder(null, chef2));
    }

    @Test
    void transferOrder_throwsForNullReceivingChef() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        Order order = makeOrder();
        chef.acceptOrder(order);
        assertThrows(IllegalArgumentException.class, () -> chef.transferOrder(order, null));
    }

    @Test
    void transferOrder_throwsForOrderNotInQueue() throws TooManyInstancesException {
        Chef chef1 = makeChef("E1", "Gordon");
        Chef chef2 = makeChef("E2", "Julia");
        chef1.clockIn();
        chef2.clockIn();
        Order order = makeOrder();
        assertThrows(IllegalStateException.class, () -> chef1.transferOrder(order, chef2));
    }

    // ---------------------------------------------------------------
    // viewQueue tests
    // ---------------------------------------------------------------

    @Test
    void viewQueue_returnsNoOrdersMessageWhenEmpty() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertTrue(chef.viewQueue().contains("No current orders"));
    }

    @Test
    void viewQueue_containsChefNameWhenEmpty() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertTrue(chef.viewQueue().contains("Gordon"));
    }

    @Test
    void viewQueue_listsOrdersWhenPresent() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.acceptOrder(makeOrder());
        String queue = chef.viewQueue();
        assertTrue(queue.contains("Gordon"));
        assertTrue(queue.contains("1."));
    }

    // ---------------------------------------------------------------
    // updateStatus tests
    // ---------------------------------------------------------------

    @Test
    void updateStatus_setsAvailable() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.updateStatus("Available");
        assertEquals("Available", chef.getChefStatus());
        assertTrue(chef.isOnDuty());
    }

    @Test
    void updateStatus_setsBusy() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.updateStatus("Busy");
        assertEquals("Busy", chef.getChefStatus());
    }

    @Test
    void updateStatus_setsOffDutyWhenNoOrders() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.updateStatus("Off Duty");
        assertEquals("Off Duty", chef.getChefStatus());
        assertFalse(chef.isOnDuty());
    }

    @Test
    void updateStatus_throwsOffDutyWithActiveOrders() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.acceptOrder(makeOrder());
        assertThrows(IllegalStateException.class, () -> chef.updateStatus("Off Duty"));
    }

    @Test
    void updateStatus_throwsForInvalidStatus() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertThrows(IllegalArgumentException.class, () -> chef.updateStatus("Sleeping"));
    }

    @Test
    void updateStatus_throwsForNullStatus() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertThrows(IllegalArgumentException.class, () -> chef.updateStatus(null));
    }

    // ---------------------------------------------------------------
    // hasActiveOrders tests
    // ---------------------------------------------------------------

    @Test
    void hasActiveOrders_returnsFalseWhenQueueEmpty() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        assertFalse(chef.hasActiveOrders());
    }

    @Test
    void hasActiveOrders_returnsTrueWhenOrdersPresent() throws TooManyInstancesException {
        Chef chef = makeChef("E1", "Gordon");
        chef.clockIn();
        chef.acceptOrder(makeOrder());
        assertTrue(chef.hasActiveOrders());
    }
}
