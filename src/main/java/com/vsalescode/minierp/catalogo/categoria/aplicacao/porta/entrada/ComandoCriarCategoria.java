package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.ExcecaoChaveIdempotenciaInvalida;

public record ComandoCriarCategoria(String chaveIdempotencia, String nome, String descricao) {

	public ComandoCriarCategoria {
		if (chaveIdempotencia == null || chaveIdempotencia.isBlank()) {
			throw new ExcecaoChaveIdempotenciaInvalida("A chave de idempotência não pode estar vazia");
		}

		chaveIdempotencia = chaveIdempotencia.strip();
	}

}
