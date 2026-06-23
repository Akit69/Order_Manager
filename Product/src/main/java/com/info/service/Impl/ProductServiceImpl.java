package com.info.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.info.entity.Category;
import com.info.entity.DTO.ProductDTO;
import com.info.entity.PageResult;
import com.info.entity.Product;
import com.info.entity.VO.ProductVO;
import com.info.mapper.CategoryMapper;
import com.info.mapper.ProductMapper;
import com.info.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<ProductVO> getProductPage(Integer page, Integer size,
                                                String keyword, Long categoryId, Integer status,
                                                Long minPrice, Long maxPrice) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        wrapper.orderByDesc(Product::getSales);

        IPage<Product> productPage = productMapper.selectPage(new Page<>(page, size), wrapper);

        Set<Long> categoryIds = productPage.getRecords().stream()
                .map(Product::getCategoryId)
                .collect(Collectors.toSet());

        Map<Long, String> categoryNameMap = categoryMapper.selectBatchIds(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        List<ProductVO> voList = productPage.getRecords().stream()
                .map(product -> ProductVO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .categoryId(product.getCategoryId())
                        .categoryName(categoryNameMap.get(product.getCategoryId()))
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .mainImage(product.getMainImage())
                        .stock(product.getStock())
                        .sales(product.getSales())
                        .status(product.getStatus())
                        .createTime(product.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(voList, productPage.getTotal(),
                (int) productPage.getCurrent(), (int) productPage.getSize());
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        String categoryName = null;
        if (product.getCategoryId() != null) {
            Category category = categoryMapper.selectById(product.getCategoryId());
            categoryName = category != null ? category.getName() : null;
        }
        return ProductVO.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .price(product.getPrice())
                .description(product.getDescription())
                .mainImage(product.getMainImage())
                .stock(product.getStock())
                .sales(product.getSales())
                .status(product.getStatus())
                .createTime(product.getCreateTime())
                .build();
    }

    @Override
    public Long createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setCategoryId(productDTO.getCategoryId());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setMainImage(productDTO.getMainImage());
        product.setStock(productDTO.getStock());
        product.setStatus(1);
        product.setSales(0);
        productMapper.insert(product);
        return product.getId();
    }

    @Override
    public void updateProduct(Long id, ProductDTO productDTO) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getCategoryId() != null) {
            product.setCategoryId(productDTO.getCategoryId());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getMainImage() != null) {
            product.setMainImage(productDTO.getMainImage());
        }
        if (productDTO.getStock() != null) {
            product.setStock(productDTO.getStock());
        }
        productMapper.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }
}