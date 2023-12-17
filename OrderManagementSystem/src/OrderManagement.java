package src;
import src.dao.*;
import src.entity.*;
import src.exception.*;
import src.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderManagement {
    private static final Scanner scanner = new Scanner(System.in);
    private static final IOrderManagementRepository orderManagementRepository = new OrderProcessor();


    
    public static void main(String[] args) {
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    createProduct();
                    break;
                case 3:
                    createOrder();
                    break;
                case 4:
                    cancelOrder();
                    break;
                case 5:
                    getAllProducts();
                    break;
                case 6:
                    getOrderByUser();
                    break;
                case 7:
                    System.out.println("Exiting the program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }

        } while (choice != 7);

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n*** Order Management System Menu ***");
        System.out.println("1. Create User");
        System.out.println("2. Create Product");
        System.out.println("3. Create Order");
        System.out.println("4. Cancel Order");
        System.out.println("5. Get All Products");
        System.out.println("6. Get Order by User");
        System.out.println("7. Exit");
    }

    private static void createUser() {
        System.out.println("\n*** Create User ***");
        System.out.print("Enter userId: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (Admin/User): ");
        String role = scanner.nextLine();

        User newUser = new User(userId, username, password, role);
        orderManagementRepository.createUser(newUser);
        System.out.println("User created successfully!");
    }

    private static void createProduct() {
        System.out.println("\n*** Create Product ***");
        System.out.print("Enter userId for authentication: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        User user = new User(userId, "", "", "");

        if (orderManagementRepository.isAdmin(userId)) {
            System.out.print("Enter productId: ");
            int productId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter productName: ");
            String productName = scanner.nextLine();
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            System.out.print("Enter price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter quantityInStock: ");
            int quantityInStock = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter type (Electronics/Clothing): ");
            String type = scanner.nextLine();

            Product newProduct = new Product(productId, productName, description, price, quantityInStock, type);
            orderManagementRepository.createProduct(user, newProduct);
            System.out.println("Product created successfully!");
        } else {
            System.out.println("Access denied. Only admins can create products.");
        }
    }

    private static void createOrder() {
        System.out.println("\n*** Create Order ***");
        System.out.print("Enter userId for authentication: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        User user = new User(userId, "", "", "");

        List<Product> products = new ArrayList<>();
        boolean continueAdding = true;

        while (continueAdding) {
            System.out.print("Enter productId to add to the order (0 to finish): ");
            int productId = scanner.nextInt();
            scanner.nextLine();

            if (productId == 0) {
                continueAdding = false;
            } else {
                Product product = new Product(productId, "", "", 0.0, 0, ""); // Placeholder product
                products.add(product);
            }
        }

        orderManagementRepository.createOrder(user, products);
        System.out.println("Order created successfully!");
    }

    private static void cancelOrder() {
        System.out.println("\n*** Cancel Order ***");
        System.out.print("Enter userId for authentication: ");
        int userId = scanner.nextInt();
        scanner.nextLine();


        System.out.print("Enter orderId to cancel: ");
        int orderId = scanner.nextInt();

        orderManagementRepository.cancelOrder(userId, orderId);
        System.out.println("Order canceled successfully!");
    }

    private static void getAllProducts() {
        System.out.println("\n*** Get All Products ***");
        List<Product> productList = orderManagementRepository.getAllProducts();
        displayProducts(productList);
    }

    private static void getOrderByUser() {
        System.out.println("\n*** Get Order by User ***");
        System.out.print("Enter userId: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        User user = new User(userId, "", "", "");

        List<Product> orderList = orderManagementRepository.getOrderByUser(user);
        displayProducts(orderList);
    }

    private static void displayProducts(List<Product> productList) {
        if (productList.isEmpty()) {
            System.out.println("No products found.");
        } else {
            System.out.println("Product List:");
            for (Product product : productList) {
                System.out.println(productToString(product));
            }
        }
    }

    private static String productToString(Product product) {
        return String.format(
                "ProductId: %d, ProductName: %s, Description: %s, Price: %.2f, QuantityInStock: %d, Type: %s",
                product.getProductId(), product.getProductName(), product.getDescription(),
                product.getPrice(), product.getQuantityInStock(), product.getType()
        );
    }
}
