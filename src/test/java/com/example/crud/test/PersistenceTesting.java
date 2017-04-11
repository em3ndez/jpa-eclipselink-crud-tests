package com.example.crud.test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Persistence testing helper which creates an EMF providing testing overrides
 * to use direct JDBC instead of a data source
 */
public class PersistenceTesting {

    public static EntityManagerFactory createEMF(boolean replaceTables) {
        Map<String, Object> props = new HashMap<String, Object>();

        // Ensure the persistence.xml provided data source are ignored during test runs
        props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
        props.put(PersistenceUnitProperties.JTA_DATASOURCE, "");
        props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");

        // Configure the use of embedded derby for the tests allowing system properties of the same name to override
        setProperty(props, PersistenceUnitProperties.JDBC_DRIVER, "org.postgresql.Driver");
        setProperty(props, PersistenceUnitProperties.JDBC_URL, "jdbc:postgresql://127.0.0.1:26257/crud");
        setProperty(props, PersistenceUnitProperties.JDBC_USER, "root");
        setProperty(props, PersistenceUnitProperties.JDBC_PASSWORD, "");

        // Ensure weaving is not used during testing
        props.put(PersistenceUnitProperties.WEAVING, "false");

        if (replaceTables) {
            props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        }

        return Persistence.createEntityManagerFactory("default", props);
    }

    /**
     * Add the system property value if it exists, otherwise use the default
     * value.
     */
    private static void setProperty(Map<String, Object> props, String key, String defaultValue) {
        String value = defaultValue;
        if (System.getProperties().containsKey(key)) {
            value = System.getProperty(key);
        }
        props.put(key, value);
    }

}
