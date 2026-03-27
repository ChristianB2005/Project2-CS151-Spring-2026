package model;
import core.Discountable;
import exceptions.InvalidDiscountException;
import exceptions.TooManyInstancesException;
import java.util.ArrayList;
import java.util.HashMap;
import util.Constants;
import util.OrderStatus;

public class Order implements Discountable{
    private static int numOrders = 0;
    private HashMap<Customer, MenuItem> orderList;
    private double totalPrice;
    private OrderStatus orderStatus;

    public Order() throws TooManyInstancesException{
        if (numOrders >= Constants.MAXIMUM_INSTANCES){
            throw new TooManyInstancesException("Maximum number of Order instances reached");
        }
        orderList = new HashMap<>();
        orderStatus = OrderStatus.TAKING_ORDER;
        numOrders++;
        totalPrice = 0;
    }

    public void addOrder(Customer customer, MenuItem order) {
        if (orderStatus != OrderStatus.TAKING_ORDER){
            throw new IllegalStateException("Order.status must be set to OrderStatus.READY before adding order.");
        }
        orderList.put(customer, order);
        totalPrice += order.getPrice();
    }

    public void removeOrder(Customer customer){
        if (orderStatus != OrderStatus.TAKING_ORDER){
            throw new IllegalStateException("Order.status must be set to OrderStatus.TAKING_ORDER before adding order.");
        }
        totalPrice -= orderList.get(customer).getPrice();
        orderList.remove(customer);
    }
@Override
    public double getPrice(){
        return totalPrice;
    }

    public void setOrderStatus(OrderStatus status){
        orderStatus = status;
    }

    public OrderStatus getOrderStatus(){
        return orderStatus;
    }

    public ArrayList<MenuItem> getOrderContents(){
        ArrayList<MenuItem> order = new ArrayList<>();
        for (MenuItem item : orderList.values()){
            order.add(item);
        }
        return order;
    }

@Override
    public void applyFlatDiscount(double discountAmount) throws InvalidDiscountException{


    public void applyFlatDiscount(double discountAmount) throws InvalidDiscountException {

        if (totalPrice - discountAmount < 0){
            throw new InvalidDiscountException("Flat discount cannot make the price negative");
        }else{
            totalPrice -= discountAmount;
        }
    }

@Override
    public void applyDiscount(double percentage) throws InvalidDiscountException{
      
    public void applyDiscount(double percentage) throws InvalidDiscountException {

        if (percentage < 0 || percentage > 1){
            throw new InvalidDiscountException("Percentage discount must be between 0 and 1");
        }else{
            totalPrice *= (1 - percentage);
        }
    }

    @Override
    public String toString(){
        String returnString = "";
        for (Customer customer : orderList.keySet()){
            returnString += customer.getName() + " ordered " + orderList.get(customer).getName() + "\n";
        }
        returnString += "Total price: " + totalPrice;
        return returnString;
    }
}