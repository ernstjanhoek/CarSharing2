package carsharing;

import carsharing.car.Car;
import carsharing.car.CarService;
import carsharing.company.CompanyService;
import carsharing.company.Company;
import carsharing.customer.Customer;
import carsharing.customer.CustomerService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class ConsoleMenuController {
    private Menu menuState = Menu.MAIN;
    private final CompanyService companyService;
    private final CarService carService;
    private final CustomerService customerService;
    Scanner scanner = new Scanner(System.in);

    Company selectedCompany;
    Customer selectedCustomer;

    public ConsoleMenuController(CompanyService companyService, CarService carService, CustomerService customerService) {
        this.companyService = companyService;
        this.carService = carService;
        this.customerService = customerService;
    }

    private enum Menu {
        MAIN, EXIT, COMPANY_LIST, COMPANY_MANAGER, COMPANY_SELECT, CAR_LIST, CUSTOMER_SELECT, CUSTOMER_MANAGER, CAR_SELECT
    }

    public boolean isRunning() {
        return menuState != Menu.EXIT;
    }

    void processMenu() {
        switch (menuState) {
            case MAIN -> mainMenu();
            case COMPANY_SELECT -> companySelect();
            case CUSTOMER_SELECT -> customerListMenu();
            case COMPANY_LIST -> companyListMenu();
            case COMPANY_MANAGER -> companyManager();
            case CAR_LIST -> carListMenu();
            case CUSTOMER_MANAGER -> customerManager();
            case CAR_SELECT -> carSelect();
        }
    }

    private void carSelect() {
        try {
            List<Car> availableCars = carService.getAllAvailableCarsByCompany(selectedCompany.getName());
            System.out.println("Choose a car:");
            for (int i = 0; i < availableCars.size(); i++) {
                System.out.printf("%s. %s\n", i + 1, availableCars.get(i).getName());
            }
            int choice = Integer.parseInt(scanner.nextLine());
            customerService.rentCar(selectedCustomer, availableCars.get(choice - 1));
            System.out.println("You rented '" + selectedCustomer.getCar().getName() + "'");
            menuState = Menu.CUSTOMER_MANAGER;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }

    private void customerManager() {
        try {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                if (selectedCustomer.getCar() == null) {
                    List<Company> companyList = companyService.getAllCompanies();
                    System.out.println("Choose a company: ");
                    if (companyList.isEmpty()) {
                        System.out.println("The company list is empty");
                        return;
                    } else {
                        for (int i = 0; i < companyList.size(); i++) {
                            System.out.printf("%d. %s\n", i+1, companyList.get(i).getName());
                        }
                    }
                    System.out.println("0. Back");
                    int companyChoice = Integer.parseInt(scanner.nextLine());
                    if (companyChoice > 0) {
                        selectedCompany = companyList.get(companyChoice - 1);
                        menuState = Menu.CAR_SELECT;
                    }
                } else {
                    System.out.println("You've already rented a car!");
                }
            } else if (choice == 2) {
                if (selectedCustomer.getCar() != null) {
                    customerService.returnCar(selectedCustomer);
                    System.out.println("You've returned a rented car!");
                } else {
                    System.out.println("You didn't rent a car!");
                }
            } else if (choice == 3) {
                if (selectedCustomer.getCar() != null) {
                    System.out.println("Your rented car:");
                    System.out.println(selectedCustomer.getCar().getName());
                    System.out.println("Company:");
                    System.out.println(selectedCustomer.getCar().getCompany().getName());
                } else {
                    System.out.println("You didn't rent a car!");
                }
            } else if (choice == 0) {
                menuState = Menu.MAIN;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }

    private void companyManager() {
        try {
            System.out.println(selectedCompany.getName() + " company:");
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                menuState = Menu.CAR_LIST;
            } else if (choice == 2) {
                System.out.println("Enter the car name:");
                String name = scanner.nextLine();
                Car car = new Car(-1, name, true, selectedCompany);
                carService.addCar(car);

            } else if (choice == 0) {
                menuState = Menu.COMPANY_SELECT;

            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }

    private void carListMenu() {
        try {
            System.out.println(selectedCompany.getName() + " cars:");
            List<Car> carList = carService.getAllCarsByCompany(selectedCompany.getName());
            if (carList.isEmpty()) {
                System.out.println("The car list is empty!");
            } else {
                for (int i = 0; i < carList.size(); i++) {
                    System.out.printf("%d. %s\n", i+1, carList.get(i).getName());
                }
            }
            menuState = Menu.COMPANY_MANAGER;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }

    private void customerListMenu() {
       try {
           List<Customer> customerList = customerService.getAllCustomers();
           System.out.println("Choose a customer:");
           if (customerList.isEmpty()) {
               System.out.println("The customer list is empty!");
               menuState = Menu.MAIN;
           } else {
               for (int i = 0; i < customerList.size(); i++) {
                   System.out.printf("%d. %s\n", i+1, customerList.get(i).getName());
               }
               System.out.println("0. Back");
               int choice = Integer.parseInt(scanner.nextLine());
               if (choice == 0) {
                   menuState = Menu.COMPANY_SELECT;
               } else {
                   selectedCustomer = customerList.get(choice - 1);
                   menuState = Menu.CUSTOMER_MANAGER;
               }
           }
       } catch (Exception e) {
           System.out.println("Error: " + e.getMessage());
           System.out.println("Try again");
       }
    }

    private void companyListMenu() {
        try {
            List<Company> companyList = companyService.getAllCompanies();
            System.out.println("Choose a company: ");
            if (companyList.isEmpty()) {
                System.out.println("The company list is empty");
                menuState = Menu.COMPANY_SELECT;
                return;
            } else {
                for (int i = 0; i < companyList.size(); i++) {
                    System.out.printf("%d. %s\n", i+1, companyList.get(i).getName());
                }
            }
            System.out.println("0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                menuState = Menu.COMPANY_SELECT;
            } else {
                selectedCompany = companyList.get(choice - 1);
                menuState = Menu.COMPANY_MANAGER;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }

    private void mainMenu() {
        try {
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            System.out.println("0. Exit");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                menuState = Menu.COMPANY_SELECT;
            } else if (choice == 2){
                menuState = Menu.CUSTOMER_SELECT;
            } else if (choice == 3) {
                System.out.println("Enter the customer name:");
                String name = scanner.nextLine();
                customerService.createCustomer(name);
            } else if (choice == 0) {
                menuState = Menu.EXIT;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }

    private void companySelect() {
        try {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                menuState = Menu.COMPANY_LIST;
            } else if (choice == 2) {
                System.out.println("Enter the company name:");
                String name = scanner.nextLine();
                if (companyService.addCompany(name)) {
                    System.out.println("The company was created!");
                }
            } else if (choice == 0) {
                menuState = Menu.MAIN;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try again");
        }
    }
}
