/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config;

import java.lang.annotation.*;

/**
 * @author geNAZt
 * @version 1.0
 * @deprecated Will be moved into the package 'io.gomint.config.annotation' for consistency purposes
 */
@Deprecated
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
@Repeatable( Comments.class )
public @interface Comment {

    String value() default "";

}
