import java.net.*;
import java.io.*;

public class torrentserver {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
		int basePort=4455;
        try {
            serverSocket = new ServerSocket(basePort);
        } catch (IOException e) {
            System.err.println("Could not listen on given port.");
            System.exit(-1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
				new InputStreamReader(
				clientSocket.getInputStream()));
        String fname;
        int chunks;
        fname =in.readLine();				//took file name
        chunks=Integer.parseInt(in.readLine());   //took number of chunks
        
        //breaks the file into parts   
             
       	File ifile = new File(fname);		
		FileOutputStream chunk;
		int fileSize = (int) ifile.length();
		int chunksize=(int)(ifile.length()/chunks +1);
		int numchunks = 0, read = 0, readLength = chunksize;
		byte[] byteChunk;
    	FileInputStream fin = new FileInputStream(ifile);
		String newName;
    	
    	while (fileSize > 0) {
        	if (fileSize <= chunksize) {
           		readLength = fileSize;
        	}
        	byteChunk = new byte[readLength];
        	read = fin.read(byteChunk, 0, readLength);
        	fileSize -= read;
        	newName = Integer.toString(numchunks);
        	File newfile= new File(newName);
        	if(newfile.exists())newfile.delete();
        	chunk = new FileOutputStream(newfile);
        	chunk.write(byteChunk);
        	chunk.flush();
        	chunk.close();
        	numchunks++;
    	}
    	fin.close();
    	
    	MultiTorrentServerThread[] thread= new MultiTorrentServerThread[chunks];
    	
    	//opens ports and send port numbers to client
    	for(int i=0;i<chunks;i++){
    		thread[i]= new MultiTorrentServerThread(basePort+i+1, i);
    		thread[i].start();
    		out.println(Integer.toString(basePort+i+1));
    	}
    	
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
