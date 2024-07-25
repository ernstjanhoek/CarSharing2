package carsharing.customer;

import carsharing.car.Car;
import carsharing.car.CarDao;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private final CustomerDao customerDao;
    private final CarDao carDao;

    public CustomerService(CustomerDao customerDao, CarDao carDao) {
        this.customerDao = customerDao;
        this.carDao = carDao;
    }

    public boolean createCustomer(String name) throws SQLException {
        Customer customer = new Customer(-1, name, null);
        return customerDao.create(customer);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDao.readAll();
    }

    public Customer getCustomer(int id) throws SQLException {
        return customerDao.read(id);
    }

    public Customer getCustomerByName(String name) throws SQLException {
        return customerDao.readByName(name);
    }

    public boolean rentCar(Customer customer, Car car) throws SQLException {
        if (car.isAvailable()) {
            car.setAvailable(false);
            carDao.update(car);
            customer.setCar(car);
            customerDao.update(customer);
            return true;
        }
        return false;
    }

    public boolean returnCar(Customer customer) throws SQLException {
        if (customer.getCar() != null) {
            customer.getCar().setAvailable(true);
            carDao.update(customer.getCar());
            customer.setCar(null);
            customerDao.update(customer);
            return true;
        }
        return false;
    }
}
