package org.mule.extension.dpl.api;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;

import javax.sql.DataSource;

import static org.mule.extension.dpl.api.ExtensionConstants.CONFIG_ELEMENT;
import static org.mule.extension.dpl.api.ExtensionConstants.EXTENSION_NAME;

public class DynamicPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

    private static final String EXTENSION_NAMESPACE = EXTENSION_NAME.toLowerCase().replace(" ", "-");
    private static final ComponentIdentifier DYNAMIC_PROPERTIES_PROVIDER = ComponentIdentifier
            .builder()
            .namespace(EXTENSION_NAMESPACE)
            .name(CONFIG_ELEMENT)
            .build();

    @Override
    public ComponentIdentifier getSupportedComponentIdentifier() {
        return DYNAMIC_PROPERTIES_PROVIDER;
    }

    @Override
    public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters, ResourceProvider externalResourceProvider) {
        String tableName = parameters.getStringParameter("tableName");
        return new DynamicPropertiesProvider(tableName, getDataSource(parameters));
    }

    private DataSource getDataSource(ConfigurationParameters parameters) {
        String username = parameters.getStringParameter("username");
        String password = parameters.getStringParameter("password");
        String jdbcUrl = parameters.getStringParameter("jdbcUrl");
        String driverClassName = parameters.getStringParameter("driverClassName");
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setUrl(jdbcUrl);
        basicDataSource.setDriverClassName(driverClassName);
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setCacheState(true);
        basicDataSource.setPoolPreparedStatements(true);
        basicDataSource.setMaxOpenPreparedStatements(100);
        return basicDataSource;
    }


}
