package es.osoco.workshops.antlr.server

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.CharsetUtil
import io.netty.util.ReferenceCountUtil
import io.netty.util.concurrent.Future
import org.jetbrains.annotations.NotNull
import groovy.transform.CompileStatic

/**
 * {@link io.netty.channel.ChannelInboundHandlerAdapter} implementation for SimpleProtocol grammar.
 */
@CompileStatic
class NettyBackendChannelHandler
    extends ChannelInboundHandlerAdapter {

    private SimpleProtocolRequestHandler requestHandler

    /**
     * Creates a new instance.
     * @param handler the {@link SimpleProtocolRequestHandler handler}.
     */
    public NettyBackendChannelHandler(@NotNull final SimpleProtocolRequestHandler handler)
    {
        this.requestHandler = handler
    }

    public SimpleProtocolRequestHandler getRequestHandler() {
        return this.requestHandler
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelRead(@NotNull final ChannelHandlerContext ctx, @NotNull final Object msg) {
        try {
            @NotNull final ByteBuf buffer = (ByteBuf) msg

            @NotNull final byte[] aux = new byte[buffer.readableBytes()]

            for (int index = 0; index < aux.length; index++) {
                aux[index] = buffer.readByte()
            }

            processMessage(new String(aux, CharsetUtil.US_ASCII), getRequestHandler())

        } finally {
            ReferenceCountUtil.release(msg)
        }

        final ChannelFuture future = ctx.writeAndFlush("ACK")
        future.addListener(
            (Future<? super Void> channelFuture) -> {
                System.out.println("Closing context")
                ctx.close()
            })
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
        throws Exception {
        cause.printStackTrace()
        ctx.close()
    }

    /**
     * Processes given message.
     * @param message the message.
     * @param handler the {@link SimpleProtocolRequestHandler handler}.
     */
    public void processMessage(@NotNull final String message, @NotNull final SimpleProtocolRequestHandler handler) {
        handler.process(message)
    }
}
