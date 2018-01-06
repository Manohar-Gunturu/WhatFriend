package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPRequestThread implements Runnable{
	
	
	DatagramSocket socket = null;
    DatagramPacket request = null;
    public UDPRequestThread(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.request = packet;
    }

	
	@Override
	public void run() {
		String tmp = new String(request.getData(),request.getOffset(),request.getLength()).trim();
        System.out.println("got req from "+request.getAddress()+" "+request.getPort()); 
		if( getParameter(tmp,"type").equals("register") ){
			int id = Data.seq.getAndIncrement();
			String[] tm = { getParameter(tmp,"name") , getParameter(tmp,"place")  };
			send( "type=user_id;id="+id+";", request.getAddress(), request.getPort(), -1);							
		    for( Integer i : Data.clients.keySet() ) {
				send("type=users;"+tm[0]+":"+tm[1]+":"+id, Data.clients.get(i), Data.ports.get(i), i);	
			}
			Data.s.put(id, tm);
			Data.clients.put(id, request.getAddress());
			Data.ports.put(id, request.getPort());	
		}else if( getParameter(tmp,"type").equals("update_address") ){			
			int i = Integer.parseInt(getParameter(tmp,"id"));
			update_address(i, request.getAddress(), request.getPort());
		}
		else if( getParameter(tmp,"type").equals("get_users") ){
		   String r = "type=users;";
		   for( Integer i : Data.s.keySet() ){
				String[] s = Data.s.get(i); 
				r += "#$"+s[0]+":"+s[1]+":"+i;
			}
			send(r,request.getAddress(), request.getPort(), -1);
		}else if( getParameter(tmp,"type").equals("message") ){
			int remote_user_id = Integer.parseInt(getParameter(tmp,"tid"));
			System.out.println("Sent ID "+remote_user_id);
			
			if(Data.s.containsKey(remote_user_id) ){
			    System.out.println("sending to user remote_user_id");
				send(tmp, Data.clients.get(remote_user_id), Data.ports.get(remote_user_id), remote_user_id);
			}
		}
	}


	public String getParameter(String str,String parameterName){
			str = str.substring(str.indexOf(parameterName+"=") + parameterName.length()+1);
			return str.substring(0, str.indexOf(";"));		
	}
	
	
	public void send(String message, InetAddress address, int port , int i){					
					System.out.println("sent reply as "+message);
					byte[] b = (message).getBytes();					
					DatagramPacket reply = new DatagramPacket(b, b.length, 
							address, port );										
					try {			
						socket.send(reply);
					} catch (IOException e) {						
						if(i != -1){
							Data.s.remove(i);
							Data.clients.remove(i);
							Data.ports.remove(i);
						}
						e.printStackTrace();
					}
	}		
	
	public void update_address(int id, InetAddress address, int port){
			Data.clients.put(id, address);
			Data.ports.put(id, port);			

	}
	
	
}
