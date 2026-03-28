package model;

import core.Discountable;
import exceptions.InvalidDiscountException;
import exceptions.TooManyInstancesException;
import exceptions.InvalidOrderState;
import util.Constants;
import util.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class Order implements Discountable {
    private static int numOrders = 0;
    private HashMap<Customer, MenuItem> orderList;
    private double totalPrice;
    private OrderStatus orderStatus;

    public Order() throws TooManyInstancesException {
        if (numOrders >= Constants.MAXIMUM_INSTANCES) {
            throw new TooManyInstancesException("Maximum number of Order instances reached");
        }
        orderList = new HashMap<>();
        orderStatus = OrderStatus.TAKING_ORDER;
        numOrders++;
        totalPrice = 0;
    }

    public void addOrder(Customer customer, MenuItem order) throws InvalidOrderState{
        if (orderStatus != OrderStatus.TAKING_ORDER){
            throw new InvalidOrderState("Cannot modify order after it has been submitted");
        }
        if (orderList.containsKey(customer)){
            totalPrice -= orderList.get(customer).getPrice();
        }
        orderList.put(customer, order);
        totalPrice += order.getPrice();
    }

    public void removeOrder(Customer customer) throws InvalidOrderState{
        if (orderStatus != OrderStatus.TAKING_ORDER){
            throw new InvalidOrderState("Cannot modify order after it has been submitted");
        }
        totalPrice -= orderList.get(customer).getPrice();
        orderList.remove(customer);
    }

    @Override
    public double getPrice() {
        return totalPrice;
    }

    public void setOrderStatus(OrderStatus status) {
        orderStatus = status;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public ArrayList<MenuItem> getOrderContents() {
        ArrayList<MenuItem> order = new ArrayList<>();
        for (MenuItem item : orderList.values()) {
            order.add(item);
        }
        return order;
    }

    @Override
    public void applyFlatDiscount(double discountAmount) throws InvalidDiscountException {
        if (totalPrice - discountAmount < 0) {
            throw new InvalidDiscountException("Flat discount cannot make the price negative");
        }
        if (discountAmount < 0){
            throw new InvalidDiscountException("Discount amount cannot be negative");
        }
        totalPrice -= discountAmount;
    }

    @Override
    public void applyDiscount(double percentage) throws InvalidDiscountException {
        if (percentage < 0 || percentage > 1) {
            throw new InvalidDiscountException("Percentage discount must be between 0 and 1");
        } else {
            totalPrice *= (1 - percentage);
        }
    }

    @Override
    public String toString() {
        String returnString = "";
        for (Customer customer : orderList.keySet()) {
            returnString += customer.getName() + " ordered " + orderList.get(customer).getName() + "\n";
        }
        returnString += "Order status: " + orderStatus;
        returnString += "Total price: " + totalPrice;
        return returnString;
    }
}