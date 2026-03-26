public class Chef extends Employee {
    private String specialty;
    private Order[] currentOrders;
    private int orderCount;
    private static int instanceCount = 0;

    public Chef(String employeeId, String name, String specialty) {
        super(employeeId, name);

        if (instanceCount >= Constants.MAXIMUM_INSTANCES) {
            throw new IllegalStateException("Maximum number of Chef instances reached.");
        }

        this.specialty = specialty;
        this.currentOrders = new Order[Constants.MAXIMUM_INSTANCES];
        this.orderCount = 0;
        instanceCount++;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Order[] getCurrentOrders() {
        return currentOrders;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public void clockIn() {
        // TODO: mark chef as on duty
    }

    public void acceptOrder(Order order) {
        // TODO:
        // 1. validate order is not null
        // 2. validate chef is on duty
        // 3. add order to currentOrders if space exists
        // 4. possibly update order status to "IN_PROGRESS"
    }

    public void completeOrder(Order order) {
        // TODO:
        // 1. locate the order in currentOrders
        // 2. remove it from the array
        // 3. shift remaining orders left
        // 4. decrement orderCount
        // 5. mark order as ready/completed
    }

    @Override
    public void updateStatus(String status) {
        // TODO:
        // define what "status" means for Chef in your project
        // examples: "Available", "Busy", "Off Duty"
    }

    public String viewQueue() {
        // TODO: return a formatted String of current orders
        return "";
    }

    @Override
    public String toString() {
        return "Chef{" +
                "employeeId='" + getEmployeeId() + '\'' +
                ", name='" + getName() + '\'' +
                ", specialty='" + specialty + '\'' +
                ", isOnDuty=" + isOnDuty() +
                ", orderCount=" + orderCount +
                '}';
    }
}