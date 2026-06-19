package com.property.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应体
 */
@Data
public class PageResult<T> implements Serializable {
    private int code = 200;
    private String msg = "success";
    private long total;
    private int page;
    private int size;
    private List<T> list;

    public static <T> PageResult<T> of(Page<?> page, List<T> list) {
        PageResult<T> r = new PageResult<>();
        r.total = page.getTotalElements();
        r.page = page.getNumber() + 1;
        r.size = page.getSize();
        r.list = list;
        return r;
    }
}
