package src.dao;

import src.entity.*;
import src.util.*;
import src.exception.*;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class OrderProcessor implements IOrderManagementRepository {

    private Connection connection;

    public OrderProcessor() {
        connection = DBUtil.getDBConn();
    }

    @Override
    public void createOrder(User user, List<Product> products) {
        try {
            if (!userExists(user.getUserId())) {
                createUser(user);
            }

            String insertOrderQuery = "INSERT INTO OrderTable (userId, productId) VALUES (?,?)";
            for(Product product : products) {
                try (PreparedStatement statement = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, user.getUserId());
                    statement.setInt(2, product.getProductId());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) {
        try {
            if (userExists(userId)) {
                if (orderExists(orderId, userId)) {
                    String deleteOrderQuery = "DELETE FROM OrderTable WHERE orderId = ? AND userId = ?";
                    try (PreparedStatement statement = connection.prepareStatement(deleteOrderQuery)) {
                        statement.setInt(1, orderId);
                        statement.setInt(2, userId);
                        statement.executeUpdate();
                    }
                } else {
                    throw new OrderNotFoundException("OrderTable with OrderID: "+orderId+" and UserID: "+userId+" does not exist.");
                }
            } else {
                throw new UserNotFoundException("UserID: "+userId+" does not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createProduct(User user, Product product) {
        try {
            if (isAdmin(user.getUserId())) {
                String insertProductQuery = "INSERT INTO Product (productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertProductQuery, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, product.getProductName());
                    statement.setString(2, product.getDescription());
                    statement.setDouble(3, product.getPrice());
                    statement.setInt(4, product.getQuantityInStock());
                    statement.setString(5, product.getType());
                    statement.executeUpdate();
                }
            } else {
                throw new AdminNotFoundException("UserID: "+user.getUserId()+" either does not exist or is not an Admin.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(User user) {
        try {
            String insertUserQuery = "INSERT INTO User (userId, username, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertUserQuery)) {
                statement.setInt(1, user.getUserId());
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getPassword());
                statement.setString(4, user.getRole());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        try {
            String selectAllProductsQuery = "SELECT * FROM Product";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectAllProductsQuery)) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("productId");
                    String productName = resultSet.getString("productName");
                    String description = resultSet.getString("description");
                    double price = resultSet.getDouble("price");
                    int quantityInStock = resultSet.getInt("quantityInStock");
                    String type = resultSet.getString("type");

                    Product product = new Product(productId, productName, description, price, quantityInStock, type);
                    productList.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> orderList = new ArrayList<>();
        try {
            if(userExists(user.getUserId())){
                String selectOrderByUserQuery = "SELECT p.* FROM OrderTable o " +
                        "JOIN Product p ON o.productId = p.productId " +
                        "WHERE o.userId = ?";
                try (PreparedStatement statement = connection.prepareStatement(selectOrderByUserQuery)) {
                    statement.setInt(1, user.getUserId());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            int productId = resultSet.getInt("productId");
                            String productName = resultSet.getString("productName");
                            String description = resultSet.getString("description");
                            double price = resultSet.getDouble("price");
                            int quantityInStock = resultSet.getInt("quantityInStock");
                            String type = resultSet.getString("type");

                            Product product = new Product(productId, productName, description, price, quantityInStock, type);
                            orderList.add(product);
                        }
                    }
                }
            } else {
                throw new UserNotFoundException("UserID: "+user.getUserId()+" does not exist.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderList;
    }

    // Helper methods

    @Override
    public boolean isAdmin(int userId) {
        String isAdminQuery = "SELECT * FROM User WHERE userId = ? AND role = 'Admin'";
        try (PreparedStatement statement = connection.prepareStatement(isAdminQuery)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean userExists(int userId) throws SQLException {
        String userExistsQuery = "SELECT * FROM User WHERE userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(userExistsQuery)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean orderExists(int orderId, int userId) throws SQLException {
        String orderExistsQuery = "SELECT * FROM OrderTable WHERE orderId = ? AND userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(orderExistsQuery)) {
            statement.setInt(1, orderId);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

}



