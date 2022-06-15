package org.mule.modules.dataverse.internal.connection;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.mule.modules.dataverse.internal.error.DataverseErrorTypeDefinition;
import org.mule.modules.dataverse.internal.exception.DataverseException;
import org.mule.modules.dataverse.internal.operation.graph.rest.DataverseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This class represents an extension connection just as example (there is no real connection).
 * 
 * Daniel Sánchez Fraile
 * @author dsanchfr
 *
 */
public final class DataverseSyncConnection {

	private static final Logger log = LoggerFactory.getLogger(DataverseSyncConnection.class);

	private String token;
	private String urlToken;
	private String clientId;
	private String clientSecret;
	private String scope;
	private String resource;
	private Double version;
	private Integer timeout = 0;
	private DataverseUtil dataverseUtil = new DataverseUtil();
	
	

	public DataverseSyncConnection(String urlToken, String clientId, String clientSecret, String scope, String resource, Double version, Integer timeout) {
		this.urlToken 		= urlToken;
		this.clientId 		= clientId;
		this.clientSecret 	= clientSecret;
		this.scope 			= scope;
		this.resource 		= resource;
		this.version 		= version;
		this.timeout		= timeout;
	}

	/**
	 * Metodo al que hace la conexion con el googleanalytic mediante el fichero json.
	 * @throws DataverseException
	 */
	public void connect() throws DataverseException {
		tokenClientCredentials(); 
		validateTokenResource();
	}

	private void tokenClientCredentials() {
		HttpResponse httpResponse = null;
		try {
			
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(this.urlToken);
	
			
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(new BasicNameValuePair("grant_type", "client_credentials"));
			nameValuePairList.add(new BasicNameValuePair("client_id", this.clientId));
			nameValuePairList.add(new BasicNameValuePair("client_secret", this.clientSecret));
			nameValuePairList.add(new BasicNameValuePair("scope", this.scope));
			nameValuePairList.add(new BasicNameValuePair("resource", this.resource));
	        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
			
			httpPost.setEntity(formEntity);
			
			
			httpResponse = httpclient.execute(httpPost);
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			String error = "Error [DataverseConnection::connect()-tokenClientCredentials()::executeRequest]";
			log.error(error, e);
			if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.TIMEOUT);
			} else {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		} 
		
		//Leo la respuesta del post token
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = dataverseUtil.getBodyResponse(httpResponse.getEntity().getContent());
			if (statusCode == HttpURLConnection.HTTP_OK) {
				JSONObject jResponse = new JSONObject(response);
				if (jResponse.has("access_token")) {
					this.token = jResponse.getString("access_token");
				}
			} else {
				throw new DataverseException("Error [DataverseConnection::connect()-tokenClientCredentials()::response code:["+ statusCode + "], Error: " + response,
						DataverseErrorTypeDefinition.CONNECTIVITY);
			}
		} catch (Exception e) {
			String error = "Error [DataverseConnection::connect()-tokenClientCredentials()::read response formnat json]";
			log.error(error, e);
			throw new DataverseException(error + " ex:" + e.getMessage(),
					DataverseErrorTypeDefinition.CONNECTIVITY);
		}
	}

	
	private void validateTokenResource() {
		HttpResponse httpResponse = null;
		try {
			
			String urlDataverse = this.resource + "/api/data/v" + this.version + "/";
			
			HttpClient httpclient = HttpClientBuilder.create().build();

			HttpGet httpGet = new HttpGet(urlDataverse);
			
			if (this.timeout != null && this.timeout.compareTo(0) == 1) {
				RequestConfig requestConfig = RequestConfig.custom()
				    .setConnectionRequestTimeout(this.timeout)
				    .setConnectTimeout(this.timeout)
				    .setSocketTimeout(this.timeout)
				    .build();
				httpGet.setConfig(requestConfig);
			}
			
	
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("OData-MaxVersion", "4.0");
			httpGet.setHeader("OData-Version", "4.0");
			httpGet.setHeader("Authorization", "Bearer " + getToken());
			
			httpResponse = httpclient.execute(httpGet);
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			String error = "Error [DataverseConnection::connect()-validateTokenResource()::executeRequest]";
			log.error(error, e);
			if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.TIMEOUT);
			} else {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		} 
		
		//Leo la respuesta del post token
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = dataverseUtil.getBodyResponse(httpResponse.getEntity().getContent());
			if (statusCode == HttpURLConnection.HTTP_OK) {
				log.info("Correct connection to dataverse: " + this.resource);
			} else {
				throw new DataverseException("Error [DataverseConnection::connect()-validateTokenResource()::response code:["+ statusCode + "], Error: " + response,
						DataverseErrorTypeDefinition.CONNECTIVITY);
			}
		} catch (Exception e) {
			String error = "Error [DataverseConnection::connect()-validateTokenResource()::read response formnat json]";
			log.error(error, e);
			throw new DataverseException(error + " ex:" + e.getMessage(),
					DataverseErrorTypeDefinition.CONNECTIVITY);
		}
	}

	public void disconnect() {
		// do something to invalidate this connection!
	}

	public String getToken() {
		return token;
	}

	public String getResource() {
		return resource;
	}

	public Double getVersion() {
		return version;
	}

	public Integer getTimeout() {
		return timeout;
	}
	
	

	
}
