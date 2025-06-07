| Método HTTP | URI            | Query Params | Request Body | Response Body | Códigos HTTP de respuesta |
|-------------|----------------|--------------|--------------|----------------|----------------------------|
| POST | `/orders` | — | <pre>{<br>  "books": []<br>}</pre> | <pre>{<br>  "code": ,<br>  "msg": "",<br>  "id": ,<br>  "total": ,<br>  "books": []<br>}</pre> | 200 OK – Orden creada<br>400 Bad Request – Error o campos vacíos<br>404 Not Found – Orden no existe |
| GET | `/orders` | — | — | <pre>[<br>  {<br>    "id": "1a2b3c",<br>    "bookId": "12345",<br>    "quantity": 2,<br>    "customerId": "abcde",<br>    "status": "CREATED"<br>  }<br>]</pre> | 200 OK – Lista de órdenes (puede estar vacía) |
| GET | `/orders/{id}` | — | — | <pre>{<br>  "id": "1a2b3c",<br>  "bookId": "12345",<br>  "quantity": 2,<br>  "customerId": "abcde",<br>  "status": "CREATED"<br>}</pre> | 200 OK – Orden encontrada<br>404 Not Found – Orden no existe |
