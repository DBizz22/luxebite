# LuxeBite 🍽️

**LuxeBite** is a Java servlet-based food-ordering & dining e-commerce web application. It provides user registration/authentication, a product catalogue, a shopping cart, and checkout/order management — built on the Jakarta EE servlet stack with a MySQL backend.

## ✨ Features

- **User accounts** — registration, login/logout, password hashing, and "remember me" tokens
- **Security** — authentication & authorization filters, CSRF protection, character-encoding and caching filters
- **Product catalogue** — browse products by category
- **Shopping cart** — add, update, remove, and view cart items
- **Checkout & orders** — place orders, track order items and payment status
- **Validation** — email, phone number (libphonenumber), and ID validation utilities
- **Logging** — structured request logging via SLF4J + Logback

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Web | Jakarta Servlet 6.1, JSP, JSTL |
| Build | Maven (WAR packaging) |
| Database | MySQL (`mysql-connector-j`) |
| Connection pool | HikariCP |
| Security | Spring Security Crypto (password encoding) |
| JSON | Jackson Databind |
| Validation | Apache Commons Validator, libphonenumber |
| Config | dotenv-java |
| Logging | SLF4J + Logback |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers (MySQL) |

## 📁 Project Structure

```
luxeBite/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/dbizz/
    │   │   ├── model/      # User, Product, Order, OrderItem, ...
    │   │   ├── repo/       # DAO interfaces
    │   │   ├── database/   # MySQL repository implementations
    │   │   ├── service/    # UserService, ProductService, OrderService
    │   │   ├── servlet/    # Login, Register, Cart, Checkout, ...
    │   │   ├── filter/     # Auth, CSRF, Caching, Encoding, Logging
    │   │   └── util/       # Email, Phone, Password, Token, Time helpers
    │   ├── resources/      # logback.xml, db.properties
    │   └── webapp/         # WEB-INF/web.xml, JSP views
    └── test/               # JUnit + Testcontainers tests
```

## 🚀 Getting Started

### Prerequisites

- JDK 21+
- Maven 3.9+
- A running MySQL instance

### Configuration

Database credentials are supplied via `luxeBite/src/main/resources/db.properties` (ignored by git for safety). Create it based on your environment, for example:

```properties
db.url=jdbc:mysql://localhost:3306/luxebite
db.username=your_user
db.password=your_password
```

### Build & Run

```bash
cd luxeBite

# Build the WAR
mvn clean package

# Deploy target/luxeBite.war to a Jakarta-compatible servlet container
# (e.g. Tomcat 11 / Jetty 12), or run via your IDE's server integration.
```

### Run tests

```bash
cd luxeBite
mvn test
```

> Tests use Testcontainers and require a running Docker daemon.

## 📝 License

This project is provided as-is for educational and portfolio purposes.
