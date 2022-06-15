package org.mule.modules.dataverse.internal.error;

import java.util.HashSet;
import java.util.Set;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public class DataverseErrorProvider implements ErrorTypeProvider {
	
	@SuppressWarnings("rawtypes")
	@Override
    public Set<ErrorTypeDefinition> getErrorTypes() {
		Set<ErrorTypeDefinition> errors = new HashSet<>();
		errors.add(DataverseErrorTypeDefinition.PARSING);
		errors.add(DataverseErrorTypeDefinition.TIMEOUT);
		errors.add(DataverseErrorTypeDefinition.SECURITY);
		errors.add(DataverseErrorTypeDefinition.CLIENT_SECURITY);
		errors.add(DataverseErrorTypeDefinition.SERVER_SECURITY);
		errors.add(DataverseErrorTypeDefinition.TRANSFORMATION);
		errors.add(DataverseErrorTypeDefinition.CONNECTIVITY);
		errors.add(DataverseErrorTypeDefinition.BAD_REQUEST);
		errors.add(DataverseErrorTypeDefinition.BASIC_AUTHENTICATION);
		errors.add(DataverseErrorTypeDefinition.UNAUTHORIZED);
		errors.add(DataverseErrorTypeDefinition.FORBIDDEN);
		errors.add(DataverseErrorTypeDefinition.NOT_FOUND);
		errors.add(DataverseErrorTypeDefinition.METHOD_NOT_ALLOWED);
		errors.add(DataverseErrorTypeDefinition.NOT_ACCEPTABLE);
		errors.add(DataverseErrorTypeDefinition.TOO_MANY_REQUESTS);
		errors.add(DataverseErrorTypeDefinition.INTERNAL_SERVER_ERROR);
		errors.add(DataverseErrorTypeDefinition.SERVICE_UNAVAILABLE);
		errors.add(DataverseErrorTypeDefinition.BAD_GATEWAY);
		errors.add(DataverseErrorTypeDefinition.GATEWAY_TIMEOUT);
		
		return errors;
	}
}


