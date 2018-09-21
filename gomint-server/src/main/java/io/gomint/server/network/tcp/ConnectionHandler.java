/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.tcp;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.tcp.protocol.Packet;
import io.gomint.server.network.tcp.protocol.UpdatePingPacket;
import io.gomint.server.network.tcp.protocol.WrappedMCPEPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ConnectionHandler extends SimpleChannelInboundHandler<Packet> {

    private ChannelHandlerContext ctx;
    private Consumer<Void> whenConnected;
    @Getter
    private LinkedBlockingQueue<PacketBuffer> data = new LinkedBlockingQueue<>();
    private Consumer<Throwable> exceptionCallback;
    private Consumer<Void> disconnectCallback;
    private Consumer<Integer> pingCallback;

    ConnectionHandler() {
        super( true );
    }

    public Channel getChannel() {
        return ctx.channel();
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        this.ctx = ctx;

        if ( this.whenConnected != null ) {
            this.whenConnected.accept( null );
        }
    }

    public void send( Packet packet ) {
        this.ctx.writeAndFlush( packet );
    }

    @Override
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        if ( this.disconnectCallback != null ) {
            this.disconnectCallback.accept( null );
        }
    }

    @Override
    protected void channelRead0( ChannelHandlerContext channelHandlerContext, final Packet packet ) throws Exception {
        if ( packet instanceof WrappedMCPEPacket ) {
            WrappedMCPEPacket wrappedMCPEPacket = ( (WrappedMCPEPacket) packet );

            for ( PacketBuffer buffer : wrappedMCPEPacket.getBuffer() ) {
                this.data.offer( buffer );
            }
        } else if ( packet instanceof UpdatePingPacket ) {
            this.pingCallback.accept( ( (UpdatePingPacket) packet ).getPing() );
        }
    }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        if ( this.exceptionCallback != null ) {
            this.exceptionCallback.accept( cause );
        }
    }

    public void onPing( Consumer<Integer> callback ) {
        this.pingCallback = callback;
    }

    void whenConnected( Consumer<Void> callback ) {
        this.whenConnected = callback;
    }

    public void onException( Consumer<Throwable> callback ) {
        this.exceptionCallback = callback;
    }

    public void whenDisconnected( Consumer<Void> callback ) {
        this.disconnectCallback = callback;
    }

    public void disconnect() {
        this.ctx.disconnect().syncUninterruptibly();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + Integer.toHexString( this.hashCode() );
    }

}
