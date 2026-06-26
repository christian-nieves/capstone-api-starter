package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.service.CategoryService;
import org.yearup.service.ProductService;

import java.util.List;

@RestController // makes this class a rest controller
@RequestMapping("categories") // base url is /categories
@CrossOrigin // allows frontend website to call these endpoints
public class CategoriesController
{
    private CategoryService categoryService; // handles category business logic
    private ProductService productService; // used to get products by category

    // Constructor
    @Autowired
    public CategoriesController(CategoryService categoryService, ProductService productService)
    {
        this.categoryService = categoryService; // injects the category service
        this.productService = productService; // injects the product service
    }

    // GET Methods
    @GetMapping // GET /categories
    @PreAuthorize("permitAll()") // anyone can view categories
    public List<Category> getAll()
    {
        return categoryService.getAllCategories(); // returns every category
    }

    @GetMapping("{id}") // GET /categories/1
    @PreAuthorize("permitAll()") // anyone can view a single category
    public Category getById(@PathVariable int id)
    {
        Category category = categoryService.getById(id); // looks up the category by id

        if (category == null) // if it does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // return 404 instead of an empty 200

        return category; // otherwise return the category
    }

    @GetMapping("{categoryId}/products") // GET /categories/1/products
    @PreAuthorize("permitAll()") // anyone can view products in a category
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return productService.listByCategoryId(categoryId); // returns all products in that category
    }

    // POST Method
    @PostMapping // POST /categories
    @PreAuthorize("hasRole('ROLE_ADMIN')") // only admins can add a category
    public ResponseEntity<Category> addCategory(@RequestBody Category category)
    {
        Category createdCategory = categoryService.create(category); // saves the new category
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory); // returns it with 201 Created
    }

    // PUT Method
    @PutMapping("{id}") // PUT /categories/1
    @PreAuthorize("hasRole('ROLE_ADMIN')") // only admins can update a category
    public Category updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        return categoryService.update(id, category); // updates and returns the category
    }

    // DELETE Method
    @DeleteMapping("{id}") // DELETE /categories/1
    @PreAuthorize("hasRole('ROLE_ADMIN')") // only admins can delete a category
    public ResponseEntity<Void> deleteCategory(@PathVariable int id)
    {
        categoryService.delete(id); // deletes the category by id
        return ResponseEntity.noContent().build(); // returns 204 No Content
    }
}