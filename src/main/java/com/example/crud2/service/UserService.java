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

    // ========== 4. deleteUser - УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ==========
    @Transactional
    public void deleteUser(Long id) {
        // Проверяем, существует ли пользователь
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Проверяем, есть ли у пользователя заказы
        if (user.getOrders() != null && !user.getOrders().isEmpty()) {
            throw new UserDeletionException(id, user.getOrders().size());
        }

        // Удаляем пользователя
        userRepository.deleteById(id);
    }

    // ========== 5. getAllUsers - ПОЛУЧЕНИЕ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ С ФИЛЬТРАЦИЕЙ ==========
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

        // Создаем объект-пример для фильтрации
        UserEntity exampleUser = new UserEntity();
        exampleUser.setFirstName(firstName);
        exampleUser.setLastName(lastName);
        exampleUser.setEmail(email);
        exampleUser.setPhone(phone);

        // Настройка матчера
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)  // LIKE %...%
                .withIgnoreCase()                                            // регистронезависимо
                .withIgnoreNullValues();                                    // игнорируем null

        Example<UserEntity> example = Example.of(exampleUser, matcher);

        // Сортировка
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        // Пагинация
        Pageable pageable = PageRequest.of(page, size, sort);

        // Запрос в БД
        Page<UserEntity> usersPage = userRepository.findAll(example, pageable);

        // Конвертация каждой Entity в DTO и возврат
        return usersPage.map(this::convertToDto);
    }
}