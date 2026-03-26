package com.example.crud2.service;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.dto.mapper.ClientMapper;
import com.example.crud2.entity.ClientEntity;
import com.example.crud2.exception.*;
import com.example.crud2.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        log.info("Создание нового клиента: email={}, firstName={}, lastName={}",
                clientDto.getEmail(), clientDto.getFirstName(), clientDto.getLastName());

        try {
            log.debug("Проверка существования email: {}", clientDto.getEmail());
            if (clientRepository.existsByEmail(clientDto.getEmail())) {
                log.warn("Попытка создать клиента с уже существующим email: {}", clientDto.getEmail());
                throw new DuplicateEmailException(clientDto.getEmail());
            }

            log.debug("Преобразование DTO в Entity и сохранение");
            ClientEntity entity = clientMapper.toEntity(clientDto);
            ClientEntity savedEntity = clientRepository.save(entity);

            log.info("Клиент успешно создан: id={}, email={}", savedEntity.getId(), savedEntity.getEmail());
            return clientMapper.toDto(savedEntity);

        } catch (DuplicateEmailException e) {
            log.error("Ошибка создания клиента: email уже существует", e);
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании клиента: email={}", clientDto.getEmail(), e);
            throw e;
        }
    }

    @Transactional
    public ClientDto updateClient(Long id, ClientDto clientDto) {
        log.info("Обновление клиента: id={}, email={}", id, clientDto.getEmail());

        try {
            ClientEntity entity = clientRepository.findById(id)
                    .orElseThrow(() -> new ClientNotFoundException(id));

            log.debug("Найден клиент для обновления: текущий email={}", entity.getEmail());

            if (!entity.getEmail().equals(clientDto.getEmail())
                    && clientRepository.existsByEmail(clientDto.getEmail())) {
                log.warn("Попытка изменить email на уже существующий: {}", clientDto.getEmail());
                throw new DuplicateEmailException(clientDto.getEmail());
            }

            clientMapper.updateEntity(entity, clientDto);
            ClientEntity updatedEntity = clientRepository.save(entity);

            log.info("Клиент обновлен: id={}, email={}", updatedEntity.getId(), updatedEntity.getEmail());
            return clientMapper.toDto(updatedEntity);

        } catch (ClientNotFoundException | DuplicateEmailException e) {
            log.error("Ошибка обновления клиента: id={}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обновлении клиента: id={}", id, e);
            throw e;
        }
    }
    @Transactional(readOnly = true)
    public ClientDto getClientById(Long id) {
        log.debug("Запрос клиента по id: {}", id);

        try {
            ClientEntity entity = clientRepository.findById(id)
                    .orElseThrow(() -> new ClientNotFoundException(id));

            log.debug("Клиент найден: id={}, email={}", entity.getId(), entity.getEmail());
            return clientMapper.toDto(entity);

        } catch (ClientNotFoundException e) {
            log.error("Клиент не найден: id={}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при поиске клиента: id={}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deleteClient(Long id) {
        log.info("Удаление клиента: id={}", id);

        try {
            ClientEntity client = clientRepository.findById(id)
                    .orElseThrow(() -> new ClientNotFoundException(id));

            if (client.getOrders() != null && !client.getOrders().isEmpty()) {
                log.warn("Попытка удалить клиента с активными заказами: id={}, количество заказов={}",
                        id, client.getOrders().size());
                throw new ClientDeletionException(id, client.getOrders().size());
            }

            clientRepository.deleteById(id);
            log.info("Клиент удален: id={}", id);

        } catch (ClientNotFoundException | ClientDeletionException e) {
            log.error("Ошибка удаления клиента: id={}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при удалении клиента: id={}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<ClientDto> getAllClients(
            String firstName, String lastName, String email, String phone,
            int page, int size, String sortBy, String sortDirection) {

        log.debug("Запрос списка клиентов: page={}, size={}, sortBy={}, sortDir={}, filters=[firstName={}, lastName={}, email={}, phone={}]",
                page, size, sortBy, sortDirection, firstName, lastName, email, phone);

        try {
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

            log.debug("Найдено клиентов: {}", clientsPage.getTotalElements());
            return clientsPage.map(clientMapper::toDto);

        } catch (Exception e) {
            log.error("Ошибка получения списка клиентов", e);
            throw e;
        }
    }
}