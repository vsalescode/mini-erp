package com.vsalescode.minierp;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZoneOffset;

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

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@ActiveProfiles("test")
@Testcontainers
class MiniErpAplicacaoTestes {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRESQL = new PostgreSQLContainer(
			DockerImageName.parse("postgres:18.6-alpine"));

	@Autowired
	private DataSource fonteDeDados;

	@Test
	void iniciaComPostgresqlGerenciadoPeloFlywayEmUtc() throws Exception {
		assertThat(ZoneId.systemDefault().normalized()).isEqualTo(ZoneOffset.UTC);

		try (Connection conexao = fonteDeDados.getConnection();
				Statement comando = conexao.createStatement()) {
			ResultSet historicoFlyway = comando.executeQuery("""
					select count(*)
					from information_schema.tables
					where table_schema = 'mini_erp'
					  and table_name = 'flyway_schema_history'
					""");
			assertThat(historicoFlyway.next()).isTrue();
			assertThat(historicoFlyway.getInt(1)).isEqualTo(1);

			ResultSet fusoHorario = comando.executeQuery("show timezone");
			assertThat(fusoHorario.next()).isTrue();
			assertThat(fusoHorario.getString(1)).isEqualTo("UTC");
		}
	}

}
