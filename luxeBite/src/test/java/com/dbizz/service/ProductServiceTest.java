package com.dbizz.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbizz.model.OrderItem;
import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.ProductRepo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepo productRepoMock;

    @Mock
    private OrderItemRepo orderItemRepoMock;

    private ProductService productService;

    private @Captor ArgumentCaptor<Product> productCaptor;

    private static Product newProduct;
    private static Product product;
    private static int newId = 1;

    private static boolean equalsProductDetails(Product p1, Product newProduct, int productId) {
        return p1.id() == productId &&
                p1.name().equals(newProduct.name()) &&
                p1.category() == newProduct.category() &&
                p1.description().equals(newProduct.description()) &&
                p1.price() == newProduct.price() &&
                p1.stock() == newProduct.stock();
    }

    @BeforeAll
    static void init() {
        newProduct = new Product(0, "Sample Product", ProductCategory.MAIN_COURSE,
                "A sample product for testing", 19.99, 100, null, null);
        product = new Product(1, "Existing Product", ProductCategory.APPETIZER,
                "An existing product for testing", 9.99, 50, null, null);
    }

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepoMock, orderItemRepoMock);
    }

    @Test
    void addProduct_whenProductIsNull_returnsNull() {
        var createdProduct = assertDoesNotThrow(() -> productService.saveProduct(null));
        assertThat(createdProduct).isNull();

        verifyNoInteractions(productRepoMock);
    }

    @Test
    void addProduct_whenRepoCreateProductError_throwsException() throws Exception {
        when(productRepoMock.create(any(Product.class))).thenThrow(Exception.class);
        assertThrows(Exception.class, () -> productService.saveProduct(newProduct));

        verify(productRepoMock, only()).create(eq(newProduct));
    }

    @Test
    void addProduct_whenRepoFindByIdError_throwsException() throws Exception {
        when(productRepoMock.create(any(Product.class))).thenReturn(newId);
        when(productRepoMock.findById(newId)).thenThrow(Exception.class);

        assertThrows(Exception.class, () -> productService.saveProduct(newProduct));

        InOrder inOrder = Mockito.inOrder(productRepoMock);
        inOrder.verify(productRepoMock, times(1)).create(eq(newProduct));
        inOrder.verify(productRepoMock, times(1)).findById(eq(newId));
    }

    @Test
    void addProduct_whenValidDetails_returnsNewProduct() throws Exception {
        when(productRepoMock.create(any(Product.class))).thenReturn(newId);
        when(productRepoMock.findById(newId)).thenAnswer(invocation -> {
            return new Product(newId, newProduct.name(), newProduct.category(),
                    newProduct.description(), newProduct.price(), newProduct.stock(), null, null);
        });

        var createdProduct = assertDoesNotThrow(() -> productService.saveProduct(newProduct));
        assertThat(createdProduct).isNotNull();
        assertThat(equalsProductDetails(createdProduct, newProduct, newId)).isTrue();
        // assertThat(equalsProductDetails(productCaptor.getValue(), product)).isTrue();

        InOrder inOrder = Mockito.inOrder(productRepoMock);
        inOrder.verify(productRepoMock, times(1)).create(eq(newProduct));
        inOrder.verify(productRepoMock, times(1)).findById(eq(newId));
    }

    // @Test
    // void getProductById_whenProductDoesNotExist_returnsNull() {
    // var searchedProduct = assertDoesNotThrow(() ->
    // productService.getProductById(1));
    // assertThat(searchedProduct).isNull();
    // }

    @Test
    void removeProduct_whenProductIsNull_returnsNull() {
        Product deletedProduct = assertDoesNotThrow(() -> productService.deleteProduct(null));
        assertThat(deletedProduct).isNull();

        verifyNoInteractions(productRepoMock);
    }

    @Test
    void removeProduct_whenRepoError_throwsException() throws Exception {
        when(productRepoMock.delete(anyInt())).thenThrow(Exception.class);

        assertThrows(Exception.class, () -> productService.deleteProduct(product));

        verify(productRepoMock, only()).delete(product.id());
    }

    @Test
    void removeProduct_whenProductIdDoesNotExist_returnsNull() throws Exception {
        when(productRepoMock.delete(anyInt())).thenReturn(0);

        Product deletedProduct = assertDoesNotThrow(() -> productService.deleteProduct(product));
        assertThat(deletedProduct).isNull();

        verify(productRepoMock, only()).delete(product.id());
    }

    @Test
    void removeProduct_whenProductIsNotDeleted_returnsNull() throws Exception {
        when(productRepoMock.delete(anyInt())).thenReturn(0);

        Product deletedProduct = assertDoesNotThrow(() -> productService.deleteProduct(product));
        assertThat(deletedProduct).isNull();

        verify(productRepoMock, times(1)).delete(product.id());
    }

    @Test
    void removeProduct_whenProductIsDeleted_returnsDeletedProduct() throws Exception {
        when(productRepoMock.delete(anyInt())).thenReturn(product.id());
        when(productRepoMock.findById(anyInt())).thenReturn(null);

        Product deletedProduct = assertDoesNotThrow(() -> productService.deleteProduct(product));
        assertThat(deletedProduct).isNotNull().isEqualTo(product);

        InOrder inOrder = Mockito.inOrder(productRepoMock);
        inOrder.verify(productRepoMock, times(1)).delete(product.id());
        inOrder.verify(productRepoMock, times(1)).findById(product.id());
    }

    @Test
    void updateProductStock_whenProductIsNull_returnsNull() {
        var updatedProduct = assertDoesNotThrow(() -> productService.saveProduct(null));
        assertThat(updatedProduct).isNull();

        verifyNoInteractions(productRepoMock);
    }

    @Test
    void updateProductStock_whenProductIdDoesNotExist_returnsNull() throws Exception {
        when(productRepoMock.update(any(Product.class))).thenReturn(0);

        var updatedProduct = assertDoesNotThrow(() -> productService.saveProduct(product));
        assertThat(updatedProduct).isNull();

        verify(productRepoMock, only()).update(product);
    }

    @Test
    void updateProductStock_whenRepoError_throwsException() throws Exception {
        when(productRepoMock.update(any(Product.class))).thenThrow(Exception.class);

        assertThrows(Exception.class, () -> productService.saveProduct(product));

        verify(productRepoMock, only()).update(product);
    }

    @Test
    void updateProduct_whenProductIsNotUpdated_returnsNull() throws Exception {
        when(productRepoMock.update(any(Product.class))).thenReturn(0);

        var updatedProduct = assertDoesNotThrow(() -> productService.saveProduct(product));
        assertThat(updatedProduct).isNull();

        verify(productRepoMock, only()).update(product);
    }

    @Test
    void updateProductStock_whenValidDetails_returnsUpdatedProduct() throws Exception {
        when(productRepoMock.update(any(Product.class))).thenReturn(product.id());
        when(productRepoMock.findById(product.id())).thenReturn(product);

        var updatedProduct = assertDoesNotThrow(() -> productService.saveProduct(product));
        assertThat(updatedProduct).isNotNull().isEqualTo(product);

        InOrder inOrder = Mockito.inOrder(productRepoMock);
        inOrder.verify(productRepoMock, times(1)).update(product);
        inOrder.verify(productRepoMock, times(1)).findById(product.id());
    }

    @Test
    void searchProducts_whenProductNameIsNull_returnsEmptyList() {

        List<Product> results = assertDoesNotThrow(() -> productService.searchProductsByName(null));
        assertThat(results).isEmpty();

        verifyNoInteractions(productRepoMock);
    }

    @Test
    void searchProducts_whenNoMatchingProductName_returnsEmptyList() throws Exception {
        when(productRepoMock.findByName(any(String.class))).thenReturn(new ArrayList<Product>());

        List<Product> results = assertDoesNotThrow(
                () -> productService.searchProductsByName("unlikely-product-name-xyz"));
        assertThat(results).isEmpty();

        verify(productRepoMock, only()).findByName("unlikely-product-name-xyz");
    }

    @Test
    void searchProducts_whenMatchingProductNamesExist_returnsListOfProducts() throws Exception {
        List<Product> mockProducts = List.of(
                new Product(2, "Sample Product A", ProductCategory.DESSERT, "Description A", 5.99, 20, null, null),
                new Product(3, "Sample Product B", ProductCategory.DESSERT, "Description B", 6.99, 15, null, null));
        when(productRepoMock.findByName(any(String.class))).thenReturn(mockProducts);

        List<Product> results = assertDoesNotThrow(() -> productService.searchProductsByName("Sample"));
        assertThat(results).isNotNull().hasSize(2).containsAll(mockProducts);

        verify(productRepoMock, only()).findByName("Sample");
    }

    @Test
    void listTopProducts_withNonPositiveCount_returnsEmptyList() {
        List<Product> results = assertDoesNotThrow(() -> productService.listTopSellingProducts(0));
        assertThat(results).isEmpty();
        results = assertDoesNotThrow(() -> productService.listTopSellingProducts(-5));
        assertThat(results).isEmpty();

        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listTopSellingProducts_withPositiveCount_returnsTopCountProducts() throws Exception {
        // create list 5 Order Items
        Product topProduct1 = new Product(2, "Sample Product A", ProductCategory.DESSERT, "Description A", 20, 15,
                null, null);
        Product topProduct2 = new Product(3, "Sample Product B", ProductCategory.DESSERT, "Description B", 15, 15,
                null, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(1, 2, 1, 10.0, 2, null),
                new OrderItem(2, 1, 2, 20.0, 4, null),
                new OrderItem(3, 5, 2, 20.0, 1, null),
                new OrderItem(4, 3, 3, 15.0, 5, null),
                new OrderItem(5, 4, 2, 20.0, 1, null),
                new OrderItem(6, 1, 3, 15.0, 2, null));

        when(orderItemRepoMock.findAll()).thenReturn(orderItems);
        when(productRepoMock.findById(2)).thenReturn(topProduct1);
        // when(productRepoMock.findById(1)).thenReturn(
        // new Product(1, "Sample Product A", ProductCategory.APPETIZER, "Description
        // A", 10, 20, null));
        when(productRepoMock.findById(3)).thenReturn(topProduct2);

        List<Product> results = assertDoesNotThrow(() -> productService.listTopSellingProducts(2));
        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(0)).isEqualTo(topProduct1);
        assertThat(results.get(1)).isEqualTo(topProduct2);

        InOrder inOrder = Mockito.inOrder(orderItemRepoMock, productRepoMock);
        inOrder.verify(orderItemRepoMock, times(1)).findAll();
        inOrder.verify(productRepoMock, times(1)).findById(topProduct1.id());
        inOrder.verify(productRepoMock, times(1)).findById(topProduct2.id());
    }

    @Test
    void listAllProducts_returnsListOfProducts() throws Exception {
        when(productRepoMock.findAll()).thenReturn(List.of(product));

        List<Product> results = assertDoesNotThrow(() -> productService.listAllProducts());
        assertThat(results).isNotNull().hasSize(1).containsExactly(product);

        verify(productRepoMock, only()).findAll();
    }

    @Test
    void listProductsByCategory_withNullCategory_returnsEmptyListOrNull() {
        List<Product> results = assertDoesNotThrow(() -> productService.listProductsByCategory(null));
        assertThat(results).isEmpty();

        verifyNoInteractions(productRepoMock);
    }

    @Test
    void listProductsByCategory_withValidCategory_returnsListOfProducts() throws Exception {
        List<Product> mockProducts = List.of(
                new Product(2, "Sample Product A", ProductCategory.DESSERT, "Description A", 5.99, 20, null, null),
                new Product(3, "Sample Product B", ProductCategory.DESSERT, "Description B", 6.99, 15, null, null));
        when(productRepoMock.findByCategory(any(ProductCategory.class))).thenReturn(mockProducts);

        List<Product> results = assertDoesNotThrow(
                () -> productService.listProductsByCategory(ProductCategory.DESSERT));
        assertThat(results).isNotNull().hasSize(2).containsAll(mockProducts);

        verify(productRepoMock, only()).findByCategory(ProductCategory.DESSERT);
    }
}
