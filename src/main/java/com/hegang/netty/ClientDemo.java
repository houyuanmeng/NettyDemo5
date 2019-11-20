package com.hegang.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteBuffer;


/**
 * Creat By ${侯某某} on 2019/11/19
 */
public class ClientDemo {
    public static void main(String[] args){
        new ClientDemo().clientStart();
    }
    private void clientStart(){
        EventLoopGroup worker =new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                      System.out.println("通道初始化完成");
                      ch.pipeline().addLast(new ClientHandler());
                    }
                });

        try {
            System.out.println("开始链接");
            b.connect("127.0.0.1",8888).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("*******");
        }finally {
            worker.shutdownGracefully();
        }
    }
}
class ClientHandler extends ChannelInboundHandlerAdapter{
    public void channelActive(ChannelHandlerContext ctx)throws Exception{
    System.out.println("通道已经连接上");
    final ChannelFuture f=ctx.writeAndFlush(Unpooled.copiedBuffer("HelloNetty".getBytes()));
    f.addListener(new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
            System.out.println("消息已经发送！");
        }
    });
    }

    public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
        try {
            ByteBuffer buf= (ByteBuffer) msg;
            System.out.println(buf.toString());

        } finally {
            ReferenceCountUtil.release(msg);
        }

    }
}
