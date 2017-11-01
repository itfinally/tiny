package top.itfinally.builder.annotation;

import java.lang.annotation.*;

@Documented
@Target( value = ElementType.TYPE )
@Retention( value = RetentionPolicy.RUNTIME )
public @interface MetaData {
}
