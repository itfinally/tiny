package top.itfinally.builder.annotation;

import java.lang.annotation.*;

@Documented
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface Table {
    String name() default "";
}
