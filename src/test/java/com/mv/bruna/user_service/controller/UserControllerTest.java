//package com.mv.bruna.user_service.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.mv.bruna.user_service.dto.UserDTO;
//import com.mv.bruna.user_service.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserService userService;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setup() {
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void listAll_ShouldReturnUsers() throws Exception {
//        List<UserDTO> users = List.of(new UserDTO(1L, "João", "joao@email.com"));
//        Mockito.when(userService.listAll()).thenReturn(users);
//
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].name").value("João"));
//    }
//
//    @Test
//    void getById_ShouldReturnUser() throws Exception {
//        UserDTO user = new UserDTO(1L, "Maria", "maria@email.com");
//        Mockito.when(userService.findById(1L)).thenReturn(user);
//
//        mockMvc.perform(get("/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Maria"));
//    }
//
//    @Test
//    void create_ShouldReturnCreatedUser() throws Exception {
//        UserDTO userInput = new UserDTO(null, "Lucas", "lucas@email.com");
//        UserDTO userOutput = new UserDTO(1L, "Lucas", "lucas@email.com");
//
//        Mockito.when(userService.create(any(UserDTO.class))).thenReturn(userOutput);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userInput)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("Lucas"));
//    }
//
//    @Test
//    void update_ShouldReturnUpdatedUser() throws Exception {
//        UserDTO userInput = new UserDTO(null, "Novo Nome", "novo@email.com");
//        UserDTO userOutput = new UserDTO(1L, "Novo Nome", "novo@email.com");
//
//        Mockito.when(userService.update(eq(1L), any(UserDTO.class))).thenReturn(userOutput);
//
//        mockMvc.perform(put("/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userInput)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Novo Nome"));
//    }
//
//    @Test
//    void delete_ShouldReturnNoContent() throws Exception {
//        mockMvc.perform(delete("/users/1"))
//                .andExpect(status().isNoContent());
//
//        Mockito.verify(userService).delete(1L);
//    }
//}
