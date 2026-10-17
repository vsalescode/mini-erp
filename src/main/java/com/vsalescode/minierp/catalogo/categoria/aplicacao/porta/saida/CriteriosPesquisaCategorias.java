package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.CampoOrdenacaoCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.DirecaoOrdenacao;

public record CriteriosPesquisaCategorias(
		String nome,
		Boolean ativa,
		int pagina,
		int tamanho,
		CampoOrdenacaoCategoria ordenarPor,
		DirecaoOrdenacao direcao) {
}
