package org.mule.modules.dataverse.internal.connection.provider;

import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;
import static org.mule.runtime.extension.api.annotation.param.display.Placement.ADVANCED_TAB;

import org.mule.modules.dataverse.internal.connection.DataverseSyncConnection;
import org.mule.modules.dataverse.internal.error.DataverseErrorTypeDefinition;
import org.mule.modules.dataverse.internal.exception.DataverseException;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Daniel Sánchez Fraile
 * @author dsanchfr
 *
 */
public class DataverseConnectionProvider implements CachedConnectionProvider<DataverseSyncConnection>  {

	private static final Logger log = LoggerFactory.getLogger(DataverseConnectionProvider.class);

	@Parameter
	@Expression(SUPPORTED)
	@DisplayName("Url Token")
	@Summary("Url to get dataverse token")
	@Example("https://login.microsoftonline.com/{{terantId}}/oauth2/v2.0/token")
	private String urlToken;
	
	@Parameter
	@Expression(SUPPORTED)
	@DisplayName("Client Id")
	@Summary("Client Id of the app from which the token is obtained")
	private String clientId;

	@Parameter
	@Expression(SUPPORTED)
	@DisplayName("Client Secret")
	@Summary("Client Secret of the app from which the token is obtained")
	private String clientSecret;
	
	
	@Parameter
	@Expression(SUPPORTED)
	@DisplayName("Scope")
	@Example("https://graph.microsoft.com/.default openid")
	private String scope;
	
	@Parameter
	@Expression(SUPPORTED)
	@DisplayName("Resource")
	@Example("https://{host}}.crm4.dynamics.com")
	private String resource;
	
	@Parameter
	@Expression(SUPPORTED) 
	@Summary("version of api the dataverse") 
	@Example("9.2") 
	private Double version;
	
	
	@Parameter
	@Optional
	@DisplayName("Timeout (ms)")
	@Expression(SUPPORTED) 
	@Summary("timeout for conection of api the dataverse") 
	@Placement(tab = ADVANCED_TAB)
	private Integer timeout;
	
	
	@Override
	public DataverseSyncConnection connect() throws DataverseException {
		DataverseSyncConnection dataverseConnection = null;
		try {
			dataverseConnection = new DataverseSyncConnection(urlToken, clientId, clientSecret, scope, resource, version, timeout);
			dataverseConnection.connect();
		} catch (Exception e) {
			if (e instanceof DataverseException) {
				throw (DataverseException) e;
			} else {
				String error = "Error [DataverseConnectionProvider::connect()::DataverseConnectionProvider.connect]";
				log.error(error, e);
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
			}
		}

		return dataverseConnection;
	}

	@Override
	public void disconnect(DataverseSyncConnection dataverseConnection) {
		try {
			dataverseConnection.disconnect();
		} catch (Exception e) {
			if (e instanceof DataverseException) {
				throw (DataverseException) e;
			} else {
				String error = "Error [DataverseConnectionProvider::disconnect()::DataverseConnectionProvider.disconnectt()]";
				log.error(error, e);
				throw new DataverseException(error + " ex:" + e.getMessage(),
						DataverseErrorTypeDefinition.CONNECTIVITY);
			}
		}
	}

	@Override
	public ConnectionValidationResult validate(DataverseSyncConnection connection) {
		return ConnectionValidationResult.success();
	}
}
