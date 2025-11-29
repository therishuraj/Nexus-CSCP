package com.nexus.product_service.service;

import com.nexus.product_service.model.Product;
import com.nexus.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------------------
    // CreateProduct tests
    // -------------------------------------------------------------------------

    @Test
    void testCreateProduct_Success() {
        Product product = new Product();
        product.setName("Phone");
        product.setSupplierId("sup1");

        when(productRepository.findByNameAndSupplierId("Phone", "sup1"))
                .thenReturn(Optional.empty());

        Product saved = new Product();
        saved.setId("p123");
        when(productRepository.save(any(Product.class)))
                .thenReturn(saved);

        String result = productService.CreateProduct(product, "sup1");

        assertEquals("p123", result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testCreateProduct_AlreadyExists() {
        Product product = new Product();
        product.setName("Phone");
        product.setSupplierId("sup1");

        when(productRepository.findByNameAndSupplierId("Phone", "sup1"))
                .thenReturn(Optional.of(product));

        String result = productService.CreateProduct(product, "sup1");

        assertTrue(result.contains("already exists"));
        verify(productRepository, never()).save(any(Product.class));
    }

    // -------------------------------------------------------------------------
    // updateProductQuantity tests
    // -------------------------------------------------------------------------

    @Test
    void testUpdateProductQuantity_Success() {
        Product product = new Product();
        product.setId("p1");
        product.setQuantity(10);

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        boolean result = productService.updateProductQuantity("p1", 20);

        assertTrue(result);
        assertEquals(20, product.getQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void testUpdateProductQuantity_ProductNotFound() {
        when(productRepository.findById("p1"))
                .thenReturn(Optional.empty());

        boolean result = productService.updateProductQuantity("p1", 20);

        assertFalse(result);
        verify(productRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // GetProductsByCategory tests
    // -------------------------------------------------------------------------

    @Test
    void testGetProductsByCategory_Found() {
        when(productRepository.findByCategory("Electronics"))
                .thenReturn(List.of(new Product()));

        List<Product> products = productService.GetProductsByCategory("Electronics");

        assertFalse(products.isEmpty());
    }

    @Test
    void testGetProductsByCategory_EmptyList() {
        when(productRepository.findByCategory("Electronics"))
                .thenReturn(List.of());

        List<Product> products = productService.GetProductsByCategory("Electronics");

        assertTrue(products.isEmpty());
    }

    // -------------------------------------------------------------------------
    // GetProductsBySupplier tests
    // -------------------------------------------------------------------------

    @Test
    void testGetProductsBySupplier_Found() {
        when(productRepository.findBySupplierId("sup123"))
                .thenReturn(List.of(new Product()));

        List<Product> result = productService.GetProductsBySupplier("sup123");

        assertFalse(result.isEmpty());
        verify(productRepository).findBySupplierId("sup123");
    }

    @Test
    void testGetProductsBySupplier_EmptyList() {
        when(productRepository.findBySupplierId("sup123"))
                .thenReturn(List.of());

        List<Product> result = productService.GetProductsBySupplier("sup123");

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // GetAllProducts tests
    // -------------------------------------------------------------------------

    @Test
    void testGetAllProducts_Found() {
        when(productRepository.findAll()).thenReturn(List.of(new Product()));

        List<Product> result = productService.GetAllProducts();

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllProducts_Empty() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = productService.GetAllProducts();

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // GetProductsInShortage tests
    // -------------------------------------------------------------------------

    @Test
    void testGetProductsInShortage_SomeShortage() {
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);

        when(p1.isShortage()).thenReturn(true);
        when(p2.isShortage()).thenReturn(false);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.GetProductsInShortage();

        assertEquals(1, result.size());
        assertTrue(result.contains(p1));
    }

    @Test
    void testGetProductsInShortage_NoneShortage() {
        Product p1 = mock(Product.class);
        when(p1.isShortage()).thenReturn(false);

        when(productRepository.findAll()).thenReturn(List.of(p1));

        List<Product> result = productService.GetProductsInShortage();

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getByID tests
    // -------------------------------------------------------------------------

    @Test
    void testGetById_Found() {
        Product product = new Product();
        product.setId("p1");

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getByID("p1");

        assertTrue(result.isPresent());
    }

    @Test
    void testGetById_NotFound() {
        when(productRepository.findById("p1"))
                .thenReturn(Optional.empty());

        Optional<Product> result = productService.getByID("p1");

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // updateProduct tests
    // -------------------------------------------------------------------------

    @Test
    void testUpdateProduct_Success() {
        Product existing = new Product();
        existing.setId("p1");
        existing.setSupplierId("sup1");

        Product updated = new Product();
        updated.setName("NewName");
        updated.setCategory("NewCat");
        updated.setQuantity(50);
        updated.setPrice(100.0);

        when(productRepository.findById("p1"))
                .thenReturn(Optional.of(existing));

        boolean result = productService.updateProduct("p1", updated, "sup1");

        assertTrue(result);
        verify(productRepository).save(existing);
        assertEquals("NewName", existing.getName());
    }

    @Test
    void testUpdateProduct_SupplierMismatch() {
        Product existing = new Product();
        existing.setId("p1");
        existing.setSupplierId("sup1");

        when(productRepository.findById("p1"))
                .thenReturn(Optional.of(existing));

        boolean result = productService.updateProduct("p1", new Product(), "wrongSupplier");

        assertFalse(result);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById("p1"))
                .thenReturn(Optional.empty());

        boolean result = productService.updateProduct("p1", new Product(), "sup1");

        assertFalse(result);
        verify(productRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteProduct tests
    // -------------------------------------------------------------------------

    @Test
    void testDeleteProduct_Found() {
        when(productRepository.existsById("p1")).thenReturn(true);

        boolean result = productService.deleteProduct("p1");

        assertTrue(result);
        verify(productRepository).deleteById("p1");
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.existsById("p1")).thenReturn(false);

        boolean result = productService.deleteProduct("p1");

        assertFalse(result);
        verify(productRepository, never()).deleteById(anyString());
    }
}
