import java.io.*;
import java.net.*;

public class torrentclient {
    public static void main(String[] args) throws IOException {

        Socket serverSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
		String filename,serverIP;
		int numchunks,  serverPort;
		long first=System.currentTimeMillis();
		if(args.length!=4){
			//System.out.println("Invalid combination of inputs");
			System.out.println("usage: (for client)\n$java torrentclient filename numchunks serverIP serverPort\nfilename: name of file to be downloaded \nnumchunks: number of chunks/parts in which file should be obtained from server \nserverIP: IP address of server\n serverPost: listening port");
			System.exit(1); 
		}
		
		//client connected		
		//filename numchunks serverIP serverPort
		filename= args[0];
		numchunks= Integer.parseInt(args[1]);
		serverIP= args[2];
		serverPort= Integer.parseInt(args[3]);		
        try {
            serverSocket = new Socket(serverIP, serverPort);
            System.out.println("Connected to server:  IP="+serverIP+",  Port="+ Integer.toString(serverPort)+", file name=\""+filename+"\", number of chunks="+Integer.toString(numchunks) );
            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to.");
            System.exit(1);
        }
		out.println(filename);
		out.println(numchunks);
		
		//Accept port numbers and create threads.
		MultiTorrentClientThread[] clients= new MultiTorrentClientThread[numchunks];
		System.out.print("Port numbers for receiving the chunks are : ");
		String[] ports=new String[numchunks];
		ports[0]=in.readLine();
		System.out.print(ports[0]);
		for(int i=1;i< numchunks;i++){
			ports[i]=in.readLine();
			System.out.print(", "+ports[i]);
		}
		System.out.println();
		for(int i=0;i< numchunks;i++){
			int tempport= Integer.parseInt(ports[i]);
			clients[i]= new MultiTorrentClientThread(serverIP,tempport,i);
			clients[i].start();
		}
		
		
		
		int done=0;
		while(done<numchunks){
   			done=0;
    		for(int i=0;i<numchunks;i++)
    			if(clients[i].transferComplete)done++;
    	}
    	
		out.close();
        in.close();
        serverSocket.close();
		
		//join received files
		File ofile = new File("received.txt");
		if(ofile.exists())ofile.delete();				//removed if a file with same name already present
		FileOutputStream fou;
		FileInputStream fin;
		byte[] fBytes;
		int bytesRead = 0;
    		fou = new FileOutputStream(ofile,true);     
    		for (int i=0;i< numchunks;i++) {
    			File file=new File("temp"+Integer.toString(i));
        		fin = new FileInputStream(file);
        		fBytes = new byte[(int) file.length()];
        		bytesRead = fin.read(fBytes, 0,(int)  file.length());
        		fou.write(fBytes);
        		fou.flush();
        		fin.close();
        		file.delete();
    		}
    		fou.close();
    		
    		
    	System.out.println("File download successful. Required file saved as \"received.txt\"");       
    	long end=System.currentTimeMillis();
    	System.out.println("Time required for transfer : "+(end-first)+" ms");
    	long speed=((ofile.length()*1000)/(end-first));
    	System.out.println("Average download rate : "+speed+" bytes/second");
    	
    }
}
