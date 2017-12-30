package server;
import java.util.HashMap;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public final class Data{
	public static final AtomicInteger seq= new AtomicInteger(1);
	public static HashMap<Integer,String[]> s = new HashMap<Integer,String[]>();
	public static HashMap<Integer,InetAddress> clients = new HashMap<Integer,InetAddress>();
	public static HashMap<Integer,Integer> ports = new HashMap<Integer,Integer>();
}