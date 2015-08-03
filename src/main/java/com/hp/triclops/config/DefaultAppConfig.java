import com.hp.triclops.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyVetoException;
import java.util.Properties;


@ComponentScan(basePackages = "com.hp.triclops.*.service")
@Import(DataSourceConfig.class)
@Configuration
@EnableTransactionManagement
public class DefaultAppConfig {

    @Autowired
    DataSourceConfig config;

    @Bean
    public PropertySourcesPlaceholderConfigurer placehodlerConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory()
            throws PropertyVetoException {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(config.dataSource());
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect",
                "org.hibernate.dialect.MySQLDialect");
        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        sessionFactoryBean.setPackagesToScan("com.coderli.shurnim.*.model");
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager txManager() throws PropertyVetoException {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory().getObject());
        return txManager;
    }
}