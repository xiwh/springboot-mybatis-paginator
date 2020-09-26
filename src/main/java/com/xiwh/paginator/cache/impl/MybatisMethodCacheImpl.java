package com.xiwh.paginator.cache.impl;

import com.xiwh.paginator.annotations.NPlusOnePaginator;
import com.xiwh.paginator.annotations.NormalPaginator;
import com.xiwh.paginator.cache.MybatisMethodCache;
import com.xiwh.paginator.utils.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("mybatisMethodCacheImpl")
public class MybatisMethodCacheImpl implements MybatisMethodCache {

    private final static MethodInfoImpl INVALID_METHOD = new MethodInfoImpl(null);

    private static final Logger logger = LoggerFactory.getLogger(MybatisMethodCacheImpl.class);

    private Map<String,MethodInfo> methodInfoCache = new ConcurrentHashMap<>();

    public MybatisMethodCache.MethodInfo getValidMethod(MappedStatement mappedStatement) {
        String id = mappedStatement.getId();
        MethodInfo methodInfo = methodInfoCache.get(id);
        if(methodInfo==null){
            int temp = id.lastIndexOf(".");
            String className = id.substring(0, temp);
            String methodName = id.substring(temp+1);
            Method method = _getMethod(className, methodName);
            if(method==null){
                methodInfoCache.put(id, INVALID_METHOD);
            }else{
                //Double check
                synchronized (method) {
                    methodInfo = methodInfoCache.get(id);
                    if(methodInfo==null) {
                        methodInfo = _collectInfo(className, method);
                        methodInfoCache.put(id, methodInfo);
                    }
                }
            }
        }

        if(methodInfo.getMethod()==null){
            return null;
        }
        return methodInfo;
    }


    private MethodInfo _collectInfo(String className, Method method){
        Annotation[] annotations = method.getAnnotations();
        MethodInfoImpl methodInfo = INVALID_METHOD;
        for(int i=0;i<annotations.length;i++){
            Annotation annotation = annotations[i];
            if(annotation instanceof NormalPaginator){
                methodInfo = new MethodInfoImpl(method);
                NormalPaginator normalPaginator = (NormalPaginator)annotation;
                methodInfo.normalPaginator = normalPaginator;
                if(normalPaginator.customLimit()){
                    if(StringUtils.isEmpty(normalPaginator.countMethod())){
                        throw new Error("When customizing the limit, please also customize the countåå method！");
                    }
                }
                methodInfo.type = MybatisMethodCache.TYPE_NORMAL;
                if(!StringUtils.isEmpty(methodInfo.normalPaginator.countMethod())){
                    methodInfo.customCountMapperId = String.join(
                            ".",
                            className,
                            methodInfo.normalPaginator.countMethod()
                    );
                }
                break;
            }else if(annotation instanceof NPlusOnePaginator){
                methodInfo = new MethodInfoImpl(method);
                methodInfo.NPlusOnePaginator = (NPlusOnePaginator)annotation;
                methodInfo.type = MybatisMethodCache.TYPE_N_PLUS_ONE;
                break;
            }
        }
        if(methodInfo != INVALID_METHOD){
            ParameterizedType returnGenericType = (ParameterizedType) method.getGenericReturnType();
            Class firstGenericReturnClass = (Class) returnGenericType.getActualTypeArguments()[0];
            methodInfo.returnClass = method.getReturnType();
            methodInfo.genericReturnClass = firstGenericReturnClass;
        }
        return methodInfo;
    }

    private Method _getMethod(String className, String methodName){
        try {
            Class clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            Method method = null;
            for (Method tempM : methods) {
                if (tempM.getName().equals(methodName)) {
                    method = tempM;
                    break;
                }
            }
            if (method==null){
                throw new Exception(
                        String.format("MappedStatement " +
                                ",Class ‘%s’, Method ‘%s’ not found",
                                className , methodName
                        )
                );
            }
            return method;
        }catch (Exception e){
            logger.warn("Get mapper method error!", e);
            return null;
        }
    }

    private static class MethodInfoImpl implements MethodInfo{
        private Method method;
        private int type = MybatisMethodCache.TYPE_NONE;
        private NormalPaginator normalPaginator;
        private NPlusOnePaginator NPlusOnePaginator;
        private Class returnClass;
        private Class genericReturnClass;
        private String customCountMapperId;

        private MethodInfoImpl(Method method){
            this.method = method;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public int getPaginatorType() {
            return type;
        }

        @Override
        public NPlusOnePaginator getNPlusOnePaginator() {
            return NPlusOnePaginator;
        }

        @Override
        public NormalPaginator getNormalPaginator() {
            return normalPaginator;
        }

        @Override
        public Class getReturnClass() {
            return returnClass;
        }

        @Override
        public Class getGenericReturnClass() {
            return genericReturnClass;
        }

        @Override
        public String getCustomCountMapperId() {
            return customCountMapperId;
        }
    }
}
