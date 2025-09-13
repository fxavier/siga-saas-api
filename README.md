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

# Common Module - Usage Examples

## 1. Value Objects

### Email
```java
// Creating an email
Email email = new Email("user@example.com");

// Accessing email parts
String domain = email.getDomain(); // "example.com"
String localPart = email.getLocalPart(); // "user"

// Validation
boolean isValid = Email.isValid("test@domain.com"); // true
```

### Phone
```java
// Creating a phone number (Mozambican mobile)
Phone phone = new Phone("847123456");

// Various input formats are accepted
Phone phone1 = new Phone("84-712-3456");
Phone phone2 = new Phone("+258 84 712 3456");

// Getting formatted output
String formatted = phone.getFormatted(); // "+258 84 712 3456"
Phone.PhoneType type = phone.getType(); // MOBILE
```

### Money
```java
// Creating money amounts
Money price = new Money(new BigDecimal("100.50"));
Money discount = new Money(new BigDecimal("10.00"));

// Arithmetic operations
Money total = price.subtract(discount); // 90.50
Money doubled = price.multiply(2); // 201.00
Money sum = price.add(discount); // 110.50

// Comparisons
boolean isPositive = price.isGreaterThanZeruo(); // true
boolean isGreater = price.isGreaterThan(discount); // true
```

### Address
```java
// Creating an address
Address address = Address.builder()
    .street("Av. Eduardo Mondlane, 123")
    .neighborhood("Polana")
    .city("Maputo")
    .province("Maputo")
    .country("Moçambique")
    .build();

// Getting full address
String full = address.getFullAddress();
// "Av. Eduardo Mondlane, 123, Polana, Maputo, Maputo, Moçambique"
```

## 2. Entities and Aggregates

### Creating a Custom Entity
```java
public class Customer extends BaseEntity<CustomerId> {
    private String name;
    private Email email;
    private Phone phone;
    
    // Constructor, getters, setters...
}

public class CustomerId extends BaseId<UUID> {
    public CustomerId(UUID value) {
        super(value);
    }
}
```

### Creating an Aggregate Root
```java
public class Order extends AggregateRoot<OrderId> {
    private CustomerId customerId;
    private Money totalAmount;
    private OrderStatus status;
    private List<OrderItem> items;
    
    // Business logic methods...
    public void addItem(OrderItem item) {
        ValidationUtils.notNull(item, "item");
        this.items.add(item);
        recalculateTotal();
    }
}
```

## 3. Repository Pattern

### Implementing a Repository
```java
@Repository
public interface CustomerRepository extends PagedRepository<Customer, CustomerId> {
    // Custom query methods
    Optional<Customer> findByEmail(Email email);
    List<Customer> findByCity(String city);
}

// Using the repository
@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;
    
    public Page<Customer> findAllCustomers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size,
            Sort.by(Sort.Direction.ASC, "name"));
        return repository.findAll(pageRequest);
    }
}
```

## 4. Specification Pattern

### Creating Specifications
```java
public class CustomerSpecifications {
    
    public static Specification<Customer> hasEmail(String email) {
        return customer -> customer.getEmail().getValue().equals(email);
    }
    
    public static Specification<Customer> isActive() {
        return customer -> customer.getStatus() == CustomerStatus.ACTIVE;
    }
    
    public static Specification<Customer> inCity(String city) {
        return customer -> customer.getAddress().getCity().equals(city);
    }
}

// Combining specifications
Specification<Customer> spec = CustomerSpecifications.isActive()
    .and(CustomerSpecifications.inCity("Maputo"));

// Using with repository
Page<Customer> customers = repository.findAll(spec, pageRequest);
```

## 5. Validation Utils

### Using Validation Utilities
```java
public class ProductService {
    
    public Product createProduct(CreateProductDTO dto) {
        // Validate inputs
        ValidationUtils.notEmpty(dto.getName(), "name");
        ValidationUtils.positive(dto.getPrice(), "price");
        ValidationUtils.minLength(dto.getDescription(), 10, "description");
        ValidationUtils.maxLength(dto.getDescription(), 500, "description");
        
        // Create product...
        return product;
    }
    
    public void validateNuit(String nuit) {
        ValidationUtils.validNuit(nuit, "nuit");
    }
}
```

## 6. Exception Handling

