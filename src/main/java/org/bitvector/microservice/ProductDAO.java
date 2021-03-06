package org.bitvector.microservice;

import java.util.List;

public interface ProductDAO {
    List<Product> getAllProducts();

    Product getProductById(Integer id);

    void addProduct(Product product);

    void updateProduct(Product product);

    void deleteProduct(Product product);
}
