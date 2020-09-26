package com.xiwh.paginator.interceptors;

import com.xiwh.paginator.cache.MybatisMethodCache;
import com.xiwh.paginator.sqlGenerator.DataBaseType;
import com.xiwh.paginator.sqlGenerator.PaginatorSqlGenerator;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseInterceptor implements Interceptor {

    private static final ThreadLocal<PagingInfoWrapper> mThreadLocal = new ThreadLocal();
    private static final ThreadLocal<FixedLimitRowBounds> mThreadLocal2 = new ThreadLocal();
    private static final Map<String, DataBaseType> dbTypeMap = new ConcurrentHashMap();

    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    public PagingInfoWrapper createPaging(){
        PagingInfoWrapper pagingInfoWrapper = mThreadLocal.get();
        if(pagingInfoWrapper==null){
            pagingInfoWrapper = new PagingInfoWrapper();
            mThreadLocal.set(pagingInfoWrapper);
        }else{
            pagingInfoWrapper.reset();
        }
        return pagingInfoWrapper;
    }

    public PagingInfoWrapper getPagingInfoWrapper(){
        PagingInfoWrapper pagingInfoWrapper = mThreadLocal.get();
        return pagingInfoWrapper;
    }

    public void removePagingInfoWrapper(){
        mThreadLocal.remove();
    }

    public DataBaseType getDataBaseType(MappedStatement mappedStatement){
        String id = mappedStatement.getDatabaseId();
        id = id==null?"default":id;
        DataBaseType dataBaseType = dbTypeMap.get(id);
        if (dataBaseType == null) {
            synchronized (dbTypeMap) {
                dataBaseType = dbTypeMap.get(id);
                if (dataBaseType == null) {
                    try {
                        dataBaseType = DataBaseType.findByName(
                                getConnection().getMetaData().getDatabaseProductName()
                        );
                        dbTypeMap.put(id, dataBaseType);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to get database type！", e);
                    }
                }
            }
        }
        return dataBaseType;
    }

    /**
     * Compatible with mybatis logical paging
     */
    public RowBounds createFixedLimitRowBounds(int limit){
        FixedLimitRowBounds fixedLimitRowBounds = mThreadLocal2.get();
        if(fixedLimitRowBounds ==null){
            fixedLimitRowBounds = new FixedLimitRowBounds(limit+1);
            mThreadLocal2.set(fixedLimitRowBounds);
        }else{
            fixedLimitRowBounds.setLimit(limit+1);
        }
        return fixedLimitRowBounds;
    }

    private static class FixedLimitRowBounds extends RowBounds{
        private int limit;

        private FixedLimitRowBounds(int limit){
            this.limit = limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        @Override
        public int getLimit() {
            return limit;
        }

        @Override
        public int getOffset() {
            return 0;
        }
    }

    public static class PagingInfoWrapper{
        MybatisMethodCache.MethodInfo methodInfo;
        PagingRowBounds pagingRowBounds;
        PaginatorSqlGenerator sqlGenerator;
        int count;
        MappedStatement mappedStatement;
        Executor executor;
        DataBaseType dataBaseType;
        BoundSql boundSql;
        StatementHandler statementHandler;
        boolean counting = false;

        public void reset(){
            methodInfo = null;
            pagingRowBounds = null;
            sqlGenerator = null;
            count = 0;
            mappedStatement = null;
            executor = null;
            dataBaseType = null;
            boundSql = null;
            statementHandler = null;
            counting = false;
        }
    }

    protected Connection getConnection() throws SQLException {
        PagingInfoWrapper pagingInfoWrapper = getPagingInfoWrapper();
        Log statementLog = pagingInfoWrapper.mappedStatement.getStatementLog();
        Connection connection = pagingInfoWrapper.executor.getTransaction().getConnection();
        if (statementLog.isDebugEnabled()) {
            return ConnectionLogger.newInstance(connection, statementLog, 0);
        } else {
            return connection;
        }
    }

    public PreparedStatement createCountStatement(boolean optimization) throws SQLException
    {
        PagingInfoWrapper pagingInfoWrapper = getPagingInfoWrapper();
        String sql = optimization ?
                pagingInfoWrapper.sqlGenerator.toOptimizedCountSql()
                : pagingInfoWrapper.sqlGenerator.toCountSql();

        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Custom count preparing: %s", sql));
        }

        Connection connection = getConnection();
        if (pagingInfoWrapper.mappedStatement.getKeyGenerator() instanceof Jdbc3KeyGenerator) {
            String[] keyColumnNames = pagingInfoWrapper.mappedStatement.getKeyColumns();
            if (keyColumnNames == null) {
                return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            } else {
                return connection.prepareStatement(sql, keyColumnNames);
            }
        } else if (pagingInfoWrapper.mappedStatement.getResultSetType() == ResultSetType.DEFAULT) {
            return connection.prepareStatement(sql);
        } else {
            return connection.prepareStatement(
                    sql,
                    pagingInfoWrapper.mappedStatement.getResultSetType().getValue(),
                    ResultSet.CONCUR_READ_ONLY
            );
        }
    }

    public CacheKey createCountCacheKey(){
        PagingInfoWrapper pagingInfoWrapper = getPagingInfoWrapper();
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(pagingInfoWrapper.mappedStatement.getId());
        cacheKey.update(pagingInfoWrapper.boundSql.getSql());
        List<ParameterMapping> parameterMappings = pagingInfoWrapper.boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = pagingInfoWrapper.mappedStatement.getConfiguration().getTypeHandlerRegistry();
        Object parameterObject = pagingInfoWrapper.boundSql.getParameterObject();
        Configuration configuration = pagingInfoWrapper.mappedStatement.getConfiguration();
        // mimic DefaultParameterHandler logic
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (pagingInfoWrapper.boundSql.hasAdditionalParameter(propertyName)) {
                    value = pagingInfoWrapper.boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                cacheKey.update(value);
            }
        }
        return cacheKey;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
