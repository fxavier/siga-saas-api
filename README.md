# siga-saas-api
# Spring Boot Modulith Application

## 🏗️ Arquitetura

Aplicação monolítica modular implementando:
- **Spring Modulith** para modularização
- **Domain-Driven Design (DDD)**
- **Arquitetura Hexagonal**
- **Lombok** para redução de boilerplate
- **MapStruct** para mapeamento de DTOs

## 📦 Estrutura de Módulos
src/main/java/com/project/ 
# Classes base DDD
├── common/          # Utilitários compartilhados # Value Objects compartilhados
└── modules/         # Bounded Contexts
└── example/     # Exemplo de módulo
├── domain/      # Lógica de negócio
├── application/ # Casos de uso
├── infrastructure/ # Implementações
└── interfaces/  # Controllers REST

## 🚀 Quick Start

### Pré-requisitos
- Java 21
- Maven 3.8+
- Lombok plugin na IDE

### Executar aplicação
```bash
# Desenvolvimento
mvn spring-boot:run -Dspring.profiles.active=dev

# Testes
mvn test

# Build
mvn clean package