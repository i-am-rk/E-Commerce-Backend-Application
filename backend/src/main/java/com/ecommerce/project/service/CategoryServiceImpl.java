package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.mapper.CategoryMapper;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

        if (categoryPage.getTotalElements() == 0)
            throw new APIException("No category created till now!!!");
        if (pageNumber >= categoryPage.getTotalPages())
            throw new APIException("Page number out of range!!!");

        List<CategoryDTO> categoryDTOS = categoryMapper.toDTOList(categories);
        CategoryResponse response = new CategoryResponse();
        response.setData(categoryDTOS);
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElement(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setLastPage(categoryPage.isLast());

        return response;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Optional<Category> existingCategory = Optional.ofNullable(categoryRepository.findByCategoryName(category.getCategoryName()));
        if (existingCategory.isPresent())
            throw new APIException("Category with the name " + categoryDTO.getCategoryName() + " already exists!!!");
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(existingCategory);
        return categoryMapper.toDTO(existingCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryName", categoryId));

        Category categoryWithSameName = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());

        if (categoryWithSameName != null && !categoryWithSameName.getCategoryId().equals(categoryId)) {
            throw new APIException("Category with the name " + categoryDTO.getCategoryName() + " already exists!!!");
        }

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        Category savedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(savedCategory);
    }
}
