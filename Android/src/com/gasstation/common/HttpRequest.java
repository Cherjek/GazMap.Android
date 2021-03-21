package com.gasstation.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpRequest {

	String url;
	
	public HttpRequest(String url) {
		this.url = url;
	}
	
	protected static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	public String requestGet() throws ClientProtocolException, IOException {
		
		HttpGet request = new HttpGet(url);
        request.setHeader("Accept", "application/json");            
        request.setHeader("Content-type", "application/json");
        
        return response(request);
	}
	
	public String requestPost(String json) throws ClientProtocolException, IOException, JSONException {
		
		HttpPost request = new HttpPost(url);
        request.setHeader("Accept", "application/json");            
        request.setHeader("Content-type", "application/json");
        
        JSONObject point = new JSONObject(json);
        StringEntity se = new StringEntity(point.toString(), "UTF-8");
        request.setEntity(se);        
        
		return response(request);
	}
	
	private String response(HttpUriRequest request) throws ClientProtocolException, IOException {
		
		String result = null;
		
		HttpClient httpClient = new DefaultHttpClient();        
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        if (entity != null && entity.getContentLength() != 0) {
        	
        	// stream reader object
        	InputStream is = response.getEntity().getContent();
        	result = convertStreamToString(is);
        	if (result.toString().startsWith("<!DOCTYPE")) {
				throw new IOException("Ошибка соединения");
			}
        }
		return result;
	}
}
