package com.dbizz.service;

import java.util.List;

import com.dbizz.model.OrderItem;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.Product;
import com.dbizz.model.User;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.OrderRepo;
import com.dbizz.repo.ProductRepo;
import com.dbizz.model.Order;

public class OrderService {

    private ProductRepo productRepo;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;

    public OrderService(ProductRepo productRepo, OrderRepo orderRepo, OrderItemRepo orderItemRepo) {
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public OrderItem saveOrder(User user, OrderItem orderItem) throws Exception {

        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        if (orderItem == null)
            throw new IllegalArgumentException("Order item cannot be null");

        Product product = productRepo.findById(orderItem.productId());
        if (product == null)
            throw new Exception("Product not found for id: " + orderItem.productId());

        if (product.stock() < orderItem.quantity())
            throw new Exception("Insufficient stock for product id: " + product.id());

        List<Order> pendingOrders = orderRepo.findByStatus(user.id(), PaymentStatus.PENDING);
        if (pendingOrders.isEmpty()) {
            Order newOrder = new Order(0, user.id(), PaymentStatus.PENDING, null);
            int orderId = orderRepo.create(newOrder);
            if (orderId < 1)
                throw new Exception("Failed to create new order for user id: " + user.id());

            orderItem = new OrderItem(0, orderId, orderItem.productId(), product.price(), orderItem.quantity(), null);
            int orderItemId = orderItemRepo.create(orderItem);
            if (orderItemId < 1)
                throw new Exception("Failed to create order item for order id: " + orderId);

            return orderItemRepo.findById(orderItemId);
        } else {
            Order existingOrder = pendingOrders.get(0);
            orderItem = new OrderItem(0, existingOrder.id(), orderItem.productId(), product.price(),
                    orderItem.quantity(), null);
            int orderItemId = orderItemRepo.create(orderItem);
            if (orderItemId < 1)
                throw new Exception("Failed to create order item for order id: " + existingOrder.id());

            return orderItemRepo.findById(orderItemId);
        }
    }

    public Order cancelOrder(User user, Order order) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        if (order.status() != PaymentStatus.PENDING)
            throw new Exception("Order is not in pending status: " + order.status());

        List<Order> userPendingOrders = orderRepo.findByStatus(user.id(), PaymentStatus.PENDING);
        if (userPendingOrders.isEmpty())
            throw new Exception("No pending orders found for user id: " + user.id());

        Order pendingOrder = userPendingOrders.get(0);
        if (pendingOrder != order)
            throw new Exception("No matching pending order found for user id: " + user.id());

        int deletedOrderId = orderRepo.delete(pendingOrder.id());
        if (deletedOrderId < 1)
            throw new Exception("Failed to delete order with id: " + pendingOrder.id());
        return pendingOrder;
    }

    public OrderItem cancelOrderItem(User user, OrderItem orderItem) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        if (orderItem == null)
            throw new IllegalArgumentException("Order item cannot be null");

        List<Order> orders = orderRepo.findByStatus(user.id(), PaymentStatus.PENDING);
        if (orders.isEmpty())
            throw new Exception("No pending orders found for user id: " + user.id());

        Order pendingOrder = orders.get(0);
        if (pendingOrder.status() != PaymentStatus.PENDING)
            throw new Exception("Order is not in pending status: " + pendingOrder.status());

        List<OrderItem> existingOrderItems = orderItemRepo.findByOrderId(pendingOrder.id());
        if (existingOrderItems.isEmpty())
            throw new Exception("No order items found for order id: " + pendingOrder.id());

        if (!existingOrderItems.contains(orderItem))
            throw new Exception("Order item does not belong to the pending order id: " + pendingOrder.id());

        int deletedOrderItemId = orderItemRepo.delete(orderItem.id());
        if (deletedOrderItemId < 1)
            throw new Exception("Failed to delete order item with id: " + orderItem.id());

        return orderItem;
    }

    public List<Order> getAllOrders(User user) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        return orderRepo.findByUserId(user.id());
    }

    public List<Order> getAllOrders(User user, PaymentStatus status) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        if (status == null)
            throw new IllegalArgumentException("Payment status cannot be null");

        return orderRepo.findByStatus(user.id(), status);
    }

    public List<OrderItem> getAllOrderItems(User user, Order order) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        List<Order> userOrders = orderRepo.findByUserId(user.id());
        if (userOrders.isEmpty())
            throw new Exception("No orders found for user id: " + user.id());

        if (!userOrders.contains(order))
            throw new Exception("Order does not belong to user id: " + user.id());

        return orderItemRepo.findByOrderId(order.id());
    }

    public Order checkout(User user, Order order) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        if (order.status() != PaymentStatus.PENDING)
            throw new Exception("Order is not in pending status: " + order.status());

        List<Order> pendingOrders = orderRepo.findByStatus(user.id(), PaymentStatus.PENDING);
        if (pendingOrders.isEmpty())
            throw new Exception("No pending orders found for user id: " + user.id());

        Order pendingOrder = pendingOrders.get(0);
        if (!pendingOrder.equals(order))
            throw new Exception("No matching pending order found for user id: " + user.id());

        List<OrderItem> orderItems = orderItemRepo.findByOrderId(pendingOrder.id());
        if (orderItems.isEmpty())
            throw new Exception("No order items found for order id: " + pendingOrder.id());

        for (OrderItem item : orderItems) {
            Product product = productRepo.findById(item.productId());
            if (product == null)
                throw new Exception("Product not found for product id: " + item.productId());

            if (product.stock() < item.quantity())
                throw new Exception("Insufficient stock for product id: " + item.productId());

            Product updatedProduct = new Product(product.id(), product.name(), product.category(),
                    product.description(),
                    product.price(), product.stock() - item.quantity(), null, null);
            int updatedProductId = productRepo.update(updatedProduct);
            if (updatedProductId < 1)
                throw new Exception("Failed to update product stock for product id: " + product.id());
        }
        Order completedOrder = new Order(pendingOrder.id(), pendingOrder.userId(), PaymentStatus.COMPLETED,
                null);
        int updatedOrderId = orderRepo.update(completedOrder);
        if (updatedOrderId < 1)
            throw new Exception("Failed to update order status for order id: " + pendingOrder.id());

        return orderRepo.findById(updatedOrderId);
    }

}
