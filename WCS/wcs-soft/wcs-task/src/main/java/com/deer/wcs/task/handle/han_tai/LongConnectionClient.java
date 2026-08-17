package com.deer.wcs.task.handle.han_tai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LongConnectionClient {
    private  Socket socket;
    private  DataOutputStream out;
    private  DataInputStream in;

    // 初始化连接
    public  boolean initConnection(String ip, Integer port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 3000);
            socket.setSoTimeout(2000);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            System.out.println("成功建立长连接");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    // 发送数据并接收响应
    public  char[] send(char[] sendChars) {
        if (socket == null || !socket.isConnected()) {
            System.out.println("连接未建立，请先初始化连接");
            return null;
        }
        try {
            // 发送 char 数组的长度
            byte[] msg = new byte[sendChars.length];
            // 逐个发送 char
            for (int i = 0; i < sendChars.length; i++) {
                msg[i] = (byte) sendChars[i];
            }
            out.write(msg);
            out.flush();
            System.out.println("客户端已发送 char 数组: " + new String(sendChars));

            byte[] result = new byte[30];
            // 接收服务器返回的 char 数组
            in.read(result);

            char[] resultChar = new char[result.length];
            // 逐个转换为 char
            for (int i = 0; i < result.length; i++) {
                resultChar[i] = (char) result[i];
            }

            System.out.println("客户端接收到服务器的 char 数组: " + new String(resultChar));
            return resultChar;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    // 关闭连接
    public  void closeConnection() throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (socket != null) {
            socket.close();
        }
        System.out.println("长连接已关闭");
    }

//    public static void main(String[] args) {
//        String ip = "127.0.0.1";
//        Integer port = 8888;
//        if (initConnection(ip, port)) {
//            char[] sendChars = {'H', 'e', 'l', 'l', 'o'};
//            char[] result = send(sendChars);
//            // 可以多次调用 send 方法进行数据传输
//            result = send(sendChars);
//            try {
//                closeConnection();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
