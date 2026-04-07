package com.andi.carikopi.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebResponse<T> {
    private int code;
    private String status;
    private T data;
    private String errors;
    private PagingResponse paging;
}
