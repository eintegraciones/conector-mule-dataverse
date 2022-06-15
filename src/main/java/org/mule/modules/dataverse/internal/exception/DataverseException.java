package org.mule.modules.dataverse.internal.exception;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * Daniel Sánchez Fraile
 * @author dsanchfr
 *
 */
public class DataverseException extends ModuleException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 8832634801806526602L;

	public <T extends Enum<T>> DataverseException(String message, ErrorTypeDefinition<T> errorTypeDefinition) {
        super(message, errorTypeDefinition);
    }

	

	
}
