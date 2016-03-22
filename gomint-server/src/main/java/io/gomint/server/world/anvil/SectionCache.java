/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class SectionCache {

    private int         sectionY;
    private byte[]      blocks;
    private NibbleArray add;
    private NibbleArray data;
    private NibbleArray blockLight;
    private NibbleArray skyLight;

}
