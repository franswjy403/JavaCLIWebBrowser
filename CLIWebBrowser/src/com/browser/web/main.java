package com.browser.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
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
				String respo = getWebsite(url, false, null, false);
				System.out.println(respo);
				System.out.println("\nComplete");
			}
		} while (choice != 0);

	}
	
	public static class loginDetails {
		public String route;
		public String param1;
		public String param2;
		public String cookie;
		
		public loginDetails(String d, String p1, String p2) {
			// TODO Auto-generated constructor stub
			this.route = d;
			this.param1 = p1;
			this.param2 = p2;
		}
	}

	public static String getWebsite(String url, boolean auth, String autho, boolean isLogin) {
		try {
			String domain = url.split("/")[0];
			String path = "";
			if (url.indexOf("/") != -1)
				path = url.substring(url.indexOf("/"));
			else
				path = "/";
			path.trim();
			Socket socket = new Socket(domain, 80);
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

			String req = "GET " + path + " HTTP/1.1\r\nHost: " + domain + "\r\n\r\n";
			if (auth) {
				req = "GET " + path + " HTTP/1.1\r\nAuthorization: Basic " + autho + "\r\nHost: " + domain + "\r\n\r\n";
			}
			if(isLogin) {
				req = "POST " + path + " HTTP/1.1\r\nHost: " + domain + "\r\nContent-Type: application/x-www-form-urlencoded"
						+"\r\nContent-Length: "+autho.length()+"\r\n\n"+autho + "\r\n\r\n";
			}
			System.out.println(req);
			bos.write(req.getBytes());
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

			socket.close();
			String[] tempResp = resp.split("\n\r");
			String header = tempResp[0];
			String[] basket = header.split("\n");
			String[] indiv = basket[0].split(" ");
			String code = indiv[1];
			String respo = "";
			
			String checkCookie = isCookie(basket);
			
			System.out.println(header);
			System.out.println(basket[6]);
			
			if (code.charAt(0) == '2') {
				for(int i = 1;i<tempResp.length; i++) {
					respo+=tempResp[i];
				}
				loginDetails routeLogin = checkLogin(respo);
				
				if(!(routeLogin.route == null)) {
					if(!checkCookie.equals("cookie:"))routeLogin.cookie = checkCookie;
					Scanner input = new Scanner(System.in);
					String route = routeLogin.route;
					route = route.substring(route.indexOf('/')+2, route.length());
					
					System.out.println(routeLogin.param1+": ");
					String param1 = input.nextLine();
					System.out.println(routeLogin.param2+": ");
					String param2 = input.nextLine();
					
					String loginParams = routeLogin.cookie + "\r\n"+routeLogin.param1.toLowerCase()+"="+param1+"&"+routeLogin.param2.toLowerCase()+"="+param2;
					respo = getWebsite(route, false, loginParams, true);
				}
				
			} else if (code.equals("401")) {
				Scanner input = new Scanner(System.in);
				String user, pass;
				System.out.println("Username: ");
				user = input.nextLine();
				System.out.println("Password: ");
				pass = input.nextLine();

				String authString = user + ":" + pass;

				String authEncBytes = Base64.getEncoder().encodeToString(authString.getBytes());
				respo = getWebsite(url, true, authEncBytes, false);
			} else {
				for (int i = 1; i < indiv.length; i++) {
					respo += (indiv[i] + ' ');
				}
			}
			String[] redi = basket[4].split(";");
			if (redirect(redi)) {
				redi = redi[1].split(":");
				System.out.println(redi[1]);
				String fin = redi[1].substring(2, redi[1].length() - 1);
				respo = getWebsite(fin, false, null, false);
			}

			ArrayList<String> links = checkLink(respo);
			for (int i = 0; i < links.size(); i++) {
				respo += ("\n" + links.get(i));
			}
			
			
			
			return respo;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("There is an error occured");
			e.printStackTrace();
		}
		return "Not working somehow";
	}

	public static char checkError(String[] ind) {
		return ind[1].charAt(0);
	}

	public static boolean redirect(String[] ind) {
		if (ind.length > 1 && ind[1].contains("url")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String isCookie(String[] basket) {
		String cookie = "cookie:";
		for(int i = 0;i<basket.length;i++) {
			if(basket[i].contains("set-cookie")) {
				cookie = cookie + " "+basket[i].substring(12, basket[i].indexOf(';')+1);
			}
		}
		cookie = cookie.substring(0, cookie.length()-1);
		return cookie;
	}

		
	public static loginDetails checkLogin(String response) {
		String[] basket = response.split("\n");
		String destination = null;
		String param1 = null;
		String param2 = null;
		for(int i = 0;i<basket.length;i++) {
			if(basket[i].contains("<form") && basket[i].contains("method=\"POST\"")) {
				int start = basket[i].indexOf("action=\"")+8;
				int end = basket[i].indexOf('"', start);
				
				destination = basket[i].substring(start, end);
			}
			if(basket[i].contains("<input") 
					&& (basket[i].contains("name=\"email\"") && basket[i].contains("type=\"email\""))) {
				param1 = "Email";
			}
			if(basket[i].contains("<input") 
					&& (basket[i].contains("name=\"username\"") && basket[i].contains("type=\"text\""))) {
				param1 = "Username";
			}
			if(basket[i].contains("<input") 
					&& (basket[i].contains("name=\"password\"") && basket[i].contains("type=\"password\""))) {
				param2 = "Password";
			}
		}
		loginDetails loginD = new loginDetails(destination, param1, param2);
		return loginD;
	}
	
	public static ArrayList<String> checkLink(String ind) {
		String[] basket = ind.split("\n");
		ArrayList<String> linkz = new ArrayList<String>();
		for (int i = 0; i < basket.length; i++) {
			if (basket[i].contains("<a ")) {
				linkz.add(getClickable(basket[i]));
			}
		}
		return linkz;
	}
	
	public static String getClickable(String tag) {
		int start = tag.indexOf("href=\"")+6;
		int end = tag.indexOf('"', start);
		String destination = tag.substring(start, end);
		
		int startButton = tag.indexOf('>', end)+1;
		int endButton = tag.indexOf("</a>");
		String link = tag.substring(startButton, endButton);
		
		String clickable = "Destination: "+destination+"; Link: "+link;
		return clickable;
	}
	
	
	
//	public static download

}
