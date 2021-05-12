package org.mule.extension.dpl.api;

import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

public class DynamicPropertiesProvider implements ConfigurationPropertiesProvider, Initialisable, Disposable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicPropertiesProvider.class);

    private static final String DYNAMIC_PROPERTIES_PREFIX = "dynamic::";
    private final static Pattern DYNAMIC_PROPERTIES_PATTERN = Pattern.compile("\\$\\{" + DYNAMIC_PROPERTIES_PREFIX + "[^}]*}");
    private final String tableName;
    private final DataSource dataSource;
    private final String columnName;

    private Connection connection;

    public DynamicPropertiesProvider(String tableName,String columnName, DataSource dataSource) {
        this.tableName = tableName;
        this.dataSource = dataSource;
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        if (configurationAttributeKey.startsWith(DYNAMIC_PROPERTIES_PREFIX)) {
            String effectiveKey = configurationAttributeKey.substring(DYNAMIC_PROPERTIES_PREFIX.length());
            final String effectiveValue = getValue(getTableName(),getColumnName(), effectiveKey);
            if (effectiveValue != null) {
                return Optional.of(new ConfigurationProperty() {

                    @Override
                    public Object getSource() {
                        return "Database";
                    }

                    @Override
                    public Object getRawValue() {
                        return effectiveValue;
                    }

                    @Override
                    public String getKey() {
                        return effectiveKey;
                    }
                });
            }
        }
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "Dynamic Properties Provider";
    }

    @DisplayName("Get Property Values")
    public String getValue(String tableName,String columnName, String key) {
        String sqlQuery = "SELECT " + columnName + " as value FROM " + tableName + " WHERE  key=?";
        String value = "";
        if (getConnection() != null) {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, key);
                ResultSet result = preparedStatement.executeQuery();
                while (result.next()) {
                    value = result.getString("value");
                }
            } catch (SQLException ex) {
                return null;
            }
        } else {
            return null;
        }
        return value;
    }

    public String getTableName() {
        return tableName;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void initialise() throws InitialisationException {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.info(" connection failed " + e.getMessage());
            e.printStackTrace();
            this.connection = null;
            throw new InitialisationException(e, this);
        }
    }


    @Override
    public void dispose() {
        try {
            this.connection.close();
        } catch (SQLException exception) {
            LOGGER.error("Connection is either closed or null");
        }
    }
}

