/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class BlockIdentifier {

    private String blockId;
    private short data;

}
