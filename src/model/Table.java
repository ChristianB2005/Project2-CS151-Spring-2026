package model;
import java.util.ArrayList;

public class Table{
    private static int numTables = 0;
    private int maxCapacity;
    private Server server;
    private boolean isOccupied;
    private ArrayList<Customer> customersAtTable = new ArrayList<Customer>();

    public Table(int maxCapacity){
        if (numTables >= Constants.MAXIMUM_INSTANCES){
            throw new RuntimeException("Maximum number of Table instances reached.");
        }
        numTables++;
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity(){
        return this.maxCapacity;
    }

    public void assignServer(Server newServer){
        this.server = newServer;
    }

    public boolean isOccupied(){
        return this.isOccupied;
    }

    public void setOccupation(boolean isOccupied){
        this.isOccupied = isOccupied;
    }

    public void clearOccupancy(){
        this.server = null;
        customersAtTable.clear();
    }

    public void addCustomer(Customer newCustomer){
        customersAtTable.add(newCustomer);
    }
}