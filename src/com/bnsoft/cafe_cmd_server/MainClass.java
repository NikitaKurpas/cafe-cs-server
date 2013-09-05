package com.bnsoft.cafe_cmd_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

public class MainClass {

	static ServerThread serverThread;

	private static Logger log = Logger.getLogger(MainClass.class.getName());
	static BufferedReader systemIn;
	
	//STATIC STRINGS
	public static String APP_REVISION_VERSION = "Revision version = 3.0-beta";
	
	public static String APP_BUILD_DATE = "Build date = 11.07.2013";
	//STATIC STRINGS

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AnsiConsole.systemInstall(); 
		
		try {
            LogManager.getLogManager().readConfiguration(
                    MainClass.class.getResourceAsStream("/logger.properties"));
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

		System.out.print(Ansi.ansi().eraseScreen().fg(Color.CYAN).bold().newline()
				.a("       __________  _______    _________       _____  __                \n"+
				   "       \\______   \\ \\      \\  /   _____/ _____/ ____\\/  |_              \n"+
				   "        |    |  _/ /   |   \\ \\_____  \\ /  _ \\   __\\\\   __\\             \n"+
				   "        |    |   \\/    |    \\/        (  <_> )  |   |  |               \n"+
				   "        |______  /\\____|__  /_______  /\\____/|__|   |__|               \n"+
				   "               \\/         \\/        \\/                                 \n"+
				" _________         _____          _________                                \n"+
				" \\_   ___ \\_____ _/ ____\\____    /   _____/ ______________  __ ___________ \n"+
				" /    \\  \\/\\__  \\\\   __\\/ __ \\   \\_____  \\_/ __ \\_  __ \\  \\/ // __ \\_  __ \\ \n"+
				" \\     \\____/ __ \\|  | \\  ___/   /        \\  ___/|  | \\/\\   /\\  ___/|  | \\/\n"+
				"  \\______  (____  /__|  \\___  > /_______  /\\___  >__|    \\_/  \\___  >__|   \n"+
				"         \\/     \\/          \\/          \\/     \\/                 \\/       \n\n").fg(Color.GREEN).reset());
//				.a("BNSoft Cafe Server").fg(Color.CYAN) 
//				.a(" ------------------------------").newline().reset().boldOff()); 
		System.out.print(Ansi.ansi().fg(Color.YELLOW).cursorRight(50)
				.a(APP_REVISION_VERSION).newline());
		System.out.print(Ansi.ansi().cursorRight(50)
				.a(APP_BUILD_DATE).reset().newline().newline());

		try {
			File file = new File(S.APP_ORDERS_FOLDER_NAME);
			if (!file.exists())						
				file.mkdir();						
			file = new File(S.APP_DB_FOLDER_NAME);   
			if (!file.exists())						
				file.mkdir();
			file = new File("logs");
			if (!file.exists())						
				file.mkdir();
			file = null;			
		} catch (Exception e) {
			log.log(Level.SEVERE, null, e);
		}
			
		serverThread = new ServerThread(Config.PORT);
		serverThread.start();						

		while (true) { 		
			systemIn = new BufferedReader(new InputStreamReader(System.in)); 
			try {															
				String input = systemIn.readLine();			
				if (input.toLowerCase().equals("exit") || input.toLowerCase().equals("stop")) {  
					U.infoOut("Stopping...");	
					serverThread.interrupt();				
					AnsiConsole.systemUninstall();			
					System.exit(0);							
				} else if (input.toLowerCase().equals("help")) { 
					printHelp();							
				} else if (input.toLowerCase().contains("samplefile")) { 
					int size = input.split(" ").length == 1 ? 0 : Integer.parseInt(input.split(" ")[1]);
					U.infoOut("Generating base sample file + additional "+size+" items");
					if (writeSampleGoodsFile(size)) { U.infoOut("Sample file written!"); }    
					else { U.errOut("An error occured! Sample file not written!", null); }
				} else	
					U.errOut("Unknown command. To list availible commands type <help>", null); 
			} catch (IOException e) {
				log.log(Level.SEVERE, "User input error", e); 
			}
		}

	}
	
	private static void printHelp() {
		/*
		System.out.print(Ansi.ansi().fg(Color.YELLOW).newline()
				.a("help").cursorRight(12).fg(Color.WHITE)
				.a("- Lists all availible commands;").newline());
		System.out.print(Ansi.ansi().fg(Color.YELLOW)
				.a("exit").cursorRight(12).fg(Color.WHITE)
				.a("- Stops server and exits the application;").newline());
		System.out.print(Ansi.ansi().fg(Color.YELLOW)
				.a("samplefile <N>").cursorRight(2).fg(Color.WHITE)
				.a("- Creates s sample file with some predefined goods that you can use to test the apps. You can define a nuber of generated strings (1-5000) for testing by setting the <N> parameter").newline().reset());
		 */
		U.helpOut("help", "Lists all availible commands");
		U.helpOut("exit", "Stops server and exits the application");
		U.helpOut("stop", "Same as exit");
		U.helpOut("samplefile <N>", "Creates s sample file with some predefined goods that you can use to test the apps. You can define a number of additionaly generated strings (1-5000) for testing by setting the <N> parameter");
	}

	

