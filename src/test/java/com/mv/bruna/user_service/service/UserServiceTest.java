package com.mv.bruna.user_service.service;

import com.mv.bruna.user_service.dto.TaskInfoDTO;
import com.mv.bruna.user_service.dto.UserDTO;
import com.mv.bruna.user_service.entity.User;
import com.mv.bruna.user_service.exception.CustomHttpException;
import com.mv.bruna.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    private final String TASK_URL = "http://localhost:8081";

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
        ReflectionTestUtils.setField(userService, "taskServiceUrl", TASK_URL);
    }

    @Test
    void listAll_ShouldReturnUserList() throws Exception {
        List<User> users = List.of(new User(1L, "João", "joao@email.com"));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.listAll();

        assertEquals(1, result.size());
        assertEquals("João", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() throws Exception {
        User user = new User(1L, "Maria", "maria@email.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.findById(1L);

        assertEquals("Maria", result.getName());
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> userService.findById(1L));
        assertEquals("Usuário não encontrado", exception.getErrorResponse().getErrorMessage());
    }

    @Test
    void create_ShouldReturnSavedUser_WhenEmailIsUnique() throws Exception {
        UserDTO dto = new UserDTO(null, "Lucas", "lucas@email.com");
        User user = new User(null, "Lucas", "lucas@email.com");
        User savedUser = new User(1L, "Lucas", "lucas@email.com");

        Mockito.when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.create(dto);

        assertEquals("Lucas", result.getName());
        assertNotNull(result.getId());
    }

    @Test
    void create_ShouldThrowException_WhenEmailExists() throws Exception {
        UserDTO dto = new UserDTO(null, "Lucas", "lucas@email.com");
        Mockito.when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> userService.create(dto));
        assertEquals("Email já cadastrado", exception.getErrorResponse().getErrorMessage());
    }

    @Test
    void update_ShouldReturnUpdatedUser_WhenExists() throws Exception {
        UserDTO dto = new UserDTO(null, "Novo Nome", "novo@email.com");
        User existingUser = new User(1L, "Antigo", "antigo@email.com");
        User updatedUser = new User(1L, "Novo Nome", "novo@email.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.update(1L, dto);

        assertEquals("Novo Nome", result.getName());
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() throws Exception {
        UserDTO dto = new UserDTO(null, "Novo Nome", "novo@email.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> userService.update(1L, dto));
        assertEquals("Usuário não encontrado", exception.getErrorResponse().getErrorMessage());
    }

    @Test
    void delete_ShouldDeleteUser_WhenNoBlockedTasks() throws Exception {
        userService.setRestTemplate(restTemplate);
        User user = new User(1L, "Pedro", "pedro@email.com");
        TaskInfoDTO[] tasks = new TaskInfoDTO[] {
                new TaskInfoDTO(1L, "COMPLETED")
        };

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(TaskInfoDTO[].class)))
                .thenReturn(new ResponseEntity<>(tasks, HttpStatus.OK));

        userService.delete(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenHasPendingTasks() throws Exception {
        TaskInfoDTO[] tasks = { new TaskInfoDTO(1L,  "IN_PROGRESS") };
        userService.setRestTemplate(restTemplate);
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(TaskInfoDTO[].class)))
                .thenReturn(new ResponseEntity<>(tasks, HttpStatus.OK));

        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> userService.delete(1L));
        assertTrue(exception.getErrorResponse().getErrorMessage().contains("tarefas pendentes"));
    }
}
