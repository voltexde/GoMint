package io.gomint.server.network;

import io.gomint.server.network.packet.PacketLogin;
import io.gomint.server.player.DeviceInfo;
import io.gomint.server.player.PlayerSkin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.zip.InflaterInputStream;

/**
 * @author geNAZt
 * @version 1.0
 */
public class LoginHandler {

    private static final String MOJANG_PUBLIC = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";
    private static KeyFactory KEY_FACTORY;

    static {
        try {
            KEY_FACTORY = KeyFactory.getInstance( "ECDH", "BC" );
        } catch ( NoSuchAlgorithmException e ) {
            System.out.println( "Cryptography error: could not initialize ECDH keyfactory!" );
            e.printStackTrace();
            System.exit( -1 );
        } catch ( NoSuchProviderException e ) {
            System.out.println( "BC Could not be found. Be sure to have installed org.bouncycastle.bcprov-jdk15on library" );
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    // Chain additional data
    @Getter
    private String userName;
    @Getter
    private UUID uuid;
    @Getter
    private long xboxId;
    @Getter
    private DeviceInfo deviceInfo;
    @Getter
    private PlayerSkin skin;
    @Getter
    private long clientId;

    // Validation of login
    private String skinValidationKey;
    private String validationKey;
    @Getter
    private boolean valid = true;
    private boolean firstCertAuth = true;

    public LoginHandler( PacketLogin login ) {
        InflaterInputStream inputStream = new InflaterInputStream( new ByteArrayInputStream( login.getPayload() ) );
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {
            byte[] comBuffer = new byte[1024];

            int read;
            while ( ( read = inputStream.read( comBuffer ) ) != -1 ) {
                bout.write( comBuffer, 0, read );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            this.valid = false;
            return;
        }

        // More data please
        ByteBuffer byteBuffer = ByteBuffer.wrap( bout.toByteArray() );
        byteBuffer.order( ByteOrder.LITTLE_ENDIAN );
        byte[] stringBuffer = new byte[byteBuffer.getInt()];
        byteBuffer.get( stringBuffer );

        // Decode the json stuff
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse( new String( stringBuffer ) );
            JSONArray chainArray = (JSONArray) jsonObject.get( "chain" );
            if ( chainArray != null ) {
                this.validationKey = this.skinValidationKey = parseBae64JSON( (String) chainArray.get( chainArray.size() - 1 ) ); // First key in chain is last response in chain #brainfuck :D
                for ( Object chainObj : chainArray ) {
                    Map<String, Object> data = decodeBase64JSON( "Mojang", (String) chainObj );
                    if ( data != null ) {
                        if ( data.containsKey( "identity" ) ) {
                            // For a valid user we need a XUID (xbox live id)
                            String xboxId = (String) data.get( "XUID" );
                            if ( xboxId == null ) {
                                this.valid = false;
                            } else {
                                this.xboxId = Long.parseLong( xboxId );
                            }

                            this.userName = (String) data.get( "displayName" );
                            this.uuid = UUID.fromString( (String) data.get( "identity" ) );
                        }
                    }
                }
            }
        } catch ( ParseException e ) {
            e.printStackTrace();
            this.valid = false;
        }

        // Skin comes next
        byte[] skin = new byte[byteBuffer.getInt()];
        byteBuffer.get( skin );

        this.validationKey = this.skinValidationKey;

        try {
            Map<String, Object> data = decodeBase64JSON( null, new String( skin ) );
            if ( data != null ) {
                if ( data.containsKey( "SkinData" ) && data.containsKey( "SkinId" ) ) {
                    this.skin = new PlayerSkin( (String) data.get( "SkinId" ), Base64.getDecoder().decode( (String) data.get( "SkinData" ) ) );
                }

                if ( data.containsKey( "DeviceOS" ) && data.containsKey( "DeviceModel" ) ) {
                    this.deviceInfo = new DeviceInfo( (int) data.get( "DeviceOS" ), (String) data.get( "DeviceModel" ) );
                }

                Object clientIdObj = data.get( "ClientRandomId" );
                this.clientId = clientIdObj instanceof Long ? (long) clientIdObj : (int) clientIdObj;
            }
        } catch ( ParseException e ) {
            e.printStackTrace();
            this.valid = false;
        }
    }

    private String parseBae64JSON( String data ) throws ParseException {
        // Be able to "parse" the payload
        String[] tempBase64 = data.split( "\\." );

        String payload = new String( Base64.getDecoder().decode( tempBase64[1] ) );
        JSONObject chainData = (JSONObject) new JSONParser().parse( payload );
        return (String) chainData.get( "identityPublicKey" );
    }

    private Map<String, Object> decodeBase64JSON( String neededIssuer, String data ) throws ParseException {
        try {
            // Get validation key
            Key key = getPublicKey( Base64.getDecoder().decode( this.validationKey ) );
            if ( key == null ) {
                return null;
            }

            // Check JWT
            Claims claims = Jwts.parser().setSigningKey( key ).parseClaimsJws( data ).getBody();

            // Only certification authory is allowed to set new validation keys
            Boolean certificateAuthority = (Boolean) claims.get( "certificateAuthority" );
            if ( certificateAuthority != null && certificateAuthority ) {
                this.validationKey = (String) claims.get( "identityPublicKey" );

                // We have to blindy trust this auth when its the first (they send the root cert in 0.15.4+)
                if ( this.firstCertAuth && this.validationKey.equals( MOJANG_PUBLIC ) ) {
                    this.firstCertAuth = false;
                    return null;
                }
            }

            // Invalid Issuer ?
            if ( neededIssuer != null && !neededIssuer.equals( claims.getIssuer() ) ) {
                this.valid = false;
            }

            // Check for extra data
            Map<String, Object> extraData = (Map<String, Object>) claims.get( "extraData" );
            if ( extraData != null ) {
                return extraData;
            } else {
                return claims;
            }
        } catch ( Exception e ) {
            // This normally comes when the user is not logged in into xbox live since the payload only sends
            // the self signed cert without a certifaction authory
            this.valid = false;

            // Be able to "parse" the payload
            String[] tempBase64 = data.split( "\\." );

            String payload = new String( Base64.getDecoder().decode( tempBase64[1] ) );
            JSONObject chainData = (JSONObject) new JSONParser().parse( payload );
            if ( chainData.containsKey( "extraData" ) ) {
                return (Map<String, Object>) chainData.get( "extraData" );
            }
        }

        return null;
    }

    private ECPublicKey getPublicKey( byte[] publicKeyBlob ) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec( publicKeyBlob );

        try {
            return (ECPublicKey) KEY_FACTORY.generatePublic( ks );
        } catch ( InvalidKeySpecException e ) {
            System.out.println( "Received invalid key specification from client" );
            this.valid = false;
            return null;
        } catch ( ClassCastException e ) {
            System.out.println( "Received valid X.509 key from client but it was not EC Public Key material" );
            this.valid = false;
            return null;
        }
    }

}
