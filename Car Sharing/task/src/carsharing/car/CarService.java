package carsharing.car;

import java.sql.SQLException;
import java.util.List;

public class CarService {
    private final CarDao carDao;

    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public boolean addCar(Car car) throws SQLException {
        return carDao.create(car);
    }

    public List<Car> getAllCars() throws SQLException {
        return carDao.readAll();
    }

    public List<Car> getAllCarsByCompany(String company) throws SQLException {
        List<Car> unfilteredList = carDao.readAll();
        return unfilteredList.stream()
                .filter(c -> c.getCompany().getName().equals(company))
                .toList();
    }

    public List<Car> getAllAvailableCarsByCompany(String company) throws SQLException {
        List<Car> unfilteredList = carDao.readAll();
        return unfilteredList.stream()
                .filter(c -> c.isAvailable() && c.getCompany().getName().equals(company))
                .toList();
    }
}
