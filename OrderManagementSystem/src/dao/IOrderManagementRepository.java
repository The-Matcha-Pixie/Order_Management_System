package src.dao;


import src.entity.*;

import java.util.List;

public interface IOrderManagementRepository {

    boolean isAdmin(int userId);

    void createOrder(User user, List<Product> products);

    void cancelOrder(int userId, int orderId);

    void createProduct(User user, Product product);

    void createUser(User user);

    List<Product> getAllProducts();

    List<Product> getOrderByUser(User user);
}

