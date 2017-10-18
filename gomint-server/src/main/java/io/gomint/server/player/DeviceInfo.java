package io.gomint.server.player;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class DeviceInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger( DeviceInfo.class );

    private final String deviceName;
    private final DeviceOS os;

    /**
     * Information about the device of the player
     *
     * @param deviceOS which the player is using
     * @param deviceName which the player is using
     */
    public DeviceInfo( int deviceOS, String deviceName ) {
        this.os = DeviceOS.fromValue( deviceOS );
        this.deviceName = deviceName;
    }

    public enum DeviceOS {
        /**
         * Android OS, can be tablet, phones or even tv sticks and other handhelds
         */
        ANDROID,

        /**
         * iOS, apple OS for iphones, ipads and some ipods
         */
        IOS,

        /**
         * Windows. What else?
         */
        WINDOWS;

        private static DeviceOS fromValue( int value ) {
            switch ( value ) {
                case 1:
                    return ANDROID;
                case 2:
                    return IOS;
                case 7:
                    return WINDOWS;
                default:
                    LOGGER.warn( "Unknown device OS ID: " + value );
                    return null;
            }
        }
    }

}
