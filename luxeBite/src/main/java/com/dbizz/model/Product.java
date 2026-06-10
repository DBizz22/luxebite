package com.dbizz.model;

import java.time.LocalDateTime;

import com.dbizz.util.IDCheck;
import com.dbizz.util.NameUtil;
import com.dbizz.util.PhoneNoUtil;
import com.dbizz.util.TimeUtils;

public record Product(int id, String name, ProductCategory category, String description, double price, int stock,
        String imageUrl,
        LocalDateTime createdAt) implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return id == product.id &&
                Double.compare(product.price, price) == 0 &&
                stock == product.stock &&
                name.equals(product.name) &&
                category == product.category &&
                ((description == null && product.description == null)
                        || (description != null && description.equals(product.description)));

    }
}
