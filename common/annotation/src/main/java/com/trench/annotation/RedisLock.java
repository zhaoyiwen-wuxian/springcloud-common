package com.trench.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisLock {

    /**
     *
     * @return lock key
     */
    String value() default "";

    /**
     *
     * @return mills
     */
    long expireMills() default 30000;

    /**
     *
     * @return retry times
     */
    int retryTimes() default 0;

    /**
     *
     * @return mills
     */
    long retryDurationMills() default 200;

}
