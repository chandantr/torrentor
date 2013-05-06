all : MultiTorrentServerThread.class MultiTorrentClientThread.class torrentclient.class torrentserver.class

MultiTorrentServerThread.class: MultiTorrentServerThread.java
	javac MultiTorrentServerThread.java

MultiTorrentClientThread.class: MultiTorrentClientThread.java
	javac MultiTorrentClientThread.java

torrentclient.class: torrentclient.java
	javac torrentclient.java

torrentserver.class: torrentserver.java
	javac torrentserver.java

clean:
	rm *.class
