package top.itfinally.builder.annotation;

import java.lang.annotation.*;

@Documented
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Association {
    String property() default "";

    String column() default "";

    Class<?> join();
}
