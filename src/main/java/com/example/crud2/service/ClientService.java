package com.example.crud2.service;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.entity.ClientEntity;
import com.example.crud2.exception.*;
import com.example.crud2.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    private ClientDto convertToDto(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());

        return dto;
    }

    private ClientEntity convertToEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        ClientEntity entity = new ClientEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        return entity;
    }

    @Transactional
    public ClientDto createUser(ClientDto clientDto) {
        if (clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new DuplicateEmailException(clientDto.getEmail());
        }

        ClientEntity entity = convertToEntity(clientDto);

        ClientEntity savedEntity = clientRepository.save(entity);

        return convertToDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public ClientDto getUserById(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return convertToDto(entity);
    }

    @Transactional
    public ClientDto updateUser(Long id, ClientDto clientDto) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (!entity.getEmail().equals(clientDto.getEmail()) &&
                clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new DuplicateEmailException(clientDto.getEmail());
        }

        entity.setFirstName(clientDto.getFirstName());
        entity.setLastName(clientDto.getLastName());
        entity.setEmail(clientDto.getEmail());
        entity.setPhone(clientDto.getPhone());

        ClientEntity updatedEntity = clientRepository.save(entity);

        return convertToDto(updatedEntity);
    }

    @Transactional
    public void deleteUser(Long id) {
        ClientEntity user = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (user.getOrders() != null && !user.getOrders().isEmpty()) {
            throw new ClientDeletionException(id, user.getOrders().size());
        }

        clientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ClientDto> getAllUsers(
            String firstName,
            String lastName,
            String email,
            String phone,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        ClientEntity exampleUser = new ClientEntity();
        exampleUser.setFirstName(firstName);
        exampleUser.setLastName(lastName);
        exampleUser.setEmail(email);
        exampleUser.setPhone(phone);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        Example<ClientEntity> example = Example.of(exampleUser, matcher);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientEntity> usersPage = clientRepository.findAll(example, pageable);

        return usersPage.map(this::convertToDto);
    }
}