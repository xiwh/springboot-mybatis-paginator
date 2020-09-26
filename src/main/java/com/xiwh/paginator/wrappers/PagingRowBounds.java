package com.xiwh.paginator.wrappers;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

public class PagingRowBounds extends RowBounds {
    private boolean forceCounting = false;
    private int end = -1;
    private int page = -1;
    private int pageOffset = -1;
    private int count = -1;
    private boolean hasNext;
    public PagingRowBounds(int page, int size, int pageOffset, boolean forceCounting){
        super((page-pageOffset)*size, size);
        this.page = page;
        this.pageOffset = pageOffset;
        this.end = getOffset() + getLimit();
        this.forceCounting = forceCounting;
    }

    public PagingRowBounds(int page, int size){
        super(page*size, size);
        this.page = page;
        this.pageOffset = 0;
        this.forceCounting = false;
        this.end = getOffset() + getLimit();
    }

    public PagingRowBounds(PageParamsWrapper pageParams, int pageOffset){
        super((pageParams.getPage()-pageOffset) * pageParams.getLimit(), pageParams.getLimit());
        this.forceCounting = pageParams.isForceCounting();
        this.end = getOffset() + getLimit();
        this.page = pageParams.getPage() - pageOffset;
        this.pageOffset = pageOffset;
    }

    public PagingRowBounds(RowBounds rowBounds){
        super(rowBounds.getOffset(), rowBounds.getLimit());
        this.forceCounting = false;
        this.page = getOffset()/getLimit();
        this.pageOffset = 0;
        this.end = getOffset() + getLimit();
    }

    public boolean isForceCounting() {
        return forceCounting;
    }

    public int getEnd(){
        return end;
    }

    public int getPage() {
        return page;
    }

    public int getPageOffset() {
        return pageOffset;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public <T extends NormalPageWrapper> T toNormalPage(Class<T> clazz, List list){
        try {
            T obj = clazz.newInstance();
            obj.init(list, getCount(), pageOffset, getPage()-pageOffset,getLimit());
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("toNormalPage Error!", e);
        }
    }

    public <T extends NPlusOnePage> T toNPlushOnePage(Class<T> clazz, List list){
        try {
            T obj = clazz.newInstance();
            obj.init(list, hasNext, pageOffset, getPage()-pageOffset, getLimit());
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("toNormalPage Error!", e);
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PagingRowBounds{");
        sb.append("forceCounting=").append(forceCounting);
        sb.append(", offset=").append(getOffset());
        sb.append(", limit=").append(getLimit());
        sb.append(", end=").append(end);
        sb.append(", page=").append(page);
        sb.append(", pageOffset=").append(pageOffset);
        sb.append(", count=").append(count);
        sb.append(", hasNext=").append(hasNext);
        sb.append('}');
        return sb.toString();
    }
}
