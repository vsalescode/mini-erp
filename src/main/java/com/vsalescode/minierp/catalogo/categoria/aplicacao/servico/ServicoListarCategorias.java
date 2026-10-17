package com.vsalescode.minierp.catalogo.categoria.aplicacao.servico;

import java.util.List;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.ConsultaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.DadosCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.ListarCategoriasCasoDeUso;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.PaginaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.CriteriosPesquisaCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.RepositorioCategorias;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.ResultadoPesquisaCategorias;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServicoListarCategorias implements ListarCategoriasCasoDeUso {

	private final RepositorioCategorias repositorioCategorias;

	@Override
	public PaginaCategorias executar(ConsultaCategorias consulta) {
		CriteriosPesquisaCategorias criterios = new CriteriosPesquisaCategorias(
				consulta.nome(),
				consulta.ativa(),
				consulta.pagina(),
				consulta.tamanho(),
				consulta.ordenarPor(),
				consulta.direcao());
		ResultadoPesquisaCategorias resultado = repositorioCategorias.pesquisar(criterios);
		List<DadosCategoria> categorias = resultado.categorias().stream()
				.map(DadosCategoria::aPartirDe)
				.toList();

		return PaginaCategorias.criar(
				categorias,
				consulta.pagina(),
				consulta.tamanho(),
				resultado.totalElementos());
	}

}
