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
- ✅ Support replyTo
- ✅ Support priority
- ✅ Customize sender name via organization field
- ✅ Easy integration into any Spring application

---

## Technologies

- Java 21
- Spring Boot : 3.4.4
- Maven


## Getting Started

To use this starter you will need to add the following dependency to your project.

```xml
<dependency>
    <groupId>dev.kkm</groupId>
    <artifactId>notification-spring-boot-starter </artifactId>
    <version>1.0.0</version>
</dependency>
```

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

	</dependencies>
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
These base parameter are mandatory for any combination
``` java
        try {

            CourierDetail courierDetail = CourierDetail.builder()
                    .withSender("noreply@company.com")
                    .addRecipient("client@example.com")
                    .withSubject("Your Order Confirmation")
                    .withBody("Thank you for your order!")
                    .withOrganizationName("Organization")
                    .build();

            courierService.sendCourier(courierDetail);

        } catch (CourierException courierException) {
            switch (courierException.getStatus()) {
                case 901 -> {
                    // something went wrong during sending email (pending mode)
                }
                case 902 -> {
                    // mail not send (pending mode)
                   
                }
                case 903 -> {
                    // invalid email address (canceling mode)
                }
                case 904 -> {
                    // unsupported encoding (canceling mode)
                }
                
                case 905 -> {
                    // probably template not found (canceling mode)
                }
                default -> {
                   // invalid or required field (canceling mode)
                }
            }
        }
```

### 2. Send basic email with attachment

``` java
try {
            byte[] data = new byte[0];
            DataSource attachment  = new ByteArrayDataSource(data, "application/pdf");

            CourierDetail courierDetail1 = CourierDetail.builder()
                    .withSender("noreply@company.com")
                    .addRecipient("client@example.com")
                    .withSubject("Your Order Confirmation")
                    .withBody("Thank you for your order!")
                    .withOrganizationName("Organization")
                    .addAttachment("order-confirmation", attachment )
                    .build();

            courierService.sendCourier(courierDetail);

        } catch (CourierException courierException) {
            switch (courierException.getStatus()) {
                case 901 -> {
                    // something went wrong during sending email (pending mode)
                }
                case 902 -> {
                    // mail not send (pending mode)
                   
                }
                case 903 -> {
                    // invalid email address (canceling mode)
                }
                case 904 -> {
                    // unsupported encoding (canceling mode)
                }
                
                case 905 -> {
                    // probably template not found (canceling mode)
                }
                default -> {
                   // invalid or required field (canceling mode)
                }
            }
        }
```

### 3. Send courier with template

``` java
 try {
 
Map<String, Object> variables = new HashMap<>();
variables.put("name", "John Doe");
variables.put("orderNumber", "12345");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("orders@company.com")
    .addRecipient("john.doe@example.com")
    .withSubject("Order #12345 Confirmation")
    .withOrganizationName("Organization")
    .withTemplate("order-confirmation")
    .addVariables(variables)
    .build();
    
        courierService.sendCourier(courierDetail);
        
        } catch (CourierException courierException) {
            switch (courierException.getStatus()) {
                case 901 -> {
                    // something went wrong during sending email (pending mode)
                }
                case 902 -> {
                    // mail not send (pending mode)
                   
                }
                case 903 -> {
                    // invalid email address (canceling mode)
                }
                case 904 -> {
                    // unsupported encoding (canceling mode)
                }
                
                case 905 -> {
                    // probably template not found (canceling mode)
                }
                default -> {
                   // invalid or required field (canceling mode)
                }
            }
        }     
```
### 4. Send courier with template and with attachment

``` java

        try {
Map<String, Object> variables = new HashMap<>();
variables.put("name", "John Doe");
variables.put("orderNumber", "12345");

byte[] data = new byte[0];
DataSource attachment = new ByteArrayDataSource(data, "application/pdf");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("orders@company.com")
    .addRecipient("john.doe@example.com")
    .withSubject("Order #12345 Confirmation")
    .withTemplate("order-confirmation")
    .withOrganizationName("Organization")
    .addVariables(variables)
    .addAttachment("invoice-january", attachment)
    .build();
    
     courierService.sendCourier(courierDetail);
     
         } catch (CourierException courierException) {
            switch (courierException.getStatus()) {
                case 901 -> {
                    // something went wrong during sending email (pending mode)
                }
                case 902 -> {
                    // mail not send (pending mode)
                   
                }
                case 903 -> {
                    // invalid email address (canceling mode)
                }
                case 904 -> {
                    // unsupported encoding (canceling mode)
                }
                
                case 905 -> {
                    // probably template not found (canceling mode)
                }
                default -> {
                   // invalid or required field (canceling mode)
                }
            }
        }    
```

### 5. Send email with all details of courier 

``` java
        try {
Map<String, Object> variables = new HashMap<>();
variables.put("name", "John Doe");
variables.put("orderNumber", "12345");

byte[] data = new byte[0];
DataSource attachment = new ByteArrayDataSource(data, "application/pdf");

CourierDetail courierDetail = CourierDetail.builder()
    .withSender("orders@company.com")
    .addRecipient("john.doe@example.com")
    .addCc("john.doe@example.com")
    .addBcc("john.doe@example.com")
    .withSubject("Order #12345 Confirmation")
    .withTemplate("order-confirmation")
    .withOrganizationName("BE-SHOP")
    .addVariables(variables)
    .addAttachment("invoice-january", attachment)
    .build();
    
    courierService.sendCourier(courierDetail);
    
        } catch (CourierException courierException) {
            switch (courierException.getStatus()) {
                case 901 -> {
                    // something went wrong during sending email (pending mode)
                }
                case 902 -> {
                    // mail not send (pending mode)
                   
                }
                case 903 -> {
                    // invalid email address (canceling mode)
                }
                case 904 -> {
                    // unsupported encoding (canceling mode)
                }
                
                case 905 -> {
                    // probably template not found (canceling mode)
                }
                default -> {
                   // invalid or required field (canceling mode)
                }
            }
        }    
```

## Status code explanation

``` json5
900 : invalid or required field
901 : something went wrong during sending email
902 : mail not send
903 : invalid email address
904 : unsupported encoding
905 : probably template not found

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

## ⏭️ To do
 - send sms notification
---

## 👨‍💻 Author

This service was designed to facilitate the management of email notifications in modern Spring Boot projects, with a particular focus on **code clarity**, **reusability**, and **good testing practices**.

Feel free to contact me : <a href="mailto:maximiliendenver@gmail.com">Maximilien KENGNE KONGNE</a>

---

