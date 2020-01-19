package es.osoco.workshops.antlr.server.netty

import es.osoco.logging.LoggingFactory
import es.osoco.workshops.antlr.server.UrlCheckerRequestHandler
import groovy.transform.CompileStatic
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Netty-based TCP/IP server for SimpleProtocol grammar.
 */
@CompileStatic
class NettyBackend {
    /**
     * The server bootstrap.
     */
    private ServerBootstrap serverBootstrap

    /**
     * The event loop group.
     */
    private EventLoopGroup eventLoopGroup

    /**
     * The channel future.
     */
    private ChannelFuture channelFuture

    protected void setServerBootstrap(@NotNull final ServerBootstrap bootstrap) {
        this.serverBootstrap = bootstrap
    }

    /**
     * Retrieves the {@link ServerBootstrap}.
     * @return such bootstrap.
     */
    @SuppressWarnings("unused")
    @Nullable
    protected ServerBootstrap getServerBootstrap() {
        return this.serverBootstrap
    }

    /**
     * Specifies the event loop group.
     * @param group such {@link EventLoopGroup}.
     */
    protected void setEventLoopGroup(@NotNull final EventLoopGroup group) {
        this.eventLoopGroup = group
    }

    /**
     * Retrieves the event loop group.
     * @return such {@link EventLoopGroup}.
     */
    protected EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup
    }

    /**
     * Specifies the channel future.
     * @param future such {@link ChannelFuture}.
     */
    @SuppressWarnings("unused")
    protected void setChannelFuture(@NotNull final ChannelFuture future) {
        this.channelFuture = future
    }

    /**
     * Retrieves the channel future.
     * @return such {@link ChannelFuture}.
     */
    @SuppressWarnings("unused")
    protected ChannelFuture getChannelFuture() {
        return this.channelFuture
    }

    @NotNull
    void start(final int port) {
        try {
            final ChannelFuture future = launchServer(port)

            future.sync()
        } catch (final InterruptedException | IOException interruption) {
            LoggingFactory.instance.createLogging().error(interruption.message, interruption)
        }
    }
    /**
     * Launches the server.
     * @param port the port.
     * @return the {@link ChannelFuture}.
     * @throws InterruptedException if the server gets interrupted.
     * @throws IOException if the socket cannot be bound.
     */
    ChannelFuture launchServer(final int port)
        throws InterruptedException,
               IOException {

        return launchServer(port, new NettyBackendChannelHandler(new UrlCheckerRequestHandler()))
    }

    /**
     * Launches the server.
     * @param port the port.
     * @param handler the {@link ChannelHandlerAdapter handler} to handle incoming connections.
     * @return the {@link ChannelFuture}.
     * @throws InterruptedException if the server gets interrupted.
     * @throws IOException if the socket cannot be bound.
     */
    @NotNull
    protected ChannelFuture launchServer(final int port, @NotNull final ChannelHandlerAdapter handler)
        throws InterruptedException,
               IOException {
        @NotNull final ChannelFuture result

        @Nullable ChannelFuture aux = null

        @NotNull final EventLoopGroup bossGroup = new NioEventLoopGroup()
        setEventLoopGroup(bossGroup)
        @NotNull final EventLoopGroup workerGroup = new NioEventLoopGroup()
        try
        {
            final ServerBootstrap b = new ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>()
                {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    void initChannel(@NotNull final SocketChannel ch)
                        throws Exception
                    {
                        ch.pipeline().addLast(handler)
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            LoggingFactory.instance.createLogging().info('***')
            LoggingFactory.instance.createLogging().info("*** Server started on port ${port}")
            LoggingFactory.instance.createLogging().info('***')

            // Bind and start to accept incoming connections.
            aux = b.bind(port).sync() 

        } catch (final Throwable throwable) {
            LoggingFactory.instance.createLogging().error(
                "Cannot run the ANTLR workshop server", throwable)
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }

        if (aux == null) {
            throw new RuntimeException("Error starting the ANTLR workshop server")
        } else {
            result = aux
        }

        return result
    }

    /**
     * Stops the server.
     * @throws InterruptedException if the server cannot be stopped.
     */
    @SuppressWarnings("unused")
    void stopServer() throws InterruptedException {
        stopServer(getEventLoopGroup())
    }

    /**
     * Stops the server.
     * @param group the {@link EventLoopGroup group}.
     * @throws InterruptedException if the server cannot be stopped.
     */
    protected void stopServer(@NotNull final EventLoopGroup group)
        throws InterruptedException {
        group.shutdownGracefully().sync()
    }
}
