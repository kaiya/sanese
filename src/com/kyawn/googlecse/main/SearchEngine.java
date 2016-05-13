package com.kyawn.googlecse.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class SearchEngine {

	public JSONObject search(String Keyword) throws Exception {
		String url = "https://www.googleapis.com/customsearch/v1?cx=007867314732921671936%3Actx7cmwb3z8&key=AIzaSyB6WhsCwHF_g8y3NsYGSKEo5f8d7NCM5Tg&alt=json&num=10&q="
				+ URLEncoder.encode(Keyword, "UTF-8");

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		request.setProtocolVersion(HttpVersion.HTTP_1_1);
		request.addHeader("accept", "application/json");
		HttpResponse response = client.execute(request);
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent()), "utf-8"));
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = br.readLine()) != null) {
			builder.append(line);
		}
		JSONObject resultObject = new JSONObject(builder.toString());

		// int timeout = 0;
		// int managerTimeout = 200;
		// HttpClientConnectionManager clientConnManager = null;
		//
		// RequestConfig defaultRequestConfig =
		// RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout)
		// .setConnectionRequestTimeout(managerTimeout).build();
		//
		// CloseableHttpClient httpClient =
		// HttpClients.custom().setConnectionManager(clientConnManager)
		// .setDefaultRequestConfig(defaultRequestConfig).build();
		//
		// RequestConfig requestConfig =
		// RequestConfig.copy(defaultRequestConfig).setConnectTimeout(timeout *
		// 2)
		// .setSocketTimeout(timeout *
		// 2).setConnectionRequestTimeout(managerTimeout * 2).build();
		//
		// HttpGet get = new HttpGet(url);
		// get.setConfig(requestConfig);
		// get.setProtocolVersion(HttpVersion.HTTP_1_1);
		// HttpResponse resp = httpClient.execute(get);
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader((resp.getEntity().getContent()), "utf-8"));
		// String line;
		// StringBuilder builder = new StringBuilder();
		// while ((line = br.readLine()) != null) {
		// builder.append(line);
		// }
		// JSONObject resultObject = new JSONObject(builder.toString());

		// CloseableHttpClient httpClient = HttpClients.createDefault();
		// HttpGet httpGet = new HttpGet(url);
		// RequestConfig requestConfig =
		// RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
		// httpGet.setConfig(requestConfig);
		// HttpResponse response = httpClient.execute(httpGet);
		//
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader((response.getEntity().getContent()), "utf-8"));
		// String line;
		// StringBuilder builder = new StringBuilder();
		// while ((line = br.readLine()) != null) {
		// builder.append(line);
		// }
		// JSONObject resultObject = new JSONObject(builder.toString());

		return resultObject;
	}


}
