package io.gomint.server.config;

import io.gomint.config.Comment;
import io.gomint.config.YamlConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author geNAZt
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ConnectionConfig extends YamlConfig {

    @Comment("Enable connection encryption. It uses AES-128 so it costs a bit of CPU power")
    private boolean enableEncryption = true;

    @Comment("Level of compression used for packets. Lower = less CPU / higher traffic; Higher = more CPU / lower traffic")
    private int compressionLevel = 7;

    @Comment("Amount of bytes which will be used as threshold to not get compressed. This must be under 1024 bytes, otherwise Gomint will fail")
    private int compressionThreshold = 256;

}
