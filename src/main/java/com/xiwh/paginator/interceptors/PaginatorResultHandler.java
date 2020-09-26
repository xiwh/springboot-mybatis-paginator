package com.xiwh.paginator.interceptors;

import com.xiwh.paginator.Paginator;
import com.xiwh.paginator.annotations.NormalPaginator;
import com.xiwh.paginator.cache.CountResultCache;
import com.xiwh.paginator.cache.MybatisMethodCache;
import com.xiwh.paginator.cache.impl.CountResultCacheImpl;
import com.xiwh.paginator.cache.impl.MybatisMethodCacheImpl;
import com.xiwh.paginator.wrappers.NPlusOnePage;
import com.xiwh.paginator.wrappers.NPlusOnePageWrapper;
import com.xiwh.paginator.wrappers.NormalPageWrapper;
import com.xiwh.paginator.sqlGenerator.PaginatorSqlGenerator;
import com.xiwh.paginator.utils.StringUtils;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;


@Intercepts({
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
                )
        })
@Component
public class PaginatorResultHandler extends BaseInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PaginatorLimitHandler.class);

    @Autowired
    @Qualifier("mybatisMethodCacheImpl")
    MybatisMethodCache methodCache;
    @Autowired
    @Qualifier("countResultCacheImpl")
    CountResultCache countResultCache;

    private ThreadLocal<ArrayList> listThreadLocal = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        PagingInfoWrapper pagingInfoWrapper = getPagingInfoWrapper();
        if(pagingInfoWrapper==null){
            return invocation.proceed();
        }else if(pagingInfoWrapper.counting){
            return invocation.proceed();
        }

        BoundSql boundSql = pagingInfoWrapper.boundSql;
        StatementHandler statementHandler = pagingInfoWrapper.statementHandler;
        MybatisMethodCache.MethodInfo methodInfo = pagingInfoWrapper.methodInfo;
        List result = (List) invocation.proceed();
        PagingRowBounds pagingRowBounds = pagingInfoWrapper.pagingRowBounds;
        if(methodInfo.getPaginatorType() == MybatisMethodCache.TYPE_NORMAL) {
            NormalPaginator normalPaginator = methodInfo.getNormalPaginator();
            // The count result can be calculated on the last page
            // If the count result can be calculated directly, it is forced to update
            boolean canGetCountResult = result.size()< pagingRowBounds.getLimit();
            Callable<Integer> countCall = () -> {
                // The count result can be calculated on the last page
                if(canGetCountResult){
                    int count = pagingRowBounds.getOffset() + result.size();
                    if(logger.isDebugEnabled()) {
                        logger.debug(String.format("Enable last page optimization"));
                    }
                    return count;
                }
                String customCountMapperId = pagingInfoWrapper.methodInfo.getCustomCountMapperId();
                pagingInfoWrapper.counting = true;
                try {
                    //Auto count
                    if (StringUtils.isEmpty(customCountMapperId)) {
                        PreparedStatement preparedStatement = createCountStatement(methodInfo.getNormalPaginator().countOptimization());
                        statementHandler.parameterize(preparedStatement);
                        ResultSet executeQuery = preparedStatement.executeQuery();
                        executeQuery.next();
                        int tempCount = executeQuery.getInt(PaginatorSqlGenerator.COUNT_ALIAS);
                        executeQuery.close();
                        preparedStatement.close();
                        return tempCount;
                    }
                    //Custom Count statement
                    else {
                        Executor executor = pagingInfoWrapper.executor;
                        MappedStatement customCountMappedStatement = pagingInfoWrapper.mappedStatement
                                .getConfiguration().getMappedStatement(
                                        customCountMapperId, false
                                );

                        if(logger.isDebugEnabled()) {
                            logger.debug(String.format("Count preparing:: %s", boundSql));
                        }
                        List<Integer> results = executor.query(
                                customCountMappedStatement,
                                boundSql.getParameterObject(),
                                RowBounds.DEFAULT,
                                Executor.NO_RESULT_HANDLER
                        );
                        return results.get(0);
                    }
                }finally {
                    pagingInfoWrapper.counting = false;
                }
            };
            if(methodInfo.getNormalPaginator().cache()) {
                //Create count cache key
                CacheKey cacheKey = createCountCacheKey();

                //Get count result
                int count = countResultCache.getCacheCount(
                        cacheKey,
                        countCall,
                        pagingInfoWrapper.methodInfo.getNormalPaginator().cacheExpiryTime() * 1000,
                        canGetCountResult || pagingRowBounds.isForceCounting()
                );
                pagingInfoWrapper.count = count;
            }else{
                pagingInfoWrapper.count = countCall.call();
            }

            if(logger.isInfoEnabled()) {
                logger.info(String.format("Paging count: %s", pagingInfoWrapper.count));
            }

            pagingRowBounds.setCount(pagingInfoWrapper.count);
            pagingRowBounds.setHasNext(pagingRowBounds.getCount()>pagingRowBounds.getEnd());

            Class returnClass = methodInfo.getReturnClass();
            if(NormalPageWrapper.class.isAssignableFrom(returnClass)) {
                NormalPageWrapper pageWrapper = (NormalPageWrapper) returnClass.newInstance();
                pageWrapper.init(
                        result,
                        pagingInfoWrapper.count,
                        methodInfo.getNormalPaginator().startOffset(),
                        pagingRowBounds.getPage(),
                        pagingRowBounds.getLimit()
                );
                ArrayList arrayList = getArrayList();
                arrayList.add(pageWrapper);
                return arrayList;
            }else{
                return result;
            }
        }else if(methodInfo.getPaginatorType() == MybatisMethodCache.TYPE_N_PLUS_ONE){
            boolean hasNext = result.size() > pagingRowBounds.getLimit();
            if (hasNext) {
                result.remove(result.size() - 1);
            }
            pagingRowBounds.setHasNext(hasNext);
            Class returnClass = methodInfo.getReturnClass();
            if(NPlusOnePageWrapper.class.isAssignableFrom(returnClass)) {
                NPlusOnePage pageWrapper = (NPlusOnePage) methodInfo.getReturnClass().newInstance();
                pageWrapper.init(
                        result,
                        hasNext,
                        pagingRowBounds.getPageOffset(),
                        pagingRowBounds.getPage(), pagingRowBounds.getLimit()
                );
                ArrayList arrayList = getArrayList();
                arrayList.add(pageWrapper);
                return arrayList;
            }else{
                return result;
            }
        }else{
            return result;
        }
    }

    private ArrayList getArrayList(){
        ArrayList arrayList = listThreadLocal.get();
        if(arrayList==null){
            arrayList = new ArrayList(1);
            listThreadLocal.set(arrayList);
        }else{
            arrayList.clear();
        }
        return arrayList;
    }
}
