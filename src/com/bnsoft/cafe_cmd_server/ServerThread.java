package com.bnsoft.cafe_cmd_server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
	
/* MAIN SERVER */	
	ServerSocket mainServer;
	Socket connection;
	private int MAIN_SERVER_PORT = 8800;
/* |MAIN SERVER */
		
/* OTHER */	
//	private static Logger log = Logger.getLogger(ServerRunnable.class.getName());
	boolean sACTIVE = true;
/* |OTHER */	
	
	public ServerThread() {
		//TODO smth in the init if needed, else - leave empty
	}
	
	public ServerThread(int MAIN_PORT) {
		this.MAIN_SERVER_PORT = MAIN_PORT;
	}
	
	@Override
	public void run() {
		try {
			mainServer = new ServerSocket();
			mainServer.bind(new InetSocketAddress(MAIN_SERVER_PORT));

			U.infoOut("Server started!");
			U.infoOut("Server listening on port " + MAIN_SERVER_PORT);

			while (sACTIVE) {
				connection = mainServer.accept();
				new ClientThread(connection);
			}
		} catch (Exception e) {
			U.errOut("An error occurred in server thread: "+e.getMessage(), e);
		}
	}

}
