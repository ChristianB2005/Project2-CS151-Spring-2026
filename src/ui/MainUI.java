package ui;

import util.Constants;
import model.MenuItem;
import model.Order;
import model.Customer;
import model.Table;
import model.Kitchen;
import model.Chef;
import exceptions.InvalidDiscountException;
import exceptions.TooManyInstancesException;
import exceptions.KitchenAtCapacityException;
import exceptions.ReservationException;
import util.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MainUI {

    private static Scanner scanner = new Scanner(System.in);

    // Current working objects
    private static MenuItem currentItem = null;
    private static Order currentOrder = null;
    private static Customer currentCustomer = null;
    private static Table currentTable = null;
    private static Kitchen kitchen = new Kitchen(5); // max 5 active orders
    private static Chef currentChef = null;

    // Storage lists
    private static ArrayList<MenuItem> menuItems = new ArrayList<>();
    private static ArrayList<Customer> customers = new ArrayList<>();
    private static ArrayList<Table> tables = new ArrayList<>();
    private static ArrayList<Order> orders = new ArrayList<>();
    private static ArrayList<Chef> chefs = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the Restaurant Management System");
        showMainMenu();
    }

    public static void showMainMenu() {
        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Manage Menu Items");
            System.out.println("2. Manage Tables");
            System.out.println("3. Manage Orders");
            System.out.println("4. Manage Customers");
            System.out.println("5. Manage Kitchen");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            String input = getInput();

            switch (input) {
                case "1": menuItemMenu(); break;
                case "2": tableMenu(); break;
                case "3": orderMenu(); break;
                case "4": customerMenu(); break;
                case "5": kitchenMenu(); break;
                case "6":
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MENU ITEM MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    public static void menuItemMenu() {
        while (true) {
            System.out.println("\n===== MENU ITEM MANAGEMENT =====");
            System.out.println("1. Create Menu Item");
            System.out.println("2. Update Price");
            System.out.println("3. Mark Item Unavailable");
            System.out.println("4. Restock Item");
            System.out.println("5. Apply Discount");
            System.out.println("6. Apply Flat Discount");
            System.out.println("7. View All Menu Items");
            System.out.println("8. Select Existing Item");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = getInput();

            switch (input) {
                case "1": createMenuItem(); break;
                case "2": updateMenuItemPrice(); break;
                case "3": markMenuItemUnavailable(); break;
                case "4": restockMenuItem(); break;
                case "5": applyMenuItemDiscount(); break;
                case "6": applyMenuItemFlatDiscount(); break;
                case "7": viewAllMenuItems(); break;
                case "8": selectMenuItem(); break;
                case "9": return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
            }
        }
    }

    public static void createMenuItem() {
        System.out.println("\n-- Create Menu Item --");

        System.out.print("Enter item ID: ");
        String itemId = getInput();

        System.out.print("Enter item name: ");
        String name = getInput();

        double price = getValidDouble("Enter price (must be > 0): ", 0);

        System.out.print("Enter category: ");
        String category = getInput();

        int stock = getValidInt("Enter stock count (must be >= 0): ", -1);

        try {
            currentItem = new MenuItem(itemId, name, price, category, stock);
            menuItems.add(currentItem);
            System.out.println("Menu item created successfully: " + currentItem);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void updateMenuItemPrice() {
        if (currentItem == null) {
            System.out.println("No menu item selected. Please create or select one first.");
            return;
        }
        double newPrice = getValidDouble("Enter new price (must be > 0): ", 0);
        try {
            currentItem.updatePrice(newPrice);
            System.out.println("Price updated successfully. " + currentItem);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void markMenuItemUnavailable() {
        if (currentItem == null) {
            System.out.println("No menu item selected. Please create or select one first.");
            return;
        }
        currentItem.markUnavailable();
        System.out.println("Item marked as unavailable. " + currentItem);
    }

    public static void restockMenuItem() {
        if (currentItem == null) {
            System.out.println("No menu item selected. Please create or select one first.");
            return;
        }
        int quantity = getValidInt("Enter restock quantity (must be > 0): ", 0);
        try {
            currentItem.restock(quantity);
            System.out.println("Item restocked successfully. " + currentItem);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void applyMenuItemDiscount() {
        if (currentItem == null) {
            System.out.println("No menu item selected. Please create or select one first.");
            return;
        }
        double pct = getValidDouble("Enter discount percentage (0.0 - 1.0): ", -0.01);
        if (pct > 1) {
            System.out.println("Percentage must be between 0 and 1.");
            return;
        }
        try {
            currentItem.applyDiscount(pct);
            System.out.println("Discount applied. " + currentItem);
        } catch (InvalidDiscountException e) {
            System.out.println("Discount error: " + e.getMessage());
        }
    }

    public static void applyMenuItemFlatDiscount() {
        if (currentItem == null) {
            System.out.println("No menu item selected. Please create or select one first.");
            return;
        }
        double amount = getValidDouble("Enter flat discount amount: ", -0.01);
        try {
            currentItem.applyFlatDiscount(amount);
            System.out.println("Flat discount applied. " + currentItem);
        } catch (InvalidDiscountException e) {
            System.out.println("Discount error: " + e.getMessage());
        }
    }

    public static void viewAllMenuItems() {
        if (menuItems.isEmpty()) {
            System.out.println("No menu items created yet.");
            return;
        }
        System.out.println("\n-- All Menu Items --");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.println((i + 1) + ". " + menuItems.get(i));
        }
    }

    public static void selectMenuItem() {
        if (menuItems.isEmpty()) {
            System.out.println("No menu items to select. Create one first.");
            return;
        }
        viewAllMenuItems();
        int idx = getValidInt("Select item number: ", 0) - 1;
        if (idx >= 0 && idx < menuItems.size()) {
            currentItem = menuItems.get(idx);
            System.out.println("Selected: " + currentItem);
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // TABLE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    public static void tableMenu() {
        while (true) {
            System.out.println("\n===== TABLE MANAGEMENT =====");
            System.out.println("1. Create Table");
            System.out.println("2. Seat Customer at Table");
            System.out.println("3. Clear Table");
            System.out.println("4. View Table Status");
            System.out.println("5. View All Tables");
            System.out.println("6. Select Existing Table");
            System.out.println("7. Assign Server to Table");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = getInput();

            switch (input) {
                case "1": createTable(); break;
                case "2": seatCustomer(); break;
                case "3": clearTable(); break;
                case "4": viewTableStatus(); break;
                case "5": viewAllTables(); break;
                case "6": selectTable(); break;
                case "7": assignServer(); break;
                case "8": return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    public static void createTable() {
        System.out.println("\n-- Create Table --");

        System.out.print("Enter table ID: ");
        String tableId = getInput();

        int capacity = getValidInt("Enter seating capacity: ", 0);

        try {
            currentTable = new Table(tableId, capacity);
            tables.add(currentTable);
            System.out.println("Table created: " + currentTable);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void seatCustomer() {
        if (currentTable == null) {
            System.out.println("No table selected. Please create or select one first.");
            return;
        }
        if (currentCustomer == null) {
            System.out.println("No customer selected. Please create a customer first in Customer Management.");
            return;
        }
        try {
            currentTable.seatCustomer(currentCustomer);
            System.out.println("Customer " + currentCustomer.getName() + " seated at table " + currentTable.getTableId());
        } catch (ReservationException e) {
            System.out.println("Seating error: " + e.getMessage());
        }
    }

    public static void clearTable() {
        if (currentTable == null) {
            System.out.println("No table selected.");
            return;
        }
        currentTable.clearTable();
        System.out.println("Table " + currentTable.getTableId() + " has been cleared.");
    }

    public static void viewTableStatus() {
        if (currentTable == null) {
            System.out.println("No table selected.");
            return;
        }
        System.out.println(currentTable);
    }

    public static void viewAllTables() {
        if (tables.isEmpty()) {
            System.out.println("No tables created yet.");
            return;
        }
        System.out.println("\n-- All Tables --");
        for (int i = 0; i < tables.size(); i++) {
            Table t = tables.get(i);
            String status = t.isOccupied() ? "OCCUPIED" : "AVAILABLE";
            System.out.println((i + 1) + ". Table " + t.getTableId() + " (Capacity: " + t.getCapacity() + ") - " + status);
        }
    }

    public static void selectTable() {
        if (tables.isEmpty()) {
            System.out.println("No tables to select. Create one first.");
            return;
        }
        viewAllTables();
        int idx = getValidInt("Select table number: ", 0) - 1;
        if (idx >= 0 && idx < tables.size()) {
            currentTable = tables.get(idx);
            System.out.println("Selected: Table " + currentTable.getTableId());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static void assignServer() {
        if (currentTable == null) {
            System.out.println("No table selected.");
            return;
        }
        System.out.print("Enter server name: ");
        String serverName = getInput();
        currentTable.setServerName(serverName);
        System.out.println("Server " + serverName + " assigned to table " + currentTable.getTableId());
    }

    // ═══════════════════════════════════════════════════════════════
    // ORDER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    public static void orderMenu() {
        while (true) {
            System.out.println("\n===== ORDER MANAGEMENT =====");
            System.out.println("1. Create New Order");
            System.out.println("2. Add Item to Order");
            System.out.println("3. Remove Item from Order");
            System.out.println("4. View Current Order");
            System.out.println("5. Apply Discount to Order");
            System.out.println("6. Apply Flat Discount to Order");
            System.out.println("7. Submit Order to Kitchen");
            System.out.println("8. View All Orders");
            System.out.println("9. Select Existing Order");
            System.out.println("10. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = getInput();

            switch (input) {
                case "1": createOrder(); break;
                case "2": addItemToOrder(); break;
                case "3": removeItemFromOrder(); break;
                case "4": viewCurrentOrder(); break;
                case "5": applyOrderDiscount(); break;
                case "6": applyOrderFlatDiscount(); break;
                case "7": submitOrderToKitchen(); break;
                case "8": viewAllOrders(); break;
                case "9": selectOrder(); break;
                case "10": return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
            }
        }
    }

    public static void createOrder() {
        System.out.println("\n-- Create New Order --");
        try {
            currentOrder = new Order();
            orders.add(currentOrder);
            System.out.println("Order created successfully. Status: " + currentOrder.getOrderStatus());
        } catch (TooManyInstancesException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void addItemToOrder() {
        if (currentOrder == null) {
            System.out.println("No order selected. Please create one first.");
            return;
        }
        if (currentOrder.getOrderStatus() != OrderStatus.TAKING_ORDER) {
            System.out.println("Cannot modify order - it's already been submitted.");
            return;
        }
        if (menuItems.isEmpty()) {
            System.out.println("No menu items available. Create some first.");
            return;
        }
        if (currentCustomer == null) {
            System.out.println("No customer selected. Create/select a customer first.");
            return;
        }

        viewAllMenuItems();
        int idx = getValidInt("Select item to add: ", 0) - 1;
        if (idx >= 0 && idx < menuItems.size()) {
            MenuItem item = menuItems.get(idx);
            if (!item.isAvailable()) {
                System.out.println("Item is unavailable.");
                return;
            }
            currentOrder.addOrder(currentCustomer, item);
            item.decrementStock();
            System.out.println("Added " + item.getName() + " for " + currentCustomer.getName());
            System.out.println("Order total: $" + String.format("%.2f", currentOrder.getPrice()));
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static void removeItemFromOrder() {
        if (currentOrder == null) {
            System.out.println("No order selected.");
            return;
        }
        if (currentOrder.getOrderStatus() != OrderStatus.TAKING_ORDER) {
            System.out.println("Cannot modify order - it's already been submitted.");
            return;
        }
        if (currentCustomer == null) {
            System.out.println("No customer selected.");
            return;
        }
        currentOrder.removeOrder(currentCustomer);
        System.out.println("Removed item for " + currentCustomer.getName());
    }

    public static void viewCurrentOrder() {
        if (currentOrder == null) {
            System.out.println("No order selected.");
            return;
        }
        System.out.println("\n-- Current Order --");
        System.out.println("Status: " + currentOrder.getOrderStatus());
        System.out.println(currentOrder);
    }

    public static void applyOrderDiscount() {
        if (currentOrder == null) {
            System.out.println("No order selected.");
            return;
        }
        double pct = getValidDouble("Enter discount percentage (0.0 - 1.0): ", -0.01);
        try {
            currentOrder.applyDiscount(pct);
            System.out.println("Discount applied. New total: $" + String.format("%.2f", currentOrder.getPrice()));
        } catch (InvalidDiscountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void applyOrderFlatDiscount() {
        if (currentOrder == null) {
            System.out.println("No order selected.");
            return;
        }
        double amount = getValidDouble("Enter flat discount amount: ", -0.01);
        try {
            currentOrder.applyFlatDiscount(amount);
            System.out.println("Flat discount applied. New total: $" + String.format("%.2f", currentOrder.getPrice()));
        } catch (InvalidDiscountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void submitOrderToKitchen() {
        if (currentOrder == null) {
            System.out.println("No order selected.");
            return;
        }
        if (currentOrder.getOrderStatus() != OrderStatus.TAKING_ORDER) {
            System.out.println("Order already submitted.");
            return;
        }
        try {
            kitchen.addOrder(currentOrder);
            currentOrder.setOrderStatus(OrderStatus.IN_KITCHEN);
            System.out.println("Order submitted to kitchen!");
        } catch (KitchenAtCapacityException e) {
            System.out.println("Kitchen error: " + e.getMessage());
        }
    }

    public static void viewAllOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders created yet.");
            return;
        }
        System.out.println("\n-- All Orders --");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.println((i + 1) + ". Status: " + o.getOrderStatus() + " | Total: $" + String.format("%.2f", o.getPrice()));
        }
    }

    public static void selectOrder() {
        if (orders.isEmpty()) {
            System.out.println("No orders to select.");
            return;
        }
        viewAllOrders();
        int idx = getValidInt("Select order number: ", 0) - 1;
        if (idx >= 0 && idx < orders.size()) {
            currentOrder = orders.get(idx);
            System.out.println("Selected order with status: " + currentOrder.getOrderStatus());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CUSTOMER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    public static void customerMenu() {
        while (true) {
            System.out.println("\n===== CUSTOMER MANAGEMENT =====");
            System.out.println("1. Create Customer");
            System.out.println("2. View Customer Info");
            System.out.println("3. Update Customer Name");
            System.out.println("4. Add Loyalty Points");
            System.out.println("5. Redeem Loyalty Points");
            System.out.println("6. View All Customers");
            System.out.println("7. Select Existing Customer");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = getInput();

            switch (input) {
                case "1": createCustomer(); break;
                case "2": viewCustomerInfo(); break;
                case "3": updateCustomerName(); break;
                case "4": addLoyaltyPoints(); break;
                case "5": redeemLoyaltyPoints(); break;
                case "6": viewAllCustomers(); break;
                case "7": selectCustomer(); break;
                case "8": return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    public static void createCustomer() {
        System.out.println("\n-- Create Customer --");

        System.out.print("Enter customer ID: ");
        String customerId = getInput();

        System.out.print("Enter customer name: ");
        String name = getInput();

        int partySize = getValidInt("Enter party size: ", 0);

        try {
            currentCustomer = new Customer(customerId, name, partySize);
            customers.add(currentCustomer);
            System.out.println("Customer created: " + currentCustomer);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewCustomerInfo() {
        if (currentCustomer == null) {
            System.out.println("No customer selected.");
            return;
        }
        System.out.println(currentCustomer);
    }

    public static void updateCustomerName() {
        if (currentCustomer == null) {
            System.out.println("No customer selected.");
            return;
        }
        System.out.print("Enter new name: ");
        String newName = getInput();
        currentCustomer.setName(newName);
        System.out.println("Name updated: " + currentCustomer);
    }

    public static void addLoyaltyPoints() {
        if (currentCustomer == null) {
            System.out.println("No customer selected.");
            return;
        }
        int points = getValidInt("Enter points to add: ", 0);
        currentCustomer.addLoyaltyPoints(points);
        System.out.println("Points added. Total: " + currentCustomer.getLoyaltyPoints());
    }

    public static void redeemLoyaltyPoints() {
        if (currentCustomer == null) {
            System.out.println("No customer selected.");
            return;
        }
        int points = getValidInt("Enter points to redeem: ", 0);
        try {
            currentCustomer.redeemPoints(points);
            System.out.println("Points redeemed. Remaining: " + currentCustomer.getLoyaltyPoints());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAllCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers created yet.");
            return;
        }
        System.out.println("\n-- All Customers --");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i));
        }
    }

    public static void selectCustomer() {
        if (customers.isEmpty()) {
            System.out.println("No customers to select. Create one first.");
            return;
        }
        viewAllCustomers();
        int idx = getValidInt("Select customer number: ", 0) - 1;
        if (idx >= 0 && idx < customers.size()) {
            currentCustomer = customers.get(idx);
            System.out.println("Selected: " + currentCustomer.getName());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // KITCHEN MANAGEMENT
    // ═══════════════════════════════════════════════════════════════

    public static void kitchenMenu() {
        while (true) {
            System.out.println("\n===== KITCHEN MANAGEMENT =====");
            System.out.println("1. View Active Orders");
            System.out.println("2. Complete an Order");
            System.out.println("3. View Kitchen Status");
            System.out.println("4. Add Chef");
            System.out.println("5. View All Chefs");
            System.out.println("6. Select Chef");
            System.out.println("7. Chef Clock In");
            System.out.println("8. Chef Clock Out");
            System.out.println("9. Assign Order to Chef");
            System.out.println("10. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = getInput();

            switch (input) {
                case "1": viewActiveOrders(); break;
                case "2": completeOrder(); break;
                case "3": viewKitchenStatus(); break;
                case "4": addChef(); break;
                case "5": viewAllChefs(); break;
                case "6": selectChef(); break;
                case "7": chefClockIn(); break;
                case "8": chefClockOut(); break;
                case "9": assignOrderToChef(); break;
                case "10": return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
            }
        }
    }

    public static void viewActiveOrders() {
        ArrayList<Order> activeOrders = kitchen.getActiveOrders();
        if (activeOrders.isEmpty()) {
            System.out.println("No active orders in kitchen.");
            return;
        }
        System.out.println("\n-- Active Kitchen Orders --");
        for (int i = 0; i < activeOrders.size(); i++) {
            Order o = activeOrders.get(i);
            System.out.println((i + 1) + ". " + o.getOrderStatus() + " | $" + String.format("%.2f", o.getPrice()));
        }
    }

    public static void completeOrder() {
        ArrayList<Order> activeOrders = kitchen.getActiveOrders();
        if (activeOrders.isEmpty()) {
            System.out.println("No active orders to complete.");
            return;
        }
        viewActiveOrders();
        int idx = getValidInt("Select order to mark complete: ", 0) - 1;
        if (idx >= 0 && idx < activeOrders.size()) {
            Order o = activeOrders.get(idx);
            o.setOrderStatus(OrderStatus.READY);
            kitchen.removeOrder(o);
            System.out.println("Order marked as READY!");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static void viewKitchenStatus() {
        System.out.println("\n-- Kitchen Status --");
        System.out.println("Active orders: " + kitchen.getActiveOrders().size() + "/" + kitchen.getMaxCapacity());
        System.out.println("Chefs on duty: " + getOnDutyChefCount() + "/" + chefs.size());
    }

    private static int getOnDutyChefCount() {
        int count = 0;
        for (Chef c : chefs) {
            if (c.isOnDuty()) count++;
        }
        return count;
    }

    public static void addChef() {
        System.out.println("\n-- Add Chef --");

        System.out.print("Enter employee ID: ");
        String empId = getInput();

        System.out.print("Enter chef name: ");
        String name = getInput();

        System.out.print("Enter specialty (e.g., Grill, Pastry, Sauté): ");
        String specialty = getInput();

        try {
            currentChef = new Chef(empId, name, specialty);
            chefs.add(currentChef);
            kitchen.addChef(currentChef);
            System.out.println("Chef added: " + currentChef);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAllChefs() {
        if (chefs.isEmpty()) {
            System.out.println("No chefs added yet.");
            return;
        }
        System.out.println("\n-- All Chefs --");
        for (int i = 0; i < chefs.size(); i++) {
            Chef c = chefs.get(i);
            String status = c.isOnDuty() ? "ON DUTY" : "OFF";
            System.out.println((i + 1) + ". " + c.getName() + " (" + c.getSpecialty() + ") - " + status);
        }
    }

    public static void selectChef() {
        if (chefs.isEmpty()) {
            System.out.println("No chefs to select.");
            return;
        }
        viewAllChefs();
        int idx = getValidInt("Select chef number: ", 0) - 1;
        if (idx >= 0 && idx < chefs.size()) {
            currentChef = chefs.get(idx);
            System.out.println("Selected: " + currentChef.getName());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static void chefClockIn() {
        if (currentChef == null) {
            System.out.println("No chef selected.");
            return;
        }
        currentChef.clockIn();
        System.out.println(currentChef.getName() + " clocked in.");
    }

    public static void chefClockOut() {
        if (currentChef == null) {
            System.out.println("No chef selected.");
            return;
        }
        currentChef.clockOut();
        System.out.println(currentChef.getName() + " clocked out.");
    }

    public static void assignOrderToChef() {
        if (currentChef == null) {
            System.out.println("No chef selected.");
            return;
        }
        if (!currentChef.isOnDuty()) {
            System.out.println("Chef is not on duty. Clock them in first.");
            return;
        }
        ArrayList<Order> activeOrders = kitchen.getActiveOrders();
        if (activeOrders.isEmpty()) {
            System.out.println("No active orders to assign.");
            return;
        }
        viewActiveOrders();
        int idx = getValidInt("Select order to assign: ", 0) - 1;
        if (idx >= 0 && idx < activeOrders.size()) {
            Order o = activeOrders.get(idx);
            currentChef.acceptOrder(o);
            System.out.println("Order assigned to " + currentChef.getName());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════

    public static String getInput() {
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("EXIT")) {
            System.out.println("Exiting program. Goodbye!");
            scanner.close();
            System.exit(0);
        }
        return input;
    }

    public static double getValidDouble(String prompt, double minValue) {
        while (true) {
            System.out.print(prompt);
            String input = getInput();
            try {
                double value = Double.parseDouble(input);
                if (value <= minValue) {
                    System.out.println("Value must be greater than " + minValue + ". Try again.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public static int getValidInt(String prompt, int minValue) {
        while (true) {
            System.out.print(prompt);
            String input = getInput();
            try {
                int value = Integer.parseInt(input);
                if (value <= minValue) {
                    System.out.println("Value must be greater than " + minValue + ". Try again.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }
}