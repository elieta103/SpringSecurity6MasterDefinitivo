### 09 Proceso de autorización personalizados, CustomAuthorizationManager

- Crear estructura de entities (User, Operation, Module, Role, Permission)
- Crear estructura de repositories
- Crear script con datos

- Modificar la implementacion
```
    @Autowired
    private AuthorizationManager<RequestAuthorizationContext> authorizationManager;
    
     .authorizeHttpRequests( authReqConfig -> {
                    authReqConfig.anyRequest().access(authorizationManager);
                } )
```

- La logica de la implementacion esta en:
```
@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {...}
```
1. Del request obtiene la URI('/api/v1/products/10/disabled')
2. Del request extrae el Method (GET, POST, PUT, DELETE)
3. ¿Es pública?
- Busca en el Repository de Operaciones todas las publicas y con un stream valida las coincidencias
- Valida url y method
- Si lo encuentra(es public) regresa un true y termina.
4. Si fue privado
- Buscar que el user que realiza la peticion tenga los permisos correctos
- Valida url y method
5. Se devuelve new AuthorizationDecision(isGranted); true or false


### De las relaciones
- @OneToMany, La carga de las relaciones por default es LAZY, NO obtiene objetos relacionados
- @ManyToOne, La carga de las relaciones por default es EAGER, SI obtiene objetos relacionados

### Para evitar la relacion cliclica
- Al obtener el profile se hace una relacion ciclica infinita
- Rol <-> GrantedPermission
- Devuelve un :  "backendMessage": "Could not write JSON: Infinite recursion (StackOverflowError)"
- Se agrega  @JsonIgnore a la propiedad permissions del Role, la ignora

### Codigo innecesario
- De las secciones previas  se agregaba :
- { "authority": "ROLE_ASSISTANT_ADMINISTRATOR" }
- Ya no es necesario se puede omitir la linea 44 de la clase User.

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