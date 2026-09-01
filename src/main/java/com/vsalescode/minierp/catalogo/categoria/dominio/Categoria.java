package com.vsalescode.minierp.catalogo.categoria.dominio;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class Categoria {

	private static final Pattern ESPACOS = Pattern.compile("\\s+");

	private final IdentificadorCategoria identificador;
	private final Instant criadaEm;
	private String nome;
	private String descricao;
	private boolean ativa;
	private Instant atualizadaEm;

	private Categoria(IdentificadorCategoria identificador, String nome, String descricao, Instant criadaEm) {
		this.identificador = Objects.requireNonNull(identificador, "O identificador da categoria não pode ser nulo");
		this.nome = normalizarNome(nome);
		this.descricao = normalizarDescricao(descricao);
		this.ativa = true;
		this.criadaEm = exigirData(criadaEm);
		this.atualizadaEm = criadaEm;
	}

	public static Categoria criar(IdentificadorCategoria identificador, String nome, String descricao,
			Instant criadaEm) {
		return new Categoria(identificador, nome, descricao, criadaEm);
	}

	public void atualizarDados(String nome, String descricao, Instant alteradaEm) {
		String nomeNormalizado = normalizarNome(nome);
		String descricaoNormalizada = normalizarDescricao(descricao);

		if (this.nome.equals(nomeNormalizado) && Objects.equals(this.descricao, descricaoNormalizada)) {
			return;
		}

		this.atualizadaEm = exigirDataAtualOuFutura(alteradaEm);
		this.nome = nomeNormalizado;
		this.descricao = descricaoNormalizada;
	}

	public void ativar(Instant alteradaEm) {
		if (ativa) {
			return;
		}

		this.atualizadaEm = exigirDataAtualOuFutura(alteradaEm);
		this.ativa = true;
	}

	public void desativar(Instant alteradaEm) {
		if (!ativa) {
			return;
		}

		this.atualizadaEm = exigirDataAtualOuFutura(alteradaEm);
		this.ativa = false;
	}

	public IdentificadorCategoria identificador() {
		return identificador;
	}

	public String nome() {
		return nome;
	}

	public Optional<String> descricao() {
		return Optional.ofNullable(descricao);
	}

	public boolean estaAtiva() {
		return ativa;
	}

	public Instant criadaEm() {
		return criadaEm;
	}

	public Instant atualizadaEm() {
		return atualizadaEm;
	}

	private static String normalizarNome(String valor) {
		if (valor == null || valor.isBlank()) {
			throw new ExcecaoCategoriaInvalida("O nome da categoria não pode estar vazio");
		}

		return ESPACOS.matcher(valor.strip()).replaceAll(" ");
	}

	private static String normalizarDescricao(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}

		return valor.strip();
	}

	private static Instant exigirData(Instant valor) {
		if (valor == null) {
			throw new ExcecaoCategoriaInvalida("A data da categoria não pode ser nula");
		}

		return valor;
	}

	private Instant exigirDataAtualOuFutura(Instant valor) {
		Instant data = exigirData(valor);
		if (data.isBefore(atualizadaEm)) {
			throw new ExcecaoCategoriaInvalida("A data da categoria não pode retroceder");
		}

		return data;
	}

}
