/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.gomint.io;

import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;

import java.io.*;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SectionFile implements AutoCloseable {

    private File file;

    // Cached streams
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    /**
     * Construct new section based file
     *
     * @param file which is used to read or write sections to
     */
    public SectionFile( File file ) {
        this.file = file;
    }

    /**
     * Create a file output which we can write to
     *
     * @return stream to write into the file
     * @throws IOException when there is an error creating the file
     */
    public DataOutputStream getOutputStream() throws IOException {
        if ( this.outputStream != null ) {
            return this.outputStream;
        }

        if ( !this.file.exists() ) {
            this.file.createNewFile();
        }

        return this.outputStream = new DataOutputStream( new LZ4FrameOutputStream( new FileOutputStream( this.file ), LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB ) );
    }

    /**
     * Create a file input which we can read from
     *
     * @return stream to read from the file
     * @throws IOException when there is an error opening the file
     */
    public DataInputStream getInputStream() throws IOException {
        if ( this.inputStream != null ) {
            return this.inputStream;
        }

        return this.inputStream = new DataInputStream( new LZ4FrameInputStream( new FileInputStream( this.file ) ) );
    }

    /**
     * Create new section. This needs to be written in one session and closed before opening another section.
     *
     * @return new section to write data to
     * @throws IOException when something goes wrong with creating / opening files
     */
    public Section createSection() throws IOException {
        return new Section( this, SectionMode.WRITE );
    }

    public Section getSection() throws IOException {
        return new Section( this, SectionMode.READ );
    }

    @Override
    public void close() throws IOException {
        if ( this.inputStream != null ) {
            this.inputStream.close();
        }

        if ( this.outputStream != null ) {
            // Attach a empty section
            this.createSection().close();
            this.outputStream.close();
        }
    }

}
