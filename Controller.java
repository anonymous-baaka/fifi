import java.net.*;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.io.*;
import java.util.Map;
import java.util.HashMap;

/*class ControllerThread extends Thread
{
    final static boolean ClassWindowsBusyStatus[]=new boolean[3];
    static int ClasstokenNumber=0;
    final static MyRequestQueue requestqueue=new MyRequestQueue();
    
    static int turnToken;
    int windowNumber;
    int tokenNumber;
    Socket cl_coSocket;     
    Socket co_svSocket;
    String timeStamp;

    String cl_ip;
    int cl_port;

    DataOutputStream co_cloutStream;

    ControllerThread(Socket _clientSocket)
    {
        cl_coSocket=_clientSocket;
        tokenNumber=ClasstokenNumber;
        cl_ip=_clientSocket.getInetAddress().toString().substring(1);
        cl_port=_clientSocket.getPort();
        ClasstokenNumber++;
        windowNumber=-1;  //temp
        
        timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        turnToken=-1;
        try
        {        
            co_cloutStream = new DataOutputStream(cl_coSocket.getOutputStream());
            //token no to client
            String clientMessage="Assigned Token no #"+tokenNumber;    
            co_cloutStream.flush();
            co_cloutStream.writeUTF(clientMessage);

            requestqueue.push(new Request(timeStamp, cl_ip, tokenNumber, windowNumber));
        }catch(Exception e){
            System.out.println(e);
        }
    }
    @Override
    public void run()
    {
        String clientMessage;
        try
        {
            windowNumber=getWindowNumber();
            updateWaitQueue();
            clientMessage="Assigned window number: "+windowNumber;
            co_cloutStream.flush();
            co_cloutStream.writeUTF(clientMessage);
        }
    }

    public static void popSetTurn()
    {
        turnToken=requestqueue.pop().tokenNumber;
    }
    private void updateWaitQueue()
    {
        requestqueue.updateQueueWindowNumber(tokenNumber, windowNumber);
    }

    int getWindowNumber()
    {
        while(true)
        {
            ControllerThread.popSetTurn();
            for(int i=0;i<ClassWindowsBusyStatus.length;i++)
            {
                if(ClassWindowsBusyStatus[i]==false && turnToken==tokenNumber)
                    return i;
            }
        }
    }
    
}*/
class Shared{
    public static Queue<Integer>myqueue=new LinkedList<>();
    public static int maxcount=100;
    public static String ip="";
    public static int port=5557;
    public static Map<Integer,Socket>connections=new HashMap<Integer,Socket>();
}

class acceptConn extends Thread
{
    DataOutputStream co_cloutStream;
    int counter=0;

    @Override
    public void run()
    {
        try{System.out.println("Server started...");
        ServerSocket server=new ServerSocket(8888);
    
        while(counter<Shared.maxcount)
        {
            
            Socket clientControllerSocket=server.accept();
            System.out.println("Client connected");
            counter++;
            
            System.out.println("Client assigned token "+counter);
            co_cloutStream = new DataOutputStream(clientControllerSocket.getOutputStream());
            
            //token no to client
            String clientMessage="Assigned Token no #"+counter;    
            co_cloutStream.flush();
            co_cloutStream.writeUTF(clientMessage);

            Shared.myqueue.add(counter);
            Shared.connections.put(counter, clientControllerSocket);
            }
        }catch (Exception e)
        {
            //System.out.println("Exception: "+e);
        }
        return;
    }
}

class handleClient extends Thread{
    int windowNumber;
    handleClient(int _wn)
    {
        windowNumber=_wn;
    }
    @Override
    public void run(){
        ReentrantLock lock = new ReentrantLock();
        while(true){
            try{
            System.out.println("Window "+ windowNumber+" is available");
            lock.lock();
            while(Shared.myqueue.size()==0)
            {
                System.out.print("");
                continue;
            }
            int temp=Shared.myqueue.remove();
            Socket sc=Shared.connections.get(temp);
            
            System.out.println("Window "+ windowNumber+" assigned to token #"+temp);
            lock.unlock();
            //send window no.
            DataOutputStream co_cloutStream = new DataOutputStream(sc.getOutputStream());
            String clientMessage="Window "+ windowNumber+" assigned to token #"+temp ;   
            co_cloutStream.flush();
            co_cloutStream.writeUTF(clientMessage);

            int expectedTimeofCompletion=(int)(5000+Math.random()*10000);
            System.out.println("expected Time of Completion for Token "+ temp+" = " +expectedTimeofCompletion+"ms");
            
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            System.out.println("Token\tWindow\tTimeStamp\t\tService Time");
            System.out.println("=======================================================");
            System.out.println(temp+"\t"+windowNumber+"\t"+timeStamp+"\t"+expectedTimeofCompletion+" ms");
            
            Thread.sleep(expectedTimeofCompletion);
            

            System.out.println("Token #"+temp+" serviced. Window "+windowNumber+" freed!");
            
            clientMessage="Token #"+temp+" serviced. Window "+windowNumber+" freed!";   
            co_cloutStream.flush();
            co_cloutStream.writeUTF(clientMessage);
            //lock.unlock();
        }catch (Exception e)
		{
			//System.out.println("Exception: "+e);
		}
            
        }
    }
}
public class Controller
{
    public static void main(String[] args) throws Exception
    {
        /*try
        {
            ServerSocket controllersocket=new ServerSocket(8888);
            
      		System.out.println("Controller Started ....");

			while(true){
			Socket clientController=controllersocket.accept(); 
			ControllerThread sct = new ControllerThread(clientController); //send  the request to a separate thread
			sct.start();
            }
        }catch(Exception e){
			System.out.println(e);
		}*/
        try{
            acceptConn acpt=new acceptConn();
            acpt.start();

            handleClient hc=new handleClient(0);
            hc.start();

            handleClient hc1=new handleClient(1);
            hc1.start();

            handleClient hc2=new handleClient(2);
            hc2.start();
        }catch(Exception e){
			//System.out.println(e);
        }
    }
}