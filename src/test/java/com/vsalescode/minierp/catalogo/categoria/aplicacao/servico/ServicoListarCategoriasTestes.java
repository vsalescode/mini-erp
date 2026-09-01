package com.vsalescode.minierp.catalogo.categoria.aplicacao.servico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.CampoOrdenacaoCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.DirecaoOrdenacao;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.ExcecaoPaginacaoInvalida;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.ConsultaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.PaginaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.CriteriosPesquisaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.RepositorioCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.ResultadoPesquisaCategorias;
import com.vsalescode.minierp.catalogo.categoria.dominio.Categoria;
import com.vsalescode.minierp.catalogo.categoria.dominio.IdentificadorCategoria;

class ServicoListarCategoriasTestes {

	private static final Instant AGORA = Instant.parse("2026-09-01T12:00:00Z");

	@Test
	void listaCategoriasComFiltroOrdenacaoEPaginacao() {
		Categoria primeira = Categoria.criar(IdentificadorCategoria.novo(), "Eletrônicos", null, AGORA);
		Categoria segunda = Categoria.criar(IdentificadorCategoria.novo(), "Escritório", "Materiais", AGORA);
		RepositorioPesquisaEmMemoria repositorio = new RepositorioPesquisaEmMemoria(
				new ResultadoPesquisaCategorias(List.of(primeira, segunda), 12));
		ServicoListarCategorias servico = new ServicoListarCategorias(repositorio);
		ConsultaCategorias consulta = new ConsultaCategorias(
				"  e  ",
				true,
				1,
				5,
				CampoOrdenacaoCategoria.CRIADA_EM,
				DirecaoOrdenacao.DESCENDENTE);

		PaginaCategorias resultado = servico.executar(consulta);

		assertThat(repositorio.criteriosRecebidos.nome()).isEqualTo("e");
		assertThat(repositorio.criteriosRecebidos.ativa()).isTrue();
		assertThat(repositorio.criteriosRecebidos.pagina()).isEqualTo(1);
		assertThat(repositorio.criteriosRecebidos.tamanho()).isEqualTo(5);
		assertThat(repositorio.criteriosRecebidos.ordenarPor())
				.isEqualTo(CampoOrdenacaoCategoria.CRIADA_EM);
		assertThat(repositorio.criteriosRecebidos.direcao()).isEqualTo(DirecaoOrdenacao.DESCENDENTE);
		assertThat(resultado.conteudo()).extracting("nome")
				.containsExactly("Eletrônicos", "Escritório");
		assertThat(resultado.totalElementos()).isEqualTo(12);
		assertThat(resultado.totalPaginas()).isEqualTo(3);
	}

	@Test
	void usaOrdenacaoPadraoERemoveFiltroDeNomeVazio() {
		ConsultaCategorias consulta = new ConsultaCategorias("  ", null, 0, 20, null, null);

		assertThat(consulta.nome()).isNull();
		assertThat(consulta.ordenarPor()).isEqualTo(CampoOrdenacaoCategoria.NOME);
		assertThat(consulta.direcao()).isEqualTo(DirecaoOrdenacao.ASCENDENTE);
	}

	@Test
	void rejeitaNumeroDePaginaNegativo() {
		assertThatThrownBy(() -> new ConsultaCategorias(null, null, -1, 20, null, null))
				.isInstanceOf(ExcecaoPaginacaoInvalida.class)
				.hasMessage("O número da página não pode ser negativo");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 101 })
	void rejeitaTamanhoDePaginaForaDoLimite(int tamanho) {
		assertThatThrownBy(() -> new ConsultaCategorias(null, null, 0, tamanho, null, null))
				.isInstanceOf(ExcecaoPaginacaoInvalida.class)
				.hasMessage("O tamanho da página deve estar entre 1 e 100");
	}

	private static final class RepositorioPesquisaEmMemoria implements RepositorioCategorias {

		private final ResultadoPesquisaCategorias resultado;
		private CriteriosPesquisaCategorias criteriosRecebidos;

		private RepositorioPesquisaEmMemoria(ResultadoPesquisaCategorias resultado) {
			this.resultado = resultado;
		}

		@Override
		public void salvar(Categoria categoria) {
			throw new UnsupportedOperationException("Este teste não salva categorias");
		}

		@Override
		public Optional<Categoria> buscarPorIdentificador(IdentificadorCategoria identificador) {
			throw new UnsupportedOperationException("Este teste não busca categoria por identificador");
		}

		@Override
		public ResultadoPesquisaCategorias pesquisar(CriteriosPesquisaCategorias criterios) {
			this.criteriosRecebidos = criterios;
			return resultado;
		}

	}

}
