package com.mv.bruna.user_service.service;

import com.mv.bruna.user_service.dto.UserDTO;
import com.mv.bruna.user_service.entity.User;
import com.mv.bruna.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

//    @Test
//    void testListAll() {
//        List<User> mockUsers = List.of(
//                new User(1L, "Bruna", "bruna@mv.com", LocalDateTime.now()),
//                new User(2L, "Carlos", "carlos@mv.com", LocalDateTime.now())
//        );
//        when(userRepository.findAll()).thenReturn(mockUsers);
//
//        List<UserDTO> result = userService.listAll();
//
//        assertEquals(2, result.size());
//        assertEquals("Bruna", result.get(0).getName());
//    }

//    @Test
//    void testCreateSuccess() {
//        UserDTO dto = new UserDTO(null, "Bruna", "bruna@mv.com");
//        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
//        when(userRepository.save(any(User.class)))
//                .thenAnswer(invocation -> {
//                    User u = invocation.getArgument(0);
//                    u.setId(1L);
//                    return u;
//                });
//
//        UserDTO result = userService.create(dto);
//
//        assertNotNull(result.getId());
//        assertEquals("Bruna", result.getName());
//    }

    @Test
    void testCreateDuplicateEmail() {
        UserDTO dto = new UserDTO(null, "Bruna", "bruna@mv.com");
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.create(dto));
    }
}
