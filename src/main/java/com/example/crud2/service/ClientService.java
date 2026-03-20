package com.example.crud2.service;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.dto.mapper.ClientMapper;
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
    private final ClientMapper clientMapper;

    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        if (clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new DuplicateEmailException(clientDto.getEmail());
        }

        ClientEntity entity = clientMapper.toEntity(clientDto);

        ClientEntity savedEntity = clientRepository.save(entity);

        return clientMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public ClientDto getClientById(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return clientMapper.toDto(entity);
    }

    @Transactional
    public ClientDto updateClient(Long id, ClientDto clientDto) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (!entity.getEmail().equals(clientDto.getEmail()) &&
                clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new DuplicateEmailException(clientDto.getEmail());
        }

        clientMapper.updateEntity(entity, clientDto);

        ClientEntity updatedEntity = clientRepository.save(entity);

        return clientMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteClient(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (client.getOrders() != null && !client.getOrders().isEmpty()) {
            throw new ClientDeletionException(id, client.getOrders().size());
        }

        clientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ClientDto> getAllClients(
            String firstName,
            String lastName,
            String email,
            String phone,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        ClientEntity exampleClient = new ClientEntity();
        exampleClient.setFirstName(firstName);
        exampleClient.setLastName(lastName);
        exampleClient.setEmail(email);
        exampleClient.setPhone(phone);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        Example<ClientEntity> example = Example.of(exampleClient, matcher);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientEntity> clientsPage = clientRepository.findAll(example, pageable);

        return clientsPage.map(clientMapper::toDto);
    }
}