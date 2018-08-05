package com.justplay1994.httpClient.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;


/**
 * @Package: com.justplay1994.httpClient
 * @Project: justplayHttpClient
 * @Description:   //TODO
 * @Creator: huangzezhou
 * @Create_Date: 2018/8/2 15:25
 * @Updater: huangzezhou
 * @Update_Date: 2018/8/2 15:25
 * @Update_Description: huangzezhou 补充
 **/
public class JustPlayHttpClientInboundHandler extends ChannelInboundHandlerAdapter{

//    private static final Logger logger = LoggerFactory.getLogger(JustPlayHttpClientInboundHandler.class);

    public StringBuilder stringBuilder = new StringBuilder();

    @Override
     public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse)
        {
            HttpResponse response = (HttpResponse) msg;
//            System.out.println("CONTENT_TYPE:" + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));
        }
        if(msg instanceof HttpContent)
        {
            HttpContent content = (HttpContent)msg;
            ByteBuf buf = content.content();
//            System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
            stringBuilder.append(buf.toString(io.netty.util.CharsetUtil.UTF_8));
            buf.release();
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelReadComplete();
//        logger.info("Finished!");
        ctx.close();//关闭链路
    }
}
