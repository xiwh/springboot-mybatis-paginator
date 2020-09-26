package com.xiwh.paginator.interceptors;

import com.xiwh.paginator.Paginator;
import com.xiwh.paginator.cache.MybatisMethodCache;
import com.xiwh.paginator.cache.impl.MybatisMethodCacheImpl;
import com.xiwh.paginator.sqlGenerator.PaginatorSqlGenerator;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.*;


@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
                )
        })
@Component
public class PaginatorLimitHandler extends BaseInterceptor {
    @Autowired
    Paginator paginator;
    @Autowired
    @Qualifier("mybatisMethodCacheImpl")
    MybatisMethodCache methodCache;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        PagingInfoWrapper pagingInfoWrapper = getPagingInfoWrapper();
        if(pagingInfoWrapper==null){
            return invocation.proceed();
        }else if(pagingInfoWrapper.counting){
            return invocation.proceed();
        }
        BoundSql boundSql = statementHandler.getBoundSql();
        pagingInfoWrapper.boundSql = boundSql;
        pagingInfoWrapper.statementHandler = statementHandler;
        //Get raw SQL
        String sql = boundSql.getSql();
        //Generate paging statement
        PaginatorSqlGenerator sqlGenerator = new PaginatorSqlGenerator(sql, pagingInfoWrapper.dataBaseType);
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        PagingRowBounds pagingRowBounds = pagingInfoWrapper.pagingRowBounds;
        pagingInfoWrapper.sqlGenerator = sqlGenerator;
        MybatisMethodCache.MethodInfo methodInfo = pagingInfoWrapper.methodInfo;

        if (methodInfo.getPaginatorType() == MybatisMethodCache.TYPE_NORMAL) {
            if(!methodInfo.getNormalPaginator().customLimit()) {
                //Modify limit sql
                String limitSql = sqlGenerator.toLimitSql(pagingRowBounds.getOffset(), pagingRowBounds.getLimit());
                field.set(boundSql, limitSql);
            }else{
                //Custom limit
                sql = sql.replaceAll(
                        ":limit", String.valueOf(pagingRowBounds.getLimit())
                ).replaceAll(":offset", String.valueOf(pagingRowBounds.getOffset())
                ).replaceAll(":end", String.valueOf(pagingRowBounds.getEnd()));
                field.set(boundSql, sql);
            }

        }else if(methodInfo.getPaginatorType() == MybatisMethodCache.TYPE_N_PLUS_ONE) {
            if (!methodInfo.getNPlusOnePaginator().customLimit()) {
                String limitSql = sqlGenerator.toLimitSql(pagingRowBounds.getOffset(), pagingRowBounds.getLimit()+1);
                field.set(boundSql, limitSql);
            }else{
                //Custom limit
                sql = sql.replaceAll(
                        ":limit", String.valueOf(pagingRowBounds.getLimit()+1)
                ).replaceAll(":offset", String.valueOf(pagingRowBounds.getOffset())
                ).replaceAll(":end", String.valueOf(pagingRowBounds.getEnd()+1));
                field.set(boundSql, sql);
            }
        }
        return invocation.proceed();
    }
}
