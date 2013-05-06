import java.net.*;
import java.io.*;

//class for creating client threads
public class MultiTorrentClientThread extends Thread {
    private Socket serverSocket = null;
	boolean transferComplete=false;
	String fileName;
	int fileNumber;
    
    public MultiTorrentClientThread(String serverIP, int tempport,int fileNum) {
    	fileName="temp"+ Integer.toString(fileNum);
    	fileNumber=fileNum;
		try {
            serverSocket = new Socket(serverIP, tempport);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to.");
            System.exit(1);
        }
    }

    public void run() {
	 // receive file
    try{
	int size=65536;
    byte [] bytes  = new byte [size];
    InputStream is = serverSocket.getInputStream();
    FileOutputStream fos = new FileOutputStream(fileName);
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    int b=is.read(bytes,0,size);
    do{
       bos.write(bytes, 0 , b);
       b=is.read(bytes,0,size);
    }while(b>-1);
    System.out.println("Received chunk no. "+Integer.toString(fileNumber+1));
    is.close();
    bos.close();
    serverSocket.close();    
    }
	catch(IOException ioe){
		System.err.println("I/O error");
	}
	transferComplete= true;
	
    }
}
