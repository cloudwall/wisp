/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wisp.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wisp.api.Configuration;
import wisp.api.ServiceModule;
import wisp.websocket.api.WebSocketService;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * A Netty-based async I/O websocket server.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class WebsocketServer implements ServiceModule {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketServer.class);

    private final Map<String, WebSocketService> servicePaths = new HashMap<>();

    private boolean ssl = false;
    private int port = 8080;

    @Override
    public void configure(Configuration config) {
        // find all WebSocketServices in our ModuleLayer
        for (var wss : ServiceLoader.load(getClass().getModule().getLayer(), WebSocketService.class)) {
            String path = wss.getPath();
            if (servicePaths.containsKey(path)) {
                throw new IllegalStateException("duplicate WebSocketService#getPath(): " + path);
            } else {
                servicePaths.put(path, wss);
            }
            logger.info("registered {} on path {}", wss.getClass().getSimpleName(), path);
            wss.configure(config);
        }

        if (config.hasPath("wisp.websocket.ssl")) {
            ssl = config.getBoolean("wisp.websocket.ssl");
        }

        if (config.hasPath("wisp.websocket.port")) {
            port = config.getInt("wisp.websocket.port");
        }
    }

    @Override
    public void start() {
        logger.info("starting {} module", getClass().getSimpleName());

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final SslContext sslCtx;
            if (ssl) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebSocketServerInitializer(sslCtx, servicePaths));

            Channel ch = b.bind(port).sync().channel();
            logger.info("listening on port " + port);

            ch.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        logger.info("stopping {} module", getClass().getSimpleName());
    }

    @Override
    public void destroy() {
        logger.info("destroying {} module", getClass().getSimpleName());
    }
}
