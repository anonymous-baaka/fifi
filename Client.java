import java.net.*;
import java.io.*;

class ClientThread extends Thread
{
	@Override
	public void run()
	{
		try
		{
			Socket socket=new Socket("127.0.0.1",8888);
			DataInputStream inStream=new DataInputStream(socket.getInputStream());
			DataOutputStream outStream=new DataOutputStream(socket.getOutputStream());
			String controllerMessage="";
	
			System.out.println("Welcome!!\nPlease wait...");
			
			//assign token number
			controllerMessage=inStream.readUTF();
			System.out.println(controllerMessage);
			
			//assign window number
			controllerMessage=inStream.readUTF();
			System.out.println(controllerMessage);
			
			//finished 
			controllerMessage=inStream.readUTF();
			System.out.println(controllerMessage);
			
			outStream.close();
			outStream.close();
			socket.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception: "+e);
		}
	}
}

public class Client extends Thread {
	public static void main(String[] args) throws Exception
	{
		int n=Integer.parseInt(args[0]);
		for(int i=0;i<n;i++)
		{
			ClientThread clientThread=new ClientThread();
			clientThread.start();
			Thread.sleep(1000);
		}
	}
}