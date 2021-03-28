package org.mule.extension.dpl.api;

import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class DynamicPropertiesProvider implements ConfigurationPropertiesProvider, Initialisable, Disposable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicPropertiesProvider.class);

    private static final String DYNAMIC_PROPERTIES_PREFIX = "dynamic::";
    private final static Pattern DYNAMIC_PROPERTIES_PATTERN = Pattern.compile("\\$\\{" + DYNAMIC_PROPERTIES_PREFIX + "[^}]*}");
    private final String tableName;
    private final String columnName;

    private final DataSource dataSource;

    private Connection connection;

    public DynamicPropertiesProvider(String tableName, String columnName, DataSource dataSource) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataSource = dataSource;
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        if (configurationAttributeKey.startsWith(DYNAMIC_PROPERTIES_PREFIX)) {
            String effectiveKey = configurationAttributeKey.substring(DYNAMIC_PROPERTIES_PREFIX.length());
            final String effectiveValue = getValue(getTableName(), effectiveKey, getColumnName());
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
    public String getValue(String tableName, String key, String columnName) {
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE " + columnName + "=?";
        String value = "";
        if (getConnection() != null) {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, key);
                ResultSet result = preparedStatement.executeQuery();
                while (result.next()) {
                    String valueFromKey = getValueFromKey(result, columnName);
                    if (valueFromKey != null) {
                        value = valueFromKey;
                        break;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
        return value;
    }

    private String getValueFromKey(ResultSet resultSet, String columnName) throws SQLException {
        Map<String, String> resultMap = new HashMap<>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            String columnLabel = resultSet.getMetaData().getColumnName(i);
            resultMap.put(columnLabel, resultSet.getString(columnLabel));
        }
        Gson gson = new Gson();
        String resultStr = gson.toJson(resultMap);
        AppProperty property = gson.fromJson(resultStr, AppProperty.class);
        return property.getValue();
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
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
