package carsharing;

import carsharing.car.Car;
import carsharing.car.CarService;
import carsharing.company.Company;
import carsharing.company.CompanyService;
import carsharing.customer.Customer;
import carsharing.customer.CustomerService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockApp {
    private final CarService carService;
    private final CompanyService companyService;
    private final CustomerService customerService;

    public MockApp(CarService carService, CompanyService companyService, CustomerService customerService) {
        this.carService = carService;
        this.companyService = companyService;
        this.customerService = customerService;
        try {
            addSomeCompanies();
            addSomeCars();
            Optional<Car> car = carService.getAllCars().stream().filter(Car::isAvailable).findFirst();
            if (car.isPresent()) {
                addCustomer("Ernst");
            }

            addCustomer("Henk");

            List<Customer> customerList = new ArrayList<>();

            customerList.add(customerService.getCustomerByName("Ernst"));
            customerList.add(customerService.getCustomerByName("Henk"));

            List<Car> carList = carService.getAllCars();

            // rent car
            customerService.rentCar(customerList.get(0), carList.get(0));
            customerService.rentCar(customerList.get(1), carList.get(2));

            // return cars
            customerList.stream().filter(c -> c.getCar() != null).forEach(c -> {
                try {
                    customerService.returnCar(c);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            List<Car> cars = carService.getAllCars();
            cars.forEach(c -> {
                        System.out.print("car/company: " + c.getName() + " is available: " + c.isAvailable());
                        System.out.println("/" + c.getCompany().getName());

                    }
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSomeCompanies() throws SQLException {
        companyService.addCompany("Sachsenring");
        companyService.addCompany("Eisenach");
    }

    private void addSomeCars() throws SQLException {
        Company sachsenRing = companyService.getByName("Sachsenring");
        Car p50 = new Car(-1, "Trabant P50", true, sachsenRing);
        Car tr500 = new Car(-1, "Trabant 500", true, sachsenRing);
        Car tr600 = new Car(-1, "Trabant 600", true, sachsenRing);
        carService.addCar(p50);
        carService.addCar(tr500);
        carService.addCar(tr600);

        Company eisenach = companyService.getByName("Eisenach");
        Car w353 = new Car(-1, "Wartburg 353", true, eisenach);
        Car w13 = new Car(-1, "Wartburg 1.3", true, eisenach);
        carService.addCar(w353);
        carService.addCar(w13);
    }

    private void addCustomer(String name) throws SQLException {
        customerService.createCustomer(name);

    }
}
