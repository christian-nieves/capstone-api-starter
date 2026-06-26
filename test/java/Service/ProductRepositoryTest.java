package org.yearup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest // uses an in-memory test database
@Sql(scripts = "classpath:test-insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // reloads test data before each test
class ProductServiceTest
{
    @Autowired
    private ProductRepository productRepository; // repository connected to the test database

    private ProductService productService; // the service being tested

    // Setup
    @BeforeEach
    public void setUp()
    {
        productService = new ProductService(productRepository); // builds a fresh service before each test
    }

    // Bug 1 Tests
    @Test
    public void search_byCategory_shouldReturnAllProducts_notJustFeatured()
    {
        List<Product> results = productService.search(1, null, null, null); // searches category 1

        assertEquals(3, results.size(), "Because category 1 has 3 products and search should not filter out non-featured ones."); // category 1 has 3 products total
    }

    @Test
    public void search_byCategory_shouldIncludeNonFeaturedProduct()
    {
        List<Product> results = productService.search(1, null, null, null); // searches category 1

        boolean containsSmartphone = results.stream().anyMatch(p -> p.getName().equals("Smartphone")); // checks if the non-featured Smartphone is present
        assertTrue(containsSmartphone, "Because Smartphone is not featured but should still appear in search results."); // it should still show up
    }

    @Test
    public void search_byPriceRange_shouldReturnProductsInRange()
    {
        List<Product> results = productService.search(1, 90.0, 500.0, null); // searches category 1 between 90 and 500

        assertEquals(2, results.size(), "Because only 2 products in category 1 fall between $90 and $500."); // only Smartphone and Headphones fall in range
    }

    // Bug 2 Test
    @Test
    public void update_shouldPersistStockChange()
    {
        Product existing = productRepository.findAll().get(0); // grabs the first real product from the test database
        int id = existing.getProductId(); // uses its actual id instead of assuming 1

        Product update = new Product(); // new product holding the updated values
        update.setName(existing.getName());
        update.setPrice(existing.getPrice());
        update.setCategoryId(existing.getCategoryId());
        update.setDescription(existing.getDescription());
        update.setSubCategory(existing.getSubCategory());
        update.setStock(7); // changes stock to 7
        update.setFeatured(existing.isFeatured());
        update.setImageUrl(existing.getImageUrl());

        productService.update(id, update); // runs the update
        Product actual = productRepository.findById(id).orElse(null); // pulls that product back from the database
        assertNotNull(actual, "Because the product should exist."); // product should still exist
        assertEquals(7, actual.getStock(), "Because the updated stock value should be saved to the database."); // stock change should have saved
    }
}