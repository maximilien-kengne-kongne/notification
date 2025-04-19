# Notification Service Spring Boot Starter

A professional notification service for sending personalized emails in Java/Spring Boot applications.

---

## 🧠 Why this service?

The idea behind this service emerged from a recurring need in enterprise applications:

- To **centralize email sending logic** instead of rewriting it in every project.
- To **send HTML emails** based on dynamic **Thymeleaf templates**.
- To **handle complex use cases** like attachments, CC/BCC fields, and embedded images.
- To **ensure quality via unit testing**, so behavior is stable and predictable.

🎯 The goal is to provide a **modular, reusable, and testable** solution that fits seamlessly into modern Java architectures.

---

## ✨ Key Features

- ✅ Send simple plain-text emails
- ✅ Send HTML emails via **Thymeleaf templates**
- ✅ Attach one or more **file attachments**
- ✅ Support for `To`, `CC`, and `BCC` recipients
- ✅ Customize sender name via organization field
- ✅ Easy integration into any Spring application

---

## Getting Started

To use this starter you will need to add the following dependency to your project.

```xml
<dependency>
    <groupId>dev.kkm</groupId>
    <artifactId>notification-spring-boot-starter </artifactId>
    <version>1.0.0</version>
</dependency>
```
---
## Configuration

Add these properties to your `application.properties or application.yml`:

``` properties
spring.mail.host=smtp.yourprovider.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```


#### Inject this bean in your service class

``` java
private CourierService courierService;
```

## How to use ?

### 1. Send basic email

``` java
CourierDetail courierDetail = CourierDetail.builder()
.withSender("noreply@company.com")
.addRecipient("client@example.com")
.withSubject("Your Order Confirmation")
.withBody("Thank you for your order!")
.build();

 courierService.sendCourier(courierDetail);

```

### 2. Send basic email with attachment

``` java
DataSource attachment = new FileDataSource("invoice.pdf");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("billing@company.com")
    .addRecipient("accounting@example.com")
    .withSubject("Monthly Invoice")
    .withBody("Please find attached the monthly invoice.")
    .addAttachment("invoice-january.pdf", attachment)
    .build();
    
     courierService.sendCourier(courierDetail);
```

### 3. Send courier with template

``` java
Map<String, Object> variables = new HashMap<>();
variables.put("name", "John Doe");
variables.put("orderNumber", "12345");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("orders@company.com")
    .addRecipient("john.doe@example.com")
    .withSubject("Order #12345 Confirmation")
    .withTemplate("order-confirmation")
    .addVariables(variables)
    .build();
    
     courierService.sendCourier(courierDetail);
```
### 4. Send courier with template and with attachment

``` java
Map<String, Object> variables = new HashMap<>();
variables.put("name", "John Doe");
variables.put("orderNumber", "12345");

DataSource attachment = new FileDataSource("invoice.pdf");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("orders@company.com")
    .addRecipient("john.doe@example.com")
    .withSubject("Order #12345 Confirmation")
    .withTemplate("order-confirmation")
    .addVariables(variables)
    .addAttachment("invoice-january.pdf", attachment)
    .build();
    
     courierService.sendCourier(courierDetail);
```

### 5. Send email with all details of courier 

``` java
Map<String, Object> variables = new HashMap<>();
variables.put("name", "John Doe");
variables.put("orderNumber", "12345");

DataSource attachment = new FileDataSource("invoice.pdf");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("orders@company.com")
    .addRecipient("john.doe@example.com")
    .addCc("john.doe@example.com")
    .addBcc("john.doe@example.com")
    .withSubject("Order #12345 Confirmation")
    .withTemplate("order-confirmation")
    .withOrganizationName("BE-SHOP")
    .addVariables(variables)
    .addAttachment("invoice-january.pdf", attachment)
    .build();
    
    courierService.sendCourier(courierDetail);
```

---

## ✅ Tests

The module is completely **unit tested with Mockito**.

To run the tests :

```bash
mvn test
```

The scenarios tested include :

- sending simple emails
- with attachment
- with template
- Email validation
- error handling 

---

## Note
 Don't add again these dependencies in your project

``` xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-configuration-processor</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

## 👨‍💻 Author

This service was designed to facilitate the management of email notifications in modern Spring Boot projects, with a particular focus on **code clarity**, **reusability**, and **good testing practices**.

Feel free to contact me : <a href="mailto:maximiliendenver@gmail.com">Maximilien KENGNE KONGNE</a>

---

