package org.mule.extension.dpl.api;

import org.mule.metadata.api.ClassTypeLoader;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.extension.api.declaration.type.ExtensionsTypeLoaderFactory;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

import static org.mule.extension.dpl.api.ExtensionConstants.*;
import static org.mule.runtime.api.meta.Category.COMMUNITY;

public class DynamicPropertiesExtensionLoadingDelegate implements ExtensionLoadingDelegate {


    @Override
    public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {

        ClassTypeLoader typeLoader = ExtensionsTypeLoaderFactory.getDefault().createTypeLoader();

        // Declared Configuration
        final ConfigurationDeclarer dplConfig = extensionDeclarer.withConfig(CONFIG_ELEMENT)
                .describedAs("Dynamic Property Placeholder Configuration that allows to provide a type of source, and its properties.");
        ParameterGroupDeclarer parameterGroupDeclarer = dplConfig.onParameterGroup(DYNAMIC_PROPERTIES_PARAMETER_GROUP);
        parameterGroupDeclarer.withRequiredParameter("username")
                .describedAs("Enter Database Username")
                .ofType(typeLoader.load(String.class));
        parameterGroupDeclarer.withRequiredParameter("password")
                .describedAs("Enter Database password")
                .ofType(typeLoader.load(String.class));
        parameterGroupDeclarer.withRequiredParameter("jdbcUrl")
                .describedAs("Enter Database JDBC Url")
                .ofType(typeLoader.load(String.class));
        parameterGroupDeclarer.withRequiredParameter("driverClassName")
                .describedAs("Enter Database Driver ClassName")
                .ofType(typeLoader.load(String.class));
        parameterGroupDeclarer.withRequiredParameter("tableName")
                .describedAs("Name of the table where the properties are present")
                .ofType(typeLoader.load(String.class));

        extensionDeclarer
                .named(EXTENSION_NAME)
                .describedAs(EXTENSION_DESCRIPTION)
                .withCategory(COMMUNITY)
                .onVersion("1.0.0")
                .fromVendor(VENDOR);


    }
}
