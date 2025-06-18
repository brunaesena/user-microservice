package com.mv.bruna.user_service.service;

import com.mv.bruna.user_service.dto.TaskInfoDTO;
import com.mv.bruna.user_service.dto.UserDTO;
import com.mv.bruna.user_service.entity.User;
import com.mv.bruna.user_service.exception.BusinessRuleException;
import com.mv.bruna.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${task.service.url}")
    private String taskServiceUrl;


    public List<UserDTO> listAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public UserDTO create(UserDTO dto) {
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado");
            }

            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());

            User savedUser = userRepository.save(user);
            return toDTO(savedUser);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar usuário");
        }
    }


    public UserDTO update(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return toDTO(userRepository.save(user));
    }

    public void delete(Long id) throws Exception {
        try {
            if (!userRepository.existsById(id)) {
                throw new EntityNotFoundException("Usuário não encontrado");
            }

            String url = taskServiceUrl + "/api/tasks/user/" + id;
            ResponseEntity<TaskInfoDTO[]> response = restTemplate.getForEntity(url, TaskInfoDTO[].class);
            TaskInfoDTO[] tasks = response.getBody();

            boolean hasBlockedTasks = Arrays.stream(tasks)
                    .anyMatch(task -> task.getStatus().equals("PENDING") || task.getStatus().equals("IN_PROGRESS"));

            if (hasBlockedTasks) {
                throw new BusinessRuleException("Usuário não pode ser excluído pois possui tarefas pendentes ou em andamento.");
            }
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }
    }


    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}