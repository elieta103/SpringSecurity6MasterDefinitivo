### 06 Method Security: Autorización basada en aseguramiento de métodos Services & Repository, permitAll denyAll.

- Quitar @PreAuthorize("hasAuthority('READ_ALL_PRODUCTS')") del metodo :
```
@GetMapping
public ResponseEntity<Page<Product>> findAll(Pageable pageable){...}
```
- Y Se agrega la anotacion en (Uno a la vez) :
- ProductService     => @PreAuthorize("hasAuthority('READ_ALL_PRODUCTS')")
- ProductServiceImpl => @PreAuthorize("hasAuthority('READ_ALL_PRODUCTS')")
- ProductRepository metodo sobreescrito  => @PreAuthorize("hasAuthority('READ_ALL_PRODUCTS')")

- Hasta este punto solo los matchers publicos funcionan con el HttpSecurity>Config y los
- protegidos con las anotaciones.
- En la siguiente seccion se comentan los matchers y toda la autorizacion queda con anotaciones

### permitAll denyAll
- Comentar :
```
                /*.authorizeHttpRequests( authReqConfig -> {
                    buildRequestMatchersMethods(authReqConfig);
                } )*/
```

- Agregar :
```
    @PreAuthorize("permitAll")
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validate(@RequestParam String jwt){...}
    
    @PreAuthorize("permitAll")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest){...}
    
    @PreAuthorize("permitAll")
    @PostMapping
    public ResponseEntity<RegisteredUser> registerOne(@RequestBody @Valid SaveUser newUser){...}

    @PreAuthorize("denyAll")
    @GetMapping
    public String getMessage(){...}
            
```


- Se requiere habilitar:
```
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class HttpSecurityConfig {...}
```
- Cuando se asegura metodos, y no se tiene permitido acceder al recurso, manda excepcion
- es correcto, porque la autorizacion funciona en distintas etapas
```
{   "backedMessage": "Access Denied",
    "message": "Error interno en el servidor, vuelva a intentarlo",
    "httpCode": 500,
    "time": "2024-02-20T14:05:58.9111792"
}
```

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