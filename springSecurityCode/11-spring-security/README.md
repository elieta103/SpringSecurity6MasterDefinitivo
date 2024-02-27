### 11 Proceso de Logout

- Se agrega endpoint /logout
  - Entity JwtToken, JwtTokenRepository, 
  - Recibe un HttpRequest y devuelve LogoutResponse

- Se debe guardar el token, en DB en dos partes del flujo
  - Cuando se genera un user nuevo
  - Cuando se hace login

- Se debe invalidar el token,en DB, cuando se llame a /logout
- Se modifica el JwtAuthenticationFilter, para validar que el jwt existe en DB

- Agregar esa nueva url en la DB, (MODULOS(Auth), OPERACIONES(logout), PERMISOS(16), es public y no se agrega a nadie)

- Al hacer el login/logout y guardar el JWT en DB, permite hacer un login único
  Es decir, permite tener solo "Una session/Login" activa, para ello, cuando el usuario
  se logee debe buscar en BD si tiene mas JWT validos, se procede a invalidarlos
  y se deja como válido solo el de la peticion actual. 
- Esa funcionalidad no esta implementada. 


### Testing el logout
1. Logear como administrador y obtener token
2. Con el token, hacer peticion a productos
3. Hacer logout con ese token
4. Volver a hacer peticion a productos



### Endpoints
- Logout
- POST http://localhost:9191/api/v1/auth/logout
- Authorization Bearer Token


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

### Mecanismo de logout en aplicaciones StateLess
- En una aplicación stateless con JWT (JSON Web Tokens), el concepto de "logout" es un poco diferente en comparación con las aplicaciones  
- stateful tradicionales. Dado que no se mantiene un estado de sesión en el servidor, el proceso de "logout" generalmente se realiza en el cliente, 
- invalidando o eliminando el token JWT almacenado en el lado del cliente. Sin embargo, aquí hay algunas alternativas que puedes considerar 
- para implementar un proceso de "logout" en una aplicación stateless con Spring Security y JWT:


#### Invalidación del Token en el Cliente: 
- Cuando un usuario desea hacer "logout", debes indicar al cliente que elimine o invalide el token JWT almacenado. 
- Esto generalmente se hace mediante el borrado del token en el almacenamiento del navegador, como las cookies, el local storage o el session storage.

#### Token Expiración: Configura una expiración relativamente corta para los tokens JWT. 
- Esto significa que los tokens expirarán después de un período específico, y los usuarios tendrán que volver a autenticarse después de ese tiempo. 
- Esto reduce la ventana de tiempo en la que un token podría ser utilizado después de que el usuario haya realizado "logout".

#### Blacklist o tabla en base de datos de Tokens (nosotros utilizaremos esta opción): 
- Mantén una lista negra (blacklist) o un registro en base de datos de tokens JWT inválidos o revocados en el servidor. 
- Cada vez que un usuario realiza "logout", podrías agregar el token a esta lista o tabla de base de datos.

#### Endpoint de "Logout" Simulado: 
- Aunque no es una práctica estándar y puede no ser necesario en una arquitectura stateless, podrías implementar un endpoint de "logout" 
- que simplemente responda con un mensaje indicando que el "logout" fue exitoso. Sin embargo, esto no invalidará los tokens ni realizará acciones 
- adicionales. Simplemente podrías usarlo para notificar al usuario que el "logout" se realizó correctamente.