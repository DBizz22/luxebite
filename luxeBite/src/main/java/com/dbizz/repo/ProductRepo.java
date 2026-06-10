package com.dbizz.repo;

import java.util.List;

import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;

public interface ProductRepo {
    public int create(Product product) throws Exception;

    public int update(Product product) throws Exception;

    public Product findById(int id) throws Exception;;

    public List<Product> findByName(String name) throws Exception;

    public List<Product> findByCategory(ProductCategory category) throws Exception;

    public List<Product> findAll() throws Exception;

    public int delete(int id) throws Exception;

}
