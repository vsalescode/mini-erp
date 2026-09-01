package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada;

import java.util.List;

public record PaginaCategorias(
		List<DadosCategoria> conteudo,
		int pagina,
		int tamanho,
		long totalElementos,
		int totalPaginas) {

	public PaginaCategorias {
		conteudo = List.copyOf(conteudo);
	}

	public static PaginaCategorias criar(
			List<DadosCategoria> conteudo,
			int pagina,
			int tamanho,
			long totalElementos) {
		int totalPaginas = totalElementos == 0
				? 0
				: (int) Math.ceil((double) totalElementos / tamanho);
		return new PaginaCategorias(conteudo, pagina, tamanho, totalElementos, totalPaginas);
	}

}
