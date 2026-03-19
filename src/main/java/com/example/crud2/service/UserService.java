package com.example.crud2.service;

import com.example.crud2.dto.UserDto;
import com.example.crud2.entity.UserEntity;
import com.example.crud2.exception.*;
import com.example.crud2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserDto convertToDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());

        return dto;
    }

    private UserEntity convertToEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        return entity;
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException(userDto.getEmail());
        }

        UserEntity entity = convertToEntity(userDto);

        UserEntity savedEntity = userRepository.save(entity);

        return convertToDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return convertToDto(entity);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!entity.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException(userDto.getEmail());
        }

        entity.setFirstName(userDto.getFirstName());
        entity.setLastName(userDto.getLastName());
        entity.setEmail(userDto.getEmail());
        entity.setPhone(userDto.getPhone());

        UserEntity updatedEntity = userRepository.save(entity);

        return convertToDto(updatedEntity);
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getOrders() != null && !user.getOrders().isEmpty()) {
            throw new UserDeletionException(id, user.getOrders().size());
        }

        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(
            String firstName,
            String lastName,
            String email,
            String phone,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        UserEntity exampleUser = new UserEntity();
        exampleUser.setFirstName(firstName);
        exampleUser.setLastName(lastName);
        exampleUser.setEmail(email);
        exampleUser.setPhone(phone);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        Example<UserEntity> example = Example.of(exampleUser, matcher);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> usersPage = userRepository.findAll(example, pageable);

        return usersPage.map(this::convertToDto);
    }
}