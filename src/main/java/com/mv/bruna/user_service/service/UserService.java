package com.mv.bruna.user_service.service;

import com.mv.bruna.user_service.dto.TaskInfoDTO;
import com.mv.bruna.user_service.dto.UserDTO;
import com.mv.bruna.user_service.entity.User;
import com.mv.bruna.user_service.exception.BusinessRuleException;
import com.mv.bruna.user_service.exception.CustomHttpException;
import com.mv.bruna.user_service.exception.ErrorResponse;
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

    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public List<UserDTO> listAll() {
        try {
            return userRepository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao listar usuários",
                    e.getMessage()
            );
            throw new CustomHttpException(error);
        }
    }

    public UserDTO findById(Long id) {
        try {
            return userRepository.findById(id)
                    .map(this::toDTO)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        } catch (EntityNotFoundException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "EntityNotFoundException"
            );
            throw new CustomHttpException(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao buscar usuário por ID",
                    e.getMessage()
            );
            throw new CustomHttpException(error);
        }
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
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "IllegalArgumentException"
            );
            throw new CustomHttpException(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao criar usuário",
                    e.getMessage()
            );
            throw new CustomHttpException(error);
        }
    }

    public UserDTO update(Long id, UserDTO dto) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            return toDTO(userRepository.save(user));

        } catch (EntityNotFoundException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "EntityNotFoundException"
            );
            throw new CustomHttpException(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao atualizar usuário",
                    e.getMessage()
            );
            throw new CustomHttpException(error);
        }
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

        } catch (BusinessRuleException | EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    ex.getMessage(),
                    ex.getClass().getSimpleName()
            );
            throw new CustomHttpException(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno inesperado",
                    ex.getMessage()
            );
            throw new CustomHttpException(errorResponse);
        }
    }


    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}