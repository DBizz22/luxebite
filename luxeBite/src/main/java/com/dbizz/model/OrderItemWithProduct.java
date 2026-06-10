package com.dbizz.model;

public class OrderItemWithProduct {

    private OrderItem orderItem;
    private Product product;

    public OrderItemWithProduct(OrderItem orderItem, Product product) {
        this.orderItem = orderItem;
        this.product = product;
    }

    // JavaBean-style getters
    public int getId() {
        return orderItem.id();
    }

    public int getOrderId() {
        return orderItem.orderId();
    }

    public int getProductId() {
        return orderItem.productId();
    }

    public double getUnitPrice() {
        return orderItem.unitPrice();
    }

    public int getQuantity() {
        return orderItem.quantity();
    }

    public Product getProduct() {
        return product;
    }

    public int id() {
        return orderItem.id();
    }

    public int orderId() {
        return orderItem.orderId();
    }

    public int productId() {
        return orderItem.productId();
    }

    public double unitPrice() {
        return orderItem.unitPrice();
    }

    public int quantity() {
        return orderItem.quantity();
    }

    public Product product() {
        return product;
    }

}
