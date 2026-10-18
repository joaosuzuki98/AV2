package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.modelos.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/clientes")
public class ClienteControle {
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;

	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obterCliente(@PathVariable long id) {
		Cliente cliente = repositorio.findById(id).orElse(null);
		if (cliente == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		adicionadorLink.adicionarLink(cliente);
		return new ResponseEntity<>(cliente, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<CollectionModel<Cliente>> obterClientes() {
		List<Cliente> clientes = repositorio.findAll();
		adicionadorLink.adicionarLink(clientes);
		CollectionModel<Cliente> colecao = CollectionModel.of(clientes);
		colecao.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterClientes())
				.withSelfRel());
		colecao.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).cadastrarCliente(null))
				.withRel("create"));
		return new ResponseEntity<>(colecao, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
		if (cliente.getId() != null) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		Cliente cadastrado = repositorio.save(cliente);
		adicionadorLink.adicionarLink(cadastrado);
		return ResponseEntity.created(WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterCliente(cadastrado.getId())).toUri())
				.body(cadastrado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarCliente(@PathVariable long id, @RequestBody Cliente atualizacao) {
		Cliente cliente = repositorio.findById(id).orElse(null);
		if (cliente == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ClienteAtualizador atualizador = new ClienteAtualizador();
		atualizador.atualizar(cliente, atualizacao);
		Cliente atualizado = repositorio.save(cliente);
		adicionadorLink.adicionarLink(atualizado);
		return new ResponseEntity<>(atualizado, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluirCliente(@PathVariable long id) {
		Cliente cliente = repositorio.findById(id).orElse(null);
		if (cliente == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		repositorio.delete(cliente);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
