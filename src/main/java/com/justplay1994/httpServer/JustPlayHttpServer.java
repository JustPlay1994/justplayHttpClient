package com.justplay1994.httpServer;

/**
 * Created by JustPlay1994 on 2018/8/5.
 * https://github.com/JustPlay1994/daily-log-manager
 */



    import io.netty.bootstrap.ServerBootstrap;
    import io.netty.channel.ChannelFuture;
    import io.netty.channel.ChannelInitializer;
    import io.netty.channel.EventLoopGroup;
    import io.netty.channel.nio.NioEventLoopGroup;
    import io.netty.channel.socket.SocketChannel;
    import io.netty.channel.socket.nio.NioServerSocketChannel;
    import io.netty.handler.codec.http.HttpObjectAggregator;
    import io.netty.handler.codec.http.HttpRequestDecoder;
    import io.netty.handler.codec.http.HttpResponseEncoder;
    import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by LG on 2017/11/21.
 */
public class JustPlayHttpServer {

    public void run(final int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {

                            // HTTP请求消息解码器
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());

                            /*
                             * HttpObjectAggregator解码器
                             * 将多个消息转换为单一的FullHttpRequest或FullHttpResponse对象
                             */
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));

                            //HTTP响应编码器,对HTTP响应进行编码
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());

                            //ChunkedWriteHandler的主要作用是支持异步发送大的码流,但不占用过多的内存,防止JAVA内存溢出
                            ch.pipeline().addLast("http-chunked",
                                    new ChunkedWriteHandler());

                            ch.pipeline().addLast("httpServerHandler", new HttpServerHandler());
                        }
                    });
            ChannelFuture future = b.bind("localhost", port).sync();
            System.out.println("HTTP Server startup.");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new JustPlayHttpServer().run(port);
    }

}
