# Warehouse Management System

## Overview
A comprehensive Warehouse Management System (WMS) built with Java Spring Boot. This application streamlines warehouse operations, including inventory management, order processing, supplier and customer management, and real-time notifications. It is designed for scalability, security, and ease of integration with other enterprise systems.

## Features
- **User Authentication & Authorization**: Secure login with JWT, role-based access control (Admin, User, etc.).
- **Product Management**: CRUD operations for products, categories, and inventory tracking.
- **Supplier & Customer Management**: Manage supplier and customer records, including contact information and transaction history.
- **Order Management**: Create, update, and track sales and purchase orders with detailed order items.
- **Warehouse Management**: Organize and manage multiple warehouses, including stock levels and transfers.
- **Notification System**: Real-time notifications for key events (e.g., low stock, new orders).
- **Audit Logging**: Track changes and user actions for compliance and troubleshooting.
- **RESTful API**: Well-structured endpoints for all major operations.
- **Redis Integration**: Caching and session management for improved performance.
- **Docker Support**: Containerized deployment for easy setup and scalability.

## Tech Stack
- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- Redis
- Docker & Docker Compose
- Maven

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for containerized deployment)
- Redis server (local or Dockerized)

### Installation

#### Clone the repository
```bash
git clone https://github.com/yourusername/warehouse-management-system.git
cd warehouse-management-system
```

## Configuration

Copy the example configuration file and update it with your environment variables:

```bash
cp src/main/resources/applicationExample.yml src/main/resources/application.yml
```
Update the file with your environment variables for database, Redis, etc.

## Build Project
```bash
./mvn clean install
```

## Run the Application
```bash
./mvn spring-boot:run
```

## Run with Docker Compose
```bash
docker-compose up --build
```

## Usage
The application exposes RESTful APIs for all major operations.

API documentation (e.g., Swagger/OpenAPI) can be accessed at:

```bash
/swagger-ui.html
```

## Example Endpoints
POST /api/auth/login – User authentication

GET /api/products – List products

POST /api/orders – Create a new order

GET /api/warehouses – List warehouses

## Project Structure
```bash
src/
  main/
    java/org/khanhpham/wms/
      controller/    # REST controllers
      service/       # Business logic
      repository/    # Data access
      domain/        # Entities, DTOs, mappers
      config/        # Configuration classes
      security/      # Security and JWT
      utils/         # Utility classes
    resources/
      applicationExample.yml
      static/
      templates/
  test/
    java/org/khanhpham/wms/
      # Unit and integration tests
```

## Contributing
Contributions are welcome!
Please fork the repository and submit a pull request.
For major changes, open an issue first to discuss your ideas.


## License
This project is licensed under the terms of the MIT License.