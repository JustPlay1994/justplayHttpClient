package com.justplay.httpClient.bio;

import com.justplay.httpClient.nio.HttpClientInboundHandler;
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

/**
 * @Package: com.justplay.httpClient
 * @Project: justplayHttpClient
 * @Description:   //NIO客户端
 * @Creator: huangzezhou
 * @Create_Date: 2018/8/2 15:10
 * @Updater: huangzezhou
 * @Update_Date: 2018/8/2 15:10
 * @Update_Description: huangzezhou 补充
 **/
public class JustPlayBIOHttpClient {
    public void connect(String url, String type, String body) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(SocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpClientInboundHandler());
                }
            });

            URI _url = new URI(url);
            String host = _url.getHost();
            int port = _url.getPort();
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            URI uri = new URI("/sjtsfw-ldxx/v1/dps?service=DataPivotService&version=1.0.0&request=DescribeDataSet&datasetId=dffa5369-1b73-457c-9ff9-ef11aa852021&format=json");


            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod(type),
                    uri.toASCIIString(), Unpooled.wrappedBuffer(body.getBytes("UTF-8")));

            // 构建http请求
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());

            // 发送http请求
            f.channel().write(request);
            f.channel().flush();
            
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        JustPlayBIOHttpClient client = new JustPlayBIOHttpClient();
        client.connect(
                "http://10.190.55.62:8080/sjtsfw-ldxx/v1/dps?service=DataPivotService&version=1.0.0&request=DescribeDataSet&datasetId=dffa5369-1b73-457c-9ff9-ef11aa852021&format=json",
                "GET",
                ""
        );
    }
}
