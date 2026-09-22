package SocketUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClient {

    public final static int serverPort = 8888;

    public static void main(String[] args) throws InterruptedException {
        try (DatagramSocket ds = new DatagramSocket()) {
            InetAddress serverIP = InetAddress.getByName("localhost");
            System.out.println("UDP CLIENT da duoc tao");

            for (char ch = '0'; ch <= '9'; ch++) {
                String sendData = "data | " + ch; // [cite: 8]
                byte[] sendBuffer = sendData.getBytes();
                DatagramPacket dp = new DatagramPacket(sendBuffer, sendBuffer.length, serverIP, serverPort);
                ds.send(dp);

                byte[] resBuffer = new byte[1024];
                DatagramPacket resPacket = new DatagramPacket(resBuffer, resBuffer.length);
                ds.receive(resPacket);

                String resData = new String(resPacket.getData(), resPacket.getOffset(), resPacket.getLength()); 
                System.out.println("Server phan hoi: " + resData); // [cite: 8]

                Thread.sleep(2000); // [cite: 8]
            }
            System.out.println("Client da goi du 0-9 va nhan du phan hoi "); // [cite: 8]

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}