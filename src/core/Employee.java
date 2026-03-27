package core;
public abstract class Employee {
    protected String employeeId;
    protected String name;
    protected boolean isOnDuty;

    public Employee(String employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
        this.isOnDuty = false;
    }

    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public boolean isOnDuty() { return isOnDuty; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setName(String name) { this.name = name; }
    public void setOnDuty(boolean onDuty) { this.isOnDuty = onDuty; }

    public void clockIn() { this.isOnDuty = true; }
    public void clockOut() { this.isOnDuty = false; }

    public abstract void updateStatus(String status);

    @Override
    public String toString() {
        return "Employee[id=" + employeeId + ", name=" + name + ", onDuty=" + isOnDuty + "]";
    }
}