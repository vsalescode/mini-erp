package com.vsalescode.minierp.catalogo.categoria.aplicacao.servico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.ExcecaoChaveIdempotenciaInvalida;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.ExcecaoChaveIdempotenciaReutilizada;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.ComandoCriarCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.DadosCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.ControleIdempotenciaCriacaoCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.CriteriosPesquisaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.Relogio;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.RepositorioCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.ResultadoPesquisaCategorias;
import com.vsalescode.minierp.catalogo.categoria.dominio.Categoria;
import com.vsalescode.minierp.catalogo.categoria.dominio.IdentificadorCategoria;

class ServicoCriarCategoriaTestes {

	private static final Instant AGORA = Instant.parse("2026-08-31T12:00:00Z");

	private RepositorioCategoriasEmMemoria repositorio;
	private ControleIdempotenciaEmMemoria controleIdempotencia;
	private ServicoCriarCategoria servico;

	@BeforeEach
	void preparar() {
		repositorio = new RepositorioCategoriasEmMemoria();
		controleIdempotencia = new ControleIdempotenciaEmMemoria();
		Relogio relogio = () -> AGORA;
		servico = new ServicoCriarCategoria(repositorio, relogio, controleIdempotencia);
	}

	@Test
	void criaESalvaUmaCategoria() {
		DadosCategoria resultado = servico.executar(
				new ComandoCriarCategoria("chave-1", "  Material   escolar ", "  Cadernos e lápis  "));

		assertThat(resultado.nome()).isEqualTo("Material escolar");
		assertThat(resultado.descricao()).isEqualTo("Cadernos e lápis");
		assertThat(resultado.ativa()).isTrue();
		assertThat(resultado.criadaEm()).isEqualTo(AGORA);
		assertThat(repositorio.quantidadeSalvamentos).isEqualTo(1);
	}

	@Test
	void devolveAMesmaCategoriaAoRepetirAChaveEOConteudo() {
		ComandoCriarCategoria comando = new ComandoCriarCategoria("chave-repetida", "Material escolar", null);

		DadosCategoria primeiraResposta = servico.executar(comando);
		DadosCategoria segundaResposta = servico.executar(comando);

		assertThat(segundaResposta).isEqualTo(primeiraResposta);
		assertThat(repositorio.quantidadeSalvamentos).isEqualTo(1);
	}

	@Test
	void rejeitaAChaveReutilizadaComConteudoDiferente() {
		servico.executar(new ComandoCriarCategoria("mesma-chave", "Material escolar", null));

		assertThatThrownBy(() -> servico.executar(
				new ComandoCriarCategoria("mesma-chave", "Eletrônicos", null)))
				.isInstanceOf(ExcecaoChaveIdempotenciaReutilizada.class)
				.hasMessage("A chave de idempotência já foi usada com outro conteúdo");
		assertThat(repositorio.quantidadeSalvamentos).isEqualTo(1);
	}

	@Test
	void rejeitaChaveDeIdempotenciaVazia() {
		assertThatThrownBy(() -> new ComandoCriarCategoria(" ", "Material escolar", null))
				.isInstanceOf(ExcecaoChaveIdempotenciaInvalida.class)
				.hasMessage("A chave de idempotência não pode estar vazia");
	}

	private static final class RepositorioCategoriasEmMemoria implements RepositorioCategorias {

		private final Map<IdentificadorCategoria, Categoria> categorias = new HashMap<>();
		private int quantidadeSalvamentos;

		@Override
		public void salvar(Categoria categoria) {
			categorias.put(categoria.identificador(), categoria);
			quantidadeSalvamentos++;
		}

		@Override
		public Optional<Categoria> buscarPorIdentificador(IdentificadorCategoria identificador) {
			return Optional.ofNullable(categorias.get(identificador));
		}

		@Override
		public ResultadoPesquisaCategorias pesquisar(CriteriosPesquisaCategorias criterios) {
			throw new UnsupportedOperationException("Este teste não pesquisa categorias");
		}

	}

	private static final class ControleIdempotenciaEmMemoria implements ControleIdempotenciaCriacaoCategoria {

		private final Map<String, RegistroIdempotencia> registros = new HashMap<>();

		@Override
		public IdentificadorCategoria executar(
				String chave,
				String assinaturaSolicitacao,
				Supplier<IdentificadorCategoria> operacao) {
			RegistroIdempotencia existente = registros.get(chave);
			if (existente != null) {
				if (!existente.assinaturaSolicitacao.equals(assinaturaSolicitacao)) {
					throw new ExcecaoChaveIdempotenciaReutilizada(
							"A chave de idempotência já foi usada com outro conteúdo");
				}
				return existente.identificador;
			}

			IdentificadorCategoria identificador = operacao.get();
			registros.put(chave, new RegistroIdempotencia(assinaturaSolicitacao, identificador));
			return identificador;
		}

	}

	private record RegistroIdempotencia(
			String assinaturaSolicitacao,
			IdentificadorCategoria identificador) {
	}

}
