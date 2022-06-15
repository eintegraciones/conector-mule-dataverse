package org.mule.modules.dataverse.internal.operation;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;

import org.json.JSONObject;
import org.mule.modules.dataverse.internal.connection.DataverseSyncConnection;
import org.mule.modules.dataverse.internal.error.DataverseErrorProvider;
import org.mule.modules.dataverse.internal.exception.DataverseException;
import org.mule.modules.dataverse.internal.operation.graph.rest.DataverseQuery;
import org.mule.modules.dataverse.internal.params.DataverseHeaders;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Ignore;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;



/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 * 
 * Daniel Sánchez Fraile
 * @author dsanchfr
 *
 */
public class DataverseOperations {

	@Inject
	ExpressionManager expressionManager;

	@Ignore
	public void setExpressionManager(ExpressionManager expressionManager) {
		this.expressionManager = expressionManager;
	}

	@MediaType(value = MediaType. APPLICATION_JSON, strict = false)
	@DisplayName("Select ODATA")
    @Summary("Select to retrieve information from the dataverse with the ODATA api")
	@Throws(DataverseErrorProvider.class)
	public String selectOdata(@Connection DataverseSyncConnection connection,
			@Expression(SUPPORTED) @Summary("query for select in dataverse") String query,
			@Expression(ExpressionSupport.NOT_SUPPORTED) @Summary("The dimensions filters") @Optional @ParameterDsl(allowReferences = false) List<DataverseHeaders> headers)
					throws DataverseException {

		connection.connect();
		DataverseQuery dataverseQuery = new DataverseQuery(connection);
		String response = dataverseQuery.querySelectOData(query, headers);
		return response;
	}
	
	@SuppressWarnings("resource")
	@MediaType(value = MediaType. APPLICATION_JSON, strict = false)
	@DisplayName("Insert ODATA")
    @Summary("To insert records in the table you specify with Api ODATA")
	@Throws(DataverseErrorProvider.class)
	public String insertOdata(@Connection DataverseSyncConnection connection,
			@Expression(SUPPORTED) @Summary("query for select in dataverse") String query,
			@Expression(SUPPORTED) @DisplayName("Body (json)") @Summary("Body in json format") @Content Object body,
			@Expression(ExpressionSupport.NOT_SUPPORTED) @Summary("The dimensions filters") @Optional @ParameterDsl(allowReferences = false) List<DataverseHeaders> headers)
					throws DataverseException {
		
		connection.connect();
		String sBody = "";
		if (body instanceof Map) {
			JSONObject jBody = new JSONObject((Map<?, ?>) body);
			sBody = jBody.toString();
		} else if (body instanceof InputStream) {
			InputStream inputStream = (InputStream) body;
			sBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
				        .lines()
				        .collect(Collectors.joining("\n"));
			
		} else {
			sBody = (String) body;
		}

		DataverseQuery dataverseQuery = new DataverseQuery(connection);
		String response = dataverseQuery.queryInsertOData(query, sBody, headers);
		return response;
	}
	
	
}
