import java.util.HashMap;

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
    }

    public void removeOrder(Customer customer){
        orderList.remove(customer);
    }

    public double getPrice(){
        return totalPrice;
    }

    public void submitOrder(){
        if (orderStatus == OrderStatus.TAKING_ORDER){
            orderStatus = OrderStatus.IN_KITCHEN;
            //TODO: iterate over orderList and update stock based on orders taken
        }else{
            // throw error
        }
    }

    public void applyFlatDiscount(double discountAmount){
        if (totalPrice - discountAmount < 0){
            // Current implementation eats discount over allowed amount. Maybe throw error instead?
            totalPrice = 0;
        }else{
            totalPrice -= discountAmount;
        }
    }

    public void applyDiscount(double percentage){
        if (percentage < 0 || percentage > 1){
            // throw error
        }else{
            totalPrice *= (1 - percentage);
        }
    }
}