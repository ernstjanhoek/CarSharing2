package carsharing.car;

import carsharing.company.Company;
import carsharing.inherit.CRUD;
import carsharing.DBClient;
import carsharing.inherit.Dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDao extends Dao implements CRUD<Car> {

    public CarDao(DBClient client) {
        super(client);
    }

    @Override
    public boolean create(Car car) throws SQLException {
        String sql = "INSERT INTO car (name, company_id, available) VALUES (?, ?, ?)";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)){
            statement.setString(1, car.getName());
            statement.setInt(2, car.getCompany().getId());
            statement.setBoolean(3, car.isAvailable());
            return statement.execute();
        }
    }

    @Override
    public Car read(int id) throws SQLException {
        String sqlCar = "SELECT car.id AS car_id, car.name AS car_name, car.available AS car_available, " +
                "company.id AS company_id, company.name AS company_name " +
                "FROM car " +
                "INNER JOIN company ON car.company_id = company.id " +
                "WHERE car.id = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sqlCar)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Company company = new Company(rs.getInt("company.id"), rs.getString("company.name"));

                return new Car(rs.getInt("car.id"), rs.getString("car.name"), rs.getBoolean("car.available"), company);
            }
        }
        return null;
    }

    @Override
    public List<Car> readAll() throws SQLException {
        List<Car> carList= new ArrayList<>();
        String sql = "SELECT car.id, car.name, car.available, company.id, company.name "
                + "FROM car, company " +
                "WHERE car.company_id = company.id";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String carName = resultSet.getString("car.name");
                int carId = resultSet.getInt("car.id");
                String companyName = resultSet.getString("company.name");
                boolean isAvailable = resultSet.getBoolean("car.available");
                int companyId = resultSet.getInt("company.id");
                Company company = new Company(companyId, companyName);
                carList.add(new Car(carId, carName, isAvailable, company));
            }
            resultSet.close();
        }
        return carList;
    }

    @Override
    public boolean update(Car car) throws SQLException {
        String sql = "UPDATE car SET name = ?, company_id = ?, available = ? WHERE id = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setString(1, car.getName());
            statement.setInt(2, car.getCompany().getId());
            statement.setBoolean(3, car.isAvailable());
            statement.setInt(4, car.getId());
            return statement.executeUpdate() > 0;
        }
    }
}
