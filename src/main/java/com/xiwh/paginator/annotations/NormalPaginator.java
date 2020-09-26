package com.xiwh.paginator.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NormalPaginator {

    /**
     * Automatically load parameters through get request
     */
    boolean auto() default false;

    /**
     * Cache count results
     */
    boolean cache() default false;

    /**
     * Count SQL optimization
     */
    boolean countOptimization() default true;

    /**
     * Count cache expiry time, in seconds
     */
    int cacheExpiryTime() default 3600;

    /**
     * Page offset default 0
     */
    int startOffset() default 0;

    /**
     * Custom count statement, which is automatically counted by default
     */
    String countMethod() default "";

    /**
     * Custom limit statement
     * When customizing the limit, you must also customize the count
     */
    boolean customLimit() default false;
}
