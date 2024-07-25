package carsharing.car;

import carsharing.company.Company;

public class Car {
    private final int id;
    private final String name;
    private boolean available;
    private final Company company;

    public Car(int id, String name, boolean available, Company company) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.available = available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }
}
