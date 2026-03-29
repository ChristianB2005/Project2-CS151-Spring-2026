package core;

public abstract class Employee {
    private String employeeId;
    private String name;
    private boolean isOnDuty;

    public Employee(String employeeId, String name) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty.");
        }
        this.employeeId = employeeId.trim();
        this.name = name.trim();
        this.isOnDuty = false;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public boolean isOnDuty() {
        return isOnDuty;
    }

    public void setEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty.");
        }
        this.employeeId = employeeId.trim();
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public void setOnDuty(boolean onDuty) {
        this.isOnDuty = onDuty;
    }

    public void clockIn() {
        this.isOnDuty = true;
    }

    public void clockOut() {
        this.isOnDuty = false;
    }

    public abstract void updateStatus(String status);

    @Override
    public String toString() {
        return "Employee[id=" + employeeId +
                ", name=" + name +
                ", onDuty=" + isOnDuty + "]";
    }
}