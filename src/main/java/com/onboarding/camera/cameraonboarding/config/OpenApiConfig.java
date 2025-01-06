package com.onboarding.camera.cameraonboarding.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Selahattin",
                        email = "selahattinsert25@gmail.com"
                ),
                description = "OpenApi documentation for Camera Onboarding",
                title = "OpenApi specification",
                version = "1.0"
        ),
        servers = @Server(
                description = "Local ENV",
                url = "http://localhost:8080"
        )
)
public class OpenApiConfig {
}