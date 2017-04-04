package io.gomint.server.player;

import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class DeviceInfo {

    private final String deviceName;
    private final DeviceOS os;
    public DeviceInfo( int deviceOS, String deviceName ) {
        this.os = DeviceOS.fromValue( deviceOS );
        this.deviceName = deviceName;
    }

    public enum DeviceOS {
        ANDROID,
        WINDOWS;

        public static DeviceOS fromValue( int value ) {
            switch ( value ) {
                case 1:
                    return ANDROID;
                case 7:
                    return WINDOWS;
                default:
                    System.out.println( "Unknown device OS ID: " + value );
                    return null;
            }
        }
    }

}
