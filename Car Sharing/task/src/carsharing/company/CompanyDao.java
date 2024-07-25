package carsharing.company;

import carsharing.inherit.CRUD;
import carsharing.DBClient;
import carsharing.inherit.Dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDao extends Dao implements CRUD<Company> {
    public CompanyDao(DBClient client) {
        super(client);
    }

    @Override
    public boolean create(Company company) throws SQLException {
        String sql = "INSERT INTO company (name) VALUES (?)";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)){
            statement.setString(1, company.getName());
            return statement.execute();
        }
    }

    @Override
    public Company read(int id) throws SQLException {
        String sql = "SELECT * FROM company WHERE id = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return new Company(rs.getInt("id"), rs.getString("name"));
        }
    }

    public Company readByName(String name) throws SQLException {
        String sql = "SELECT * FROM company WHERE name = ?";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Company(rs.getInt("id"), rs.getString("name"));
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Company> readAll() throws SQLException {
        List<Company> companyList= new ArrayList<>();
        String sql = "SELECT * FROM company";
        try (PreparedStatement statement = client.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int id = resultSet.getInt("id");
                companyList.add(new Company(id, name));
            }
            resultSet.close();
        }
        return companyList;
    }

    @Override
    public boolean update(Company company) throws SQLException {
        return false;
    }
}
