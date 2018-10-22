package io.gomint.server.registry;

import java.lang.annotation.*;

/**
 * @author geNAZt
 * @version 1.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@Repeatable( value = RegisterInfos.class )
public @interface RegisterInfo {

    int id() default -1;
    String sId() default "";
    boolean def() default false;

}
