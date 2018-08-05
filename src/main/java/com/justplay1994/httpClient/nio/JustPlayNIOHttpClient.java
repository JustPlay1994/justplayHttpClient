package com.justplay1994.httpClient.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Package: com.justplay1994.httpClient
 * @Project: justplayHttpClient
 * @Description:   //NIO
 * @Creator: huangzezhou
 * @Create_Date: 2018/8/2 15:10
 * @Updater: huangzezhou
 * @Update_Date: 2018/8/2 15:10
 * @Update_Description: huangzezhou
 **/
public class JustPlayNIOHttpClient {

    public StringBuilder stringBuilder;

    public void connect(String url, String type, String body) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final JustPlayHttpClientInboundHandler nioHandler = new JustPlayHttpClientInboundHandler();

            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    //
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(nioHandler);
                }
            });

            URI _url = new URI(url);
            String host = _url.getHost();
            int port = _url.getPort();
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            URI uri = new URI(_url.getRawPath()+"?"+_url.getRawQuery());

            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod(type),
                    uri.toASCIIString(), Unpooled.wrappedBuffer(body.getBytes("UTF-8")));

            //
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());

            //
            f.channel().write(request);
            f.channel().flush();
            f.channel().closeFuture().sync();
//            System.out.println("closed!");
            stringBuilder = nioHandler.stringBuilder;
        } finally {
            workerGroup.shutdownGracefully();
        }

    }


}
