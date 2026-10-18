package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.entidades.Cliente;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<Cliente> {

	@Override
	public void adicionarLink(List<Cliente> lista) {
		for (Cliente cliente : lista) {
			adicionarLink(cliente);
		}
	}

	@Override
	public void adicionarLink(Cliente objeto) {
		long id = objeto.getId();
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterCliente(id))
				.withSelfRel();
		Link linkColecao = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterClientes())
				.withRel("collection");
		Link linkAtualizacao = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).atualizarCliente(id, null))
				.withRel("update");
		Link linkExclusao = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).excluirCliente(id))
				.withRel("delete");
		objeto.add(linkProprio, linkColecao, linkAtualizacao, linkExclusao);
	}
}
