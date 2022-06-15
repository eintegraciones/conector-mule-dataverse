package org.mule.modules.dataverse.internal.extension;

import static org.mule.runtime.api.meta.Category.COMMUNITY;

import org.mule.modules.dataverse.internal.connection.provider.DataverseConnectionProvider;
import org.mule.modules.dataverse.internal.error.DataverseErrorTypeDefinition;
import org.mule.modules.dataverse.internal.operation.DataverseOperations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;

/**
 * This is the main class of an extension, is the entry point from which
 * configurations, connection providers, operations and sources are going to be
 * declared.
 */
@Xml(prefix = "dataverse")
@Extension(name = "Dataverse Token Mulesoft 4", vendor = "nttdata", category = COMMUNITY)
@ErrorTypes(DataverseErrorTypeDefinition.class)
@ConnectionProviders(DataverseConnectionProvider.class)
@Operations(DataverseOperations.class)
@RequiresEnterpriseLicense(allowEvaluationLicense = true)
public class DataverseExtension {

}

