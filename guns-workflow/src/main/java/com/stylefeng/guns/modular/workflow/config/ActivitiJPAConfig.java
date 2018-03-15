package com.stylefeng.guns.modular.workflow.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.stylefeng.guns.core.datasource.DruidProperties;
import com.stylefeng.guns.core.mutidatasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zh
 * @Description :
 * @Date Created in 10:28 2018/3/14
 * @Modified By :
 */
@Configuration
@EnableTransactionManagement//开启事物管理
@EnableJpaRepositories(//自定义数据管理的配置
        //指定EntityManager的创建工厂Bean
        entityManagerFactoryRef = "activitiEntityManagerFactory",
        //指定事物管理的Bean
        transactionManagerRef = "activitiTransactionManager")
public class ActivitiJPAConfig {

    /*
     * 配置数据源
     */
    @Bean
    @Primary
    public DataSource activitiDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        //设置数据源的属性
        setDruidProperties(dataSource);
        return dataSource;
    }


    //注入数据源配置信息
    @Autowired
    ActivitiDataSourceProperties config;

    //设置数据源的属性的方法
    private void setDruidProperties(DruidDataSource dataSource) {
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setMaxActive(config.getMaxActive());
        dataSource.setInitialSize(config.getInitialSize());
        dataSource.setMinIdle(config.getMinIdle());
        dataSource.setMaxWait(config.getMaxWait());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(config.getMaxPoolPreparedStatementPerConnectionSize());
        dataSource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        dataSource.setPoolPreparedStatements(config.getPoolPreparedStatements());
    }

    /*
     * 配置实体管理工厂Bean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean activitiEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(activitiDataSource())
                .packages("com.stylefeng.guns.modular.workflow.domain")//设置实体类所在位置
                .persistenceUnit("activiti")
                .properties(getProperties(activitiDataSource()))//设置hibernate通用配置
                .build();
    }

    //注入spring自带的jpa属性类
    @Autowired
    private JpaProperties jpaProperties;

    /*
     *拿到hibernate的通用配置
     */
    private Map<String, String> getProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    /*
     *配置事物管理的Bean
     */
    @Bean
    public PlatformTransactionManager activitiTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(activitiEntityManagerFactory(builder).getObject());
    }

}