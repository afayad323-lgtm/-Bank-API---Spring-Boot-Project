# 💳 Bank API - Spring Boot Project

A RESTful Banking API built with **Spring Boot** that simulates core banking operations such as customer management, account creation, deposits, withdrawals, and account lifecycle management.

---

## 🚀 Features

### Customer Management
- Create Customer
- Get All Customers
- Get Customer by ID
- Update Customer
- Delete Customer

### Account Management
- Create Bank Account
- Get All Accounts
- Get Account by Account Number
- Delete Account

### Banking Operations
- Deposit Money
- Withdraw Money

### Account Features
- Unique Account Number Generation
- Multiple Account Types
  - Savings
  - Current
  - Business
- Account Status Management
  - Active
  - Blocked
  - Closed

### Business Rules
- A customer cannot own two accounts of the same type.
- Deposits and withdrawals are allowed only for active accounts.
- Blocked and closed accounts cannot perform transactions.
- Every account receives a unique account number automatically.

### Error Handling
- Global Exception Handling
- Validation
- Custom Exceptions

---

## 🧱 Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Maven

---

## 📡 REST API

### Customers

| Method | Endpoint |
|--------|----------|
| POST | `/customers` |
| GET | `/customers` |
| GET | `/customers/{id}` |
| PUT | `/customers/{id}` |
| DELETE | `/customers/{id}` |

### Accounts

| Method | Endpoint |
|--------|----------|
| POST | `/accounts` |
| GET | `/accounts` |
| GET | `/accounts/{accountNumber}` |
| DELETE | `/accounts/{accountNumber}` |

### Transactions

| Method | Endpoint |
|--------|----------|
| POST | `/accounts/{accountNumber}/deposit` |
| POST | `/accounts/{accountNumber}/withdraw` |

### Account Status

| Method | Endpoint |
|--------|----------|
| PATCH | `/accounts/{accountNumber}/block` |
| PATCH | `/accounts/{accountNumber}/activate` |
| PATCH | `/accounts/{accountNumber}/close` |

---

## 🧠 Concepts Covered

- Spring Boot
- RESTful API Design
- Dependency Injection (IoC)
- Layered Architecture
- Spring Data JPA
- Entity Relationships
- One-to-Many / Many-to-One Mapping
- Bean Validation
- Global Exception Handling
- Business Rules Implementation

---

## 📌 Upcoming Features

- Money Transfer
- Transaction History
- JWT Authentication
- Role-Based Authorization
- Unit Testing
- Docker Support

---

## 👨‍💻 Author

**Ahmed Fayad**
