package com.bnsoft.cafe_cmd_server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

public class U {
	
	private static Logger log = Logger.getLogger(ServerThread.class.getName());

	public static void infoOut(String info) {
		System.out.println(Ansi.ansi().reset().fg(Color.WHITE).a(S.GetCurrentTime(S.APP_TIME_FORMAT) + " -> ").bold().fg(Color.GREEN).a("[info] ").boldOff().fg(Color.WHITE).a(info).reset());
		log.log(Level.INFO, info);
	}
	
	public static void errOut(String err, Exception exception) {
		System.out.println(Ansi.ansi().reset().fg(Color.WHITE).a(S.GetCurrentTime(S.APP_TIME_FORMAT) + " -> ").bold().fg(Color.RED).a("[error] ").boldOff().fg(Color.WHITE).a(err).reset());
		if (exception != null) { log.log(Level.SEVERE, err, exception); }
		else { log.log(Level.SEVERE, err); }
	}
	
	public static void helpOut(String helpCommand, String helpDescription) {
		System.out.println(Ansi.ansi().reset().fg(Color.YELLOW).a("[HELP] " + helpCommand).a(": ").fg(Color.WHITE).a(helpDescription).reset());
	}
}
