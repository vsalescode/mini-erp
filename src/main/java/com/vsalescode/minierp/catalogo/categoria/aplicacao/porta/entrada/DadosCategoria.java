package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada;

import java.time.Instant;
import java.util.UUID;

import com.vsalescode.minierp.catalogo.categoria.dominio.Categoria;

public record DadosCategoria(
		UUID identificador,
		String nome,
		String descricao,
		boolean ativa,
		Instant criadaEm,
		Instant atualizadaEm) {

	public static DadosCategoria aPartirDe(Categoria categoria) {
		return new DadosCategoria(
				categoria.identificador().valor(),
				categoria.nome(),
				categoria.descricao().orElse(null),
				categoria.estaAtiva(),
				categoria.criadaEm(),
				categoria.atualizadaEm());
	}

}
