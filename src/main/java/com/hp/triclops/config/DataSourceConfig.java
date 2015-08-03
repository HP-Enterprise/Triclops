package com.hp.triclops.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;


@Configuration
@PropertySource("/conf/jdbc.properties")
public class DataSourceConfig {
    @Value("${jdbc.driverClass}") String driverClass;
    @Value("${jdbc.url}") String url;
    @Value("${jdbc.user}") String user;
    @Value("${jdbc.password}") String password;

    @Bean(autowire= Autowire.BY_TYPE)
    public DataSource dataSource() throws PropertyVetoException {
        return null;
    }
}
