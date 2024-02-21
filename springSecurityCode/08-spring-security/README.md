### 08 Handling Exception, AccessDeniedException, AuthenticationCredentialsNotFoundException
- Para probar cuando no se envía token y cuando no hay permisos
- El enfoque del capitulo 07 no diferencia entre las excepciones
  - AccessDeniedException (No tienes permisos, no authorizado)
  - AuthenticationCredentialsNotFoundException (Cuando no se envía token)

- Deshabilitar autorizacion basada en metodos
```
//@EnableMethodSecurity(prePostEnabled = true)
```

- Se debe hacer con el metodo de coincidencia de solicitudes HTTP
```
        .authorizeHttpRequests( authReqConfig -> {
                    buildRequestMatchers(authReqConfig);
                } )
```
- Agregar el paquete config.security.handler, con implementaciones para:
  - CustomerAuthenticationEntryPoint 401
  - CustomerAccessDeniedHandler 403

- Se agrega libreria para el manejo de fechas , en ApiError
```
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime timestamp;

    <dependency>
	  <groupId>com.fasterxml.jackson.datatype</groupId>
	  <artifactId>jackson-datatype-jsr310</artifactId>
	  <version>2.15.2</version>
	</dependency>
```

- Se debe declarar el handler de las excepciones
```
    @Autowired private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired private AccessDeniedHandler accessDeniedHandler;

     .exceptionHandling( exceptionConfig -> {
                    exceptionConfig.authenticationEntryPoint(authenticationEntryPoint);
                    exceptionConfig.accessDeniedHandler(accessDeniedHandler);
                })   
```



### En caso de que se requiera mandar el mismo error para 401 y 403, se debe hacer:
```
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        accessDeniedHandler.handle(request, response, new AccessDeniedException("Access Denied"));

    }
}
```


### Endpoints``
- Usuario logeado
- GET http://localhost:9191/api/v1/auth/profile
- Authorization Bearer Token
```
{
    "id": 1,
    "username": "lmarquez",
    "name": "luis márquez",
    "password": "$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli",
    "role": "ROLE_CUSTOMER",
    "enabled": true,
    "authorities": [
        {
            "authority": "READ_MY_PROFILE"
        }
    ],
    "credentialsNonExpired": true,
    "accountNonExpired": true,
    "accountNonLocked": true
}
```

- Genera un nuevo usuario y su token.
- POST http://localhost:9191/api/v1/customers
- Request
```
{  "username":"eliel",
   "name": "eliel herrera",
   "password": "12345678",
   "repeatedPassword": "12345678"
}
```
- Response
```
{   "id": 4,
    "username": "eliel",
    "name": "eliel herrera",
    "role": "ROLE_CUSTOMER",
    "jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbGllbCIsImlhdCI6MTcwODMwMjk2NSwiZXhwIjoxNzA4MzA0NzY1LCJyb2xlIjoiUk9MRV9DVVNUT01FUiIsIm5hbWUiOiJlbGllbCBoZXJyZXJhIiwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IlJFQURfTVlfUFJPRklMRSJ9XX0.GU2D3FxYBfGQ6R6jHpZLO-WTRz2FL36W-_H4Yo4kBWc"
}
```

- Con un usuario creado devuelve un token
- POST http://localhost:9191/api/v1/auth/authenticate
- Request
```
{  "username":"lmarquez",
   "password":"clave123" 
}
```
- Response
```
{
    "jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsbWFycXVleiIsImlhdCI6MTcwODMwNjUwOSwiZXhwIjoxNzA4MzA4MzA5LCJyb2xlIjoiUk9MRV9DVVNUT01FUiIsIm5hbWUiOiJsdWlzIG3DoXJxdWV6IiwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IlJFQURfTVlfUFJPRklMRSJ9XX0.1sNtQe5mpHkQWcKlll9IRVnsCFgkhJi66jYF4P7kFDY"
}
```

- Con un token generado, validar que sea correcto
- GET http://localhost:9191/api/v1/auth/validate-token?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsbWFycXVleiIsImlhdCI6MTcwODMwNjY3MSwiZXhwIjoxNzA4MzA4NDcxLCJyb2xlIjoiUk9MRV9DVVNUT01FUiIsIm5hbWUiOiJsdWlzIG3DoXJxdWV6IiwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IlJFQURfTVlfUFJPRklMRSJ9XX0.f5ZLwd50wlcXTJp7daLDllvCDCpBKLOLTjvaGkFy61U
- Response
```
true
```