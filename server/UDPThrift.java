package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPThrift {

    static ExecutorService threadPool = null;
	
    public UDPThrift(){
		if(threadPool == null){
			threadPool = Executors.newCachedThreadPool();
		}
	}
	
	 public void init(int port) {		
	                	System.out.println("Started UDP Server too");		                
	                	this.startUDPServer(port);	                
	  }
	
	 
	 public void startUDPServer(int port){
		 
		 DatagramSocket aSocket = null;
		 try{
		    aSocket = new DatagramSocket(port);
			while(true){
                byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
	  			aSocket.receive(request);
	  			threadPool.execute( new UDPRequestThread(aSocket, request));  
			  }
		   }catch (SocketException e){ 
			   e.printStackTrace(); 
		   
		   }catch (IOException e) {  
			   e.printStackTrace(); 
		   
		   }
		   finally {
			   System.out.println("Socket closed");
			   if(aSocket != null) aSocket.close();
			  
		   }
	 
	 }	
	 
	 
	 public static void main(String[] args){
		 new UDPThrift().init(3003);
	 }
}
