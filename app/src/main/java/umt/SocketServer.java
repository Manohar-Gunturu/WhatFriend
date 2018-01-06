package umt;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wf.gu.udpchat.DBHelper;

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

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    SQLiteDatabase db;
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
        if(expectReply){
            byte[] respose = new byte[250];
            DatagramPacket reply = new DatagramPacket(respose, respose.length);
            try {
                socket.receive(reply);
                return new String(reply.getData(),reply.getOffset(),reply.getLength()).trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "sent";
    }



    private void onReceive(String m) {
        if(getParameter(m,"type").equals("message")){
            db.execSQL("INSERT INTO CHAT(UID,MESSAGE,TIME,UNAME,HIMAGE,IS_NEW,IS_WHOM,IS_SENT) " +
                    "VALUES("+Integer.parseInt(getParameter(m,"fid"))+",'"+getParameter(m,"value")+"','"+getParameter(m,"date")+"','"+getParameter(m,"uname")+"','user_files/download.svg',"+1+",1,1) ");
        }
        for(Callback c : SocketWrapper.callbacks) {
            c.onMessage(m);
        }
    }

    public String getParameter(String str,String parameterName){
        str = str.substring(str.indexOf(parameterName) + parameterName.length()+1);
        return str.substring(0, str.indexOf(";"));
    }

}
