package es.osoco.workshops.antlr.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.io.IOException
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.LoggerFactory
import groovy.transform.CompileStatic

/**
 * Netty-based TCP/IP server.
 */
@CompileStatic
public class NettyBackend {
    /**
     * The system property to specify the port.
     */
    public static final String SERVER_PORT = "es.osoco.workshops.antlr.server.port"

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
    public void start() {
        try {
            final ChannelFuture future = launchServer()

            future.sync()
        } catch (@NotNull final InterruptedException | IOException interruption) {
	    // TODO
        }
    }

    /**
     * Launches the server.
     * @return the {@link ChannelFuture}.
     * @throws InterruptedException if the server gets interrupted.
     * @throws IOException if the socket cannot be bound.
     */
    public ChannelFuture launchServer()
        throws InterruptedException,
               IOException {
        final int port

        @Nullable final String aux = System.getProperty(SERVER_PORT)

        if (aux != null) {
            port = Integer.valueOf(aux)
        } else {
            port = 9999
        }

        return launchServer(port)
    }

    /**
     * Launches the server.
     * @param port the port.
     * @return the {@link ChannelFuture}.
     * @throws InterruptedException if the server gets interrupted.
     * @throws IOException if the socket cannot be bound.
     */
    public ChannelFuture launchServer(final int port)
        throws InterruptedException,
               IOException {

        return launchServer(port, new NettyBackendChannelHandler(new SimpleProtocolRequestHandler()))
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
            @NotNull final ServerBootstrap b = new ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>()
                { // (4)
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void initChannel(@NotNull final SocketChannel ch)
                        throws Exception
                    {
                        ch.pipeline().addLast(handler)
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)

            // Bind and start to accept incoming connections.
            aux = b.bind(port).sync() 

        } catch (@NotNull final Throwable throwable) {
            LoggerFactory.getLogger(NettyBackend.class).error(
                "Cannot run the ANTLR workshop server", throwable)
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }

        if (aux == null) {
            throw new RuntimeException("Error starting server")
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
    public void stopServer()
        throws InterruptedException {
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
