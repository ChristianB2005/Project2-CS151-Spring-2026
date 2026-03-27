package model;
import java.util.HashMap;

import core.Discountable;
import exceptions.InvalidDiscountException;
import util.OrderStatus;

public class Order implements Discountable{
    private static int numOrders = 0;
    private HashMap<Customer, MenuItem> orderList;
    private double totalPrice;
    private OrderStatus orderStatus;

    public Order(){
        if (numOrders >= Constants.MAXIMUM_INSTANCES){
            throw new RuntimeException("Maximum number of Order instances reached");
        }
        orderList = new HashMap<>();
        orderStatus = OrderStatus.TAKING_ORDER;
        numOrders++;
        totalPrice = 0;
    }

    public void addOrder(Customer customer, MenuItem order){
        if (orderStatus != OrderStatus.TAKING_ORDER){
            // throw error
        }
        orderList.put(customer, order);
        //TODO increment price from MenuItem
    }

    public void removeOrder(Customer customer){
        orderList.remove(customer);
        //TODO decrement price from MenuItem
    }

    public double getPrice(){
        return totalPrice;
    }

    public void setOrderStatus(OrderStatus status){
        orderStatus = status;
    }

    public OrderStatus getOrderStatus(){
        return orderStatus;
    }

    public void applyFlatDiscount(double discountAmount) throws InvalidDiscountException{
        if (totalPrice - discountAmount < 0){
            throw new InvalidDiscountException("Flat discount cannot make the price negative");
        }else{
            totalPrice -= discountAmount;
        }
    }

    public void applyDiscount(double percentage) throws InvalidDiscountException{
        if (percentage < 0 || percentage > 1){
            throw new InvalidDiscountException("Percentage discount must be between 0 and 1");
        }else{
            totalPrice *= (1 - percentage);
        }
    }
}