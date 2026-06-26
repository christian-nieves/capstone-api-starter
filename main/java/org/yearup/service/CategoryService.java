package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Category;
import org.yearup.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService
{
    private final CategoryRepository categoryRepository; // talks to the categories table via JPA

    // Constructor
    public CategoryService(CategoryRepository categoryRepository)
    {
        this.categoryRepository = categoryRepository; // injects the repository
    }

    // Get All Categories
    public List<Category> getAllCategories()
    {
        return categoryRepository.findAll(); // returns every category in the table
    }

    // Get Category By Id
    public Category getById(int categoryId)
    {
        return categoryRepository.findById(categoryId).orElse(null); // returns the category or null if not found
    }

    // Create Category
    public Category create(Category category)
    {
        return categoryRepository.save(category); // inserts the new category and returns it
    }

    // Update Category
    public Category update(int categoryId, Category category)
    {
        category.setCategoryId(categoryId); // makes sure the id from the url is set so it updates instead of inserts
        return categoryRepository.save(category); // saves the changes and returns the updated category
    }

    // Delete Category
    public void delete(int categoryId)
    {
        categoryRepository.deleteById(categoryId); // removes the category by id
    }
}
