# Contributing to Nexus

Thank you for your interest in contributing to Nexus! This document provides guidelines and best practices for contributing to this open-source microservices platform.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Coding Standards](#coding-standards)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Branch Naming Convention](#branch-naming-convention)
- [Pull Request Process](#pull-request-process)
- [Reporting Issues](#reporting-issues)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

---

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment. Be kind, constructive, and professional in all interactions.

---

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Nexus.git
   cd Nexus
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/therishuraj/Nexus-CSCP.git
   ```
4. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

---

## Development Setup

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker Desktop**
- **Minikube** (for Kubernetes testing)
- **kubectl**
- **Git**

### Local Development

1. **Build all services**:
   ```bash
   # Build individual service
   cd user-service
   ./mvnw clean install
   
   # Or build all at once
   for service in user-service product-service investment-service payment-service order-service API-Gateway notification-service; do
     cd $service && ./mvnw clean install && cd ..
   done
   ```

2. **Run service locally**:
   ```bash
   cd user-service
   ./mvnw spring-boot:run
   ```

3. **Build Docker images**:
   ```bash
   eval $(minikube docker-env)
   docker build -t user-service:latest ./user-service
   docker build -t product-service:latest ./product-service
   docker build -t investment-service:latest ./investment-service
   docker build -t payment-service:latest ./payment-service
   docker build -t order-service:latest ./order-service
   docker build -t api-gateway:latest ./API-Gateway
   docker build -t notification-service:latest ./notification-service
   ```

4. **Deploy to Kubernetes**:
   ```bash
   cd k8s
   chmod +x deploy.sh
   ./deploy.sh
   ```

---

## How to Contribute

### Types of Contributions

- **Bug Fixes**: Fix existing issues
- **New Features**: Add new microservices or enhance existing ones
- **Documentation**: Improve README, API docs, or add examples
- **Tests**: Add unit/integration tests
- **Performance**: Optimize service response times, resource usage
- **DevOps**: Improve K8s deployments, CI/CD pipelines

### Contribution Workflow

1. Check existing [issues](https://github.com/therishuraj/Nexus-CSCP/issues) or create a new one
2. Fork and create a branch (see [Branch Naming](#branch-naming-convention))
3. Make your changes following [Coding Standards](#coding-standards)
4. Write/update tests
5. Update documentation if needed
6. Commit with proper messages (see [Commit Guidelines](#commit-message-guidelines))
7. Push to your fork
8. Open a Pull Request

---

## Coding Standards

### Java/Spring Boot

- **Java Version**: Java 17+
- **Framework**: Spring Boot 3.x
- **Code Style**: Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **Formatting**: Use 4 spaces for indentation (no tabs)
- **Line Length**: Max 120 characters

### Package Structure

Follow the existing package naming convention:

```
com.nexus.<service-name>/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
│   ├── request/
│   └── response/
├── model/          # Domain entities
├── repository/     # Data access layer
├── service/        # Business logic
└── utils/          # Utility classes
```

### Best Practices

- **Single Responsibility**: Each class should have one clear purpose
- **RESTful APIs**: Follow REST conventions (GET, POST, PUT, DELETE)
- **Error Handling**: Use proper HTTP status codes and consistent error responses
- **Logging**: Use SLF4J, follow existing patterns
- **Validation**: Validate input using Bean Validation annotations
- **Null Safety**: Use `Optional<>` where appropriate
- **Immutability**: Prefer immutable objects (use `@Builder`, `final` fields)

### Kubernetes Manifests

- Use descriptive resource names
- Include proper labels and annotations
- Define resource limits and requests
- Add health probes (readiness/liveness)
- Follow existing YAML structure in `k8s/`

---

## Commit Message Guidelines

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification.

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no logic change)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Build process, dependencies, tooling
- `ci`: CI/CD pipeline changes

### Examples

```bash
# Feature
feat(user-service): add wallet deposit endpoint

# Bug fix
fix(order-service): prevent duplicate order creation

# Documentation
docs(README): add Kafka setup instructions

# Refactor
refactor(payment-service): extract Razorpay client logic

# Test
test(product-service): add unit tests for ProductController

# Kubernetes
chore(k8s): increase kafka memory limit to 2Gi
```

### Scope

Use the service name or component:
- `user-service`
- `product-service`
- `investment-service`
- `payment-service`
- `order-service`
- `api-gateway`
- `notification-service`
- `k8s`
- `docs`

### Subject

- Use imperative mood ("add" not "added")
- Don't capitalize first letter
- No period at the end
- Max 50 characters

### Body (Optional)

- Explain **what** and **why**, not **how**
- Wrap at 72 characters
- Separate from subject with blank line

### Footer (Optional)

- Reference issues: `Closes #123` or `Fixes #456`
- Breaking changes: `BREAKING CHANGE: ...`

---

## Branch Naming Convention

### Format

```
<type>/<issue-number>-<short-description>
```

### Types

- `feature/` - New features
- `bugfix/` - Bug fixes
- `hotfix/` - Urgent production fixes
- `refactor/` - Code refactoring
- `docs/` - Documentation only
- `test/` - Test additions/changes
- `chore/` - Maintenance tasks

### Examples

```bash
feature/123-wallet-withdrawal
bugfix/456-fix-order-validation
hotfix/789-kafka-consumer-crash
refactor/101-extract-payment-client
docs/202-update-kubernetes-guide
test/303-add-user-service-tests
chore/404-upgrade-spring-boot
```

---

## Pull Request Process

### Before Submitting

- [ ] Code follows project style guidelines
- [ ] All tests pass locally
- [ ] Added tests for new features/fixes
- [ ] Updated documentation (README, Javadoc, OpenAPI specs)
- [ ] Rebased on latest `main` branch
- [ ] No merge conflicts
- [ ] Docker images build successfully
- [ ] Kubernetes manifests are valid (`kubectl apply --dry-run`)

### PR Template

When opening a PR, include:

**Description**
- What does this PR do?
- Why is this change needed?

**Type of Change**
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

**Testing**
- How was this tested?
- Screenshots/logs if applicable

**Checklist**
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Added tests
- [ ] Updated docs
- [ ] No new warnings

**Related Issues**
- Closes #123
- Fixes #456

### Review Process

1. At least **one approval** required from maintainers
2. All CI checks must pass
3. No unresolved conversations
4. Squash commits if requested
5. Maintainer will merge when approved

### After Merge

- Delete your branch
- Update your fork:
  ```bash
  git checkout main
  git pull upstream main
  git push origin main
  ```

---

## Reporting Issues

### Bug Reports

Use the **Bug Report** template and include:

- **Description**: Clear summary of the bug
- **Steps to Reproduce**:
  1. Deploy service X
  2. Call endpoint Y
  3. Observe error Z
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Environment**:
  - Minikube version
  - Kubernetes version
  - Java version
  - OS (macOS, Linux, Windows)
- **Logs**: Paste relevant logs
- **Screenshots**: If applicable

### Feature Requests

- **Problem**: What problem does this solve?
- **Proposed Solution**: How should it work?
- **Alternatives**: Other approaches considered?
- **Additional Context**: Use cases, examples

### Questions

- Use **Discussions** for general questions
- Check existing issues/docs first
- Provide context and what you've tried

---

## Testing Guidelines

### Unit Tests

- Use **JUnit 5** and **Mockito**
- Test classes should mirror source structure
- Naming: `ClassNameTest.java`
- Method naming: `methodName_scenario_expectedBehavior()`

Example:
```java
@Test
void createUser_validInput_returnsCreatedUser() {
    // given
    UserRequest request = UserRequest.builder()
        .name("John Doe")
        .email("john@example.com")
        .build();
    
    // when
    UserResponse response = userService.createUser(request);
    
    // then
    assertNotNull(response.getId());
    assertEquals(request.getName(), response.getName());
}
```

### Integration Tests

- Use `@SpringBootTest` for full context
- Use `@WebMvcTest` for controller tests
- Mock external dependencies (MongoDB, Kafka)
- Name: `ClassNameIntegrationTest.java`

### Test Coverage

- Aim for **80%+ coverage**
- Run with:
  ```bash
  ./mvnw clean test jacoco:report
  ```

### Testing Kubernetes Deployments

```bash
# Validate YAML
kubectl apply --dry-run=client -f k8s/user-service-deployment.yaml

# Deploy to test namespace
kubectl apply -f k8s/ -n nexus-test

# Run smoke tests
curl http://$(minikube service api-gateway -n nexus-test --url)/nexus/api/v1/users
```

---

## Documentation

### Code Documentation

- **Javadoc**: All public classes/methods
- **README**: Each service should have its own README
- **OpenAPI**: Keep API specs up to date (`apispec.yaml`)

### Service README Template

Each microservice should include:

```markdown
# Service Name

Brief description

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/resource | Get all |
| POST | /api/v1/resource | Create |

## Environment Variables

- `MONGO_URI`: MongoDB connection string
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker address

## Running Locally

...

## Testing

...
```

### Kubernetes Documentation

- Update `k8s/README.md` for deployment changes
- Document new environment variables
- Explain resource requirements

---

## Adding a New Microservice

If you're contributing a new service, include:

1. **Source Code**:
   - Follow package structure above
   - Include Dockerfile
   - Maven/Gradle build configuration

2. **Kubernetes Manifests**:
   - Deployment YAML (`k8s/your-service-deployment.yaml`)
   - Service YAML (ClusterIP)
   - ConfigMap/Secret if needed

3. **Documentation**:
   - Service README
   - API specification
   - Update main README.md

4. **Tests**:
   - Unit tests (min 80% coverage)
   - Integration tests
   - Bruno collection for API testing

5. **Example**:
   ```bash
   new-service/
   ├── src/
   ├── Dockerfile
   ├── pom.xml
   ├── README.md
   └── bruno-new-service/
   ```

---

## Questions?

- **GitHub Discussions**: For general questions and community support
- **Issues**: For bug reports and feature requests
- **Maintainers**:
  - Rishu Raj - rishurajsalarpur@gmail.com
  - Tushar Trivedi - tushar.trivedi@gmail.com
  - Mdataa Khan - trendsandfactss@gmail.com
  - Dheeraj Salunkhe - 2024sl93042@wilp.bits-pilani.ac.in
  - Sagar Dev - sagar.dev.lab@gmail.com

---

Thank you for contributing to Nexus! 🎉
