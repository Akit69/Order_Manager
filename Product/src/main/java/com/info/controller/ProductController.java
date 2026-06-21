package com.info.controller;

import com.info.entity.DTO.ProductDTO;
import com.info.entity.PageResult;
import com.info.entity.Result;
import com.info.entity.VO.ProductVO;
import com.info.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private final ProductService productService;


    @GetMapping("/list")
    public Result<PageResult<ProductVO>> list( @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Long minPrice,
                                               @RequestParam(required = false) Long maxPrice) {
        PageResult<ProductVO> pageResult = productService.getProductPage(page, size, keyword, categoryId, status, minPrice, maxPrice);
        return Result.success(pageResult);

    }

    @GetMapping("/detail/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        ProductVO vo = productService.getProductDetail(id);
        return Result.success(vo);
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProductDTO productDTO) {
        Long id = productService.createProduct(productDTO);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        productService.updateProduct(id, productDTO);
        return Result.success("修改成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success("修改成功", null);
    }

}
