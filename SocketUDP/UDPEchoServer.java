package SocketUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPEchoServer {

    public final static int serverPort = 8888;

    public static void main(String[] args) {
        try (DatagramSocket ds = new DatagramSocket(serverPort)) {
            System.out.println("UDP Server da duoc tao va dang cho du lieu...");
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                ds.receive(incoming);

                String receivedData = new String(incoming.getData(), incoming.getOffset(), incoming.getLength());
                System.out.println("Nhan tu Client: " + receivedData);

                DatagramPacket outsending = new DatagramPacket(
                        incoming.getData(),
                        incoming.getLength(),
                        incoming.getAddress(),
                        incoming.getPort());
                ds.send(outsending);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}