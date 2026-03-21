package com.ecommerce.project.mapper;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.categoryId", target = "categoryId")
    ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<Product> products);
}
