/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.command;

/**
 * Created by Markus on 21.10.2015.
 */
public interface CommandSender {

    //TODO Add something like a message builder instead of sending a single string message
    void sendMessage(String message);

}
