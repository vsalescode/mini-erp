package com.vsalescode.minierp.catalogo.categoria.aplicacao;

public final class ExcecaoChaveIdempotenciaInvalida extends RuntimeException {

	public ExcecaoChaveIdempotenciaInvalida(String mensagem) {
		super(mensagem);
	}

}
