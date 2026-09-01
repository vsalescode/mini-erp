package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.CampoOrdenacaoCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.DirecaoOrdenacao;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.ExcecaoPaginacaoInvalida;

public record ConsultaCategorias(
		String nome,
		Boolean ativa,
		int pagina,
		int tamanho,
		CampoOrdenacaoCategoria ordenarPor,
		DirecaoOrdenacao direcao) {

	public static final int TAMANHO_MAXIMO = 100;

	public ConsultaCategorias {
		if (pagina < 0) {
			throw new ExcecaoPaginacaoInvalida("O número da página não pode ser negativo");
		}
		if (tamanho < 1 || tamanho > TAMANHO_MAXIMO) {
			throw new ExcecaoPaginacaoInvalida("O tamanho da página deve estar entre 1 e 100");
		}

		nome = normalizarFiltroNome(nome);
		ordenarPor = ordenarPor == null ? CampoOrdenacaoCategoria.NOME : ordenarPor;
		direcao = direcao == null ? DirecaoOrdenacao.ASCENDENTE : direcao;
	}

	private static String normalizarFiltroNome(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}

		return valor.strip();
	}

}
