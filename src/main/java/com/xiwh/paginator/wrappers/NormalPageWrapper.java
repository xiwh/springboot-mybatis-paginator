package com.xiwh.paginator.wrappers;

import java.util.List;

public interface NormalPageWrapper<T> extends Iterable<T>{
    void init(List<T> list, int count, int startOffset, int physicalPage, int size);
}
