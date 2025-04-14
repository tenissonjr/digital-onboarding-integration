# Digital Onboarding Integration System

A Spring Boot application for integrating with external onboarding systems, processing and validating onboarding data.

## Features

- Receives notifications from external onboarding systems
- Fetches and processes onboarding data
- Validates onboarding data against business rules
- Provides status tracking and statistics
- Rate limiting and retry mechanism

## Technical Stack

- Java 17
- Spring Boot 3.1.5
- Spring Data JPA
- Spring Security
- H2 Database (Development) / PostgreSQL (Production)
- Prometheus/Grafana for monitoring
- Docker for containerization

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Docker and Docker Compose (for production deployment)

### Development Setup

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/digital-onboarding-integration.git
   cd digital-onboarding-integration