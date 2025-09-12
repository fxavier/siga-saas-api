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