	private static boolean writeSampleGoodsFile(int size) {
		boolean completedB = false;
		Workbook RET_VAL = new HSSFWorkbook();
		Sheet sheet = RET_VAL.createSheet();
		
		sheet.createRow(0).createCell(0).setCellValue("Pizza Italiano 400g");
		sheet.getRow(0).createCell(1).setCellValue("5.62");
		sheet.createRow(1).createCell(0).setCellValue("Pizza Italiano 800g");
		sheet.getRow(1).createCell(1).setCellValue("8.00");
		sheet.createRow(2).createCell(0).setCellValue("Pizza Moccarelo 500g");
		sheet.getRow(2).createCell(1).setCellValue("6.65");
		sheet.createRow(3).createCell(0).setCellValue("Pizza Moccarelo 900g");
		sheet.getRow(3).createCell(1).setCellValue("10.50");
		sheet.createRow(4).createCell(0).setCellValue("Pizza Margarita 300g");
		sheet.getRow(4).createCell(1).setCellValue("4.00");
		sheet.createRow(5).createCell(0).setCellValue("Pizza Margarita 600g");
		sheet.getRow(5).createCell(1).setCellValue("7.25");
		sheet.createRow(6).createCell(0).setCellValue("Pizza Margarita 1000g");
		sheet.getRow(6).createCell(1).setCellValue("13.00");
		sheet.createRow(7).createCell(0).setCellValue("Pizza Benedict 500g");
		sheet.getRow(7).createCell(1).setCellValue("8.00");
		sheet.createRow(8).createCell(0).setCellValue("Pizza Benedict 900g");
		sheet.getRow(8).createCell(1).setCellValue("14.80");
		sheet.createRow(9).createCell(0).setCellValue("Pizza Milano 300g");
		sheet.getRow(9).createCell(1).setCellValue("3.00");
		sheet.createRow(10).createCell(0).setCellValue("Pizza Milano 600g");
		sheet.getRow(10).createCell(1).setCellValue("5.00");
		sheet.createRow(11).createCell(0).setCellValue("Pizza Milano 900g");
		sheet.getRow(11).createCell(1).setCellValue("8.00");
		sheet.createRow(12).createCell(0).setCellValue("Tea Queen of England small");
		sheet.getRow(12).createCell(1).setCellValue("1.50");
		sheet.createRow(13).createCell(0).setCellValue("Tea Queen of England big");
		sheet.getRow(13).createCell(1).setCellValue("3.20");
		sheet.createRow(14).createCell(0).setCellValue("Coffee Espresso");
		sheet.getRow(14).createCell(1).setCellValue("1.50");
		sheet.createRow(15).createCell(0).setCellValue("CocaCola 500ml");
		sheet.getRow(15).createCell(1).setCellValue("2.30");
		sheet.createRow(16).createCell(0).setCellValue("Sprite 500ml");
		sheet.getRow(16).createCell(1).setCellValue("2.00");
		sheet.createRow(17).createCell(0).setCellValue("Fanta 500ml");
		sheet.getRow(17).createCell(1).setCellValue("2.00");
		sheet.createRow(18).createCell(0).setCellValue("BNSoft's special");
		sheet.getRow(18).createCell(1).setCellValue("50.00");
		sheet.createRow(19).createCell(0).setCellValue("Fried chicken");
		sheet.getRow(19).createCell(1).setCellValue("15.00");
		sheet.createRow(20).createCell(0).setCellValue("Hamburger");
		sheet.getRow(20).createCell(1).setCellValue("2.00");
		sheet.createRow(21).createCell(0).setCellValue("ukr: ковбасна нарізка 250 грам");
		sheet.getRow(21).createCell(1).setCellValue("15.80");
		sheet.createRow(22).createCell(0).setCellValue("rus: Пицца \"Семейная\"");
		sheet.getRow(22).createCell(1).setCellValue("122.00");
		sheet.createRow(23).createCell(0).setCellValue("Взбитые сливки 300г");
		sheet.getRow(23).createCell(1).setCellValue("12.50");
		sheet.createRow(24).createCell(0).setCellValue("jpn: 500グラムのチーズボール");
		sheet.getRow(24).createCell(1).setCellValue("1200.00");
		sheet.createRow(25).createCell(0).setCellValue("chn: 飯糰10枚");
		sheet.getRow(25).createCell(1).setCellValue("2180.00");
		sheet.createRow(26).createCell(0).setCellValue("hnd: आइसक्रीम की 100 ग्राम");
		sheet.getRow(26).createCell(1).setCellValue("2.03");
		sheet.createRow(27).createCell(0).setCellValue("DEBUG: "+S.GetCurrentTime(S.APP_DATE_TIME_FORMAT));
		sheet.getRow(27).createCell(1).setCellValue(S.GetCurrentTime("mm.ss"));
		
		int last_row = sheet.getLastRowNum();
		if (size!=0 && size<=5000)
			for (int i = 1; i <= size; i++) {
				sheet.createRow(i+last_row).createCell(0).setCellValue("Simple item "+i);
				sheet.getRow(i+last_row).createCell(1).setCellValue(i+",00");
			}
		
		try {
			File file = new File(S.APP_DB_FOLDER_NAME+File.separator+Config.APP_DB_NAME);
			file.createNewFile();
			RET_VAL.write(new FileOutputStream(file));
			completedB = true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Generating sample database file error", e);
		}
		
		return completedB;
	}

}
