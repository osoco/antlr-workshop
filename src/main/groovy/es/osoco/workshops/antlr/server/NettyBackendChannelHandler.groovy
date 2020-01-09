package es.osoco.workshops.antlr.server

import es.osoco.logging.LoggingFactory
import groovy.transform.CompileStatic
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.util.CharsetUtil
import io.netty.util.ReferenceCountUtil
import org.jetbrains.annotations.NotNull

/**
 * {@link io.netty.channel.ChannelInboundHandlerAdapter} implementation for SimpleProtocol grammar.
 */
@CompileStatic
@ChannelHandler.Sharable
class NettyBackendChannelHandler
    extends ChannelInboundHandlerAdapter {

    SimpleProtocolRequestHandler requestHandler

    /**
     * Creates a new instance.
     * @param handler the {@link SimpleProtocolRequestHandler handler}.
     */
    NettyBackendChannelHandler(@NotNull final SimpleProtocolRequestHandler handler)
    {
        this.requestHandler = handler
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void channelRead(@NotNull final ChannelHandlerContext ctx, @NotNull final Object msg) {
        String response = 'NACK'

        try {
            @NotNull final ByteBuf buffer = (ByteBuf) msg

            @NotNull final byte[] aux = new byte[buffer.readableBytes()]

            for (int index = 0; index < aux.length; index++) {
                aux[index] = buffer.readByte()
            }

            response = processMessage(new String(aux, CharsetUtil.US_ASCII), getRequestHandler())

        } finally {
            ReferenceCountUtil.release(msg)
        }

        final ChannelFuture future = ctx.write(Unpooled.copiedBuffer(response + '\n', CharsetUtil.UTF_8))
        ctx.flush()
        if (!future.isSuccess()) {
            LoggingFactory.instance.createLogging().error("Send failed: ${future.cause()}")
        }
        future.addListener(
            { channelFuture ->
                LoggingFactory.instance.createLogging().error("(Client disconnected)")
                ctx.close()
            })
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
        throws Exception {
        LoggingFactory.instance.createLogging().error(cause.message, cause)
        ctx.close()
    }

    /**
     * Processes given message.
     * @param message the message.
     * @param handler the {@link SimpleProtocolRequestHandler handler}.
     */
    String processMessage(@NotNull final String message, @NotNull final SimpleProtocolRequestHandler handler) {
        handler.process(message)
    }
}
