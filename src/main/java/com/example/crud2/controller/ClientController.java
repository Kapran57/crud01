package com.example.crud2.controller;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(
            summary = "Создать клиента",
            description = "Создает нового клиента и возвращает его данные"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto clientDto) {
        log.info("POST /api/clients - создание клиента: email={}", clientDto.getEmail());

        try {
            ClientDto createdClient = clientService.createClient(clientDto);
            log.info("Клиент создан: id={}, email={}", createdClient.getId(), createdClient.getEmail());
            return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Ошибка создания клиента: email={}", clientDto.getEmail(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Посмотреть клиента",
            description = "Возвращает клиента по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        log.debug("GET /api/clients/{} - запрос клиента", id);

        ClientDto client = clientService.getClientById(id);

        log.debug("Клиент найден: id={}, email={}", client.getId(), client.getEmail());
        return ResponseEntity.ok(client);
    }

    @Operation(
            summary = "Обновить клиента",
            description = "Обновляет клиента по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {
        log.info("PUT /api/clients/{} - обновление клиента: email={}", id, clientDto.getEmail());

        try {
            ClientDto updatedClient = clientService.updateClient(id, clientDto);
            log.info("Клиент обновлен: id={}, email={}", updatedClient.getId(), updatedClient.getEmail());
            return ResponseEntity.ok(updatedClient);
        } catch (Exception e) {
            log.error("Ошибка обновления клиента: id={}, email={}", id, clientDto.getEmail(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Удалить клиента",
            description = "Удаляет клиента по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно удален"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("DELETE /api/clients/{} - удаление клиента", id);

        try {
            clientService.deleteClient(id);
            log.info("Клиент удален: id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка удаления клиента: id={}", id, e);
            throw e;
        }
    }

    @Operation(
            summary = "Список клиентов",
            description = "Возвращает весь список клиентов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Список клиентов успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<Page<ClientDto>> getAllClients(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        log.debug("GET /api/clients - запрос списка клиентов: page={}, size={}, sortBy={}, sortDir={}, filters=[firstName={}, lastName={}, email={}, phone={}]",
                page, size, sortBy, sortDirection, firstName, lastName, email, phone);

        Page<ClientDto> clients = clientService.getAllClients(
                firstName, lastName, email, phone, page, size, sortBy, sortDirection);

        log.debug("Найдено клиентов: {}", clients.getTotalElements());
        return ResponseEntity.ok(clients);
    }
}