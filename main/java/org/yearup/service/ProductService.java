package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService
{
    private final ProductRepository productRepository; // talks to the products table via JPA

    // Constructor
    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository; // injects the repository
    }

    // Search Products
    public List<Product> search(Integer categoryId, Double minPrice, Double maxPrice, String subCategory)
    {
        List<Product> products = categoryId != null // if a category was passed
                ? productRepository.findByCategoryId(categoryId) // get only that category's products
                : productRepository.findAll(); // otherwise get all products

        return products.stream()
                .filter(p -> minPrice == null || p.getPrice() >= minPrice) // keep products at or above minPrice
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice) // keep products at or below maxPrice
                .filter(p -> subCategory == null || subCategory.equalsIgnoreCase(p.getSubCategory())) // keep products matching subCategory
                .toList(); // BUG 1 FIX: removed a filter that only kept featured products
    }

    // List Products By Category
    public List<Product> listByCategoryId(int categoryId)
    {
        return productRepository.findByCategoryId(categoryId); // returns all products in category
    }

    // Get Product By Id
    public Product getById(int productId)
    {
        return productRepository.findById(productId).orElse(null); // returns the product or null if not found
    }

    // Create Product
    public Product create(Product product)
    {
        product.setProductId(0); // resets id so the database generates new one
        return productRepository.save(product); // inserts the new product and returns it
    }

    // Update Product
    public Product update(int productId, Product product)
    {
        Product existing = productRepository.findById(productId).orElseThrow(); // loads product from the database
        existing.setName(product.getName()); // update name
        existing.setPrice(product.getPrice()); // update price
        existing.setCategoryId(product.getCategoryId()); // update category
        existing.setDescription(product.getDescription()); // update description
        existing.setSubCategory(product.getSubCategory()); // update subcategory
        existing.setStock(product.getStock()); // BUG 2 FIX: this line was missing, so stock changes never saved
        existing.setFeatured(product.isFeatured()); // update featured flag
        existing.setImageUrl(product.getImageUrl()); // update image url
        return productRepository.save(existing); // save the changes and return the updated product
    }

    // Delete Product
    public void delete(int productId)
    {
        productRepository.deleteById(productId); // removes the product by id
    }
}