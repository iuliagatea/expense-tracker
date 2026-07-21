ExpenseTracker is a Spring Boot REST API for personal expense management with JWT authentication and role-based access control (ADMIN/USER roles). Users can create, retrieve, filter, and delete expenses organized by date and category.

### Tech Stack
- **Framework**: Spring Boot 4.1.0-M2 (latest milestone)
- **Security**: Spring Security + JWT (JJWT 0.12.6) for stateless authentication
- **Data**: Spring Data JPA with MySQL (dialect: MySQLDialect)
- **ORM**: Hibernate (auto DDL: update)
- **Build**: Gradle with Lombok annotation processor
- **Port**: 8080

---

### Security & JWT Flow
1. **Stateless sessions**: `SessionCreationPolicy.STATELESS` in `SecurityConfig`
2. **JWT token generation**: `JwtUtil.generateToken(username)` returns 10-hour validity tokens
3. **Filter chain**: `JwtAuthFilter` extracts token from request headers before `UsernamePasswordAuthenticationFilter`
4. **Protected endpoints**: `/expenses/**` and `/admin/**` require valid JWT; `/signup`, `/login` are public
5. **Token payload**: Username is stored as JWT subject (extractable via `extractUsername()`)

**Important**: JWT secret key is randomly generated at runtime (`Jwts.SIG.HS256.key().build()`)

---

## Testing
- Test framework: JUnit 5 (Jupiter) configured in `build.gradle`
  
---

