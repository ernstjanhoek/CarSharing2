package carsharing.customer;

import carsharing.DBClient;
import carsharing.car.Car;
import carsharing.car.CarDao;
import carsharing.company.Company;
import carsharing.company.CompanyDao;
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
        String sql = "SELECT c.id as customer_id, c.name as customer_name, " +
                "c.rented_car_id, ca.id as car_id, ca.name as car_name, ca.available as car_available, ca.company_id as company_id " +
                "FROM customer c " +
                "LEFT JOIN car ca ON c.car_id = ca.id " +
                "WHERE c.id = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        extractCarFromResultSet(rs)
                );
            } else {
                return null;
            }
        }
    }

    public Customer readByName(String name) throws SQLException {
        String sql = "SELECT * FROM customer WHERE name = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        extractCarFromResultSet(rs)
                );
            } else {
                return null;
            }
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
