package com.xiwh.paginator.interceptors;

import com.xiwh.paginator.Paginator;
import com.xiwh.paginator.cache.MybatisMethodCache;
import com.xiwh.paginator.sqlGenerator.DataBaseType;
import com.xiwh.paginator.wrappers.NPlusOnePageWrapper;
import com.xiwh.paginator.wrappers.NormalPageWrapper;
import com.xiwh.paginator.wrappers.PageParamsWrapper;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;


@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
            ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        })
@Component
public class PaginatorExecuteHandler extends BaseInterceptor {

    @Autowired
    @Qualifier("mybatisMethodCacheImpl")
    MybatisMethodCache methodCache;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        removePagingInfoWrapper();
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT)
            return invocation.proceed();
        MybatisMethodCache.MethodInfo methodInfo = methodCache.getValidMethod(mappedStatement);
        if (methodInfo == null) {
            return invocation.proceed();
        }

        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        PageParamsWrapper pageParams = null;
        PagingRowBounds pagingRowBounds = null;
        // When no RowBounds parameter is passed in
        if (rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET && rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT) {
            if (methodInfo.getPaginatorType() == MybatisMethodCache.TYPE_NORMAL) {
                if (methodInfo.getNormalPaginator().auto()) {
                    Paginator.autoInjectFromRequest();
                }
                pageParams = Paginator.readPageParams();
                if (pageParams == null) {
                    throw new RuntimeException("Please actively set paging parameters！");
                }
                pagingRowBounds = new PagingRowBounds(
                        pageParams,
                        methodInfo.getNormalPaginator().startOffset()
                );
            } else if (methodInfo.getPaginatorType() == MybatisMethodCache.TYPE_N_PLUS_ONE) {
                if (methodInfo.getNPlusOnePaginator().auto()) {
                    Paginator.autoInjectFromRequest();
                }
                pageParams = Paginator.readPageParams();
                if (pageParams == null) {
                    throw new RuntimeException("Please actively set paging parameters！");
                }
                pagingRowBounds = new PagingRowBounds(
                        pageParams,
                        methodInfo.getNPlusOnePaginator().startOffset()
                );
            }
        } else {
            if (rowBounds instanceof PagingRowBounds) {
                pagingRowBounds = (PagingRowBounds) rowBounds;
            } else {
                pagingRowBounds = new PagingRowBounds(rowBounds);
            }
        }

        //Modify rowBounds, For compatibility with mybatis caching mechanism
        //Compatible with mybatis logical paging
        invocation.getArgs()[2] = createFixedLimitRowBounds(pagingRowBounds.getLimit());
        PagingInfoWrapper pagingInfoWrapper = createPaging();
        pagingInfoWrapper.methodInfo = methodInfo;
        pagingInfoWrapper.mappedStatement = (MappedStatement) invocation.getArgs()[0];
        pagingInfoWrapper.executor = (Executor) invocation.getTarget();
        pagingInfoWrapper.pagingRowBounds = pagingRowBounds;

        //Get database type
        DataBaseType dbType = getDataBaseType(mappedStatement);
//        DataBaseType dbType = DataBaseType.MYSQL;
        pagingInfoWrapper.dataBaseType = dbType;

        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        ResultMap resultMap = resultMaps.get(0);
        if(
                NormalPageWrapper.class.isAssignableFrom(resultMap.getType()) ||
                NPlusOnePageWrapper.class.isAssignableFrom(resultMap.getType())){
            Field field = ResultMap.class.getDeclaredField("type");
            field.setAccessible(true);
            field.set(resultMap, methodInfo.getGenericReturnClass());
        }


        Object proceed = invocation.proceed();
        return proceed;
    }
}
