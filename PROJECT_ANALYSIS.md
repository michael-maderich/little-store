# LittleStore Project Analysis

## Overview
LittleStore is a **layered Spring MVC e-commerce application** built with Spring Boot 2.7.18, Java 11, MySQL backend, and external services for images (Cloudinary) and email (Gmail API).

---

## 1. CONTROLLER CLASSES (Request Routing & Endpoints)

### Main Controllers

**MainController** – Primary application controller with 30+ endpoints:
- **Home & Auth**: `/`, `/login`, `/signup`, `/forgotPassword`, `/resetPassword`, `/logout`
- **Account Management**: `/account`, `/account/edit`, `/account/orders`, `/editAccount`
- **Shopping**: 
  - `/category/{categoryName}` – Browse by main category
  - `/category/{categoryName}/{subCategoryName}` – Browse by subcategory
  - `/newitems` – View newly added products
  - `/dollarama` – Products ≤$1.00
  - `/sale` – Products on sale (current price < base price)
  - `/search` – Search functionality
- **Cart Operations**: `/addToCart`, `/cart`, `/removeFromCart`, `/clearCart`
- **Checkout**: `/checkout`, `/confirmation`, `/printOrder/{email}/{orderNum}`
- **Utilities**: `/encode/{email}`, `/resendConfirmation/{email}/{orderNum}`, `/privacyPolicy`
- **OAuth**: `/connect`, `/oauth2/callback` (Google OAuth integration)
- **Error Handling**: `/403`, `/{nonsense}` (catch invalid URLs)

**AdminController** – Admin/Owner dashboard:
- `/admin/`, `/admin/dashboard` – Admin dashboard
- `/admin/orders` – Manage orders

**AdminProductController** – Product management (CRUD):
- `GET /admin/products` – List all products
- `GET /admin/products/create` – Product creation form
- `POST /admin/products/save` – Save new/edited product (with image upload to Cloudinary)
- `GET /admin/products/edit/{upc}` – Edit product form
- `GET /admin/products/delete/{upc}` – Delete product

**SecurityController** – Minimal security:
- `GET /username` – Returns current logged-in username (email)

**CustomErrorController** – Error handling:
- `/error` – Generic error page displaying status codes

### BaseController (Abstract)
- Provides dependency injection for all services, validators, and utilities
- Common helper methods: `getCurrentUser()`, `currentUserIsAdmin()`, `getGeneralDataString()`
- Manages state lists (US states, payment methods)

---

## 2. ENTITY CLASSES (Data Model & Relationships)

| Entity | Role | Key Fields | Relationships |
|--------|------|-----------|---|
| **Customer** | User account with profile and roles | `id`, `email` (unique), `password`, `firstName`, `lastName`, `phone`, `address`, `city`, `state`, `preferredPayment`, `paymentHandle`, `accountCreated`, `lastVisited`, `lastOrdered` | `1→M` Orders, `1→1` Cart, `M↔M` Roles |
| **Product** | Catalog item with pricing & inventory | `upc` (unique PK), `categoryMain/Secondary/Specific`, `name`, `options`, `size`, `cost`, `retailPrice`, `basePrice`, `currentPrice`, `onSale`, `stockQty`, `purchaseLimit`, `description`, `image` | `1→M` CartDetails, `1→M` OrderDetails |
| **Cart** | Shopping cart (per customer) | `cartId`, `customerId` (FK), `cartCreationDateTime` | `1→1` Customer, `1→M` CartDetails (items) |
| **CartDetail** | Items in a cart (composite key) | `cartId` (FK), `upc` (FK), `qty`, `price`, `basePrice`, `retailPrice`, `lineNumber` | `M→1` Cart, `M→1` Product |
| **Order** | Placed order with status tracking | `orderNum` (auto-generated), `customerId` (FK), `orderDateTime`, `reqDeliveryDateTime`, `status` (Unshipped/Paid/Delivered), `comments` | `M→1` Customer, `1→M` OrderDetails |
| **OrderDetail** | Line items in an order (composite key) | `orderNum` (FK), `upc` (FK), `description`, `image`, `qty`, `qtyFulfilled`, `price`, `basePrice`, `retailPrice`, `lineNumber` | `M→1` Order, `M→1` Product |
| **Role** | User roles: ADMIN, OWNER, CUSTOMER | `id`, `name` | `M↔M` Customer |
| **PaymentInfo** | Payment method metadata | `id`, `name`, `handle`, `linkHandle`, `link`, `defaultLink` | Lookup table for payment info |
| **GeneralData** | Configuration key-value store | `generalId`, `generalName`, `generalValue`, `generalCategory` | Site settings, copyright info, email config |

**Key Relationships:**
- Customer → Orders (1:M) and Cart (1:1)
- Product → CartDetails and OrderDetails (1:M)
- Cart → customers' shopping cart items (1:M CartDetails)
- Order → ordered items (1:M OrderDetails)

---

## 3. SERVICE CLASSES (Business Logic Layer)

