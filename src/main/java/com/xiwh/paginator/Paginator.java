package com.xiwh.paginator;

import com.xiwh.paginator.interceptors.PaginatorLimitHandler;
import com.xiwh.paginator.utils.StringUtils;
import com.xiwh.paginator.wrappers.PageParamsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component("Springboot-Mybatis-Paginator")
public class Paginator {

    protected static Paginator instance;

    ThreadLocal<PageParamsWrapper> pageParamsThreadLocal = new ThreadLocal<>();

    @Autowired
    PaginatorLimitHandler paginatorInterceptor;
    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationContext context;
    String queryPageKey;
    String querySizeKey;
    Integer defaultSize;

    @PostConstruct
    private void init(){
        querySizeKey = environment.getProperty("paginator.size-key", "size");
        queryPageKey = environment.getProperty("paginator.page-key", "page");
        defaultSize = environment.getProperty("paginator.default-size", Integer.class, 10);
        instance = this;
    }

    private void _startPaginate(int page, int size, boolean forceCounting){
        pageParamsThreadLocal.set(new PageParamsWrapper(page, size, forceCounting));
    }

    protected void _autoInject() throws ClassNotFoundException {
        javax.servlet.http.HttpServletRequest request = context.getBean(javax.servlet.http.HttpServletRequest.class);
        int page = StringUtils.safeToInt(request.getParameter(this.queryPageKey),0);
        int size = StringUtils.safeToInt(request.getParameter(this.querySizeKey),defaultSize);
        _startPaginate(page, size, false);
    }

    public static PageParamsWrapper currentPageParams(){
        return instance.pageParamsThreadLocal.get();
    }

    public static PageParamsWrapper readPageParams(){
        PageParamsWrapper pagingRowBounds = instance.pageParamsThreadLocal.get();
        instance.pageParamsThreadLocal.remove();
        return pagingRowBounds;
    }

    public static void paginate(int page, int size){
        instance._startPaginate(page, size, false);
    }

    public static void paginate(int page, int size, boolean forceCounting){
        instance._startPaginate(page, size, forceCounting);
    }


    public static void autoInjectFromRequest(){
        try {
            instance._autoInject();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
