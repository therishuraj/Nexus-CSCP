# Security Policy

## Supported Versions

We release patches for security vulnerabilities. Currently supported versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

We take the security of Nexus-CSCP seriously. If you discover a security vulnerability, please follow these steps:

### 🔒 How to Report

**Please DO NOT report security vulnerabilities through public GitHub issues.**

Instead, please report them via:

1. **Email**: [rishurajsalarpur@gmail.com](mailto:rishurajsalarpur@gmail.com)
   - Subject: `[SECURITY] Brief description of the issue`
   - Include detailed information about the vulnerability

2. **GitHub Security Advisory** (Preferred):
   - Go to the [Security tab](https://github.com/therishuraj/Nexus-CSCP/security/advisories)
   - Click "Report a vulnerability"
   - Fill out the form with details

### 📋 What to Include

When reporting a vulnerability, please include:

- **Type of vulnerability** (e.g., SQL injection, XSS, authentication bypass)
- **Affected component/service** (e.g., user-service, API-Gateway)
- **Steps to reproduce** the vulnerability
- **Potential impact** of the vulnerability
- **Suggested fix** (if you have one)
- **Your contact information** for follow-up questions

### ⏱️ Response Timeline

- **Initial Response**: Within 48 hours of your report
- **Status Update**: Within 7 days with assessment and action plan
- **Fix Timeline**: Critical vulnerabilities will be addressed within 30 days

### 🎖️ Recognition

We value the security community's contributions. With your permission, we will:
- Acknowledge your contribution in the security advisory
- Credit you in release notes (unless you prefer to remain anonymous)

## Security Best Practices

### For Contributors

1. **Keep dependencies updated**: Regularly update Spring Boot and other dependencies
2. **Validate input**: Always validate and sanitize user input
3. **Use parameterized queries**: Prevent SQL/NoSQL injection attacks
4. **Secure secrets**: Never commit API keys, passwords, or tokens
5. **Follow secure coding guidelines**: Refer to OWASP guidelines

### For Deployment

1. **Use secrets management**: Store sensitive data in Kubernetes Secrets or external vaults
2. **Enable authentication**: Ensure all services require proper authentication
3. **Network policies**: Implement Kubernetes network policies to restrict traffic
4. **Resource limits**: Set appropriate CPU/memory limits to prevent DoS
5. **Regular updates**: Keep Kubernetes, Docker, and all dependencies up to date
6. **Monitor logs**: Set up centralized logging for security events

### API Security

1. **JWT tokens**: Use secure token generation and validation
2. **HTTPS only**: All API endpoints should use HTTPS in production
3. **Rate limiting**: Implement rate limiting on API Gateway
4. **CORS policy**: Configure appropriate CORS policies
5. **Input validation**: Validate all API inputs using Bean Validation

## Known Security Considerations

### Current Architecture

- **Microservices**: Each service should have isolated security boundaries
- **API Gateway**: Acts as the single entry point - ensure it's properly secured
- **Kafka**: Message queue should be configured with authentication
- **MongoDB**: Database connections should use authentication and encryption

### Kubernetes Security

- Use **non-root containers** when possible
- Implement **Pod Security Policies/Standards**
- Enable **RBAC** (Role-Based Access Control)
- Use **Network Policies** to restrict pod-to-pod communication
- Regularly scan container images for vulnerabilities

## Security Updates

Security patches will be released as follows:

- **Critical**: Immediate patch release
- **High**: Within 7 days
- **Medium**: Within 30 days
- **Low**: Next regular release

## Disclosure Policy

- We follow **responsible disclosure** practices
- Security issues will be disclosed after a fix is available
- We will coordinate disclosure timing with the reporter

## Contact

For security concerns or questions:
- **Email**: rishurajsalarpur@gmail.com
- **GitHub**: [@therishuraj](https://github.com/therishuraj)

---

Thank you for helping keep Nexus-CSCP and our users safe! 🛡️
