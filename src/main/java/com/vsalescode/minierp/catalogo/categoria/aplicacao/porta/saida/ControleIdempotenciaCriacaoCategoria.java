package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida;

import java.util.function.Supplier;

import com.vsalescode.minierp.catalogo.categoria.dominio.IdentificadorCategoria;

public interface ControleIdempotenciaCriacaoCategoria {

	IdentificadorCategoria executar(
			String chave,
			String assinaturaSolicitacao,
			Supplier<IdentificadorCategoria> operacao);

}
