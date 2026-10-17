package com.vsalescode.minierp.catalogo.categoria.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CategoriaTestes {

	private static final Instant CRIADA_EM = Instant.parse("2026-08-31T12:00:00Z");

	@Test
	void criaCategoriaAtivaComDadosNormalizados() {
		IdentificadorCategoria identificador = IdentificadorCategoria.novo();

		Categoria categoria = Categoria.criar(identificador, "  Material   de escritório  ",
				"  Itens de uso diário  ", CRIADA_EM);

		assertThat(categoria.identificador()).isEqualTo(identificador);
		assertThat(categoria.nome()).isEqualTo("Material de escritório");
		assertThat(categoria.descricao()).contains("Itens de uso diário");
		assertThat(categoria.estaAtiva()).isTrue();
		assertThat(categoria.criadaEm()).isEqualTo(CRIADA_EM);
		assertThat(categoria.atualizadaEm()).isEqualTo(CRIADA_EM);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	void rejeitaNomeVazio(String nome) {
		assertThatThrownBy(() -> Categoria.criar(IdentificadorCategoria.novo(), nome, null, CRIADA_EM))
				.isInstanceOf(ExcecaoCategoriaInvalida.class)
				.hasMessage("O nome da categoria não pode estar vazio");
	}

	@Test
	void consideraDescricaoVaziaComoAusente() {
		Categoria categoria = Categoria.criar(IdentificadorCategoria.novo(), "Escritório", "  ", CRIADA_EM);

		assertThat(categoria.descricao()).isEmpty();
	}

	@Test
	void atualizaOsDadosDaCategoria() {
		Instant alteradaEm = CRIADA_EM.plusSeconds(60);
		Categoria categoria = Categoria.criar(IdentificadorCategoria.novo(), "Escritório", null, CRIADA_EM);

		categoria.atualizarDados("  Escritório   e escola  ", "  Nova descrição  ", alteradaEm);

		assertThat(categoria.nome()).isEqualTo("Escritório e escola");
		assertThat(categoria.descricao()).contains("Nova descrição");
		assertThat(categoria.atualizadaEm()).isEqualTo(alteradaEm);
	}

	@Test
	void mantemADataQuandoOsDadosNaoMudam() {
		Categoria categoria = Categoria.criar(IdentificadorCategoria.novo(), "Material de escritório", null,
				CRIADA_EM);

		categoria.atualizarDados("  Material   de escritório ", " ", CRIADA_EM.plusSeconds(60));

		assertThat(categoria.atualizadaEm()).isEqualTo(CRIADA_EM);
	}

	@Test
	void ativaEDesativaSemRepetirTransicoes() {
		Instant desativadaEm = CRIADA_EM.plusSeconds(60);
		Instant ativadaEm = desativadaEm.plusSeconds(60);
		Categoria categoria = Categoria.criar(IdentificadorCategoria.novo(), "Escritório", null, CRIADA_EM);

		categoria.desativar(desativadaEm);
		categoria.desativar(desativadaEm.plusSeconds(1));

		assertThat(categoria.estaAtiva()).isFalse();
		assertThat(categoria.atualizadaEm()).isEqualTo(desativadaEm);

		categoria.ativar(ativadaEm);
		categoria.ativar(ativadaEm.plusSeconds(1));

		assertThat(categoria.estaAtiva()).isTrue();
		assertThat(categoria.atualizadaEm()).isEqualTo(ativadaEm);
	}

	@Test
	void rejeitaDataAnteriorAoEstadoAtual() {
		Categoria categoria = Categoria.criar(IdentificadorCategoria.novo(), "Escritório", null, CRIADA_EM);

		assertThatThrownBy(() -> categoria.desativar(CRIADA_EM.minusSeconds(1)))
				.isInstanceOf(ExcecaoCategoriaInvalida.class)
				.hasMessage("A data da categoria não pode retroceder");
	}

}
