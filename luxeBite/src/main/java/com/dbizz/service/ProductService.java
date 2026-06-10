package com.dbizz.service;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.dbizz.model.OrderItem;
import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.ProductRepo;

public class ProductService {

    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;

    public ProductService(ProductRepo productRepo, OrderItemRepo orderItemRepo) {
        this.productRepo = productRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public Product saveProduct(Product product) throws Exception {
        if (product == null) {
            return null;
        }

        int id;
        if (product.id() == 0)
            id = productRepo.create(product);
        else
            id = productRepo.update(product);

        if (id == 0)
            return null;
        return productRepo.findById(id);
    }

    // public Product getProductById(int id) throws Exception {
    // return productRepo.findById(id);
    // }

    public Product deleteProduct(Product product) throws Exception {
        if (product == null)
            return null;
        if (productRepo.delete(product.id()) == 0)
            return null;
        if (productRepo.findById(product.id()) != null)
            return null;
        return product;
    }

    public List<Product> searchProductsByName(String name) throws Exception {
        if (name == null || name.isBlank())
            return List.of();
        return productRepo.findByName(name);
    }

    public List<Product> listTopSellingProducts(int count) throws Exception {
        if (count <= 0)
            return List.of();
        List<OrderItem> orderItems = orderItemRepo.findAll();

        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::productId, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(Entry::getKey)
                .limit(count)
                .map(id -> {
                    try {
                        return productRepo.findById(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .toList();
    }

    public List<Product> listAllProducts() throws Exception {
        return productRepo.findAll();
    }

    public List<Product> listProductsByCategory(ProductCategory category) throws Exception {
        if (category == null) {
            return List.of();
        }
        return productRepo.findByCategory(category);
    }

}
