### 01 Proyecto Base de SpringSecurity

### Endpoints
- Obtener todos
  - Sin las propiedades del properties
    - GET http://localhost:9191/api/v1/products
    - GET http://localhost:9191/api/v1/products?page=0&size=5
    - GET http://localhost:9191/api/v1/products?page=0&size=5&sort=id,name,DESC

  - Con las propiedades del properties
    - GET http://localhost:9191/api/v1/products
    - GET http://localhost:9191/api/v1/products?p=0&limit=5
    - GET http://localhost:9191/api/v1/products?p=0&limit=5&sort=id,name,DESC

- Obtener por Id
  - GET http://localhost:9191/api/v1/products/6

- Insertar registro
  - POST http://localhost:9191/api/v1/products
```
 {
    "name": "ZapatosXXXX",
    "price": 450.00,
    "status": "ENABLED",
    "categoryId": "1"
 }
```  
- Inhabilitar registro
  - PUT http://localhost:9191/api/v1/products/11/disable


### Para el manejo de errores en las validaciones, se puede :
- Primer enfoque (SpringMicroservicesRameshFadatare, seccion 06) :
```
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

...

@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        List<ObjectError> errorsList = ex.getBindingResult().getAllErrors();

        errorsList.forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
}
    
```

- Segundo Enfoque (SpringSecurity6MasterDefinitivo, Seccion 01)
```
@RestControllerAdvice
public class GlobalExceptionHandler {
...

   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Error: la petición enviada posee un formato incorrecto");
        error.setBackedMessage(exception.getLocalizedMessage());
        error.setTime(LocalDateTime.now());
        error.setHttpCode(400);

        System.out.println("---Errores---");
        System.out.println(exception.getAllErrors().stream().map(item -> item.getDefaultMessage()).collect(Collectors.toList()) );
        System.out.println("---Fin Errores---");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}

```