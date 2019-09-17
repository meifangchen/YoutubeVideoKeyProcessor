package com.moodeo.moodeo.api;

import java.awt.List;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class FetchDataAuto 
{
	static ArrayList<String> result = new ArrayList<String>();
	static Set<String> resultSet = new HashSet<String>();
	
    public static void main( String[] args ) throws IOException
    {
    	String originalURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=funny&type=video&key=AIzaSyBVttFGSKqbKtClSCkaUuyZqPYrQY1hQzE";
    	String recursiveToken = recursivelyFetchData(originalURL);
    	while(recursiveToken.length() > 0) {
    		String nextToken = recursiveToken;
    		String url = originalURL + "&pageToken=" + nextToken;
    		recursiveToken = recursivelyFetchData(url);
    	}
    	for(String s: result) {
    		resultSet.add(s);
    	}
    	
    	System.out.println("Result size:"+ result.size() + " " + result);
    	System.out.println("Result size without duplicate:"+ resultSet.size() + " " + resultSet);
    }
    
    private static String recursivelyFetchData(String input) throws IOException {
    	String nextPageTokenHolder = "";
    	StringBuilder inline = new StringBuilder();
    	URL url = new URL(input);
    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    	conn.setRequestMethod("GET");
    	conn.connect();
    	int responsecode = conn.getResponseCode();
    	
    	if(responsecode != 200)
    		throw new RuntimeException("HttpResponseCode:" + responsecode);
    	else {
    		Scanner sc = new Scanner(url.openStream());
    		while(sc.hasNext()) {
    			inline.append(sc.nextLine());
    		}
    		
    		System.out.println(inline.toString());
    		sc.close();
    		
    		
    	String fetchedJsonString = inline.toString();
    	
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(fetchedJsonString);
        if(actualObj.get("nextPageToken") != null) {
        	nextPageTokenHolder = actualObj.get("nextPageToken").textValue();
        } else {
        	System.out.println("No nextPageToken");
        }
        if(actualObj.get("items").size() >= 50) {
            for(JsonNode youtubeKey: actualObj.get("items")) {
            	String key = youtubeKey.get("id").get("videoId").textValue();
            	String keyString = "'" + key + "'";
            	result.add(keyString);
            }
        } else {
        	return "";
        }

        
        System.out.println("Result size:"+ result.size() + " " + result);
        return nextPageTokenHolder;
    		
    	}
    }
}

