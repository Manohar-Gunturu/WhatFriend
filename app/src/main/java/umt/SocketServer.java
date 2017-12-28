package umt;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by SA on 21/12/2017.
 */
public class SocketServer {

    DatagramSocket socket = null;
    InetAddress remoteAddr = null;
    Callback callback = null;
    private String destAddr;

    public SocketServer(String destAddr) throws SocketException, UnknownHostException {
        this.destAddr = destAddr;
        socket = new DatagramSocket(null);
        remoteAddr = InetAddress.getByName(destAddr);

    }


    public void listen() {
        while (true) {
            byte[] respose = new byte[250];
            DatagramPacket packet = new DatagramPacket(respose, respose.length);
            try {
                socket.receive(packet);
                onReceive(new String(packet.getData(),packet.getOffset(),packet.getLength()).trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void registerCallback(Callback c) {
        this.callback = c;
    }

    public String send(String message, boolean expectReply) throws IOException {
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), remoteAddr, 3003);
        socket.send(packet);
        return "sent";
    }



    private void onReceive(String m) {
        for(Callback c : SocketWrapper.callbacks) {
            c.onMessage(m);
        }
    }

}
