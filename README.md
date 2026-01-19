# BookStore RESTful API

A comprehensive RESTful web service for managing an online bookstore, built with **JAX-RS (Jersey)** and **Java EE**. This project demonstrates enterprise-level API design with complete CRUD operations, exception handling, and logging.

**Academic Project** - Client-Server Architectures Module, University of Westminster  
**Grade Achieved**: 96.4%

---

## 🎯 Project Overview

A fully functional bookstore API that manages books, authors, customers, shopping carts, and orders through RESTful endpoints. The system implements proper HTTP methods, status codes, and follows REST architectural principles.

---

## 🏗️ Architecture

### Technology Stack
- **Backend Framework**: JAX-RS 2.1 (Jersey 2.32)
- **Build Tool**: Apache Maven 3.9.1
- **Server**: Apache Tomcat 9.x
- **Data Format**: JSON (Jackson)
- **Logging**: SLF4J + Logback
- **Java Version**: 1.8

### Design Pattern
- **DAO Pattern** for data access layer
- **Exception Mappers** for centralized error handling
- **Resource-based** RESTful architecture

---

## 📋 Core Features

### 1. **Books Management**
- CRUD operations for book inventory
- Stock quantity tracking
- ISBN management
- Price and publication year tracking

### 2. **Authors Management**
- Author profiles with biographies
- Link authors to their books
- Retrieve all books by specific author

### 3. **Customer Management**
- Customer registration and profiles
- Secure password storage
- Email validation
- Customer account updates

### 4. **Shopping Cart**
- Add/update/remove books from cart
- Real-time quantity validation
- Stock availability checking
- Customer-specific cart management

### 5. **Order Processing**
- Create orders from shopping cart
- Automatic total price calculation
- Order history tracking
- Stock deduction on order placement

### 6. **Exception Handling**
- Custom exception classes for different scenarios
- Global exception mappers for consistent error responses
- Detailed error logging with SLF4J

---

## 🛠️ API Endpoints

### Books `/api/books`
```http
GET    /api/books              # Get all books
GET    /api/books/{id}         # Get book by ID
POST   /api/books              # Add new book
PUT    /api/books/{id}         # Update book
DELETE /api/books/{id}         # Delete book
```

### Authors `/api/authors`
```http
GET    /api/authors            # Get all authors
GET    /api/authors/{id}       # Get author by ID
GET    /api/authors/{id}/books # Get books by author
POST   /api/authors            # Add new author
PUT    /api/authors/{id}       # Update author
DELETE /api/authors/{id}       # Delete author
```

### Customers `/api/customers`
```http
GET    /api/customers          # Get all customers
GET    /api/customers/{id}     # Get customer by ID
POST   /api/customers          # Register new customer
PUT    /api/customers/{id}     # Update customer
DELETE /api/customers/{id}     # Delete customer
```

### Shopping Cart `/api/customers/{customerId}/cart`
```http
GET    /api/customers/{customerId}/cart                    # View cart
POST   /api/customers/{customerId}/cart/items              # Add book to cart
PUT    /api/customers/{customerId}/cart/items/{bookId}     # Update quantity
DELETE /api/customers/{customerId}/cart/items/{bookId}     # Remove book
```

### Orders `/api/customers/{customerId}/orders`
```http
GET    /api/customers/{customerId}/orders           # Get customer orders
GET    /api/customers/{customerId}/orders/{orderId} # Get specific order
POST   /api/customers/{customerId}/orders           # Create order from cart
```

---

## 📊 Data Models

### Book
```json
{
  "bookId": 1,
  "title": "1984",
  "author": {
    "authorId": 3,
    "name": "George Orwell",
    "biography": "British writer..."
  },
  "isbn": "978-0451524935",
  "publicationYear": 1949,
  "price": 14.50,
  "stockQuantity": 25
}
```

### Cart
```json
{
  "id": 1,
  "customer": {
    "customerId": 1,
    "name": "Alice John",
    "email": "alice@example.com"
  },
  "items": {
    "1": 2,    // bookId: quantity
    "3": 1
  }
}
```

### Order
```json
{
  "orderId": 1,
  "customerId": 1,
  "items": {
    "1": 2,
    "3": 1
  },
  "totalPrice": 36.48
}
```

---

## 🚀 Setup & Installation

### Prerequisites
- JDK 1.8 or higher
- Apache Maven 3.x
- Apache Tomcat 9.x
- NetBeans IDE (recommended) or IntelliJ IDEA

### Installation Steps

1. **Clone the repository**
```bash
git clone https://github.com/YOUR-USERNAME/BookStore.git
cd BookStore
```

2. **Build with Maven**
```bash
mvn clean install
```

3. **Deploy to Tomcat**
   - Copy `target/BookStore-1.0-SNAPSHOT.war` to Tomcat's `webapps` directory
   - Or deploy directly from NetBeans/IDE

4. **Start Tomcat Server**
```bash
# From Tomcat bin directory
./catalina.sh start  # Unix/Mac
catalina.bat start   # Windows
```

5. **Test the API**
```bash
curl http://localhost:8080/BookStore/api/books
```

---

## 🧪 Testing with Postman

### Sample Requests

**1. Get All Books**
```http
GET http://localhost:8080/BookStore/api/books
```

**2. Add Book to Cart**
```http
POST http://localhost:8080/BookStore/api/customers/1/cart/items?bookId=3&quantity=2
Content-Type: application/json
```

**3. Create New Book**
```http
POST http://localhost:8080/BookStore/api/books
Content-Type: application/json

{
  "title": "The Great Gatsby",
  "authorId": 1,
  "isbn": "978-0743273565",
  "publicationYear": 1925,
  "price": 15.99,
  "stockQuantity": 50
}
```

