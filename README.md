# Your Digital Bridge

**Your Digital Bridge** is a money transfer web application built as part of a thesis project. It features a **Spring Boot** backend and a **Thymeleaf** front end, with seamless **Dockerized deployment** for easy setup. Perfect for demonstrating full-stack Java development and modern deployment practices.

---

## Table of Contents

- [Overview](#overview)  
- [Features](#features)  
- [Tech Stack](#tech-stack)  
- [Getting Started](#getting-started)  
  - [Prerequisites](#prerequisites)  
  - [Setup & Run](#setup--run)  
- [Application Flow](#application-flow)    
- [Future Roadmap](#future-roadmap)  

---

## Overview

This project is a **full-stack** prototype of a money transfer platform. It supports user registration, login, and secure (simulated) money transfers, with dynamic server-side rendering via Thymeleaf. Built with modularity and extensibility in mind, it’s containerized for streamlined deployment and scalability.

---

## Features

- User **Registration** and **Login**
- Secure session management
- Money transfer interface with input validation
- Dynamic views rendered with **Thymeleaf**
- Dockerized setup using **Docker** and **Docker Compose**
- Organized, modular codebase for ease of development and extension

---

## Tech Stack

| Component     | Technology                             |
|---------------|----------------------------------------|
| Backend       | Java, Spring Boot                      |
| Frontend      | Thymeleaf, HTML, CSS (templates)       |
| Authentication | OAuth2 (GitHub) +
|                  JWT tokens for stateless auth         |
| Database      | MySQL                                  |
| Containerization | Docker                              |
| Build Tool    | Maven (`mvn`)                          |


---

## Getting Started

### Prerequisites

Ensure the following are installed on your machine:

- Java JDK 23  
- Maven 3.6+ (or latest)  
- Docker (Docker Desktop)
- (Optional) IDE such as IntelliJ IDEA, Eclipse, or VS Code  

### Setup & Run

1. **Clone the repository**  
   git clone https://github.com/AntonisTerzo/Your_Digital_Bridge.git
   cd Your_Digital_Bridge
2. **Build the project with maven**
   mvn clean package
3. **Run the application using Docker compose**
   docker compose up --build
4. **Open in your browser**
   Visit http://localhost:8080

## Application Flow

1. Register → Create a new user account or register via GitHub

2. Login → Access your user dashboard

3. Transfer Money → Input recipient and amount, then send

4. Confirmation → View success or error feedback

## Future Roadmap

- Add 2factor authentication
- Add email notification
- Better error handling
- Unit tests
- Work on the user page
- Database migrations
