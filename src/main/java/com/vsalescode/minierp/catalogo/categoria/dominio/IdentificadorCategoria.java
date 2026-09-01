package com.vsalescode.minierp.catalogo.categoria.dominio;

import java.util.Objects;
import java.util.UUID;

public record IdentificadorCategoria(UUID valor) {

	public IdentificadorCategoria {
		Objects.requireNonNull(valor, "O identificador da categoria não pode ser nulo");
	}

	public static IdentificadorCategoria novo() {
		return new IdentificadorCategoria(UUID.randomUUID());
	}

}
