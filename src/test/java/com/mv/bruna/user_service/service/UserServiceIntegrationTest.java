//package com.mv.bruna.user_service.service;
//
//import com.mv.bruna.user_service.dto.UserDTO;
//import com.mv.bruna.user_service.entity.User;
//import com.mv.bruna.user_service.exception.CustomHttpException;
//import com.mv.bruna.user_service.repository.UserRepository;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
//public class UserServiceIntegrationTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @Order(1)
//    void shouldCreateUserSuccessfully() {
//        UserDTO dto = new UserDTO(null, "João", "joao@email.com");
//        UserDTO created = userService.create(dto);
//
//        assertNotNull(created.getId());
//        assertEquals("João", created.getName());
//        assertEquals("joao@email.com", created.getEmail());
//    }
//
//    @Test
//    @Order(2)
//    void shouldNotAllowDuplicateEmail() {
//        UserDTO dto = new UserDTO(null, "Maria", "joao@email.com"); // mesmo email do teste anterior
//
//        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> userService.create(dto));
//        assertTrue(ex.getErrorResponse().getErrorMessage().contains("Email já cadastrado"));
//    }
//
//    @Test
//    @Order(3)
//    void shouldUpdateUser() {
//        User user = new User(null, "Carlos", "carlos@email.com");
//        user = userRepository.save(user);
//
//        UserDTO dto = new UserDTO(null, "Carlos Atualizado", "carlos@email.com");
//        UserDTO updated = userService.update(user.getId(), dto);
//
//        assertEquals("Carlos Atualizado", updated.getName());
//    }
//
//    @Test
//    @Order(4)
//    void shouldDeleteUserWithoutTasks() throws Exception {
//        User user = new User(null, "Sem Tarefa", "sem@email.com");
//        user = userRepository.save(user);
//
//        userService.delete(user.getId());
//
//        assertFalse(userRepository.existsById(user.getId()));
//    }
//
//    @Test
//    @Order(5)
//    void shouldListAllUsers() {
//        List<UserDTO> users = userService.listAll();
//        assertFalse(users.isEmpty());
//    }
//}
