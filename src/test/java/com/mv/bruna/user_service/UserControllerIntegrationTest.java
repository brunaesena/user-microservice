package com.mv.bruna.user_service;

import com.mv.bruna.user_service.dto.TaskInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.bruna.user_service.entity.User;
import com.mv.bruna.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @TestConfiguration
    static class RestTemplateTestConfig {
        @Bean
        @Primary
        public RestTemplate restTemplate() {
            return Mockito.mock(RestTemplate.class);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso via API")
    void shouldCreateUserSuccessfully() throws Exception {
        String json = "{\"name\": \"João\", \"email\": \"joao@email.com\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar usuário com email duplicado")
    void shouldReturnErrorWhenDuplicateEmail() throws Exception {
        userRepository.save(new User(null, "Maria", "maria@email.com"));

        String json = "{\"name\": \"Outra Maria\", \"email\": \"maria@email.com\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Email já cadastrado"));
    }
    @Test
    @DisplayName("Deve atualizar um usuário existente via API")
    void shouldUpdateUserSuccessfully() throws Exception {
        User user = userRepository.save(new User(null, "Antigo Nome", "antigo@email.com"));

        String updateJson = "{\"name\": \"Nome Atualizado\", \"email\": \"antigo@email.com\"}";

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"))
                .andExpect(jsonPath("$.email").value("antigo@email.com"));
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso via API")
    void shouldDeleteUserSuccessfully() throws Exception {
        User user = userRepository.save(new User(null, "Usuário Para Deletar", "delete@email.com"));
        TaskInfoDTO[] mockTasks = new TaskInfoDTO[] {
                new TaskInfoDTO(1L, "COMPLETED") // status aceitável para deletar
        };
        Mockito.when(restTemplate.getForEntity(
                Mockito.eq("http://task-service:8080/api/tasks/user/" + user.getId()),
                Mockito.eq(TaskInfoDTO[].class))
        ).thenReturn(new ResponseEntity<>(mockTasks, HttpStatus.OK));
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve listar usuários via API")
    void shouldListUsersSuccessfully() throws Exception {
        userRepository.save(new User(null, "User 1", "user1@email.com"));
        userRepository.save(new User(null, "User 2", "user2@email.com"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}