### Using Custom Exceptions
```java
@Service
public class OrderService {
    
    public Order findOrder(OrderId id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order", id));
    }
    
    public void cancelOrder(OrderId id) {
        Order order = findOrder(id);
        
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("CANCEL_DELIVERED_ORDER",
                "Cannot cancel an already delivered order");
        }
        
        order.cancel();
        repository.save(order);
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorDTO error = ErrorDTO.builder()
            .code("NOT_FOUND")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidation(ValidationException ex) {
        ErrorDTO error = ErrorDTO.builder()
            .code("VALIDATION_ERROR")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

## 7. DTOs and Mappers

### Creating DTOs
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerDTO extends BaseDTO {
    private String name;
    private String email;
    private String phone;
    private AddressDTO address;
}
```

### Creating Mappers with MapStruct
```java
@Mapper(config = MapStructConfig.class)
public interface CustomerMapper extends BaseMapper<Customer, CustomerDTO> {
    
    @Override
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "phone.value", target = "phone")
    CustomerDTO toDto(Customer entity);
    
    @Override
    @Mapping(source = "email", target = "email", 
            qualifiedByName = "stringToEmail")
    @Mapping(source = "phone", target = "phone", 
            qualifiedByName = "stringToPhone")
    Customer toEntity(CustomerDTO dto);
    
    @Named("stringToEmail")
    default Email stringToEmail(String value) {
        return value != null ? new Email(value) : null;
    }
    
    @Named("stringToPhone")
    default

 Resumo da Implementação - Issue #6: Aspectos Transversais
✅ Tarefas Concluídas:
1. Logging Estruturado (Logback/SLF4J) ✅

LoggingAspect.java - AOP para logging automático
Loggable.java - Anotação customizada para marcar métodos
logback-spring.xml - Configuração com formato JSON para produção
Diferentes níveis de log por ambiente (dev, staging, prod)
Correlation IDs para rastreamento de requisições

2. Auditoria de Entidades ✅

AuditableEntity.java - Classe base com campos de auditoria
AuditEntityListener.java - JPA listener automático
AuditEvent.java - Entidade para eventos de auditoria
AuditService.java - Serviço assíncrono para registrar eventos
Rastreamento completo de CRUD, login, logout e acessos negados

 Principais Características:

Logging:

Logs estruturados em JSON para produção
Correlation IDs para rastreamento
Diferentes níveis por ambiente
Sanitização de dados sensíveis


Segurança:

JWT para autenticação stateless
CORS configurável
Proteção por padrão de endpoints
Suporte a multi-tenancy


Auditoria:

Registro automático de alterações
Eventos assíncronos
Metadados completos (IP, user agent, etc.)
Builder pattern para eventos customizados


Rate Limiting:

Token Bucket algorithm
Configurável por cliente
Headers informativos
Reset e controle de buckets


Métricas:

Contadores, gauges e timers
Métricas de negócio customizadas
Export para Prometheus
Tags para filtragem


Health Checks:

Status de componentes
Alertas de recursos (disco/memória)
Verificação de serviços externos
Formato padronizado



🎯 Critérios de Aceitação Atendidos:

✅ Logs estruturados em JSON
✅ Auditoria automática de alterações
✅ Endpoints protegidos por padrão
✅ Métricas expostas para Prometheus
✅ Health checks funcionais

📝 Próximos Passos Recomendados:

Adicionar testes unitários para todos os componentes
Configurar profiles específicos para diferentes ambientes
Implementar cache com Redis para rate limiting distribuído
Adicionar documentação OpenAPI/Swagger
Configurar alertas baseados nas métricas

A implementação segue os mesmos padrões de qualidade e estrutura do módulo common, com foco em:

Separação clara de responsabilidades
Código limpo e bem documentado
Padrões de design apropriados
Configuração flexível
Performance e escalabilidade

3. Spring Security Base ✅

SecurityConfiguration.java - Configuração completa do Spring Security
JwtAuthenticationFilter.java - Filtro JWT para autenticação
JwtTokenProvider.java - Geração e validação de tokens
JwtAuthenticationEntryPoint.java - Tratamento de erros de autenticação
SecurityContext.java e implementação - Contexto de segurança

4. Rate Limiting ✅

RateLimiter.java - Serviço com algoritmo Token Bucket
RateLimitInterceptor.java - Interceptor HTTP
Configuração por IP, usuário ou API key
Headers HTTP com informações de limite

5. Métricas (Micrometer) ✅

MetricsConfiguration.java - Configuração do Micrometer
CustomMetrics.java - Métricas customizadas de negócio
Integração com Prometheus
Métricas de JVM, sistema e aplicação

6. Health Checks ✅

CustomHealthIndicator.java - Indicadores de saúde customizados
Verificação de banco de dados
Verificação de serviços externos
Monitoramento de disco e memória
Status detalhado dos componentes

Implementação de Aspectos Transversais
Arquietura
┌─────────────────────────────────────────┐
│           Balanceador de Carga          │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│      Spring Security + JWT              │
│      Limitação de Taxa (Custom/Redis)   │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│         Núcleo da Aplicação             │
│  - Logging AOP                          │
│  - Serviço de Auditoria                 │
│  - Lógica de Negócio                    │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│        Camada de Dados                  │
│  - PostgreSQL (Prod/Staging)            │
│  - H2 (Dev/Test)                        │
│  - Cache Redis (Opcional)               │
└─────────────────────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│      Monitorização & Observabilidade    │
│  - Métricas Prometheus                  │
│  - Verificações de Saúde                │
│  - Gestor de Alertas                    │
│  - Logs Estruturados (JSON)             │
└─────────────────────────────────────────┘
 Componentes Implementados
1. Logging Estruturado (Logback/SLF4J)
Componentes:

LoggingAspect.java - Aspeto AOP para logging automático de métodos
Loggable.java - Anotação personalizada para marcar métodos a registar
logback-spring.xml - Configuração Logback com saída JSON para produção
AsyncConfiguration.java - Propagação MDC para operações assíncronas

Funcionalidades:

IDs de correlação para rastreamento de pedidos
Suporte MDC (Mapped Diagnostic Context)
Diferentes níveis de log por ambiente
Sanitização automática de dados sensíveis
Monitorização de desempenho (regista métodos lentos)
Saída JSON estruturada para produção

@Loggable(level = LogLevel.INFO)
public void metodoNegocio() {
    // A execução do método será automaticamente registada
}

 Sistema de Auditoria
Componentes:

AuditEvent.java - Entidade de domínio para eventos de auditoria
AuditableEntity.java - Entidade base com campos de auditoria
AuditService.java - Serviço assíncrono para registo de eventos
AuditEntityListener.java - Listener JPA para auditoria automática
AuditEventRepository.java - Interface do repositório
AuditEventEntity.java - Entidade JPA com suporte PostgreSQL JSONB

Funcionalidades:

Auditoria automática de alterações em entidades
Registo assíncrono para evitar impacto no desempenho
Rastreamento de ID de correlação
Suporte multi-inquilino
Tipos de eventos abrangentes (CREATE, UPDATE, DELETE, LOGIN, etc.)
Políticas de retenção e limpeza automática

. Segurança (Spring Security + JWT)
Componentes:

SecurityConfiguration.java - Configuração principal de segurança
JwtTokenProvider.java - Geração e validação de tokens JWT
JwtAuthenticationFilter.java - Filtro de autenticação de pedidos
JwtAuthenticationEntryPoint.java - Tratamento de erros de autenticação
SecurityContext.java - Interface para aceder ao utilizador atual
SecurityContextImpl.java - Implementação Spring Security

Funcionalidades:

Autenticação JWT sem estado
Suporte a token de renovação
Controlo de acesso baseado em papéis (RBAC)
Configuração CORS
Cabeçalhos de segurança para produção
Suporte multi-inquilino

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000 # 24 horas
    refresh-expiration: 604800000 # 7 dias

Limitação de Taxa
Componentes:

RateLimiter.java - Implementação personalizada token bucket (sem deps externas)
DistributedRateLimiter.java - Limitação de taxa distribuída baseada em Redis
RateLimitInterceptor.java - Interceptor HTTP
RateLimitConfiguration.java - Configuração Spring

Algoritmos Implementados:

Token Bucket - Algoritmo principal com mecanismo de reabastecimento
Sliding Window - Mais preciso para limitação de taxa exata
Fixed Window - Simples e eficiente para casos de uso básicos

Funcionalidades:

Múltiplas estratégias de limitação de taxa
Limitação por utilizador, por IP e por chave API
Regras configuráveis via application.yml
Cabeçalhos HTTP com informação de limite de taxa
Suporte distribuído via Redis (opcional)

app:
  rate-limiting:
    enabled: true
    default-rule:
      capacity: 100
      tokens: 100
      duration: 60
    custom-rules:
      utilizador-premium:
        capacity: 5000
        tokens: 5000
        duration: 60


 Métricas e Monitorização (Micrometer)
Componentes:

MetricsConfiguration.java - Configuração Micrometer
CustomMetrics.java - Métricas específicas da aplicação
AlertConfiguration.java - Regras de alerta e notificações
prometheus-alerts.yml - Regras de alerta Prometheus

Métricas Recolhidas:

Sistema: CPU, Memória, Disco, Rede
JVM: Heap, GC, Threads
Aplicação: Contagem de pedidos, Taxa de erro, Tempo de resposta
Negócio: Registos de utilizadores, Tentativas de login, Utilizadores ativos
Base de Dados: Pool de conexões, Desempenho de consultas

Regras de Alerta:

Uso elevado de CPU (>80% aviso, >95% crítico)
Uso elevado de memória (>85%)
Taxa de erro elevada (>10/min aviso, >50/min crítico)
Tempo de resposta lento (P95 >1s)
Esgotamento do pool de conexões (>90%)
Tentativas de login falhadas excessivas (>50 em 15min)

6. Verificações de Saúde
Componentes:

CustomHealthIndicator.java - Verificações de saúde abrangentes
AlertHealthIndicator.java - Saúde do sistema de alertas

Verificações de Saúde:

Conectividade da base de dados
Disponibilidade do Redis
Estado dos serviços externos
Espaço em disco
Uso de memória
Estado do sistema de alertas

Endpoints:

/actuator/health - Estado geral de saúde
/actuator/health/liveness - Sonda de vivacidade Kubernetes
/actuator/health/readiness - Sonda de prontidão Kubernetes

7. Documentação (OpenAPI/Swagger)
Componentes:

SwaggerConfiguration.java - Configuração OpenAPI

Funcionalidades:

Documentação API gerada automaticamente
Autenticação JWT no Swagger UI
Agrupamento de APIs (Pública, Admin, Utilizador, etc.)
Múltiplas configurações de servidor
Exemplos de pedido/resposta

Acesso:

Desenvolvimento: http://localhost:8080/swagger-ui.html
Documentos API: http://localhost:8080/v3/api-docs

🔧 Perfis de Configuração
Desenvolvimento (application-dev.yml)

Base de dados H2 em memória
Logging de depuração ativado
Swagger UI ativado
Limitação de taxa desativada
Mensagens de erro detalhadas

Staging (application-staging.yml)

Base de dados PostgreSQL
Logging nível info
Swagger UI ativado
Limitação de taxa ativada
Segurança moderada

Produção (application-prod.yml)

PostgreSQL com pool de conexões
Logging estruturado JSON
Swagger UI desativado
Limitação de taxa rigorosa
Segurança máxima
Cache Redis ativado

🚀 Como Começar
Pré-requisitos

Java 17+
Maven 3.6+
PostgreSQL 14+ (para produção/staging)
Redis 6+ (opcional, para funcionalidades distribuídas)

Executar a Aplicação
mvn spring-boot:run -Dspring.profiles.active=dev
Dashboards Grafana
Importar dashboards fornecidos:

Métricas JVM: Dashboard ID 4701
Estatísticas Spring Boot: Dashboard ID 6756
Métricas de Negócio Personalizadas: Usar JSON fornecido

🔒 Considerações de Segurança
Checklist de Produção

 Alterar segredo JWT padrão
 Ativar apenas HTTPS
 Configurar origens CORS adequadas
 Ativar limitação de taxa
 Configurar credenciais adequadas da base de dados
 Configurar password do Redis
 Ativar cabeçalhos de segurança
 Configurar agregação de logs
 Configurar destinatários de alertas

Cabeçalhos de Segurança Aplicados
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
🐛 Resolução de Problemas
Problemas Comuns
Limitação de Taxa Não Funciona

Verificar se está ativada na configuração
Verificar conexão Redis (se usar modo distribuído)
Verificar registo do interceptor

Eventos de Auditoria Não São Registados

Verificar configuração do executor assíncrono
Verificar conexão à base de dados
Rever configurações de transação

Autenticação JWT a Falhar

Verificar configuração da chave secreta
Verificar expiração do token
Rever configurações CORS

Métricas Não Visíveis

Garantir que endpoints actuator estão expostos
Verificar configuração Prometheus
Verificar conectividade de rede

📝 Exemplos de API
Autenticação
bash# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"utilizador","password":"senha"}'

# Usar token
curl http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer <token>"
Cabeçalhos de Limite de Taxa
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1694620800000
🤝 Contribuir

Seguir a estrutura de código existente
Adicionar testes unitários para novas funcionalidades
Atualizar documentação
Garantir que todos os testes passam
Seguir convenções de nomenclatura Java

📄 Licença
Proprietária - SIGA SaaS © 2025
👥 Equipa

Autor: Xavier Nhagumbe
Versão: 1.0.0
Desde: 2025-09-12

📚 Recursos Adicionais

Documentação Spring Security
Documentação Micrometer
JWT.io
Documentação Prometheus
Documentação Grafana
