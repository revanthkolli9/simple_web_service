package com.blackwaterpragmatic.webservices.spring;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@MapperScan({"com.blackwaterpragmatic.webservices.mybatis.mapper"})
public class DataConfiguration {

	@Bean
	public DataSource dataSource() {
		final EmbeddedDatabaseBuilder databaseBuilder = new EmbeddedDatabaseBuilder();
		final EmbeddedDatabase database = databaseBuilder
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("com/blackwaterpragmatic/webservices/hsql/create-db.sql")
				.addScript("com/blackwaterpragmatic/webservices/hsql/insert-data.sql")
				.build();
		return database;
	}

	@Bean
	public DataSourceTransactionManager transactionManager(final DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(final DataSource dataSource) throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setTypeAliasesPackage("com.blackwaterpragmatic.webservices.bean");
		return sessionFactory.getObject();
	}
}
