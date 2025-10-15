# SpringBoot Backend with authentification and Log4J logging
This project (codenamed ProjectYC) demonstrates a simple authentication backend using httpOnly Cookies for secure session handling. 

The backend stores encrypted user credentials as well as the authentication tokens in a MySQL database. These are then used to validate client API requests.

> ⚠️ **Note:** this project is made for demonstration purposes , as such mock user credentials were included in the code **for testing purposes**.  
> In a production setup, user credentials should never be included in the code even if encrypted. They should instead be securely managed in the database.
## Project Structure
**Framework** : Spring Boot
**Dependencies**: Spring Security, Log4J, Lombok 
**Database** : MySQL
**Tools** : Postman  
> ℹ️ This backend was tested with Postman and as well as a basic Angular Front end to ensure cookies were properly working with the browser as well as ensure the CORS configuration works. 

##  API Endpoints

|Method| Endpoint|Description|Cookie Required|Request Body|Response Body
|--|--|--|--|--|--|
| POST   | `/login`        | Authenticates user and sets an HTTP-only cookie             | ❌               | `{ "username": "string", "password": "string" }` | `{ "message": "string", "success": boolean}` |
| GET    | `/authVerify`   | Verifies if the session is valid                            | ✅               | —                                                | `boolean`                     |
| POST   | `/logout`       | Logs out the user and clears the cookie                     | ✅               | —                                                | `boolean`                           |
| GET    | `/resourceTest` | Protected resource endpoint (returns a string if authenticated, else returns a `401 Unauthorized`) | ✅               | —                                                | ``string``                            |



ℹ️ This documentation is still under developement.
