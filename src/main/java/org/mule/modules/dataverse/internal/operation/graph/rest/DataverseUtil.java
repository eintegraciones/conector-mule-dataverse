package org.mule.modules.dataverse.internal.operation.graph.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.mule.modules.dataverse.internal.error.DataverseErrorTypeDefinition;
import org.mule.modules.dataverse.internal.exception.DataverseException;
import org.mule.modules.dataverse.internal.params.DataverseHeaders;

public class DataverseUtil {

	public String getBodyResponse(InputStream isResponse) throws IOException {
		StringBuilder sbResponse = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(isResponse))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sbResponse.append(line);
			}
		}
		return sbResponse.toString();
	}
	
	public String prepareUrl(String resource, String version, String query) {
		String urlDataverse = "";
		if (query != null && query.startsWith("/")) {
			query = query.substring(1);
		}
		
		if (resource != null && resource.endsWith("/")) {
			resource = resource.substring(0, resource.length() - 1);
		} 
		
		urlDataverse = resource + "/api/data/v" + version + "/" + query;
		
		return urlDataverse;
	}
	
	public void addHeaders(List<DataverseHeaders> headers, HttpRequestBase httpRequest) {
		if (headers != null && headers.size() > 0) {
			for (int pos = 0; pos < headers.size(); pos++) {
				DataverseHeaders dataverseHeaders = headers.get(pos);
				if ("OData-MaxVersion".equalsIgnoreCase(dataverseHeaders.getKeyHeader())) {
					httpRequest.setHeader("OData-MaxVersion", dataverseHeaders.getValue());
				} else if ("OData-Version".equalsIgnoreCase(dataverseHeaders.getKeyHeader())) {
					httpRequest.setHeader("OData-Version", dataverseHeaders.getValue());
				} else if (!"Content-Type".equalsIgnoreCase(dataverseHeaders.getKeyHeader())) { 
					httpRequest.setHeader(dataverseHeaders.getKeyHeader(), dataverseHeaders.getValue());
				}
			}
			
			Header[] hODataMaxVersion = httpRequest.getHeaders("OData-MaxVersion");
			if (hODataMaxVersion == null || hODataMaxVersion.length == 0) {
				httpRequest.setHeader("OData-MaxVersion", "4.0");
			}
			Header[] hODataVersion = httpRequest.getHeaders("OData-Version");
			if (hODataVersion == null || hODataVersion.length == 0) {
				httpRequest.setHeader("OData-Version", "4.0");
			}
		}
	}
	
	
	public void thorwExceptionCode(int statusCode, String error, String response) {
		switch (statusCode) {
			case 400:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.BAD_REQUEST);
			case 401:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.UNAUTHORIZED);
			case 403:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.FORBIDDEN);
			case 404:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.NOT_FOUND);
			case 405:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.METHOD_NOT_ALLOWED);
			case 408:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.TIMEOUT);
			//case 415:
			//	throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.MEDIa);
			//	break;
			case 429:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.TOO_MANY_REQUESTS);
			case 500:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			//case 501:
			//	throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.NOT_IMPE);
			//	break;
			case 503:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.SERVICE_UNAVAILABLE);
			default:
				throw new DataverseException(error + " ex:" + response, DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
		}
	}
	
}
