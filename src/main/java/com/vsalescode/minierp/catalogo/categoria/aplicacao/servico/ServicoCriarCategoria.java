package com.vsalescode.minierp.catalogo.categoria.aplicacao.servico;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import com.vsalescode.minierp.catalogo.categoria.aplicacao.ExcecaoCategoriaNaoEncontrada;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.ComandoCriarCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.CriarCategoriaCasoDeUso;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.entrada.DadosCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.ControleIdempotenciaCriacaoCategoria;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.Relogio;
import com.vsalescode.minierp.catalogo.categoria.aplicacao.porta.saida.RepositorioCategorias;
import com.vsalescode.minierp.catalogo.categoria.dominio.Categoria;
import com.vsalescode.minierp.catalogo.categoria.dominio.IdentificadorCategoria;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ServicoCriarCategoria implements CriarCategoriaCasoDeUso {

	private final RepositorioCategorias repositorioCategorias;
	private final Relogio relogio;
	private final ControleIdempotenciaCriacaoCategoria controleIdempotencia;

	@Override
	public DadosCategoria executar(ComandoCriarCategoria comando) {
		String assinaturaSolicitacao = calcularAssinatura(comando.nome(), comando.descricao());
		IdentificadorCategoria identificador = controleIdempotencia.executar(
				comando.chaveIdempotencia(),
				assinaturaSolicitacao,
				() -> criarESalvar(comando));

		Categoria categoria = repositorioCategorias.buscarPorIdentificador(identificador)
				.orElseThrow(() -> new ExcecaoCategoriaNaoEncontrada(
						"A categoria associada à operação idempotente não foi encontrada"));

		return DadosCategoria.aPartirDe(categoria);
	}

	private IdentificadorCategoria criarESalvar(ComandoCriarCategoria comando) {
		IdentificadorCategoria identificador = IdentificadorCategoria.novo();
		Categoria categoria = Categoria.criar(
				identificador,
				comando.nome(),
				comando.descricao(),
				relogio.agora());
		repositorioCategorias.salvar(categoria);
		return identificador;
	}

	private static String calcularAssinatura(String nome, String descricao) {
		try {
			MessageDigest resumo = MessageDigest.getInstance("SHA-256");
			adicionarCampo(resumo, nome);
			adicionarCampo(resumo, descricao);
			return HexFormat.of().formatHex(resumo.digest());
		} catch (NoSuchAlgorithmException excecao) {
			throw new IllegalStateException("O algoritmo SHA-256 não está disponível", excecao);
		}
	}

	private static void adicionarCampo(MessageDigest resumo, String valor) {
		if (valor == null) {
			resumo.update((byte) 0);
			return;
		}

		byte[] bytes = valor.getBytes(StandardCharsets.UTF_8);
		resumo.update((byte) 1);
		resumo.update(ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array());
		resumo.update(bytes);
	}

}
