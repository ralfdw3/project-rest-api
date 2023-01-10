package com.api.webvote.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.api.webvote.model.Client;
import com.api.webvote.repository.ClientRepository;
import com.api.webvote.repository.ScheduleRepository;
import com.api.webvote.repository.VoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class ClientControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ClientController clientController;

	@MockBean
	private ClientRepository clientRepository;

	@MockBean
	private ScheduleRepository scheduleRepository;

	@MockBean
	private VoteRepository voteRepository;

	@BeforeEach
	public void setup() {
		standaloneSetup(this.clientController);
	}

	@Test
	public void deveRetornarSucesso_QuandoCriarCliente() throws Exception {

		Client client = new Client("Nome do Sujeito", "000.000.000-00");

		ResponseEntity<Client> clientExpected = clientController.save(client);

		assertEquals(HttpStatus.OK, clientExpected.getStatusCode());
	}

	@Test
	public void deveRetornarSucesso_QuandoBuscarCliente() throws Exception 
	{
		// cria um cliente de teste e salva no banco de dados mockado
		Client testClient = new Client();

		testClient.setId(1L);
		testClient.setName("Test Client");
		testClient.setCpf("000.000.000-00");

		when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

		// realiza uma chamada GET para a API passando o ID do cliente de teste
		MvcResult result = mockMvc.perform(get("/api/client/{id}", testClient.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		// verifica se o método findById() do repositório de clientes foi chamado com o
		// ID do cliente de teste
		verify(clientRepository).findById(testClient.getId());
	}

	@Test
	public void deveRetornarFalha_QuandoClienteNaoForEncontrado() throws Exception {

		mockMvc.perform(get("/api/client/{id}", 999999)).andExpect(status().isNotFound());
	}

}