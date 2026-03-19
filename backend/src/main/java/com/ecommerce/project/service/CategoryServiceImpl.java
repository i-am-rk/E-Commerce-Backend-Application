package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategory() {
        List<Category> categories =  categoryRepository.findAll();
        if(categories.isEmpty())
            throw new APIException("No category created till now!!");
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        Optional<Category> existingCategory = Optional.ofNullable(categoryRepository.findByCategoryName(category.getCategoryName()));
        if(existingCategory.isPresent())
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists!!!");
        else
            categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryName", categoryId));
        categoryRepository.delete(category);
        return "Category with CategoryId: " + categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        // Check if new category Name already exist
        if(Optional.ofNullable(categoryRepository.findByCategoryName(category.getCategoryName())).isPresent()){
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists!!!");
        }
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryName", categoryId));
        existingCategory.setCategoryName(category.getCategoryName());
        return categoryRepository.save(existingCategory);
    }
}
