import java.net.*;
import java.io.*;
import java.lang.*;

//for server threads
public class MultiTorrentServerThread extends Thread {
    private ServerSocket serverSocket = null;
    private String fileName=null;
	private int portNum=0;
    public MultiTorrentServerThread(int portNumber,  int fileNumber) {
    	try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Socket no. "+Integer.toString(fileNumber+1)+" bound to port no. "+Integer.toString(portNumber));
        } catch (IOException e) {
            System.err.println("Could not listen on given port.");
            System.exit(1);
        }
        fileName = Integer.toString(fileNumber);
        portNum=portNumber;
    }

    public void run() {
		try{
      Socket sock = serverSocket.accept();
	  System.out.println("Connected socket no. "+(Integer.parseInt(fileName)+1)+", client IP ="+sock.getInetAddress()+", port number ="+portNum);
	  
      // send file
      File myFile = new File (fileName);
      int size=(int)myFile.length();
      byte [] bytes  = new byte [size];
      FileInputStream fis = new FileInputStream(myFile);
      OutputStream os = sock.getOutputStream();
      BufferedInputStream bis = new BufferedInputStream(fis);      
      int b = bis.read(bytes,0,bytes.length);
    do{
       os.write(bytes, 0 , b);
       b=bis.read(bytes,0,bytes.length);
    }while(b>-1);
      System.out.println("Finished transfer of chunk no. "+Integer.toString(Integer.parseInt(fileName)+1));
      os.flush();
      myFile.delete();// delete temporary files after sending.
      sock.close();
      bis.close();
      serverSocket.close();
	}
	catch(IOException ioe){
		System.err.println("I/O error");
	}
    }
}
