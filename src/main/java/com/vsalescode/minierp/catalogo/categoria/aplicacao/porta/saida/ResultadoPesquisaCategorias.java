package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida;

import java.util.List;

import com.vsalescode.minierp.catalogo.categoria.dominio.Categoria;

public record ResultadoPesquisaCategorias(List<Categoria> categorias, long totalElementos) {

	public ResultadoPesquisaCategorias {
		categorias = List.copyOf(categorias);
	}

}
