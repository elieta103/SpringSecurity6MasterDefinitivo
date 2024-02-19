### 03 Filtros de Seguridad JwtAuthenticationFilter
- Para cada peticion se incorpora JwtAuthenticationFilter
- Toma Jwt saca la informacion y la setea en sel SecurityContextHolder
- OncePerRequestFilter. Clase abstracta que simplifica la creacion de filtros
  garantiza que se aplica una sola vez en la cadena de filtros, SE tiene acceso al HttpServletRequest
  y HttpServletResponse

### Registro del Filtro
- En SecurityFilterChain, HttpSecurityConfig,   addFilterBefore()

### Flujo
1. Crear usuario ó Con el usuario creado obtener token
2. Agregar Bearer Token en las cabeceras de la peticion
3. Se pueden consumir todos los endpoints, solo autentica, no autoriza


### Debugger sin token
1. Realizar request 
2. Entra a JwtAuthenticationFilter
3. AuthenticationEntryPoint
3. Manda un 403, no Autorizado



### Endpoints
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