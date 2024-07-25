package carsharing;

import carsharing.car.CarDao;
import carsharing.car.CarService;
import carsharing.company.CompanyDao;
import carsharing.company.CompanyService;
import carsharing.customer.CustomerDao;
import carsharing.customer.CustomerService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        String nameDB = args.length >= 2 ? args[1] : "carsharing";
        String urlDB = "jdbc:h2:./src/carsharing/db/" + nameDB;

        DBClient dbClient = new DBClient(urlDB);

        dbClient.createDataBase();
        CompanyDao companyDao = new CompanyDao(dbClient);
        CarDao carDao = new CarDao(dbClient);

        CompanyService companyService = new CompanyService(companyDao);
        CarService carService = new CarService(carDao);

        CustomerDao customerDao = new CustomerDao(dbClient, carDao);
        CustomerService customerService = new CustomerService(customerDao, carDao);

        // MockApp mockApp = new MockApp(carService, companyService, customerService);

        ConsoleMenuController menuController = new ConsoleMenuController(companyService, carService, customerService);

        while (menuController.isRunning()) {
            menuController.processMenu();
        }
    }
}