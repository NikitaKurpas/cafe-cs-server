package com.bnsoft.cafe_cmd_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.gson.Gson;

public class ClientThread extends Thread {
	
	private Socket socket;
	private BufferedReader mConLineIn;
	private PrintWriter mConLineOut;
	private int MAIN_SERVER_TIMEOUT = 5000;
	
	ClientThread (Socket socket) {
		this.socket = socket;
		this.run();
	}
	
	public void run () {
		try {
			U.infoOut("Connection accepted: " + socket);
			mConLineIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			mConLineOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8")), true);
			socket.setSoTimeout(MAIN_SERVER_TIMEOUT);

			String recievedCommand = mConLineIn.readLine();
/* UPDATE */if (recievedCommand.equals(S.UPDATE_STRING))
				try {
					U.infoOut("Update requested from client "+socket);
					
					// -------------------------------------
					File file = new File(S.APP_DB_FOLDER_NAME+File.separator+Config.APP_DB_NAME);
					FileInputStream fis = new FileInputStream(file);
					String fileMD5 = DigestUtils.md5Hex(fis);
					mConLineOut.println(Config.UPDATE_PORT);
					mConLineOut.println(fileMD5);
					mConLineOut.println(file.length());
					fis = new FileInputStream(file);
					InetSocketAddress listenAddress = new InetSocketAddress(Config.UPDATE_PORT);
					ServerSocketChannel listener = ServerSocketChannel.open();
					ServerSocket updateServer = listener.socket();
					updateServer.setReuseAddress(true);
					updateServer.bind(listenAddress);
					SocketChannel updateConnection = listener.accept();
					updateConnection.configureBlocking(true);
					FileChannel fc = fis.getChannel();
					long bytes_sent = fc.transferTo(0, file.length(), updateConnection);
					fc.close();
					fis.close();
					updateServer.close();
					listener.close();
					U.infoOut("Update sent: size="+file.length()+"; bytes sent="+bytes_sent+"; MD5="+fileMD5);
					// -------------------------------------
					
				} catch (Exception e) {
					U.errOut("An error occured while updating client's database!", e);
					mConLineOut.println(S.ERROR_STRING);
					mConLineOut.println("Server error:"+e.toString());
				}
/* ORDER */	if (recievedCommand.equals(S.ORDER_STRING)) {
				try {
//					String orderTable = mConLineIn.readLine();
//					String orderID = mConLineIn.readLine();
//					String orderTime = mConLineIn.readLine();
//					int orderSize = Integer.parseInt(mConLineIn.readLine());
//					for (int i = 0; i < orderSize; i++) {
//						order = mConLineIn.readLine();
//					}
					Gson gson = new Gson();
					OrderHolder order = gson.fromJson(mConLineIn.readLine(), OrderHolder.class);
					if (order!=null) {
						File file = new File(S.APP_ORDERS_FOLDER_NAME+File.separator+"order_" + order.tableN + "_" + order.orderID + ".xls");
						if (file.exists()) { file.delete(); }
						file.createNewFile();
						/*
						 * The code below is for adding lines to an existing workbook.
						 * I left it here because someday i will get use of it.
						 * Don't remove it.
						 */
//						Workbook wb;
//						try {
//							wb = WorkbookFactory.create(file);
//						} catch (Exception e) {
//							wb = new HSSFWorkbook();
//						}
//						Sheet sheet;
//						try {
//							sheet = wb.getSheetAt(0);
//						} catch (Exception e) {
//							sheet = wb.createSheet();
//						}
						Workbook wb = new HSSFWorkbook();
						Sheet sheet = wb.createSheet();
						int row = 0;
//						int row = sheet.getLastRowNum() == 0 ? 0 : sheet.getLastRowNum()+1;
						sheet.createRow(row).createCell(0).setCellValue("Order ID");
						sheet.getRow(row).createCell(1).setCellValue(order.orderID);
						sheet.createRow(++row).createCell(0).setCellValue("Order Table");
						sheet.getRow(row).createCell(1).setCellValue(order.tableN);
						sheet.createRow(++row).createCell(0).setCellValue("Order Price");
						sheet.getRow(row).createCell(1).setCellValue(order.orderPrice.toString());
						for (int i = 0; i < order.orderedItems.size(); i++) {
							sheet.createRow(++row).createCell(0).setCellValue(order.orderedItems.get(i).getName());
							sheet.getRow(row).createCell(1).setCellValue(order.orderedItems.get(i).getPrice().toString());
						}
						wb.write(new FileOutputStream(file));
					}
				} catch (Exception e) {
					U.errOut("An error occured while recieving order from the client!", e);
				}
			}
		} catch (Exception e) {
			U.errOut("An error occured while communicating with the client!", e);
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			U.errOut("An error occured while trying to close client's connection", e);
		}
	}
}