**4. Place Order**
```http
POST http://localhost:8080/BookStore/api/customers/1/orders
Content-Type: application/json
```

---

## 💡 Key Implementation Details

### Exception Handling System

I implemented a comprehensive exception handling framework with custom exceptions and global mappers:

```java
@Provider
public class BookNotFoundExceptionMapper implements
        ExceptionMapper<BookNotFoundException> {

    private static final Logger logger = LoggerFactory.getLogger(
        BookNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(BookNotFoundException exception) {
        logger.error("Book not found: {}", exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
```

**Custom Exceptions:**
- `BookNotFoundException`
- `AuthorNotFoundException`
- `CustomerNotFoundException`
- `CartNotFoundException`
- `OutOfStockException`
- `InvalidInputException`

### Auto-Incrementing IDs

Implemented robust ID generation for books and customers:

```java
public int getNextBookId() {
    int maxBookId = 0;
    for (Book book : books) {
        if (book.getBookId() > maxBookId) {
            maxBookId = book.getBookId();
        }
    }
    return maxBookId + 1;
}
```

### Stock Management

Real-time stock validation when adding to cart:

```java
if (book.getStockQuantity() < quantity) {
    logger.error("Not enough stock for Book ID: {}", bookId);
    throw new OutOfStockException(
        "Book with ID " + bookId + " is out of stock. Available: " 
        + book.getStockQuantity());
}
```

### Logging System

Comprehensive logging throughout the application:

```java
private static final Logger logger = LoggerFactory.getLogger(BookResource.class);

@GET
public List<Book> getAllBooks() {
    logger.info("GET request for all Books");
    return bookDAO.getAllBooks();
}
```

---

## 📁 Project Structure

```
BookStore/
├── src/main/java/com/example/
│   ├── dao/                      # Data Access Objects
│   │   ├── AuthorDAO.java
│   │   ├── BookDAO.java
│   │   ├── CartDAO.java
│   │   ├── CustomerDAO.java
│   │   └── OrderDAO.java
│   ├── exception/                # Custom Exceptions & Mappers
│   │   ├── BookNotFoundException.java
│   │   ├── BookNotFoundExceptionMapper.java
│   │   ├── CustomerNotFoundException.java
│   │   ├── OutOfStockException.java
│   │   └── ...
│   ├── model/                    # Domain Models
│   │   ├── Author.java
│   │   ├── Book.java
│   │   ├── Cart.java
│   │   ├── Customer.java
│   │   └── Order.java
│   └── resource/                 # REST Endpoints
│       ├── ApplicationConfig.java
│       ├── AuthorResource.java
│       ├── BookResource.java
│       ├── CartResource.java
│       ├── CustomerResource.java
│       └── OrderResource.java
├── src/main/webapp/
│   └── WEB-INF/
│       ├── web.xml
│       └── beans.xml
├── pom.xml                       # Maven Dependencies
└── README.md
```

---

## 🎓 Learning Outcomes

Through this project, I gained practical experience in:

### Technical Skills
✅ **RESTful API Design** - HTTP methods, status codes, resource naming  
✅ **JAX-RS Framework** - Jersey implementation, annotations, providers  
✅ **JSON Processing** - Jackson for serialization/deserialization  
✅ **Exception Handling** - Custom exceptions and global mappers  
✅ **Logging** - SLF4J and Logback configuration  
✅ **Maven Build Tool** - Dependency management, build lifecycle  
✅ **Server Deployment** - Apache Tomcat configuration

### Software Engineering Practices
✅ **Separation of Concerns** - DAO, Model, Resource layers  
✅ **Error Handling** - Comprehensive exception management  
✅ **Code Documentation** - JavaDoc and inline comments  
✅ **RESTful Principles** - Stateless, uniform interface, client-server  
✅ **HTTP Protocol** - Understanding request/response cycle

---

## 🔍 Sample Data

The application includes pre-populated test data:

**Authors:**
- J.D. Salinger
- Harper Lee
- George Orwell
- Jane Austen

**Books:**
- The Catcher in the Rye
- To Kill a Mockingbird
- 1984
- Pride and Prejudice

**Customers:**
- Alice John (alice@example.com)
- Bob Smith (bob@example.com)
- Charlie Dave (charlie@example.com)
- David Black (david@example.com)
- Emily Brown (emily@example.com)

---

## 🔒 API Response Codes

| Code | Status | Description |
|------|--------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created |
| 204 | No Content | Deletion successful |
| 404 | Not Found | Resource doesn't exist |
| 400 | Bad Request | Invalid input |
| 500 | Internal Error | Server error |

---

## 📈 Future Enhancements

Potential improvements for production:

- [ ] JWT Authentication & Authorization
- [ ] Database integration (PostgreSQL/MySQL)
- [ ] Payment gateway integration
- [ ] Email notifications for orders
- [ ] Advanced search and filtering
- [ ] Pagination for large datasets
- [ ] Rate limiting for API endpoints
- [ ] Swagger/OpenAPI documentation
- [ ] Unit and integration tests
- [ ] Docker containerization

---

## 📝 Acknowledgments

- **University of Westminster** - Client-Server Architectures Module
- **JAX-RS/Jersey** - For excellent REST framework
- **Apache Maven** - For dependency management
- **SLF4J/Logback** - For logging capabilities

---

## 📜 License

This is an academic project created for educational purposes.

**Note**: This project uses in-memory data storage. All data resets when the server restarts. For production use, integrate with a persistent database.
