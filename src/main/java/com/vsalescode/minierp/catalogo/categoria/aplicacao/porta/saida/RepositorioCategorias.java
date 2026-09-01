package com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida;

import java.util.Optional;

import com.vsalescode.minierp.catalogo.categoria.dominio.Categoria;
import com.vsalescode.minierp.catalogo.categoria.dominio.IdentificadorCategoria;

public interface RepositorioCategorias {

	void salvar(Categoria categoria);

	Optional<Categoria> buscarPorIdentificador(IdentificadorCategoria identificador);

}
