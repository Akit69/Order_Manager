package com.info.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;    // 当前页数据
    private long total;         // 总条数
    private long pages;         // 总页数
    private int page;           // 当前页码
    private int size;           // 每页条数


    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPages(total % size == 0 ? total / size : total / size + 1);
        result.setPage(page);
        result.setSize(size);
        return result;
    }

}
