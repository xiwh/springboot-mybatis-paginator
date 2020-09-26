package com.xiwh.paginator.wrappers;

import java.util.*;
import java.util.function.Consumer;

public class NPlusOnePage<T> implements NPlusOnePageWrapper<T> {

    private List<T> list;
    private int page;
    private int size;
    private boolean hasNext;
    private int pageOffset;

    private Map<String, Object> cachedMap = null;

    @Override
    public void init(List<T> list, boolean hasNext, int pageOffset, int physicalPage, int size) {
        this.list = list;
        this.page = physicalPage+pageOffset;
        this.size = size;
        this.pageOffset = pageOffset;
        this.hasNext = hasNext;
    }

    public List<T> list() {
        return list;
    }

    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }

    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    /**
     * If modifiable = false, the map will be cached
     * @param modifiable Allow modification?
     * @return
     */
    public Map<String,Object> toMap(boolean modifiable){
        if(!modifiable && cachedMap!=null) {
            return cachedMap;
        }else{
            Map<String, Object> map = new HashMap(8);
            map.put("list", this.list());
            map.put("page", this.page);
            map.put("size", this.size);
            map.put("has_next", this.hasNext());
            if(!modifiable){
                map = Collections.unmodifiableMap(map);
                cachedMap = map;
            }
            return map;
        }
    }

    /**
     * toMap == toMap(false)
     * @return Unmodifiable map
     */
    public Map<String,Object> toMap(){
        return toMap(false);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NPlusOnePage{");
        sb.append("list=").append(list);
        sb.append(", page=").append(page);
        sb.append(", size=").append(size);
        sb.append(", hasNext=").append(hasNext);
        sb.append('}');
        return sb.toString();
    }
}
