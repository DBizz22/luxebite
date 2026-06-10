package com.dbizz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbizz.model.Order;
import com.dbizz.model.OrderItem;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;
import com.dbizz.model.User;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.OrderRepo;
import com.dbizz.repo.ProductRepo;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private static final int validId = 1;
    private static final String validUsername = "validUser";
    private static final String validEmail = "email@example.com";
    private static final String validPhoneNo = "+8618340322352";
    private static final String validPassword = "ValidPass123!";

    // User user = new User(validId, validUsername, validEmail, validPhoneNo,
    // validPassword, null);

    int validOrderId = 1;
    int invalidOrderId = 999;
    int validUserId = 1;
    int invalidUserId = 999;
    int validProductId = 1;
    int invalidProductId = 999;
    int validProductStock = 10;
    int invalidStock = -5;
    double price = 100;
    int validOrderItemId = 1;
    int invalidOrderItemId = 999;
    int orderItemQty = 3;

    User validUser = new User(validUserId, validEmail, validUsername, validPhoneNo, validPassword, null);
    User invalidUser = new User(invalidUserId, "invalidEmail", "invalidUser", "+0000000000", "InvalidPass!", null);

    Product product = new Product(validProductId, "Sample Product", ProductCategory.MAIN_COURSE,
            "This is a sample product", price, validProductStock, null, null);

    Order pendingOrder = new Order(validOrderId, validUserId, PaymentStatus.PENDING, null);
    Order completedOrder = new Order(2, validUserId, PaymentStatus.COMPLETED, null);

    OrderItem pendingOrderItem = new OrderItem(validOrderItemId, validOrderId, validProductId, price, orderItemQty,
            null);

    OrderItem completedOrderItem = new OrderItem(2, completedOrder.id(), validProductId, price + 5, orderItemQty + 10,
            null);

    @Mock
    private OrderItemRepo orderItemRepoMock;

    @Mock
    private OrderRepo orderRepoMock;

    @Mock
    private ProductRepo productRepoMock;

    private OrderService orderService;

    @BeforeAll
    static void setup() {

    }

    @BeforeEach
    void init() {
        orderService = new OrderService(productRepoMock, orderRepoMock, orderItemRepoMock);
    }

    @Test
    void placeOrder_whenUserIsNull_throwsException() {

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(null, pendingOrderItem);
        });

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void placeOrder_whenOrderItemIsNull_throwsException() {

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(validUser, null);
        });

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void placeOrder_whenProductIsUnavailable_throwsException() throws Exception {
        OrderItem unavailableOrderItem = new OrderItem(0, validOrderId, invalidProductId, price, orderItemQty, null);
        when(productRepoMock.findById(invalidProductId)).thenReturn(null);

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(validUser, unavailableOrderItem);
        });

        verify(productRepoMock, only()).findById(invalidProductId);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void placeOrder_whenRequestedStockIsUnavailable_throwsException() throws Exception {
        OrderItem excessQuantityOrderItem = new OrderItem(0, validOrderId, validProductId, price, validProductStock + 1,
                null);
        when(productRepoMock.findById(validProductId)).thenReturn(product);

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(validUser, excessQuantityOrderItem);
        });

        verify(productRepoMock, only()).findById(validProductId);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void placeOrder_whenOrderNotCreated_throwsException() throws Exception {
        OrderItem firstOrderItem = new OrderItem(0, 0, validProductId, price, orderItemQty, null);
        Order firstOrder = new Order(0, validUserId, PaymentStatus.PENDING, null);
        when(productRepoMock.findById(firstOrderItem.productId())).thenReturn(product);
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of());
        when(orderRepoMock.create(firstOrder)).thenReturn(0);

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(validUser, firstOrderItem);
        });

        InOrder inOrder = Mockito.inOrder(productRepoMock, orderRepoMock);
        inOrder.verify(productRepoMock).findById(firstOrderItem.productId());
        inOrder.verify(orderRepoMock).findByStatus(validUserId,
                PaymentStatus.PENDING);
        inOrder.verify(orderRepoMock).create(firstOrder);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void placeOrder_whenFirstOrderItemNotAddedToOrder_throwsException() throws Exception {
        OrderItem firstOrderItem = new OrderItem(0, validOrderId, validProductId, price, orderItemQty, null);
        Order firstOrder = new Order(0, validUserId, PaymentStatus.PENDING, null);
        when(productRepoMock.findById(validProductId)).thenReturn(product);
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of());
        when(orderRepoMock.create(firstOrder)).thenReturn(validOrderId);
        when(orderItemRepoMock.create(firstOrderItem)).thenReturn(0);

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(validUser, firstOrderItem);
        });

        InOrder inOrder = Mockito.inOrder(productRepoMock, orderRepoMock, orderItemRepoMock);
        inOrder.verify(productRepoMock).findById(validProductId);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderRepoMock).create(firstOrder);
        inOrder.verify(orderItemRepoMock).create(firstOrderItem);
    }

    @Test
    void placeOrder_whenFirstOrderItemAddedToOrder_returnsPlacedOrderItem() throws Exception {
        OrderItem firstOrderItem = new OrderItem(0, validOrderId, validProductId, price, orderItemQty, null);
        Order firstOrder = new Order(0, validUserId, PaymentStatus.PENDING, null);
        when(productRepoMock.findById(validProductId)).thenReturn(product);
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of());
        when(orderRepoMock.create(firstOrder)).thenReturn(validOrderId);
        when(orderItemRepoMock.create(firstOrderItem)).thenReturn(validOrderItemId);
        when(orderItemRepoMock.findById(validOrderItemId)).thenReturn(firstOrderItem);

        OrderItem placedOrderItem = assertDoesNotThrow(() -> {
            return orderService.saveOrder(validUser, firstOrderItem);
        });
        assertNotNull(placedOrderItem);
        assertThat(placedOrderItem).isEqualTo(firstOrderItem);

        InOrder inOrder = Mockito.inOrder(productRepoMock, orderRepoMock, orderItemRepoMock);
        inOrder.verify(productRepoMock).findById(validProductId);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderRepoMock).create(firstOrder);
        inOrder.verify(orderItemRepoMock).create(firstOrderItem);
        inOrder.verify(orderItemRepoMock).findById(validOrderItemId);
    }

    @Test
    void placeOrder_whenSecondOrderItemNotAddedToOrder_throwsException() throws Exception {
        OrderItem secondOrderItem = new OrderItem(0, validOrderId, validProductId, price, orderItemQty, null);
        when(productRepoMock.findById(validProductId)).thenReturn(product);
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.create(secondOrderItem)).thenReturn(0);

        assertThrows(Exception.class, () -> {
            orderService.saveOrder(validUser, secondOrderItem);
        });

        InOrder inOrder = Mockito.inOrder(productRepoMock, orderRepoMock, orderItemRepoMock);
        inOrder.verify(productRepoMock).findById(validProductId);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).create(secondOrderItem);
    }

    @Test
    void placeOrder_whenSecondOrderItemAddedToOrder_returnsPlacedOrderItem() throws Exception {
        OrderItem secondOrderItem = new OrderItem(0, validOrderId, validProductId, price, orderItemQty, null);
        when(productRepoMock.findById(validProductId)).thenReturn(product);
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.create(secondOrderItem)).thenReturn(validOrderItemId);
        when(orderItemRepoMock.findById(validOrderItemId)).thenReturn(secondOrderItem);

        OrderItem placedOrderItem = assertDoesNotThrow(() -> {
            return orderService.saveOrder(validUser, secondOrderItem);
        });
        assertNotNull(placedOrderItem);
        assertThat(placedOrderItem).isEqualTo(secondOrderItem);

        InOrder inOrder = Mockito.inOrder(productRepoMock, orderRepoMock, orderItemRepoMock);
        inOrder.verify(productRepoMock).findById(validProductId);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).create(secondOrderItem);
        inOrder.verify(orderItemRepoMock).findById(validOrderItemId);
    }

    // FIXME: Use db triggers for order creation, total update and deletion
    // FIXME: Verification of data before operation
    // FIXME: use of exception or null or empty lists

    @Test
    void cancelOrder_whenUserIsNull_throwsException() throws Exception {

        assertThrows(Exception.class, () -> orderService.cancelOrder(null, pendingOrder));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrder_whenOrderIsNull_throwsException() throws Exception {

        assertThrows(Exception.class, () -> orderService.cancelOrder(validUser, null));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrder_whenNoUserPendingOrder_throwsException() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of());

        assertThrows(Exception.class, () -> {
            orderService.cancelOrder(validUser, pendingOrder);
        });

        verify(orderRepoMock, only()).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrder_whenNoMatchedUserOrder_throwsException() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of(new Order(999, 999, PaymentStatus.PENDING, null)));

        assertThrows(Exception.class, () -> {
            orderService.cancelOrder(validUser, pendingOrder);
        });

        verify(orderRepoMock, only()).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrder_whenPaymentStatusIsCompleted_throwsException() throws Exception {

        assertThrows(Exception.class, () -> {
            orderService.cancelOrder(validUser, completedOrder);
        });

        verifyNoMoreInteractions(orderRepoMock);
        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrder_whenPaymentStatusIsPending_returnsDeletedOrder() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of(pendingOrder));
        when(orderRepoMock.delete(pendingOrder.id())).thenReturn(pendingOrder.id());

        Order deletedOrder = assertDoesNotThrow(() -> orderService.cancelOrder(validUser, pendingOrder));
        assertEquals(pendingOrder, deletedOrder);

        InOrder inOrder = Mockito.inOrder(orderRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderRepoMock).delete(pendingOrder.id());
        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrderItem_whenUserIsNull_throwsException() throws Exception {

        assertThrows(Exception.class, () -> orderService.cancelOrderItem(null, pendingOrderItem));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrderItem_whenOrderItemIsNull_throwsException() throws Exception {

        assertThrows(Exception.class, () -> orderService.cancelOrderItem(validUser, null));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrderItem_whenNoUserOrderIsPending_throwsException() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of());

        assertThrows(Exception.class, () -> {
            orderService.cancelOrderItem(validUser, completedOrderItem);
        });

        verify(orderRepoMock, only()).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void cancelOrderItem_whenNoUserOrderItem_throwsException() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.findByOrderId(pendingOrder.id())).thenReturn(List.of());

        assertThrows(Exception.class, () -> orderService.cancelOrderItem(validUser, pendingOrderItem));

        InOrder inOrder = Mockito.inOrder(orderRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void cancelOrderItem_whenNoMatchingUserOrderItem_throwsException() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.findByOrderId(pendingOrder.id()))
                .thenReturn(List.of(new OrderItem(999, pendingOrder.id(), 999, 0, 0, null)));

        assertThrows(Exception.class, () -> orderService.cancelOrderItem(validUser, pendingOrderItem));

        InOrder inOrder = Mockito.inOrder(orderRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void cancelOrderItem_whenMatchedUserOrderItemNotDeleted_throwsException() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.findByOrderId(pendingOrder.id())).thenReturn(List.of(pendingOrderItem));
        when(orderItemRepoMock.delete(pendingOrderItem.id())).thenReturn(0);

        assertThrows(Exception.class, () -> orderService.cancelOrderItem(validUser, pendingOrderItem));

        InOrder inOrder = Mockito.inOrder(orderRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void cancelOrderItem_whenMatchedUserOrderItemNotDeleted_returnsDeletedOrderItem() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING))
                .thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.findByOrderId(pendingOrder.id())).thenReturn(List.of(pendingOrderItem));
        when(orderItemRepoMock.delete(pendingOrderItem.id())).thenReturn(pendingOrderItem.id());

        OrderItem deletedOrderItem = assertDoesNotThrow(
                () -> orderService.cancelOrderItem(validUser, pendingOrderItem));
        assertNotNull(deletedOrderItem);
        assertEquals(pendingOrderItem, deletedOrderItem);

        InOrder inOrder = Mockito.inOrder(orderRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrders_whenUserIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.getAllOrders(null));

        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrders_whenNoOrderByUser_returnsEmptyList() throws Exception {
        when(orderRepoMock.findByUserId(validUserId)).thenReturn(new ArrayList<>());

        List<Order> orders = assertDoesNotThrow(() -> orderService.getAllOrders(validUser));
        assertThat(orders).isEmpty();

        verify(orderRepoMock).findByUserId(validUserId);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrders_whenUserHasOrders_returnsListOfUserOrders() throws Exception {

        List<Order> expectedOrders = List.of(pendingOrder, completedOrder);
        when(orderRepoMock.findByUserId(validUserId)).thenReturn(expectedOrders);

        List<Order> actualOrders = assertDoesNotThrow(() -> orderService.getAllOrders(validUser));
        assertThat(actualOrders).hasSize(2);
        assertEquals(expectedOrders.get(0), actualOrders.get(0));
        assertEquals(expectedOrders.get(1), actualOrders.get(1));

        verify(orderRepoMock).findByUserId(validUserId);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrdersByStatus_whenUserIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.getAllOrders(null, PaymentStatus.PENDING));

        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrdersByStatus_whenPaymentStatusIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.getAllOrders(validUser, null));

        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllPendingOrders_whenNoPendingOrderByUser_returnsEmptyList() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(new ArrayList<>());
        List<Order> actualOrders = assertDoesNotThrow(
                () -> orderService.getAllOrders(validUser, PaymentStatus.PENDING));
        assertThat(actualOrders).isEmpty();

        verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllCompletedOrders_whenNoCompletedOrderByUser_returnsEmptyList() throws Exception {

        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.COMPLETED)).thenReturn(new ArrayList<>());

        List<Order> actualOrders = assertDoesNotThrow(
                () -> orderService.getAllOrders(validUser, PaymentStatus.COMPLETED));
        assertThat(actualOrders).isEmpty();

        verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.COMPLETED);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllPendingOrders_whenUserHasPendingOrders_returnsListOfUserPendingOrders() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of(pendingOrder));

        List<Order> actualOrders = assertDoesNotThrow(
                () -> orderService.getAllOrders(validUser, PaymentStatus.PENDING));
        assertThat(actualOrders).hasSize(1);
        assertEquals(pendingOrder, actualOrders.get(0));

        verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllCompletedOrders_whenUserHasCompletedOrders_returnsListOfUserCompletedOrders() throws Exception {
        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.COMPLETED)).thenReturn(List.of(completedOrder));

        List<Order> actualOrders = assertDoesNotThrow(
                () -> orderService.getAllOrders(validUser, PaymentStatus.COMPLETED));
        assertThat(actualOrders).hasSize(1);
        assertEquals(completedOrder, actualOrders.get(0));

        verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.COMPLETED);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrdersItems_whenUserIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.getAllOrderItems(null, pendingOrder));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void listAllOrdersItems_whenOrderIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.getAllOrderItems(validUser, null));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void listAllOrderItems_whenNoUserOrder_throwsException() throws Exception {

        when(orderRepoMock.findByUserId(validUserId)).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> orderService
                .getAllOrderItems(validUser,
                        new Order(invalidOrderItemId, invalidUserId, PaymentStatus.PENDING, null)));

        verify(orderRepoMock).findByUserId(validUserId);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrderItems_whenNoMatchedUserOrder_throwsException() throws Exception {

        List<Order> expectedOrders = List.of(pendingOrder, completedOrder);
        when(orderRepoMock.findByUserId(validUserId)).thenReturn(expectedOrders);

        assertThrows(Exception.class, () -> orderService
                .getAllOrderItems(validUser,
                        new Order(invalidOrderItemId, invalidUserId, PaymentStatus.PENDING, null)));

        verify(orderRepoMock).findByUserId(validUserId);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listAllOrderItems_whenMatchedUserOrder_returnsOrderDetails() throws Exception {

        List<Order> expectedOrders = List.of(pendingOrder, completedOrder);
        when(orderRepoMock.findByUserId(validUserId)).thenReturn(expectedOrders);
        when(orderItemRepoMock.findByOrderId(pendingOrder.id())).thenReturn(List.of(pendingOrderItem));

        List<OrderItem> actualOrderItems = assertDoesNotThrow(
                () -> orderService.getAllOrderItems(validUser, pendingOrder));
        assertThat(actualOrderItems).hasSize(1);
        assertEquals(pendingOrderItem, actualOrderItems.get(0));

        InOrder inOrder = Mockito.inOrder(orderRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByUserId(validUserId);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void checkout_whenUserIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.checkout(null, pendingOrder));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void checkout_whenOrderIsNull_throwsException() {

        assertThrows(Exception.class, () -> orderService.checkout(validUser, null));

        verifyNoInteractions(productRepoMock);
        verifyNoInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
    }

    @Test
    void checkout_whenCompletedOrder_throwsException() {

        assertThrows(Exception.class, () -> orderService.checkout(
                validUser, completedOrder));

        verifyNoMoreInteractions(orderRepoMock);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void checkout_whenNoUserPendingOrder_throwsException() throws Exception {

        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> orderService.checkout(
                validUser, pendingOrder));

        verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void checkout_whenNoMatchedUserPendingOrder_throwsException() throws Exception {

        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of(
                new Order(invalidOrderItemId, invalidUserId, PaymentStatus.PENDING, null)));

        assertThrows(Exception.class, () -> orderService.checkout(
                validUser,
                pendingOrder));

        verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        verifyNoInteractions(orderItemRepoMock);
        verifyNoInteractions(productRepoMock);
    }

    @Test
    void checkout_whenRequestedStockIsUnavailable_throwsException() throws Exception {

        OrderItem excessStockOrderItem = new OrderItem(validOrderItemId, validOrderId, validProductId, price,
                product.stock() * 2,
                null);

        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.findByOrderId(pendingOrder.id())).thenReturn(List.of(excessStockOrderItem));
        when(productRepoMock.findById(excessStockOrderItem.productId())).thenReturn(product);

        assertThrows(Exception.class, () -> orderService.checkout(validUser, pendingOrder));

        InOrder inOrder = Mockito.inOrder(orderRepoMock, productRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        inOrder.verify(productRepoMock).findById(excessStockOrderItem.productId());
    }

    // TODO : Repo errors handling

    @Test
    void checkout_whenValidDetails_returnsCompletedOrder() throws Exception {

        Product expectedUpdatedProduct = new Product(product.id(), product.name(), product.category(),
                product.description(),
                price,
                product.stock() - orderItemQty, null);
        Order expectedUpdatedOrder = new Order(pendingOrder.id(), pendingOrder.userId(), PaymentStatus.COMPLETED, null);

        when(orderRepoMock.findByStatus(validUserId, PaymentStatus.PENDING)).thenReturn(List.of(pendingOrder));
        when(orderItemRepoMock.findByOrderId(pendingOrder.id())).thenReturn(List.of(pendingOrderItem));
        when(productRepoMock.findById(pendingOrderItem.productId())).thenReturn(product);
        when(productRepoMock.update(expectedUpdatedProduct)).thenReturn(pendingOrderItem.productId());
        when(orderRepoMock.update(expectedUpdatedOrder)).thenReturn(pendingOrder.id());
        when(orderRepoMock.findById(pendingOrder.id())).thenReturn(expectedUpdatedOrder);

        Order actualOrder = assertDoesNotThrow(() -> orderService.checkout(validUser, pendingOrder));
        assertNotNull(actualOrder);
        assertEquals(expectedUpdatedOrder, actualOrder);

        InOrder inOrder = Mockito.inOrder(orderRepoMock, productRepoMock, orderItemRepoMock);
        inOrder.verify(orderRepoMock).findByStatus(validUserId, PaymentStatus.PENDING);
        inOrder.verify(orderItemRepoMock).findByOrderId(pendingOrder.id());
        inOrder.verify(productRepoMock).findById(pendingOrderItem.productId());
        inOrder.verify(productRepoMock).update(expectedUpdatedProduct);
        inOrder.verify(orderRepoMock).update(expectedUpdatedOrder);
        inOrder.verify(orderRepoMock).findById(pendingOrder.id());
    }

}

// TODO: temperary transcation management