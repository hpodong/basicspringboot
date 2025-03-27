package com.travplan.configs;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class JdbcConfig {

    private final String MASTER_DATA_SOURCE = "masterDataSource"; // Master 데이터 소스의 Bean 이름
    private final String SLAVE_DATA_SOURCE = "slaveDataSource";   // Slave 데이터 소스의 Bean 이름

    @Bean(MASTER_DATA_SOURCE)
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(SLAVE_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public JdbcTemplate masterJdbcTemplate(@Qualifier(MASTER_DATA_SOURCE) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate slaveJdbcTemplate(@Qualifier(SLAVE_DATA_SOURCE) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}