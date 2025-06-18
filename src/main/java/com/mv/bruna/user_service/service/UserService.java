package com.mv.bruna.user_service.service;

import com.mv.bruna.user_service.dto.UserDTO;
import com.mv.bruna.user_service.entity.User;
import com.mv.bruna.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return toDTO(userRepository.save(user));
    }

    public UserDTO update(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return toDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        userRepository.deleteById(id);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}