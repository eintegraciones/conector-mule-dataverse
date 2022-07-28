package org.mule.modules.dataverse.internal.operation.graph.rest;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.mule.modules.dataverse.internal.connection.DataverseSyncConnection;
import org.mule.modules.dataverse.internal.error.DataverseErrorTypeDefinition;
import org.mule.modules.dataverse.internal.exception.DataverseException;
import org.mule.modules.dataverse.internal.params.DataverseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Clase que tiene los metodo para hacer los select e insert
 * 
 * - Select con la API ODATA: querySelectOData
 * - Insert con la API ODATA: queryInsertOData
 * @author dsanchfr
 *
 */
public class DataverseQuery {
	private static final Logger log = LoggerFactory.getLogger(DataverseQuery.class);

	private String token;
	private String resource;
	private Double version;
	private Integer timeout;
	private DataverseUtil dataverseUtil = new DataverseUtil();
	
	public DataverseQuery(DataverseSyncConnection connection) {
		this.token = connection.getToken();
		this.resource = connection.getResource();
		this.version = connection.getVersion();
		this.timeout = connection.getTimeout();
	}

	public String querySelectOData(String query, List<DataverseHeaders> headers) throws DataverseException {
		String responseJson = "{data:[]}"; 
		HttpResponse httpResponse = null;
		try { 
			String urlDataverse = dataverseUtil.prepareUrl(this.resource, this.version.toString(), query);
			
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(urlDataverse);
			if (this.timeout != null && this.timeout.compareTo(0) != 0) {
				RequestConfig requestConfig = RequestConfig.custom()
				    .setConnectionRequestTimeout(timeout)
				    .setConnectTimeout(timeout)
				    .setSocketTimeout(timeout)
				    .build();
				httpGet.setConfig(requestConfig);
			}

			dataverseUtil.addHeaders(headers, httpGet);
			httpGet.setHeader("Authorization", "Bearer " + this.token);

			httpResponse = httpclient.execute(httpGet);
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			String error = "Error [DataverseQuery::querySelectOData()::executeRequest]";
			log.error(error, e);
			if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.TIMEOUT);
			} else {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		} 
		
		//Leo la respuesta del post de query querySelectOData
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				String response = dataverseUtil.getBodyResponse(httpResponse.getEntity().getContent());
				/*JSONObject jResponse = new JSONObject(response);
				responseJson = jResponse.toString();*/
				responseJson = response;
			} else if (statusCode == HttpURLConnection.HTTP_NO_CONTENT) { 
				responseJson = "{\"data\":\"204 - No content\"}";
			} else {
				String response = dataverseUtil.getBodyResponse(httpResponse.getEntity().getContent());
				String error = "Error [DataverseQuery::querySelectOData()::response code:["+ statusCode + "]";
				log.error(error, response);
				dataverseUtil.thorwExceptionCode(statusCode, error, response);
			}
		} catch (Exception e) {
			if (e instanceof DataverseException) {
				throw (DataverseException) e;
			} else {
				String error = "Error [DataverseQuery::querySelectOData()::read response formnat json]";
				log.error(error, e);
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		}
		
		return responseJson;
	}

	public String queryInsertOData(String query, String body, List<DataverseHeaders> headers) throws DataverseException {
		String responseJson = "{data:[]}"; 
		HttpResponse httpResponse = null;
		JSONObject jBody = null;
		try {
			jBody = new JSONObject(body);
		}  catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			String error = "Error [DataverseQuery::queryInsertOData()::JSONObject-validation::body ]";
			log.error(error, e);
			throw new DataverseException(error + " ex:" + e.getMessage(),
					DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
		} 
		
		try { 
			String urlDataverse = dataverseUtil.prepareUrl(this.resource, this.version.toString(), query);
			
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(urlDataverse);
			if (this.timeout != null && this.timeout.compareTo(0) != 0) {
				RequestConfig requestConfig = RequestConfig.custom()
				    .setConnectionRequestTimeout(timeout)
				    .setConnectTimeout(timeout)
				    .setSocketTimeout(timeout)
				    .build();
				httpPost.setConfig(requestConfig);
			}

			dataverseUtil.addHeaders(headers, httpPost);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization", "Bearer " + this.token);
			
			httpPost.setEntity(new StringEntity(jBody.toString()));
			
			httpResponse = httpclient.execute(httpPost);
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			String error = "Error [DataverseQuery::queryInsertOData()::executeRequest]";
			log.error(error, e);
			if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.TIMEOUT);
			} else {
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		} 
		
		
		//Leo la respuesta del post de query queryInsertOData
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();	
			if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
				String response = dataverseUtil.getBodyResponse(httpResponse.getEntity().getContent());
				JSONObject jResponse = new JSONObject(response);
				responseJson = jResponse.toString();
			} else if (statusCode == HttpURLConnection.HTTP_NO_CONTENT) { 
				responseJson = "{\"data\":\"create rows\"}";
			} else {
				String response = dataverseUtil.getBodyResponse(httpResponse.getEntity().getContent());
				String error = "Error [DataverseQuery::queryInsertOData()::response code:["+ statusCode + "]";
				log.error(error, response);
				dataverseUtil.thorwExceptionCode(statusCode, error, response);
			}
		} catch (Exception e) {
			if (e instanceof DataverseException) {
				throw (DataverseException) e;
			} else {
				String error = "Error [DataverseQuery::queryInsertOData()::read response formnat json]";
				log.error(error, e);
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		}
		return responseJson;
	}

	
}
