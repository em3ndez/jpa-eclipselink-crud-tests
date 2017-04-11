package com.example.crud;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.zalando.logbook.Logbook;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.zalando.logbook.Conditions.*;

@ComponentScan
@EnableCaching
@EnableJpaRepositories("com.example.crud.db.dao")
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableTransactionManagement
@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Inject private Environment env;


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        addDefaultProfile(app, source);
        logbookSetup();
        Environment env = app.run(args).getEnvironment();
    }

    private static void logbookSetup() {
        Logbook logbook = Logbook.builder()
                .condition(exclude(
                        requestTo("/health"),
                        requestTo("/admin/**"),
                        contentType("application/octet-stream"),
                        header("X-Secret", Sets.newHashSet("1", "true")::contains)))
                .build();
    }

    /**
     * If no profile has been configured, set by default the "dev" profile.
     */
    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active") &&
                !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {

            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }

    /**
     * EclipseLink JPA setup.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean ret = new LocalContainerEntityManagerFactoryBean();
        ret.setDataSource(dataSource());
        ret.setJpaVendorAdapter(jpaVendorAdapter());
        ret.setJpaDialect(eclipseLinkJpaDialect());
        ret.setJpaPropertyMap(jpaProperties());
        ret.setPackagesToScan("com.example.crud.db");
        return ret;
    }


    @Bean
    public EclipseLinkJpaDialect eclipseLinkJpaDialect() {
        return new EclipseLinkJpaDialect();
    }

    /*
     * Set this property to disable LoadTimeWeaver (i.e. Dynamic Weaving) for EclipseLink.
     * Otherwise, you'll get: Cannot apply class transformer without LoadTimeWeaver specified
     */
    @Bean
    public Map<String, String> jpaProperties() {
        Map<String, String> props = new HashMap<>();
        props.put("eclipselink.weaving", "static");
        return props;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
        jpaVendorAdapter.setGenerateDdl(true);
        return jpaVendorAdapter;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:26257/crud");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        return dataSource;
    }

    /*
    @Bean
    public CacheManager cacheManager() {
        //TODO(gburd): is there an eclipselink cache manager? or caffeine? or...?
        Cache cache = new ConcurrentMapCache("name");

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(cache));

        return manager;
    }
    */

}
