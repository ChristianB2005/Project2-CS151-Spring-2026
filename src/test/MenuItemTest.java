package test;

import exceptions.InvalidDiscountException;
import exceptions.TooManyInstancesException;
import model.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MenuItemTest {

    @BeforeEach
    void resetInstanceCount() {
        MenuItem.resetInstanceCount();
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
        MenuItem item = makeItem("M1", "Burger", 9.99, 10);
        assertEquals("M1", item.getItemId());
        assertEquals("Burger", item.getName());
        assertEquals(9.99, item.getPrice());
        assertEquals("Food", item.getCategory());
        assertEquals(10, item.getStockCount());
        assertTrue(item.isAvailable());
    }

    @Test
    void constructor_zeroStock_marksUnavailable() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 0);
        assertFalse(item.isAvailable());
        assertEquals(0, item.getStockCount());
    }

    @Test
    void constructor_throwsForNullItemId() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem(null, "Burger", 9.99, "Food", 5));
    }

    @Test
    void constructor_throwsForEmptyItemId() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("", "Burger", 9.99, "Food", 5));
    }

    @Test
    void constructor_throwsForNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", null, 9.99, "Food", 5));
    }

    @Test
    void constructor_throwsForEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "", 9.99, "Food", 5));
    }

    @Test
    void constructor_throwsForNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "Burger", -1.00, "Food", 5));
    }

    @Test
    void constructor_throwsForNullCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "Burger", 9.99, null, 5));
    }

    @Test
    void constructor_throwsForNegativeStock() {
        assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M1", "Burger", 9.99, "Food", -1));
    }

    @Test
    void constructor_instanceCap_throwsTooManyInstances() throws TooManyInstancesException {
        for (int i = 0; i < 100; i++) {
            new MenuItem("M" + i, "Item" + i, 1.00, "Food", 1);
        }
        assertThrows(TooManyInstancesException.class,
                () -> new MenuItem("M101", "Extra", 1.00, "Food", 1));
    }

    // ---------------------------------------------------------------
    // decrementStock tests
    // ---------------------------------------------------------------

    @Test
    void decrementStock_reducesCountByOne() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 3);
        item.decrementStock();
        assertEquals(2, item.getStockCount());
    }

    @Test
    void decrementStock_marksUnavailableWhenReachesZero() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 1);
        item.decrementStock();
        assertEquals(0, item.getStockCount());
        assertFalse(item.isAvailable());
    }

    @Test
    void decrementStock_throwsWhenOutOfStock() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 0);
        assertThrows(IllegalStateException.class, item::decrementStock);
    }

    // ---------------------------------------------------------------
    // restock tests
    // ---------------------------------------------------------------

    @Test
    void restock_increasesStockCount() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.restock(3);
        assertEquals(8, item.getStockCount());
    }

    @Test
    void restock_marksAvailableWhenWasUnavailable() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 0);
        item.restock(5);
        assertTrue(item.isAvailable());
    }

    @Test
    void restock_throwsForZeroQuantity() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.restock(0));
    }

    @Test
    void restock_throwsForNegativeQuantity() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.restock(-3));
    }

    // ---------------------------------------------------------------
    // markUnavailable tests
    // ---------------------------------------------------------------

    @Test
    void markUnavailable_setsIsAvailableToFalse() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.markUnavailable();
        assertFalse(item.isAvailable());
    }

    // ---------------------------------------------------------------
    // updatePrice tests
    // ---------------------------------------------------------------

    @Test
    void updatePrice_setsNewPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.updatePrice(12.50);
        assertEquals(12.50, item.getPrice());
    }

    @Test
    void updatePrice_allowsZeroPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.updatePrice(0.0);
        assertEquals(0.0, item.getPrice());
    }

    @Test
    void updatePrice_throwsForNegativePrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.updatePrice(-1.00));
    }

    // ---------------------------------------------------------------
    // applyDiscount tests
    // ---------------------------------------------------------------

    @Test
    void applyDiscount_reducesPrice() throws TooManyInstancesException, InvalidDiscountException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        item.applyDiscount(0.10);
        assertEquals(9.00, item.getPrice(), 0.001);
    }

    @Test
    void applyDiscount_fullDiscount_setsToZero() throws TooManyInstancesException, InvalidDiscountException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        item.applyDiscount(1.0);
        assertEquals(0.0, item.getPrice(), 0.001);
    }

    @Test
    void applyDiscount_throwsForNegativePercentage() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        assertThrows(InvalidDiscountException.class, () -> item.applyDiscount(-0.1));
    }

    @Test
    void applyDiscount_throwsForPercentageOverOne() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        assertThrows(InvalidDiscountException.class, () -> item.applyDiscount(1.5));
    }

    // ---------------------------------------------------------------
    // applyFlatDiscount tests
    // ---------------------------------------------------------------

    @Test
    void applyFlatDiscount_reducesPrice() throws TooManyInstancesException, InvalidDiscountException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        item.applyFlatDiscount(2.00);
        assertEquals(8.00, item.getPrice(), 0.001);
    }

    @Test
    void applyFlatDiscount_throwsForNegativeAmount() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        assertThrows(InvalidDiscountException.class, () -> item.applyFlatDiscount(-1.00));
    }

    @Test
    void applyFlatDiscount_throwsWhenExceedsPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 5.00, 5);
        assertThrows(InvalidDiscountException.class, () -> item.applyFlatDiscount(10.00));
    }

    // ---------------------------------------------------------------
    // applyModifier tests
    // ---------------------------------------------------------------

    @Test
    void applyModifier_returnsIncreasedPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        double result = item.applyModifier(2.50);
        assertEquals(12.50, result, 0.001);
    }

    @Test
    void applyModifier_returnsDecreasedPrice() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 10.00, 5);
        double result = item.applyModifier(-2.00);
        assertEquals(8.00, result, 0.001);
    }

    @Test
    void applyModifier_throwsWhenResultIsNegative() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 5.00, 5);
        assertThrows(IllegalArgumentException.class, () -> item.applyModifier(-10.00));
    }

    // ---------------------------------------------------------------
    // Setter validation tests
    // ---------------------------------------------------------------

    @Test
    void setItemId_throwsForNullOrEmpty() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.setItemId(null));
        assertThrows(IllegalArgumentException.class, () -> item.setItemId(""));
    }

    @Test
    void setName_throwsForNullOrEmpty() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.setName(null));
        assertThrows(IllegalArgumentException.class, () -> item.setName(""));
    }

    @Test
    void setStockCount_throwsForNegative() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        assertThrows(IllegalArgumentException.class, () -> item.setStockCount(-1));
    }

    @Test
    void setStockCount_zeroMarksUnavailable() throws TooManyInstancesException {
        MenuItem item = makeItem("M1", "Burger", 9.99, 5);
        item.setStockCount(0);
        assertFalse(item.isAvailable());
    }
}
