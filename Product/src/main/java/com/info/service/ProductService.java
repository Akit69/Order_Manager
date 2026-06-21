package com.info.service;

import com.info.entity.DTO.ProductDTO;
import com.info.entity.PageResult;
import com.info.entity.VO.ProductVO;

public interface ProductService {


    PageResult<ProductVO> getProductPage(Integer page, Integer size,
                                         String keyword, Long categoryId, Integer status,
                                         Long minPrice, Long maxPrice);

    ProductVO getProductDetail(Long id);

    Long createProduct(ProductDTO productDTO);

    void updateProduct(Long id, ProductDTO productDTO);

    void updateStatus(Long id, Integer status);
}
