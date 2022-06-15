package org.mule.modules.dataverse.internal.params;

import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;


import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;


/**
 * Daniel Sánchez Fraile
 * @author dsanchfr
 *
 */
public class DataverseHeaders {

	public DataverseHeaders() {
	}

	public DataverseHeaders(String keyHeader, String value) {
		this.keyHeader = keyHeader;
		this.value = value;
	}

	@Parameter
	private String keyHeader;

	@Parameter
	@Expression(SUPPORTED)
	private String value;

	

	public String getKeyHeader() {
		return keyHeader;
	}

	public void setKeyHeader(String keyHeader) {
		this.keyHeader = keyHeader;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	
}
