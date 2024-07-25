package carsharing.customer;

import carsharing.car.Car;

public class Customer {
    private final int id;
    private final String name;
    private Car car;

    public Customer(int id, String name, Car car) {
        this.id = id;
        this.name = name;
        this.car = car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Car getCar() {
        return car;
    }
}
