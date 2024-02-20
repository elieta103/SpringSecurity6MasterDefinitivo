### 04 Authorize HTTP Request: Autorización basada en coincidencia de solicitudes HTTP (Roles & Permisos)
- Authorities -> GrantedAuthorities  se insertan al objeto de Authentication
  - Representan los permisos otorgados al principal/username
- Spring Security utliza interceptores para controlar el acceso a objetos
- AuthorizationManager llamados por los componentes de authorization
- AuthorizationManager tiene 2 metodos : check() y verify()
- Tipos de autorizaciones
  - Coincidencia de solicitudes HTTP
    - Matching Using Ant
    - Matching Using Regular Expressions
    - By HTTP Method
    - By Dispatcher Type
    - Matcher Customizado
  - Asegurar metodos de controladores y servicios


- Cosa extraña en Spring, en UserDetails no hay metodo para obtener Roles
- Se podría decir que trabaja en base a permisos
- Por eso se agrega el rol, como si fuera un authority, en el User.

```
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role == null) return null;
        if(role.getPermissions() == null) return null;

        List<SimpleGrantedAuthority> authorities = role.getPermissions().stream()
                .map(item -> new SimpleGrantedAuthority(item.name()))
                .collect(Collectors.toList());
        // hasRole llama a hasAuthority pero le concatena al inicio ROLE_
        // Nos da como resultado ROLE_ADMINISTRADOR, por ende el SimpleGrantedAuthority se debe crear como: "ROLE_" + this.role
        // authorities": [{"authority": "READ_ALL_PRODUCTS"},{"authority": "READ_ONE_PRODUCT"},...,]
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        // authorities": [{"authority": "READ_ALL_PRODUCTS"},{"authority": "READ_ONE_PRODUCT"},...,{"authority": "ROLE_ADMINISTRATOR"}]
        // Si no se agrega linea 42 no permite el acceso
        return authorities;
    }
```
- Se puede cambiar entre implementaciones con hasAutority() o hasRole()
```
 .authorizeHttpRequests( authReqConfig -> {
                    buildRequestMatchersAuthorities(authReqConfig);
                    //buildRequestMatchersRoles(authReqConfig);
                } )
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