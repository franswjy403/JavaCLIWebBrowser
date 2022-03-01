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
			String domain = url.split("/")[0];
			String path = "";
			if(url.indexOf("/")!=-1) path = url.substring(url.indexOf("/"));
			else path = "/";
			path.trim();
			System.out.println(domain);
			System.out.println(path);
			Socket socket = new Socket(domain, 80);
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			
			String req = "GET "+path+" HTTP/1.1\r\nHost: "+domain+"\r\n\r\n";
			System.out.println(req);
			bos.write(req.getBytes());
			// bos.write("GET /index.php/berita/lihatBerita\r\n\r\n".getBytes());
			bos.flush();


			int bufferSize = 100;
			byte[] bResp = new byte[bufferSize];
			int c = bis.read(bResp);
			String resp = "";

			while (c != -1) {
				resp += (new String(bResp));
				bResp = new byte[bufferSize];
				c = bis.read(bResp);
			}

			String[] basket = resp.split("\n");
			String[] indiv = basket[0].split(" ");
			   
			if(checkError(indiv)) {
				System.out.println(resp);
			}
			else {
				for (int i = 1; i<indiv.length; i++)
				System.out.print(indiv[i]+' ');
			}

			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("There is an error occured");
			e.printStackTrace();
		}
	}
	
	 public static boolean checkError(String[] ind) {
		 if(ind[1].charAt(0) == '2') {
			 return true;
		 }
		 else {
			 return false;
		 }
	 }

}
