package com.example.time;

//Imports to to extract the HTML code and extract the result
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Imports to creata a GET API
import com.example.time.model.Details;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;


public class TimeAppApplication {
	//Creating GET API to send the results
	//Created without using any frameworks like Spring or Spark
	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8880), 0);
        server.createContext("/api", (exchange -> {

            if ("GET".equals(exchange.getRequestMethod())) {
				List<Details> details = Response();
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");              
                OutputStream output = exchange.getResponseBody();
				ObjectMapper objMap = new ObjectMapper();
				String str = objMap.writeValueAsString(details);
				exchange.sendResponseHeaders(200, str.getBytes(StandardCharsets.UTF_8).length);
                output.write(str.getBytes(StandardCharsets.UTF_8));
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));


        server.setExecutor(null); // creates a default executor
        server.start();
	}

	public static String formatString(String str) {
		String newStr="";
		for(int i=0; i<str.length();i++) {
			if(Character.isUpperCase(str.charAt(i))) {
				newStr+=" ";
				newStr+=str.charAt(i);
			}else {
				newStr+=str.charAt(i);
			}
		}
		return newStr;
	}
	
	//Getting the HTML code. 
	//Parsing the HTML code without using any external or internal libraries by just doing simple String manipulation
	public static List<Details> Response() throws IOException {
		List<Details> details = new ArrayList<Details>();

		URL url = new URL("https://time.com/");

		Scanner sc = new Scanner(url.openStream());

		StringBuffer sb = new StringBuffer();
		while (sc.hasNext()) {
			sb.append(sc.next());
		}

		String result = sb.toString();
		result = result.substring(result.indexOf("latest-stories__item"), result
				.indexOf("<sectionclass=\"homepage-section-v2mag-subs\"data-module_name=\"MagazineSubscription\">"));

		String[] str;
		str = result.split("<liclass=\"latest-stories__item\">");
		for (int i = 0; i < 6; i++) {
			Details detail = new Details();
			String link;
			String title;
			link = "https://time.com" + str[i].split("<h3class=\"latest-stories__item-headline\">")[0]
					.substring(str[i].split("<h3class=\"latest-stories__item-headline\">")[0].indexOf("<ahref="))
					.replace("<ahref=\"", "").replace("\">", "");
			title = str[i].split("<h3class=\"latest-stories__item-headline\">")[1].substring(0,
					str[i].split("<h3class=\"latest-stories__item-headline\">")[1].indexOf("</"));
			title = formatString(title);
			detail.setlink(link);
			detail.settitle(title);
			details.add(detail);
		}

		return details;
	}

}
