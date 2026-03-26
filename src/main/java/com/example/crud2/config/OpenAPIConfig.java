package com.example.crud2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@OpenAPIDefinition(info = @Info(
        title = "Crud2",
        version = "1.0.0",
        description = "Приложение для обработки клиентов, заказов и продуктов"
))
public class OpenAPIConfig {
}
