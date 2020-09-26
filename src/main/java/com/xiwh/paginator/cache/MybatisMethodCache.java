package com.xiwh.paginator.cache;

import com.xiwh.paginator.annotations.NPlusOnePaginator;
import com.xiwh.paginator.annotations.NormalPaginator;
import org.apache.ibatis.mapping.MappedStatement;
import java.lang.reflect.Method;

public interface MybatisMethodCache {

    public static int TYPE_NONE = 0;
    public static int TYPE_NORMAL = 1;
    public static int TYPE_N_PLUS_ONE = 2;

    public MethodInfo getValidMethod(MappedStatement mappedStatement);

    public static interface MethodInfo{

        public Method getMethod();

        public int getPaginatorType();

        public NPlusOnePaginator getNPlusOnePaginator();

        public NormalPaginator getNormalPaginator();

        public Class getReturnClass();

        public Class getGenericReturnClass();

        public String getCustomCountMapperId();

    }
}
