# Inventory Backend Service

A Spring Boot application providing a RESTful API for managing product inventory, secured using Spring Security and JWT for authentication.

##  Technologies

* **Framework:** Spring Boot 3+
* **Security:** Spring Security (JWT authentication via HttpOnly Cookies)
* **Language:** Java
* **Persistence:** Spring Data JPA / Hibernate
* **Database:** PostgreSQL

---

## API Endpoints

The API is divided into two main sections: **Authentication** and **Inventory Management**.

### 1. Authentication Endpoints (`/auth`)

These endpoints handle user login, logout, and checking the current user's status. Successful login returns a JWT set in an **HttpOnly cookie** named `jwt`.

| HTTP Method | Path | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Authenticates a user and sets the secure `jwt` cookie. | Public |
| `POST` | `/auth/logout` | Clears the `jwt` cookie. | Authenticated |
| `GET` | `/auth/me` | Retrieves the username and role of the authenticated user. | Authenticated |

### 2. Inventory Endpoints (`/api/inventory`)

These endpoints manage product CRUD operations and quantity updates. Access is controlled via **Role-Based Access Control (RBAC)**.

| HTTP Method | Path | Description | Required Role | Request Body Example |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/inventory` | List all products. | Public (`@PermitAll`) | None |
| `GET` | `/api/inventory/{id}` | Get a product by ID. | Public (`@PermitAll`) | None |
| `POST` | `/api/inventory` | Create a new product. | `ADMIN` | `{"name": "Item Name", "initialQuantity": 10}` |
| `DELETE` | `/api/inventory/{id}` | Delete a product. | `ADMIN` | None |
| `POST` | `/api/inventory/{id}/increase` | Increase product quantity. **(USER role max increase is 5)** | `ADMIN` or `USER` | `{"amount": 3}` |
| `POST` | `/api/inventory/{id}/decrease` | Decrease product quantity. **(USER role max decrease is 5)** | `ADMIN` or `USER` | `{"amount": 2}` |
| `POST` | `/api/inventory/{id}/set` | Set product quantity to a specific value. | `ADMIN` | `{"amount": 15}` |

## Access Control Details

| Operation | Access Constraint | Notes |
| :--- | :--- | :--- |
| **View/Get Products** | `@PermitAll` | Open to all, including unauthenticated users. |
| **Create/Delete/Set Quantity** | `hasRole('ADMIN')` | Requires the `ADMIN` role. |
| **Increase/Decrease Quantity** | `hasAnyRole('ADMIN','USER')` | Both roles can perform this action. |
| **User Quantity Limits** | Custom Logic | The `USER` role is restricted to modifying the quantity by a maximum of **5** units in a single `increase` or `decrease` request. |