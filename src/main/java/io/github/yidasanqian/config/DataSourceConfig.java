package io.github.yidasanqian.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author Linyu Chen
 */
@Configuration
public class DataSourceConfig {

    private static final String PRIMARY_MAPPER_BASE_PACKAGE = "io.github.yidasanqian.mapper.master";
    private static final String BUSINESS_MAPPER_BASE_PACKAGE = "io.github.yidasanqian.mapper.business";

    private static final String DATASOURCE_DRUID_PROPERTIES = "datasource/druid.properties";
    private static final String DATASOURCE_DRUID_PRIMARY_PROPERTIES = "datasource/druid-primary.properties";
    private static final String DATASOURCE_DRUID_BUSINESS_PROPERTIES = "datasource/druid-business.properties";

    public static final String CLASSPATH_MAPPER_XML = "classpath:mapper/*/*.xml";

    private static Properties commonProperties;

    static {
        commonProperties = new Properties();
        InputStream in = DataSourceConfig.class.getClassLoader().getResourceAsStream(DATASOURCE_DRUID_PROPERTIES);
        try {
            commonProperties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties loadDruidProperties(String path) throws IOException {
        Properties properties = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        properties.load(in);
        for (Map.Entry<Object, Object> entry : commonProperties.entrySet()) {
            properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        return properties;
    }

    //@ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.primary-ds")
    @Primary
    @Bean("primaryDataSource")
    public AtomikosDataSourceBean primaryDataSource() throws IOException {
        /*RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, DRUID_DATASOURCE_PREFIX);
        Map<String, Object> subProperties = propertyResolver.getSubProperties(".");
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : subProperties.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue().toString());
        }*/

        Properties properties = loadDruidProperties(DATASOURCE_DRUID_PRIMARY_PROPERTIES);
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        DruidXADataSource xaDataSource = new DruidXADataSource();
        xaDataSource.configFromPropety(properties);
        dataSourceBean.setXaDataSource(xaDataSource);
        //dataSourceBean.setXaProperties(properties);
        return dataSourceBean;
    }

    // @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.business-ds")
    @Bean("businessDataSource")
    public AtomikosDataSourceBean businessDataSource() throws IOException {
         /*RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, DRUID_DATASOURCE_PREFIX);
         Map<String, Object> subProperties = propertyResolver.getSubProperties(".");
         Properties properties = new Properties();
         for (Map.Entry<String, Object> entry : subProperties.entrySet()) {
             properties.setProperty(entry.getKey(), entry.getValue().toString());
         }*/

        Properties properties = loadDruidProperties(DATASOURCE_DRUID_BUSINESS_PROPERTIES);
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        DruidXADataSource xaDataSource = new DruidXADataSource();
        xaDataSource.configFromPropety(properties);
        dataSourceBean.setXaDataSource(xaDataSource);
        //       dataSourceBean.setXaProperties(properties);

        return dataSourceBean;
    }


    @Primary
    @Bean
    public SqlSessionFactoryBean primarySqlSessionFactoryBean(@Qualifier("primaryDataSource") AtomikosDataSourceBean primaryDataSource) {
        return getSqlSessionFactoryBean(primaryDataSource);
    }

    @Bean
    public SqlSessionFactoryBean businessSqlSessionFactoryBean(@Qualifier("businessDataSource") AtomikosDataSourceBean businessDataSource) {
        return getSqlSessionFactoryBean(businessDataSource);
    }

    private SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(CLASSPATH_MAPPER_XML));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer primaryMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(PRIMARY_MAPPER_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("primarySqlSessionFactoryBean");
        return mapperScannerConfigurer;
    }

    @Bean
    public MapperScannerConfigurer businessMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(BUSINESS_MAPPER_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("businessSqlSessionFactoryBean");
        return mapperScannerConfigurer;
    }
}
