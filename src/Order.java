import java.util.HashMap;

public class Order implements Discountable{
    private static int numOrders = 0;
    private HashMap<Customer, MenuItem> orderList;
    

    public Order(){
        if (numOrders >= Constants.MAXIMUM_INSTANCES){
            throw new RuntimeException("Maximum number of Order instances reached");
        }
        orderList = new HashMap<>();
    }

    public void addOrder(Customer customer, MenuItem order){
        orderList.put(customer, order);
    }

    public void removeOrder(Customer customer){
        orderList.remove(customer);
    }
}