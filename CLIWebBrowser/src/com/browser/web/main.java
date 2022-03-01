package com.browser.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		int choice;
		do {
			choice = input.nextInt();
			if (choice == 1) {
				input.nextLine();
				String url = input.nextLine();
				getWebsite(url);
			}
		} while (choice != 0);

	}

	public static void getWebsite(String url) {
		try {
			Socket socket = new Socket(url, 80);
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			System.out.println("1");
			String req = "GET / HTTP/1.1\r\nHost: "+url+"\r\n\r\n";
			System.out.println(req);
			bos.write(req.getBytes());
			// bos.write("GET /index.php/berita/lihatBerita\r\n\r\n".getBytes());
			bos.flush();

			System.out.println("2");

			int bufferSize = 100;
			byte[] bResp = new byte[bufferSize];
			int c = bis.read(bResp);
			String resp = "";

			while (c != -1) {
				resp += (new String(bResp));
				bResp = new byte[bufferSize];
				c = bis.read(bResp);
			}

			System.out.println("3");
			System.out.println(resp);
			System.out.println(4);

			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("There is an error occured");
			e.printStackTrace();
		}
	}

}
