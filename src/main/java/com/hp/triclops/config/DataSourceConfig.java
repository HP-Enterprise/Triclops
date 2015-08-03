package com.hp.triclops.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;


@Configuration
@PropertySource("jdbc.properties")
public class DataSourceConfig {
    @Value("${jdbc.driverClass}") String driverClass;
    @Value("${jdbc.url}") String url;
    @Value("${jdbc.user}") String user;
    @Value("${jdbc.password}") String password;

    @Bean(autowire= Autowire.BY_TYPE)
    public DataSource dataSource() throws PropertyVetoException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }
}
