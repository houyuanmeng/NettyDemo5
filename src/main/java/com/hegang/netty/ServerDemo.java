package com.hegang.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
/**
 * Creat By ${侯某某} on 2019/11/19
 */
public class ServerDemo {
    public static void main(String[] args) {
        new NettyServer(8888).serverStart();
    }
}
class NettyServer {
    int port = 8888;
    public NettyServer(int port) {
        this.port = port;
    }
    public void serverStart() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();

        //指定两个线程池负责的工作，此处bossGroup负责链接，workerGroup负责链接后的读写
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)//建立链接后通道的类型

                //给客户端加代理链接上来后一旦初始化，就再通道上加
                // 一个new Handler()对通道的处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Handler());
                    }
                });
        try {
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
class Handler extends ChannelInboundHandlerAdapter {
    @Override
    //37行处理的过程就是重写channelRead方法，当通道上的数据传输过来后，
    // 读数据的过程下面给出，由msg解析出来
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);
        System.out.println("server: channel read");
        ByteBuf buf = (ByteBuf)msg;
        System.out.println(buf.toString(CharsetUtil.UTF_8));
        ctx.writeAndFlush(msg);
        ctx.close();
        //buf.release();
    }
    @Override

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();

    }

}