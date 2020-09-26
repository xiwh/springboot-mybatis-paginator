package com.xiwh.paginator.wrappers;

import java.util.*;

public class SimplePage<T> extends NormalPageWrapperBase<T> {

    private int total;
    private transient int startOffset;
    private transient int physicalPage;
    private int page;
    private int size;
    private int totalPage;
    private List<T> list;

    private Map<String, Object> cachedMap = null;

    public boolean hasNext(){
        return list.size()>=size;
    }

    public boolean hasLast(){
        return physicalPage>0;
    }

    public int size(){
        return size;
    }

    public int page(){
        return page;
    }

    public int total(){
        return total;
    }

    public int totalPage(){
        return totalPage;
    }

    public List<T> list() {
        return list;
    }

    /**
     * Paged callback
     * @param list
     * @param count
     * @param startOffset
     * @param physicalPage
     * @param size
     */
    @Override
    public void onInit(List<T> list, int count, int startOffset, int physicalPage, int size) {
        this.list = list;
        this.total = count;
        this.physicalPage = physicalPage;
        this.startOffset = startOffset;
        this.page = physicalPage + startOffset;
        this.size = size;
        this.totalPage = total/size+(total%size==0?0:1);
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
            map.put("total", this.total);
            map.put("total_page", totalPage);
            map.put("has_last", this.hasLast());
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
        final StringBuffer sb = new StringBuffer("SimplePage{");
        sb.append("total=").append(total);
        sb.append(", startOffset=").append(startOffset);
        sb.append(", physicalPage=").append(physicalPage);
        sb.append(", page=").append(page);
        sb.append(", size=").append(size);
        sb.append(", hasLast=").append(hasLast());
        sb.append(", hasNext=").append(hasNext());
        sb.append(", totalPage=").append(totalPage);
        sb.append(", list=").append(list);
        sb.append('}');
        return sb.toString();
    }
}
