package com.xiwh.paginator.wrappers;

import org.apache.ibatis.session.RowBounds;

public class PageParamsWrapper {
    private int page;
    private int limit;
    private boolean forceCounting;
    public PageParamsWrapper(int page, int limit, boolean forceCounting){
        this.page = page;
        this.limit = limit;
        this.forceCounting = forceCounting;
    }

    public PageParamsWrapper(int page, int limit){
        this(page, limit, false);
    }

    public boolean isForceCounting() {
        return forceCounting;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }
}
