# 💳 Bank API - Spring Boot Project

A RESTful Banking API built with **Spring Boot** that simulates core banking operations including customer management, account creation, deposits, withdrawals, money transfers, and account lifecycle management.

---

## 🚀 Features

### Customer Management

* Create Customer
* Get All Customers
* Get Customer by ID
* Update Customer
* Delete Customer

### Account Management

* Create Bank Account
* Generate Unique Account Number Automatically
* Get All Accounts
* Get Account by Account Number
* Delete Account

### Banking Operations

* Deposit Money
* Withdraw Money
* Transfer Money Between Accounts

### Account Types

* Savings Account
* Current Account
* Business Account

### Account Status Management

* Activate Account
* Block Account
* Close Account

### Business Rules

* A customer cannot own two accounts of the same type.
* Transactions are allowed only for active accounts.
* Blocked and closed accounts cannot perform transactions.
* Transfers are allowed only between active accounts.
* Transfer amount must be greater than zero.
* Transfer amount cannot exceed the sender's balance.
* Money cannot be transferred to the same account.

### Error Handling

* Global Exception Handling
* Custom Exceptions
* Bean Validation
* Meaningful HTTP Status Codes

---

## 🧱 Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven

---

## 📡 REST API

### Customers

| Method | Endpoint          |
| ------ | ----------------- |
| POST   | `/customers`      |
| GET    | `/customers`      |
| GET    | `/customers/{id}` |
| PUT    | `/customers/{id}` |
| DELETE | `/customers/{id}` |

### Accounts

| Method | Endpoint                    |
| ------ | --------------------------- |
| POST   | `/accounts`                 |
| GET    | `/accounts`                 |
| GET    | `/accounts/{accountNumber}` |
| DELETE | `/accounts/{accountNumber}` |

### Transactions

| Method | Endpoint                             |
| ------ | ------------------------------------ |
| POST   | `/accounts/{accountNumber}/deposit`  |
| POST   | `/accounts/{accountNumber}/withdraw` |
| POST   | `/accounts/transfer`                 |

### Account Status

| Method | Endpoint                             |
| ------ | ------------------------------------ |
| PATCH  | `/accounts/{accountNumber}/block`    |
| PATCH  | `/accounts/{accountNumber}/activate` |
| PATCH  | `/accounts/{accountNumber}/close`    |

---

## 🧠 Concepts Covered

* Spring Boot
* RESTful API Design
* Dependency Injection (IoC)
* Layered Architecture
* Spring Data JPA
* Hibernate
* Entity Relationships
* One-to-Many / Many-to-One Mapping
* Bean Validation
* Global Exception Handling
* Custom Exceptions
* Transaction Management (`@Transactional`)
* Hibernate Dirty Checking
* Business Rules Implementation

---

## 📌 Upcoming Features

* JWT Authentication
* Role-Based Authorization
* Transaction History
* Unit Testing
* Docker Support
* API Documentation (Swagger/OpenAPI)

---

## 👨‍💻 Author

**Ahmed Fayad**
