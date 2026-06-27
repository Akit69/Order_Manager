package com.info.service;

import com.info.entity.DTO.CouponDTO;
import com.info.entity.PageResult;
import com.info.entity.VO.CouponVO;
import java.util.List;

public interface CouponService {
    void createTemplate(CouponDTO dto);
    PageResult<CouponVO> getTemplatePage(Integer page, Integer size);
    void receive(Long templateId);
    List<CouponVO> getMyCoupons();
}