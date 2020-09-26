package com.xiwh.paginator.wrappers;

import java.util.List;

public interface NPlusOnePageWrapper<T> extends Iterable<T>{
    void init(List<T> list, boolean hasNext, int pageOffset, int physicalPage, int size);
}