| Service | Primary Responsibilities |
|---------|---------------------------|
| **CustomerService** | Create/update customers, password management, encryption (BCrypt), email lookup, password reset token retrieval |
| **ProductService** | CRUD operations on products; search/filter: by name, new items, dollar items, sale items, search results, categories (main/specific); supports fuzzy searching via Specifications |
| **CartService** | Create/manage shopping carts; find cart by customer ID or email; delete carts |
| **CartDetailService** | Add/remove/update individual cart line items; find items by cart and product; handle composite key management |
| **OrderService** | Create/list/delete orders; find orders by customer (ID, email, or object); basic order persistence |
| **OrderDetailService** | Manage line items in orders; composite key handling similar to CartDetail |
| **GmailEmailService** | Send emails via Gmail API with OAuth2 authentication; creates MIME messages; handles token errors gracefully |
| **EmailTemplateService** | Load HTML email templates from `resources/email-templates/`; replace placeholders; load CSS resources; safe string conversion utilities |
| **SecurityService** | Authentication/auto-login; retrieve logged-in user email and full Customer object; session-based security context management |
| **UserDetailsServiceImpl** | Spring Security integration; load user details by email; convert Customer roles to GrantedAuthorities; throws UsernameNotFoundException if user not found |
| **PaymentInfoService** | Manage payment method metadata (lookup table) |
| **GeneralDataService** | Retrieve site configuration values (copyright name, main style, receiver email, etc.) |

---

## 4. REPOSITORY INTERFACES (Data Access Layer)

All extend JPA `CrudRepository` with custom query methods (@Query):

| Repository | Key Methods |
|------------|------------|
| **ProductRepository** | `findByName()`, `getNewItems()`, `getDollarItems()`, `getSaleItems()`, `getSearchResults()`, `getSearchResultsWithStock()`, `findByCategoryMain()`, `findByCategorySpecific()`, `findAllCategoryMainAsc()`, `findAllCategorySpecificAsc()`, `findAllCategorySpecificUnderMainAsc()`, `findProductsWithTransparentImages()` |
| **CustomerRepository** | `findByName()`, `findByEmail()`, `updatePassword()`, `findByResetToken()` |
| **CartRepository** | `findByCustomer()` (custom derived query) |
| **CartDetailRepository** | Find items by cart and product |
| **OrderRepository** | `findByCustomer()` (custom derived query) |
| **OrderDetailRepository** | Find items by order |
| **RoleRepository** | Standard CRUD for roles |
| **PaymentInfoRepository** | Standard CRUD for payment info |
| **GeneralDataRepository** | Standard CRUD for config settings |

---

## 5. SECURITY CONFIGURATION

**WebSecurityConfig** – Spring Security 5.7+ setup:

**Authentication:**
- Form-based login: `/login` endpoint with custom success handler
- BCrypt password encoding
- User details loaded from database via `UserDetailsServiceImpl`

**Role-Based Authorization (3 roles):**
- `ROLE_ADMIN` – Full system access
- `ROLE_OWNER` – Business owner; same access as admin
- `ROLE_CUSTOMER` – Regular user

**URL Access Rules:**
- **Public (no auth required)**: Static resources, `/403`, `/category/**`, `/dollarama`, `/forgotPassword`, `/index`, `/login`, `/newitems`, `/sale`, `/search`, `/signup`, `/privacyPolicy`, `/resendConfirmation/**`, `/resetPassword`, `/printOrder/**`, `/images`
- **Admin/Owner only**: `/admin/**`, `/connect`, `/oauth2/callback`
- **Authenticated**: Everything else (account, orders, cart, checkout)

**Session Management:**
- Automatic redirect to `/login` on invalid/expired session
- 5-hour time zone offset adjustment (hourDiffFromDb)

**CSRF Protection:** Enabled for form posts

**OAuth2:**
- Google OAuth2 client integration for third-party authentication

---

## 6. CUSTOM VALIDATORS & UTILITIES

### Validators

**CustomerFormValidator**
- Validates customer registration and account updates
- Checks: first/last name (≤50 chars), email (6-50 chars, uniqueness), password (8-32 chars, matching confirmation), address/city/phone lengths
- Two modes: `validate()` and `validatePasswordReset()`

**PasswordResetValidator**
- Rate limiting for password reset requests (prevents brute-force)

**UserValidator**
- User input validation

### Utilities

**Utils**
- `currentYear()` – Get current year for copyright, etc.
- `getCartInSession()` / `removeCartInSession()` – Session-based cart management
- `getLastOrderedCartInSession()` – Retrieve last order's cart from session
- `getColumnLength()` – Reflection-based utility to extract JPA `@Column.length()` for form field max-length attributes
- `encodePath()` / `buildFullUrl()` – URL encoding with preserved slashes

**ImageUtils**
- Image processing and validation

**GmailSender**
- Low-level email sending support (likely deprecated in favor of GmailEmailService)

**DbMetadata**
- Database metadata utilities

---

## 7. CONFIGURATION CLASSES

**WebSecurityConfig**
- Spring Security setup (covered above)

