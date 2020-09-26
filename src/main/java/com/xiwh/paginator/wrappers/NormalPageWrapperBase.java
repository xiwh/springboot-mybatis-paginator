package com.xiwh.paginator.wrappers;

import java.util.*;
import java.util.function.Consumer;

public abstract class NormalPageWrapperBase<T> implements NormalPageWrapper<T> {

    private transient List<T> list;

    protected abstract void onInit(List<T> list, int count, int startOffset, int physicalPage, int size);

    @Override
    public void init(List<T> list, int count, int startOffset, int physicalPage, int size) {
        this.list = list;
        onInit(list, count, startOffset, physicalPage, size);
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
}
