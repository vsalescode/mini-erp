package com.vsalescode.minierp.catalogo.categoria.aplicacao;

public final class ExcecaoChaveIdempotenciaReutilizada extends RuntimeException {

	public ExcecaoChaveIdempotenciaReutilizada(String mensagem) {
		super(mensagem);
	}

}
