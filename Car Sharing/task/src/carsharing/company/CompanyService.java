package carsharing.company;

import java.sql.SQLException;
import java.util.List;

public class CompanyService {
    CompanyDao companyDao;

    public CompanyService(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public Company getById(int id) throws SQLException {
        return companyDao.read(id);
    }

    public Company getByName(String name) throws SQLException {
        return companyDao.readByName(name);
    }

    public List<Company> getAllCompanies() throws SQLException {
        return companyDao.readAll();
    }

    public boolean addCompany(String companyName) throws SQLException {
        Company company = new Company(-1, companyName);
        return companyDao.create(company);
    }
}
