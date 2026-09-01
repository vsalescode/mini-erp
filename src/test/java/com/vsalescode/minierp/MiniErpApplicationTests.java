package com.vsalescode.minierp;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class MiniErpApplicationTests {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
			DockerImageName.parse("postgres:18.6-alpine"));

	@Autowired
	private DataSource dataSource;

	@Test
	void startsWithFlywayManagedPostgresInUtc() throws Exception {
		try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement()) {
			ResultSet flywayHistory = statement.executeQuery("""
					select count(*)
					from information_schema.tables
					where table_schema = 'mini_erp'
					  and table_name = 'flyway_schema_history'
					""");
			assertThat(flywayHistory.next()).isTrue();
			assertThat(flywayHistory.getInt(1)).isEqualTo(1);

			ResultSet timezone = statement.executeQuery("show timezone");
			assertThat(timezone.next()).isTrue();
			assertThat(timezone.getString(1)).isEqualTo("UTC");
		}
	}

}
