package top.itfinally.builder.annotation;

import java.lang.annotation.*;

@Documented
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface Column {
    String property() default "";

    String column() default "";
}
