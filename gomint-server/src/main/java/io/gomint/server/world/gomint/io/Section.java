/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.gomint.io;

import java.io.*;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Section implements AutoCloseable {

    private SectionFile file;

    private DataInputStream in;
    private DataOutputStream out;

    private ByteArrayOutputStream bOut;

    public Section( SectionFile file, SectionMode mode ) throws IOException {
        this.file = file;

        switch ( mode ) {
            case SKIP:
            case READ:
                // We need to read the section length
                DataInputStream in = file.getInputStream();
                int sectionLength = in.readInt();

                if ( mode == SectionMode.SKIP ) {
                    in.skipBytes( sectionLength );
                } else {
                    byte[] readData = new byte[sectionLength];
                    in.read( readData );
                    this.in = new DataInputStream( new ByteArrayInputStream( readData ) );
                }

                break;
            case WRITE:
                this.bOut = new ByteArrayOutputStream();
                this.out = new DataOutputStream( this.bOut );
        }
    }

    /**
     * Get the write side of this section
     *
     * @return write stream
     */
    public DataOutputStream getOutput() {
        return this.out;
    }

    public DataInputStream getInput() {
        return this.in;
    }

    @Override
    public void close() throws IOException {
        if ( this.out != null ) {
            this.out.close();
            this.bOut.close();

            // Write to file
            DataOutputStream out = this.file.getOutputStream();
            out.writeInt( this.bOut.size() );
            out.write( this.bOut.toByteArray() );
        }

        if ( this.in != null ) {
            this.in.close();
        }
    }

}