**WebConfiguration**
- General web configuration (interceptors, formatters, etc.)

**CloudinaryConfig**
- Configures Cloudinary client for image hosting
- Images for products uploaded to Cloudinary instead of local storage

**GmailConfig**
- Sets up Gmail API client with OAuth2 refresh token for sending transactional emails

**GmailProperties**
- Configuration properties for Gmail (sender email, refresh token, etc.)

---

## 8. OVERALL ARCHITECTURE PATTERN

### Architecture Style: **Layered MVC with Service Layer**

```
┌─────────────────────────────────────┐
│         View Layer (JSP/JSTL)       │  Browser templates
├─────────────────────────────────────┤
│   Controller Layer                   │  Request routing, form binding
│  (MainController, AdminControllers)  │
├─────────────────────────────────────┤
│   Service Layer                      │  Business logic, validation
│  (CustomerService, ProductService,   │  Authorization, email, etc.
│   CartService, OrderService, etc.)   │
├─────────────────────────────────────┤
│  Repository Layer (Data Access)      │  JPA interfaces, SQL queries
│  (ProductRepository, etc.)           │
├─────────────────────────────────────┤
│   Database (MySQL/H2)                │  Persistent data storage
└─────────────────────────────────────┘
```

### Key Patterns Used:

1. **Spring MVC**: Controller → Service → Repository → Database
2. **Spring Security**: Role-based access control (RBAC), session-based authentication
3. **JPA/Hibernate**: Entity mapping, lazy loading, composite keys (`CartDetailId`, `OrderDetailId`)
4. **Dependency Injection**: Constructor and field-based injection via Spring
5. **Transactional Management**: `@Transactional` annotations for data consistency
6. **OAuth2 Integration**: Google authentication for third-party login
7. **Template Method Pattern**: `BaseController` provides common functionality inherited by specific controllers
8. **Strategy Pattern**: Multiple email sending implementations (Gmail API primary)
9. **Repository Pattern**: Abstraction of data access via repository interfaces
10. **Validation Pattern**: Composite validators (`CustomerFormValidator`, `PasswordResetValidator`)

### Technology Stack:

| Layer | Tech |
|-------|------|
| **Framework** | Spring Boot 2.7.18, Spring Security 5.7+, Spring Data JPA |
| **Database** | MySQL 8.0 (production), H2 (embedded, testing) |
| **ORM** | Hibernate (via Spring Data JPA) |
| **View** | JSP/JSTL (traditional servlet-based, not REST/JSON) |
| **Email** | Gmail API v1 (OAuth2), JavaMail |
| **Image Hosting** | Cloudinary (CDN) |
| **OAuth2** | Google OAuth2 Client |
| **Password Security** | BCrypt |
| **Validation** | Spring Validation Framework, JSR-303 annotations |
| **Java Version** | Java 11 |

---

## 9. CURRENT CAPABILITIES SUMMARY

✅ **E-Commerce Features:**
- Product catalog with multi-level categorization (main → secondary → specific)
- Shopping cart with persistent storage per customer
- Order placement with order history tracking
- Order status management (Unshipped, Paid, Delivered)

✅ **User Management:**
- User registration with email validation and duplicate checking
- Password reset via email tokens (rate-limited)
- Customer profile management (address, payment method, contact info)
- Role-based access (Admin, Owner, Customer)

✅ **Admin/Owner Features:**
- Product CRUD with image upload to Cloudinary
- Order management dashboard
- Category management

✅ **Search & Discovery:**
- Full-text search on product names/descriptions
- Category browsing
- New items (based on last customer order date)
- Dollar items (≤$1.00)
- Sale items (discount detection)

✅ **Communication:**
- Order confirmation emails with HTML template
- Password reset emails
- Resend order confirmation option
- Print order receipts

✅ **Security:**
- BCrypt password encryption
- Session-based authentication
- CSRF protection
- Role-based authorization
- OAuth2 integration (Google)
- Password reset rate limiting

---

## 10. POTENTIAL ENHANCEMENT AREAS

**Strengths for Scaling:**
- Clean separation of concerns (Controller → Service → Repository)
- Dependency injection for testability
- Role-based security framework is extensible (add new roles easily)
- Email template abstraction supports multiple formats
- Cloudinary integration decouples image storage from application

**Potential Enhancement Areas:**
1. **REST API Layer** – Add REST controllers alongside JSP views for mobile/SPA clients
2. **Search Optimization** – Implement Elasticsearch for complex queries
3. **Caching** – Redis for cart/session data and product catalog
4. **Real-time Features** – WebSocket for order status updates, notifications
5. **Payment Integration** – Add Stripe/PayPal integration beyond metadata storage
6. **Microservices** – Extract email, image, and notification services to separate microservices
7. **API Documentation** – Swagger/OpenAPI for REST endpoints
8. **Frontend Modernization** – Replace JSP with React/Vue for better UX
9. **Testing** – Increase test coverage, integration tests for workflows
10. **Performance** – Database indexing optimization, query performance tuning
