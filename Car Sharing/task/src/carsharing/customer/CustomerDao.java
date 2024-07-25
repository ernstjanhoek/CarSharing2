package carsharing.customer;

import carsharing.DBClient;
import carsharing.car.Car;
import carsharing.car.CarDao;
import carsharing.company.Company;
import carsharing.inherit.CRUD;
import carsharing.inherit.Dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao extends Dao implements CRUD<Customer> {
    CarDao carDao;
    public CustomerDao(DBClient client, CarDao carDao) {
        super(client);
        this.carDao = carDao;
    }

    @Override
    public boolean create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (name, rented_car_id) VALUES (?,?)";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)){
            statement.setString(1, customer.getName());
            if (customer.getCar() != null) {
                statement.setInt(2, customer.getCar().getId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
    @Override
    public Customer read(int id) throws SQLException {
        String sql = "SELECT c.id AS customer_id, c.name AS customer_name, " +
                "c.rented_car_id, " +
                "ca.id AS car_id, ca.name AS car_name, ca.available AS car_available, " +
                "ca.company_id, co.id AS company_id, co.name AS company_name " +
                "FROM customer c " +
                "LEFT JOIN car ca ON c.car_id = ca.id " +
                "LEFT JOIN company co ON ca.company_id = co.id " +
                "WHERE c.id = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            return buildCustomerFromResultSet(statement.executeQuery());
        }
    }

    public Customer readByName(String name) throws SQLException {
        String sql = "SELECT c.id AS customer_id, c.name AS customer_name, " +
                "c.rented_car_id, " +
                "ca.id AS car_id, ca.name AS car_name, ca.available AS car_available, " +
                "ca.company_id, co.id AS company_id, co.name AS company_name " +
                "FROM customer c " +
                "LEFT JOIN car ca ON c.car_id = ca.id " +
                "LEFT JOIN company co ON ca.company_id = co.id " +
                "WHERE c.name = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            return buildCustomerFromResultSet(statement.executeQuery());
        }
    }

    private Customer buildCustomerFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Car car = null;
            int carId = rs.getInt("car_id");
            if (!rs.wasNull()) {
                Company company = new Company(rs.getInt("company_id"), rs.getString("company_name"));
                car = new Car(
                        rs.getInt("car_id"),
                        rs.getString("car_name"),
                        rs.getBoolean("car_available"),
                        company
                );
            }
            return new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    car
            );
        } else {
            return null;
        }

    }

    private Car extractCarFromResultSet(ResultSet rs) throws SQLException {
        int carId = rs.getInt("rented_car_id");
        if (!rs.wasNull()) {
            return carDao.read(carId);
        }
        return null;
    }

    @Override
    public List<Customer> readAll() throws SQLException {
        List<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM customer";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int id = resultSet.getInt("id");
                customerList.add(new Customer(id, name, extractCarFromResultSet(resultSet)));
            }
            resultSet.close();
        }
        return customerList;
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET name = ?, rented_car_id = ? WHERE id = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setString(1, customer.getName());
            if (customer.getCar() != null) {
                statement.setInt(2, customer.getCar().getId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            statement.setInt(3, customer.getId());
            int updatedRows = statement.executeUpdate();
            return updatedRows > 0;
        }
    }
}
