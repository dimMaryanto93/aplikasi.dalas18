package dallastools.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by dimmaryanto on 9/28/15.
 */
@Configurable
@PropertySource("classpath:schema.properties")
public class DatabaseConfigurer {


    @Value("${hibernate.config.show_sql}")
    private String showSQL;

    @Value("${hibernate.config.generate4ddl}")
    private String hbm2dll;

    @Value("${hibernate.config.dialect}")
    private String dialect;

    @Bean()
    public DataSource dataSource(
            @Value("${jdbc.connection.username}") String dbUser,
            @Value("${jdbc.connection.dbserver}") String dbServer,
            @Value("${jdbc.connection.password}") String dbPasswd,
            @Value("${jdbc.connection.dbhost}") String dbHost,
            @Value("${jdbc.connection.dbport}") String dbPort,
            @Value("${jdbc.connection.db}") String dbName,
            @Value("${jdbc.driver.class.loader}") String driverClassLoader) {
        BasicDataSource dataSource = new BasicDataSource();
        // set connection
        dataSource.setDriverClassName(driverClassLoader);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPasswd);
        dataSource.setUrl("jdbc:" + dbServer + "://" + dbHost + ":" + dbPort + "/" + dbName);

        // set database property
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(85);
        dataSource.setMaxIdle(3);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(18000);
        dataSource.setTimeBetweenEvictionRunsMillis(18000);
        dataSource.setNumTestsPerEvictionRun(2);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnReturn(true);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1000);

        return dataSource;
    }

    private Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.show_sql", showSQL);
                setProperty("hibernate.hbm2ddl.auto", hbm2dll);
                setProperty("hibernate.dialect", dialect);
            }
        };
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean session = new LocalSessionFactoryBean();
        // set datasource
        session.setDataSource(dataSource);
        // set hibernate.cfg.xml
        session.setHibernateProperties(hibernateProperties());
        // set mapping classes
        session.setPackagesToScan("dallastools.models");

        return session;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}
