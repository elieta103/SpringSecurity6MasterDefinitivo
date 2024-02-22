### 10 Reto de otorgar/eliminar permisos

- Se crea un flujo PermissionController -> Service -> Repository
- Se crean nuevos registros en la tabla granted_permission
- El unico que puede otorgar o revocar los permisos es un ADMINISTRATOR.
- Los permisos validos son
 - 'READ_ALL_PERMISSIONS'
 - 'READ_ONE_PERMISSION'
 - 'CREATE_ONE_PERMISSION'
 - 'DELETE_ONE_PERMISSION'


### Para probar
1. Logeo como CUSTOMER
2. Solo tiene permisos READ_MY_PROFILE
  - Token CUSTOMER
  - eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsbWFycXVleiIsImlhdCI6MTcwODY0NDAyMSwiZXhwIjoxNzA4NjQ1ODIxLCJyb2xlIjoiQ1VTVE9NRVIiLCJuYW1lIjoibHVpcyBtw6FycXVleiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJSRUFEX01ZX1BST0ZJTEUifSx7ImF1dGhvcml0eSI6IlJPTEVfQ1VTVE9NRVIifV19.AEOmSy8zF1__Ccf0xDI16DoxAwZUlKvCL3kZAj9CxUw
  - TOKEN ADMINISTRATOR
  - eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtaGVybmFuZGV6IiwiaWF0IjoxNzA4NjQ0MjA4LCJleHAiOjE3MDg2NDYwMDgsInJvbGUiOiJBRE1JTklTVFJBVE9SIiwibmFtZSI6Im1lbmdhbm8gaGVybsOhbmRleiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJSRUFEX0FMTF9QUk9EVUNUUyJ9LHsiYXV0aG9yaXR5IjoiUkVBRF9PTkVfUFJPRFVDVCJ9LHsiYXV0aG9yaXR5IjoiQ1JFQVRFX09ORV9QUk9EVUNUIn0seyJhdXRob3JpdHkiOiJVUERBVEVfT05FX1BST0RVQ1QifSx7ImF1dGhvcml0eSI6IkRJU0FCTEVfT05FX1BST0RVQ1QifSx7ImF1dGhvcml0eSI6IlJFQURfQUxMX0NBVEVHT1JJRVMifSx7ImF1dGhvcml0eSI6IlJFQURfT05FX0NBVEVHT1JZIn0seyJhdXRob3JpdHkiOiJDUkVBVEVfT05FX0NBVEVHT1JZIn0seyJhdXRob3JpdHkiOiJVUERBVEVfT05FX0NBVEVHT1JZIn0seyJhdXRob3JpdHkiOiJESVNBQkxFX09ORV9DQVRFR09SWSJ9LHsiYXV0aG9yaXR5IjoiUkVBRF9NWV9QUk9GSUxFIn0seyJhdXRob3JpdHkiOiJSRUFEX0FMTF9QRVJNSVNTSU9OUyJ9LHsiYXV0aG9yaXR5IjoiUkVBRF9PTkVfUEVSTUlTU0lPTiJ9LHsiYXV0aG9yaXR5IjoiQ1JFQVRFX09ORV9QRVJNSVNTSU9OIn0seyJhdXRob3JpdHkiOiJERUxFVEVfT05FX1BFUk1JU1NJT04ifSx7ImF1dGhvcml0eSI6IlJPTEVfQURNSU5JU1RSQVRPUiJ9XX0.C-5a4feK5-xX6jxyR7jyyKk-SiL5hsh_eBLZAob_yTc
3. Con token de administrador ingresar a : localhost:9191/api/v1/permissions
```
{   "role": "CUSTOMER",
    "operation": "READ_ALL_PRODUCTS"
}
```
4. Consultar los nuevos permisos del CUSTOMER en el profile, con token de customer, se visualiza un nuevo permiso

## Validacion Importante
- La importancia de no haber metido el objeto UserDetails adentro del SecurityContextHolder, es decir
- adentro de la authentication como principal, ya que siempre que validamos este token estamos conectado
- con la base de datos y obtenemos el usuario mas reciente

- JwtAutheticationFilter
- 
- Siempre que validamos el token, lo obtenemos de la BD, trayendo así el usuario mas reciente guardado.
```
//4. Setear objeto authentication dentro de security context holder

        User user = userService.findOneByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User not found. Username: " + username));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            username, null, user.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("Se acaba de setear el authentication");
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