/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.player;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class PlayerSkin implements io.gomint.player.PlayerSkin {

    private static final int SKIN_DATA_SIZE_STEVE = 8192;
    private static final int SKIN_DATA_SIZE_ALEX = 16384;

    public static PlayerSkin fromInputStream( InputStream inputStream ) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream( inputStream );
        BufferedImage image = ImageIO.read( imageInputStream );

        if ( image.getWidth() != 64 ) {
            throw new IOException( "Input picture is not 64 pixel wide" );
        }

        if ( image.getHeight() == 64 || image.getHeight() == 32 ) {
            byte[] skinData = new byte[image.getHeight() == 64 ? SKIN_DATA_SIZE_ALEX : SKIN_DATA_SIZE_STEVE];
            int cursor = 0;

            for ( int y = 0; y < image.getWidth(); y++ ) {
                for ( int x = 0; x < image.getHeight(); x++ ) {
                    int color = image.getRGB( x, y );
                    skinData[cursor++] = (byte) ( ( color >> 16 ) & 0xFF ); // R
                    skinData[cursor++] = (byte) ( ( color >> 8 ) & 0xFF );  // G
                    skinData[cursor++] = (byte) ( color & 0xFF );           // B
                    skinData[cursor++] = (byte) ( ( color >> 24 ) & 0xFF ); // A
                }
            }

            return new PlayerSkin( "Gomint_Skin", skinData, new byte[0], "", "" );
        } else {
            throw new IOException( "Input picture is not 64 / 32 pixel high" );
        }
    }

    private String name;
    private byte[] data;
    private byte[] capeData;
    private String geometryName;
    private String geometryData;

    // Internal image caching
    private BufferedImage image;

    public PlayerSkin( String name, byte[] data, byte[] capeData, String geometryName, String geometryData ) {
        if ( data.length != SKIN_DATA_SIZE_STEVE && data.length != SKIN_DATA_SIZE_ALEX ) {
            throw new IllegalArgumentException( "Invalid skin data buffer length" );
        }

        this.name = name;
        this.data = data;
        this.capeData = capeData;
        this.geometryName = geometryName;
        this.geometryData = geometryData;

        try ( FileOutputStream fileOutputStream = new FileOutputStream( new File( this.name + ".png" ) ) ) {
            saveSkinTo( fileOutputStream );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void createImageFromSkinData() {
        if ( this.image != null ) {
            return;
        }

        int height = this.data.length == SKIN_DATA_SIZE_ALEX ? 64 : 32;
        this.image = new BufferedImage( 64, height, BufferedImage.TYPE_INT_ARGB );

        int cursor = 0;
        for ( int y = 0; y < 64; y++ ) {
            for ( int x = 0; x < height; x++ ) {
                byte r = this.data[cursor++];
                byte g = this.data[cursor++];
                byte b = this.data[cursor++];
                byte a = this.data[cursor++];

                int rgbValue = ( ( a & 0xFF ) << 24 ) |
                    ( ( r & 0xFF ) << 16 ) |
                    ( ( g & 0xFF ) << 8 ) |
                    ( b & 0xFF );

                this.image.setRGB( x, y, rgbValue );
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public byte[] getRawData() {
        return this.data;
    }

    @Override
    public byte[] getCapeData() {
        return this.capeData;
    }

    @Override
    public String getGeometryName() {
        return this.geometryName;
    }

    @Override
    public String getGeometryData() {
        return this.geometryData;
    }

    @Override
    public void saveSkinTo( OutputStream out ) throws IOException {
        this.createImageFromSkinData();
        ImageIO.write( this.image, "PNG", out );
    }

}
