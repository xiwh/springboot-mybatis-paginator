package com.xiwh.paginator.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NPlusOnePaginator {
    /**
     * Automatically load parameters through get request
     */
    boolean auto() default false;

    /**
     * Page offset default 0
     */
    int startOffset() default 0;

    /**
     * Custom limit statement
     */
    boolean customLimit() default false;
}
