## User Service
### RestClient.java
```java
package com.x.mechanix.externalCall;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;

@Service
public class RestClient {
    private final RestTemplate restTemplate;

    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T, R> ResponseEntity<R> makePostRequest(String url, T requestBody, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }
    public <T, R> ResponseEntity<R> makePostRequest(String url, T requestBody, ParameterizedTypeReference<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }
    public <R> ResponseEntity<R> makeGetRequest(String url, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }
}

```

The `RestClient` class you provided is a Spring `@Service` that encapsulates the functionality for making HTTP requests using Spring's `RestTemplate`. It provides methods for making `POST` and `GET` requests, which can be used to interact with other services or APIs in your backend system. Let's break down the code and understand its components and functionality.

---

### **Key Components of the `RestClient` Class**

1. **RestTemplate Injection**:
   - The `RestClient` class uses `RestTemplate` to make HTTP requests.
   - `RestTemplate` is a synchronous HTTP client provided by Spring for making RESTful API calls.
   - The `RestTemplate` instance is injected into the class via constructor injection, which is a recommended practice for dependency injection in Spring.

   ```java
   private final RestTemplate restTemplate;

   public RestClient(RestTemplate restTemplate) {
       this.restTemplate = restTemplate;
   }
   ```

2. **Generic Methods for HTTP Requests**:
   - The class provides three methods:
     - `makePostRequest` (two overloaded versions for different response types).
     - `makeGetRequest`.
   - These methods are generic, meaning they can handle different types of request bodies (`T`) and response types (`R`).

3. **POST Request Methods**:
   - **First `makePostRequest` Method**:
     - This method is used for making a `POST` request with a response type that is a simple class (e.g., `String`, `UserResponse`, etc.).
     - It takes the following parameters:
       - `url`: The endpoint URL to which the request is sent.
       - `requestBody`: The body of the request (of type `T`).
       - `responseType`: The class type of the expected response (e.g., `UserResponse.class`).
     - It constructs an `HttpEntity` with the request body and headers, then uses `RestTemplate.exchange` to send the request.

     ```java
     public <T, R> ResponseEntity<R> makePostRequest(String url, T requestBody, Class<R> responseType) {
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
         return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
     }
     ```

   - **Second `makePostRequest` Method**:
     - This method is similar to the first one but is used for more complex response types, such as collections or generic types (e.g., `List<UserResponse>`).
     - It uses `ParameterizedTypeReference` to handle generic response types.

     ```java
     public <T, R> ResponseEntity<R> makePostRequest(String url, T requestBody, ParameterizedTypeReference<R> responseType) {
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
         return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
     }
     ```

4. **GET Request Method**:
   - This method is used for making a `GET` request.
   - It takes the following parameters:
     - `url`: The endpoint URL to which the request is sent.
     - `responseType`: The class type of the expected response.
   - It constructs an `HttpEntity` with headers and uses `RestTemplate.exchange` to send the request.

   ```java
   public <R> ResponseEntity<R> makeGetRequest(String url, Class<R> responseType) {
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
       return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
   }
   ```

---

### **How This Class Fits into the User Service**

In the context of the **MechaniX** project, the `RestClient` class can be used in the **User Service** to communicate with other services, such as:
- **Authentication Service**: For validating JWT tokens or fetching user details.
- **Notification Service**: For sending notifications to users or technicians.
- **Booking Service**: For fetching or updating booking details.

For example:
- When a customer books a service, the **User Service** might use the `RestClient` to send a request to the **Booking Service** to create a new booking.
- When a technician accepts a request, the **User Service** might use the `RestClient` to notify the **Notification Service** to send a confirmation to the customer.

---

### **Example Usage**

Here’s an example of how the `RestClient` class might be used in the **User Service**:

```java
@Service
public class UserService {

    private final RestClient restClient;

    @Autowired
    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<BookingResponse> createBooking(BookingRequest bookingRequest) {
        String url = "http://booking-service/api/bookings";
        return restClient.makePostRequest(url, bookingRequest, BookingResponse.class);
    }

    public ResponseEntity<UserDetails> getUserDetails(String userId) {
        String url = "http://auth-service/api/users/" + userId;
        return restClient.makeGetRequest(url, UserDetails.class);
    }
}
```

---

### **Key Points to Note**
1. **Reusability**:
   - The `RestClient` class is designed to be reusable across the application. Any service that needs to make HTTP requests can use this class.

2. **Error Handling**:
   - The current implementation does not include error handling. In a production environment, you should handle exceptions such as `HttpClientErrorException`, `HttpServerErrorException`, or `RestClientException`.

3. **Security**:
   - If the services require authentication (e.g., JWT tokens), you can modify the `RestClient` to include an `Authorization` header.

4. **Scalability**:
   - Since the `RestClient` uses `RestTemplate`, it is synchronous and blocking. For high-performance applications, consider using `WebClient` (reactive) instead of `RestTemplate`.

---

### **Improvements**
1. **Add Error Handling**:
   - Wrap the `restTemplate.exchange` calls in try-catch blocks to handle exceptions gracefully.

2. **Add Authentication**:
   - Modify the `makePostRequest` and `makeGetRequest` methods to accept an `Authorization` header or token.

3. **Use `WebClient` for Reactive Programming**:
   - If your application is built on a reactive stack, consider replacing `RestTemplate` with `WebClient`.

4. **Logging**:
   - Add logging to track requests and responses for debugging and monitoring purposes.

---

This `RestClient` class is a solid foundation for making HTTP requests in your Spring Boot application. With some enhancements, it can be a powerful tool for inter-service communication in the **MechaniX** platform.

### Account Creation Email Service
```java
package com.x.mechanix.services.emailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("accountCreationMailService")
public class AccountCreationEmailService implements IEmailService{
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String userEmail, String username, String subject, String... details) {
        String text = buildEmailContent(username);
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private String buildEmailContent(String username) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: #e9ecef; color: #333; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; color: #333; border: 2px solid #ccc; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); }" +
                "h2 { color: #28a745; text-align: left; margin-bottom: 15px; font-size: 24px; }" +
                "p { line-height: 1.6; font-size: 16px; text-align: left; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #6c757d; text-align: left; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Welcome to Mechanix, " + username + "!</h2>" +
                "<p>Your account has been successfully created. We’re excited to have you with us!</p>" +
                "<p>Feel free to log in and start exploring all the features we offer.</p>" +
                "<p>If you have any questions, don't hesitate to reach out!</p>" +
                "<div class='footer'>" +
                "<p>Cheers,<br/>Team Mechanix</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

}

```

The `AccountCreationEmailService` class is a Spring `@Service` responsible for sending emails to users upon account creation. It implements the `IEmailService` interface, which likely defines a contract for sending emails. This class uses Spring's `JavaMailSender` to send HTML-formatted emails. Let's break down the code and understand its components and functionality.

---

### **Key Components of the `AccountCreationEmailService` Class**

1. **Dependency Injection**:
   - The class uses `JavaMailSender`, a Spring interface for sending emails, which is injected via the `@Autowired` annotation.
   - `JavaMailSender` simplifies the process of sending emails by abstracting the underlying JavaMail API.

   ```java
   @Autowired
   private JavaMailSender emailSender;
   ```

2. **Implementation of `IEmailService`**:
   - The class implements the `IEmailService` interface, which likely defines a method like `sendEmail` for sending emails.
   - This ensures that the class adheres to a consistent contract for email functionality.

   ```java
   @Override
   public void sendEmail(String userEmail, String username, String subject, String... details) {
       // Implementation
   }
   ```

3. **Email Sending Logic**:
   - The `sendEmail` method constructs and sends an email using `JavaMailSender`.
   - It creates a `MimeMessage` object, which allows for HTML content in the email.
   - The `MimeMessageHelper` class is used to simplify the process of setting email properties (e.g., recipient, subject, content).

   ```java
   MimeMessage message = emailSender.createMimeMessage();
   MimeMessageHelper helper = new MimeMessageHelper(message, true);
   helper.setTo(userEmail);
   helper.setSubject(subject);
   helper.setText(text, true); // true indicates HTML content
   emailSender.send(message);
   ```

4. **HTML Email Content**:
   - The `buildEmailContent` method constructs the HTML content for the email.
   - The email is styled using inline CSS to ensure compatibility with most email clients.
   - The content includes a welcome message and instructions for the user.

   ```java
   private String buildEmailContent(String username) {
       return "<html>" +
               "<head>" +
               "<style>" +
               "body { background-color: #e9ecef; color: #333; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
               ".container { max-width: 600px; margin: auto; background: #ffffff; color: #333; border: 2px solid #ccc; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); }" +
               "h2 { color: #28a745; text-align: left; margin-bottom: 15px; font-size: 24px; }" +
               "p { line-height: 1.6; font-size: 16px; text-align: left; }" +
               ".footer { margin-top: 30px; font-size: 14px; color: #6c757d; text-align: left; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<h2>Welcome to Mechanix, " + username + "!</h2>" +
               "<p>Your account has been successfully created. We’re excited to have you with us!</p>" +
               "<p>Feel free to log in and start exploring all the features we offer.</p>" +
               "<p>If you have any questions, don't hesitate to reach out!</p>" +
               "<div class='footer'>" +
               "<p>Cheers,<br/>Team Mechanix</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
   }
   ```

5. **Error Handling**:
   - The `sendEmail` method includes a try-catch block to handle `MessagingException`, which may occur if there are issues with sending the email.
   - Errors are logged to the console using `System.err.println` and `e.printStackTrace()`.

   ```java
   try {
       // Email sending logic
   } catch (MessagingException e) {
       System.err.println("Error sending email: " + e.getMessage());
       e.printStackTrace();
   }
   ```

---

### **How This Class Fits into the MechaniX Project**

In the **MechaniX** platform, the `AccountCreationEmailService` class is used to send welcome emails to users (customers or technicians) after they successfully create an account. This is part of the onboarding process to ensure users are informed and engaged from the start.

For example:
- When a customer signs up, the **User Service** might call this service to send a welcome email.
- When a technician registers their shop, the **Shop Onboarding Service** might use this service to send a confirmation email.

---

### **Example Usage**

Here’s an example of how the `AccountCreationEmailService` might be used in the **User Service**:

```java
@Service
public class UserService {

    @Autowired
    private AccountCreationEmailService emailService;

    public void createUser(User user) {
        // Save user to the database
        // ...

        // Send welcome email
        String userEmail = user.getEmail();
        String username = user.getUsername();
        String subject = "Welcome to MechaniX!";
        emailService.sendEmail(userEmail, username, subject);
    }
}
```

---

### **Key Points to Note**

1. **HTML Emails**:
   - The email content is formatted as HTML, allowing for rich text and styling.
   - Inline CSS is used to ensure compatibility with most email clients.

2. **Reusability**:
   - The `buildEmailContent` method is encapsulated within the class, making it easy to modify the email template without affecting other parts of the code.

3. **Error Handling**:
   - While the current implementation logs errors to the console, consider using a logging framework like SLF4J or Logback for better logging practices.

4. **Configuration**:
   - Ensure that the `JavaMailSender` is properly configured in your `application.properties` or `application.yml` file with the correct SMTP server details.

   Example configuration:
   ```properties
   spring.mail.host=smtp.example.com
   spring.mail.port=587
   spring.mail.username=your-email@example.com
   spring.mail.password=your-email-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

---

### **Improvements**

1. **Externalize Email Templates**:
   - Move the HTML email template to an external file (e.g., `welcome-email.html`) and load it dynamically. This makes it easier to update the template without modifying the code.

2. **Use a Logging Framework**:
   - Replace `System.err.println` and `e.printStackTrace()` with a proper logging framework like SLF4J.

3. **Add Support for Attachments**:
   - Extend the `sendEmail` method to support email attachments if needed.

4. **Internationalization**:
   - Add support for sending emails in different languages based on user preferences.

5. **Asynchronous Email Sending**:
   - Use `@Async` to send emails asynchronously, improving the performance of the application.

   Example:
   ```java
   @Async
   public void sendEmail(String userEmail, String username, String subject, String... details) {
       // Email sending logic
   }
   ```

---

This `AccountCreationEmailService` class is a well-structured and reusable component for sending HTML emails in the **MechaniX** platform. With some enhancements, it can be made even more robust and flexible.

---
### ServiceRequestAcceptedEmail 

```java
package com.x.mechanix.services.emailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("serviceRequestAcceptedEmail")
public class ServiceRequestAcceptedEmail implements IEmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
        String requestId = details[0];
        String technician = details[1];
        String text;
        if(details.length>2) {
            String date = details[2];
            String time = details[3];
            text = buildEmailContent(username, requestId, technician, date, time);
        }
        else {
            text = buildEmailContent(username, requestId, technician, "", "");
        }
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(text, true);
        emailSender.send(message);

    }

    private String buildEmailContent(String user, String requestId, String technician, String date, String time) {
        String dateH = "";
        String timeH = "";
        if(!date.isEmpty()) {
            dateH = "<p><strong>Date:</strong> " + date + "</p>";
            timeH = "<p><strong>Time:</strong> " + time + "</p>";
        }
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: #e9ecef; color: #495057; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); border: 1px solid #dee2e6; }" +
                "h2 { color: #28a745; text-align: left; margin-bottom: 15px; font-size: 24px; }" +
                "h3 { color: #343a40; margin-top: 20px; margin-bottom: 10px; font-size: 18px; }" +
                "p { line-height: 1.6; font-size: 16px; }" +
                "hr { border: 0; height: 1px; background: #28a745; margin: 20px 0; }" +
                ".highlight { color: #28a745; font-weight: bold; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #6c757d; text-align: left; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Hey " + user + ",</h2>" +
                "<p>We're pleased to inform you that your request has been accepted, and a technician will be in touch with you shortly.</p>" +
                "<hr>" +
                "<h3>Request Details:</h3>" +
                "<p><strong>Request ID:</strong> <span class='highlight'>" + requestId + "</span></p>" +
                "<p><strong>Technician:</strong> " + technician + "</p>" +
                dateH +
                timeH +
                "<hr>" +
                "<p>We hope your job gets done efficiently by our technician. Thank you for your patience!</p>" +
                "<p>If you have any questions, feel free to reach out.</p>" +
                "<div class='footer'>" +
                "<p>Cheers,<br/>Team Mechanix</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

```

---
### ServiceRequestCreatedEmail
```java
package com.x.mechanix.services.emailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("serviceRequestCreatedEmail")
public class ServiceRequestCreatedEmail implements IEmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
        String requestId = details[0];
        String requestDescription = details[1];
        String text;
        if(details.length>2) {
            System.out.println("getting in details created " +details[2] +" "+details[3]);
            text = buildEmailContent(username, requestId, requestDescription, details[2], details[3]);
        }
        else {
            text = buildEmailContent(username, requestId, requestDescription, "", "");
        }
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(text, true);
        emailSender.send(message);
    }

    private String buildEmailContent(String user, String requestId, String requestDescription, String date, String time) {
        String timeH = "";
        String dateH = "";
        String msg = "";
        String msgHeader = "";
        if((!date.isEmpty())&&(!time.isEmpty())) {
            dateH = "<p><strong>Date:</strong> " + date + "</p>";
            timeH = "<p><strong>Time:</strong> " + time + "</p>";
            msg = "<p>We'll notify you once technician accept your request.</p>";
            msgHeader = "<p>We're excited to let you know that your request has been scheduled.</p>";
        }
        else {
            msgHeader = "<p>We're excited to let you know that your request has been created.</p>";
            msg = "<p>We'll notify you once we find a suitable technician for this job.</p>";
        }
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: #e9ecef; color: #495057; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); border: 1px solid #dee2e6; }" +
                "h2 { color: #28a745; text-align: left; margin-bottom: 15px; font-size: 24px; }" +
                "h3 { color: #343a40; margin-top: 20px; margin-bottom: 10px; font-size: 18px; }" +
                "p { line-height: 1.6; font-size: 16px; }" +
                "hr { border: 0; height: 1px; background: #28a745; margin: 20px 0; }" +
                ".highlight { color: #28a745; font-weight: bold; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #6c757d; text-align: left; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Hey " + user + ",</h2>" +
                msgHeader +
                "<hr>" +
                "<h3>Request Details:</h3>" +
                "<p><strong>Request ID:</strong> <span class='highlight'>" + requestId + "</span></p>" +
                "<p><strong>Description:</strong> " + requestDescription + "</p>" +
                dateH +
                timeH +
                "<hr>" +
                msg +
                "<p>Thanks for your patience! If you have any questions, just reach out.</p>" +
                "<div class='footer'>" +
                "<p>Cheers,<br/>Team Mechanix</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

```

---
### ServiceRequestNotAccepted
```java
package com.x.mechanix.services.emailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("serviceRequestNotAccepted")
public class ServiceRequestNotAccepted implements IEmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
        String requestId = details[0];
        String requestDescription = details[1];
        String text;
        if(details.length>2) {
            String shopName = details[2];
            String date = details[3];
            String time = details[4];
            text = buildEmailContent(username, requestId, requestDescription, shopName, date, time);
        }
        else {
            text = buildEmailContent(username, requestId, requestDescription,"", "", "");
        }
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(text, true);
        emailSender.send(message);

    }
    private String buildEmailContent(String user, String requestId, String requestDescription, String shopName, String date, String time) {
        String shop = "";
        String msg;
        String dateH = "";
        String timeH = "";
        if(!shopName.isEmpty()) {
            shop = "<p><strong>Shop:</strong> " + shopName + "</p>";
            dateH = "<p><strong>Date:</strong> " + date + "</p>";
            timeH = "<p><strong>Time:</strong> " + time + "</p>";
            msg = "<p>We're sorry, but due to some issue technician rejected your request. Please try again later.</p>";
        }
        else {
            msg = "<p>We're sorry, but right now we are not able to assign any technician to your request. Please try again later.</p>";
        }
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: #e9ecef; color: #495057; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); border: 1px solid #dee2e6; }" +
                "h2 { color: #dc3545; text-align: left; margin-bottom: 15px; font-size: 24px; }" +
                "h3 { color: #343a40; margin-top: 20px; margin-bottom: 10px; font-size: 18px; }" +
                "p { line-height: 1.6; font-size: 16px; }" +
                "hr { border: 0; height: 1px; background: #dc3545; margin: 20px 0; }" +
                ".highlight { color: #dc3545; font-weight: bold; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #6c757d; text-align: left; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Hey " + user + ",</h2>" +
                msg +
                "<hr>" +
                "<h3>Request Details:</h3>" +
                "<p><strong>Request ID:</strong> <span class='highlight'>" + requestId + "</span></p>" +
                "<p><strong>Description:</strong> " + requestDescription + "</p>" +
                shop +
                dateH +
                timeH +
                "<hr>" +
                "<p>We appreciate your understanding. If you have any questions, feel free to reach out. <br/> Thanks for your patience!</p>" +
                "<div class='footer'>" +
                "<p>Cheers,<br/>Team Mechanix</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

}

```

The provided code consists of three email service classes in the **MechaniX** project, each responsible for sending specific types of emails related to service requests. These classes implement the `IEmailService` interface and use Spring's `JavaMailSender` to send HTML-formatted emails. Let's break down each class and understand its functionality.

---

### **1. `ServiceRequestAcceptedEmail`**

#### **Purpose**
This class sends an email to the user when a technician accepts their service request.

#### **Key Features**
1. **Dynamic Email Content**:
   - The email content is dynamically generated based on whether the request is scheduled (includes date and time) or immediate.
   - Uses the `details` array to pass additional information like `requestId`, `technician`, `date`, and `time`.

2. **HTML Email Template**:
   - The email is styled using inline CSS for compatibility with email clients.
   - Includes placeholders for dynamic data like `username`, `requestId`, `technician`, `date`, and `time`.

3. **Error Handling**:
   - The `sendEmail` method throws `MessagingException` if there is an issue with sending the email.

#### **Example Email Content**
```html
<html>
<head>
<style>
/* Inline CSS for styling */
</style>
</head>
<body>
<div class='container'>
    <h2>Hey [Username],</h2>
    <p>We're pleased to inform you that your request has been accepted, and a technician will be in touch with you shortly.</p>
    <hr>
    <h3>Request Details:</h3>
    <p><strong>Request ID:</strong> <span class='highlight'>[Request ID]</span></p>
    <p><strong>Technician:</strong> [Technician Name]</p>
    <p><strong>Date:</strong> [Date]</p>
    <p><strong>Time:</strong> [Time]</p>
    <hr>
    <p>We hope your job gets done efficiently by our technician. Thank you for your patience!</p>
    <div class='footer'>
        <p>Cheers,<br/>Team Mechanix</p>
    </div>
</div>
</body>
</html>
```

---

### **2. `ServiceRequestCreatedEmail`**

#### **Purpose**
This class sends an email to the user when a service request is created.

#### **Key Features**
1. **Dynamic Email Content**:
   - The email content changes based on whether the request is scheduled (includes date and time) or immediate.
   - Uses the `details` array to pass additional information like `requestId`, `requestDescription`, `date`, and `time`.

2. **HTML Email Template**:
   - Similar to `ServiceRequestAcceptedEmail`, but with a different message and structure.
   - Includes placeholders for dynamic data like `username`, `requestId`, `requestDescription`, `date`, and `time`.

3. **Error Handling**:
   - The `sendEmail` method throws `MessagingException` if there is an issue with sending the email.

#### **Example Email Content**
```html
<html>
<head>
<style>
/* Inline CSS for styling */
</style>
</head>
<body>
<div class='container'>
    <h2>Hey [Username],</h2>
    <p>We're excited to let you know that your request has been created.</p>
    <hr>
    <h3>Request Details:</h3>
    <p><strong>Request ID:</strong> <span class='highlight'>[Request ID]</span></p>
    <p><strong>Description:</strong> [Request Description]</p>
    <p><strong>Date:</strong> [Date]</p>
    <p><strong>Time:</strong> [Time]</p>
    <hr>
    <p>We'll notify you once we find a suitable technician for this job.</p>
    <div class='footer'>
        <p>Cheers,<br/>Team Mechanix</p>
    </div>
</div>
</body>
</html>
```

---

### **3. `ServiceRequestNotAccepted`**

#### **Purpose**
This class sends an email to the user when a service request is not accepted by any technician.

#### **Key Features**
1. **Dynamic Email Content**:
   - The email content changes based on whether the request was rejected by a specific technician (includes shop name, date, and time) or no technician was available.
   - Uses the `details` array to pass additional information like `requestId`, `requestDescription`, `shopName`, `date`, and `time`.

2. **HTML Email Template**:
   - Similar to the other email services but with a different message and structure.
   - Includes placeholders for dynamic data like `username`, `requestId`, `requestDescription`, `shopName`, `date`, and `time`.

3. **Error Handling**:
   - The `sendEmail` method throws `MessagingException` if there is an issue with sending the email.

#### **Example Email Content**
```html
<html>
<head>
<style>
/* Inline CSS for styling */
</style>
</head>
<body>
<div class='container'>
    <h2>Hey [Username],</h2>
    <p>We're sorry, but due to some issue technician rejected your request. Please try again later.</p>
    <hr>
    <h3>Request Details:</h3>
    <p><strong>Request ID:</strong> <span class='highlight'>[Request ID]</span></p>
    <p><strong>Description:</strong> [Request Description]</p>
    <p><strong>Shop:</strong> [Shop Name]</p>
    <p><strong>Date:</strong> [Date]</p>
    <p><strong>Time:</strong> [Time]</p>
    <hr>
    <p>We appreciate your understanding. If you have any questions, feel free to reach out. <br/> Thanks for your patience!</p>
    <div class='footer'>
        <p>Cheers,<br/>Team Mechanix</p>
    </div>
</div>
</body>
</html>
```

---

### **Common Features Across All Classes**

1. **Reusable Email Sending Logic**:
   - All classes use `JavaMailSender` to send emails, ensuring consistency in email delivery.
   - The `sendEmail` method follows the same structure across all classes.

2. **HTML Email Templates**:
   - Each class uses inline CSS to style the email content, ensuring compatibility with email clients.
   - Dynamic data is injected into the email template using string concatenation.

3. **Error Handling**:
   - All classes throw `MessagingException` if there is an issue with sending the email.

4. **Flexibility**:
   - The `details` array allows for flexible passing of additional information, making the classes adaptable to different scenarios.

---

### **Improvements**

1. **Externalize Email Templates**:
   - Move the HTML email templates to external files (e.g., `.html` files) and load them dynamically. This makes it easier to update the templates without modifying the code.

2. **Use a Logging Framework**:
   - Replace `System.out.println` and `System.err.println` with a proper logging framework like SLF4J.

3. **Asynchronous Email Sending**:
   - Use `@Async` to send emails asynchronously, improving the performance of the application.

   Example:
   ```java
   @Async
   public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
       // Email sending logic
   }
   ```

4. **Internationalization**:
   - Add support for sending emails in different languages based on user preferences.

5. **Unit Testing**:
   - Write unit tests for each class to ensure the email content is generated correctly and the email sending logic works as expected.

---

### **Example Usage**

Here’s how these email services might be used in the **MechaniX** platform:

```java
@Service
public class BookingService {

    @Autowired
    private ServiceRequestCreatedEmail requestCreatedEmail;

    @Autowired
    private ServiceRequestAcceptedEmail requestAcceptedEmail;

    @Autowired
    private ServiceRequestNotAccepted requestNotAcceptedEmail;

    public void createBooking(BookingRequest bookingRequest) {
        // Save booking to the database
        // ...

        // Send email to the user
        String userEmail = bookingRequest.getUserEmail();
        String username = bookingRequest.getUsername();
        String requestId = bookingRequest.getRequestId();
        String description = bookingRequest.getDescription();
        requestCreatedEmail.sendEmail(userEmail, username, "Service Request Created", requestId, description);
    }

    public void acceptBooking(String bookingId, String technicianName) {
        // Update booking status in the database
        // ...

        // Send email to the user
        String userEmail = "user@example.com"; // Fetch from database
        String username = "John Doe"; // Fetch from database
        requestAcceptedEmail.sendEmail(userEmail, username, "Service Request Accepted", bookingId, technicianName);
    }

    public void rejectBooking(String bookingId, String shopName) {
        // Update booking status in the database
        // ...

        // Send email to the user
        String userEmail = "user@example.com"; // Fetch from database
        String username = "John Doe"; // Fetch from database
        requestNotAcceptedEmail.sendEmail(userEmail, username, "Service Request Not Accepted", bookingId, "Vehicle Breakdown", shopName, "2023-10-15", "10:00 AM");
    }
}
```

---

These email service classes are well-structured and reusable components for sending notifications in the **MechaniX** platform. With some enhancements, they can be made even more robust and flexible.


---
### VerificationEmailService
```java
package com.x.mechanix.services.emailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("verificationEmailService")
public class VerificationEmailService implements IEmailService{

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
        String baseUrl = "https://user.c-09499df.kyma.ondemand.com/";
        String text = buildEmailContent(username, baseUrl + "api/v1/u/signup/verify/" + details[0]);
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(message);
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    private String buildEmailContent(String username, String verificationLink) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: #f4f4f4; color: #333; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
                ".container { max-width: 600px; margin: auto; background: #ffffff; border: 2px solid #ccc; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); }" +
                "h2 { color: #28a745; margin-bottom: 15px; font-size: 24px; text-align: left; }" +
                "p { line-height: 1.6; font-size: 16px; text-align: left; margin: 10px 0; }" +
                ".button { display: inline-block; padding: 10px 20px; margin: 20px 0; background-color: #28a745; color: white; text-decoration: none; border-radius: 5px; }" +
                "img { max-width: 100%; height: auto; border: 2px solid #28a745; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Verify Your Email</h2>" +
                "<p> Hi "+ username +",<br/>Please click on the button below to verify your email:</p>" +
                "<a href='" + verificationLink + "' class='button'>Verify Email</a>" +
                "<p>Cheers,<br/>Team Mechanix</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

}
```
The `VerificationEmailService` class is a Spring `@Service` responsible for sending verification emails to users during the account creation process. It implements the `IEmailService` interface and uses Spring's `JavaMailSender` to send HTML-formatted emails containing a verification link. Let's break down the code and understand its components and functionality.

---

### **Key Components of the `VerificationEmailService` Class**

1. **Dependency Injection**:
   - The class uses `JavaMailSender`, a Spring interface for sending emails, which is injected via the `@Autowired` annotation.
   - `JavaMailSender` simplifies the process of sending emails by abstracting the underlying JavaMail API.

   ```java
   @Autowired
   private JavaMailSender emailSender;
   ```

2. **Implementation of `IEmailService`**:
   - The class implements the `IEmailService` interface, which likely defines a method like `sendEmail` for sending emails.
   - This ensures that the class adheres to a consistent contract for email functionality.

   ```java
   @Override
   public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
       // Implementation
   }
   ```

3. **Email Sending Logic**:
   - The `sendEmail` method constructs and sends an email using `JavaMailSender`.
   - It creates a `MimeMessage` object, which allows for HTML content in the email.
   - The `MimeMessageHelper` class is used to simplify the process of setting email properties (e.g., recipient, subject, content).

   ```java
   MimeMessage message = emailSender.createMimeMessage();
   MimeMessageHelper helper = new MimeMessageHelper(message, true);
   helper.setTo(userEmail);
   helper.setSubject(subject);
   helper.setText(text, true); // true indicates HTML content
   emailSender.send(message);
   ```

4. **Dynamic Verification Link**:
   - The verification link is dynamically generated using the `details` array, which contains a unique token for email verification.
   - The `baseUrl` is combined with the token to create the full verification link.

   ```java
   String baseUrl = "https://user.c-09499df.kyma.ondemand.com/";
   String text = buildEmailContent(username, baseUrl + "api/v1/u/signup/verify/" + details[0]);
   ```

5. **HTML Email Template**:
   - The `buildEmailContent` method constructs the HTML content for the email.
   - The email is styled using inline CSS to ensure compatibility with most email clients.
   - The content includes a welcome message, a verification button, and a signature.

   ```java
   private String buildEmailContent(String username, String verificationLink) {
       return "<html>" +
               "<head>" +
               "<style>" +
               "body { background-color: #f4f4f4; color: #333; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; margin: 0; }" +
               ".container { max-width: 600px; margin: auto; background: #ffffff; border: 2px solid #ccc; border-radius: 12px; padding: 30px; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1); }" +
               "h2 { color: #28a745; margin-bottom: 15px; font-size: 24px; text-align: left; }" +
               "p { line-height: 1.6; font-size: 16px; text-align: left; margin: 10px 0; }" +
               ".button { display: inline-block; padding: 10px 20px; margin: 20px 0; background-color: #28a745; color: white; text-decoration: none; border-radius: 5px; }" +
               "img { max-width: 100%; height: auto; border: 2px solid #28a745; margin-top: 20px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<h2>Verify Your Email</h2>" +
               "<p> Hi "+ username +",<br/>Please click on the button below to verify your email:</p>" +
               "<a href='" + verificationLink + "' class='button'>Verify Email</a>" +
               "<p>Cheers,<br/>Team Mechanix</p>" +
               "</div>" +
               "</body>" +
               "</html>";
   }
   ```

6. **Error Handling**:
   - The `sendEmail` method includes a try-catch block to handle `MessagingException` and other exceptions that may occur during email sending.
   - Errors are logged to the console using `System.out.println`.

   ```java
   try {
       // Email sending logic
   } catch (Exception e) {
       System.out.println(e);
   }
   ```

---

### **How This Class Fits into the MechaniX Project**

In the **MechaniX** platform, the `VerificationEmailService` class is used during the user registration process to send a verification email to the user. This ensures that the user's email address is valid and helps prevent fake or spam accounts.

For example:
- When a customer or technician signs up, the **User Service** might call this service to send a verification email.
- The email contains a link that the user must click to verify their email address and complete the registration process.

---

### **Example Usage**

Here’s an example of how the `VerificationEmailService` might be used in the **User Service**:

```java
@Service
public class UserService {

    @Autowired
    private VerificationEmailService verificationEmailService;

    public void registerUser(User user) {
        // Save user to the database
        // ...

        // Generate verification token
        String verificationToken = generateVerificationToken(user);

        // Send verification email
        String userEmail = user.getEmail();
        String username = user.getUsername();
        String subject = "Verify Your Email Address";
        verificationEmailService.sendEmail(userEmail, username, subject, verificationToken);
    }

    private String generateVerificationToken(User user) {
        // Generate a unique token for email verification
        return UUID.randomUUID().toString();
    }
}
```

---

### **Key Points to Note**

1. **Dynamic Verification Link**:
   - The verification link is dynamically generated using a unique token, ensuring that each user receives a unique link.

2. **HTML Email Template**:
   - The email content is formatted as HTML, allowing for rich text and styling.
   - Inline CSS is used to ensure compatibility with most email clients.

3. **Error Handling**:
   - While the current implementation logs errors to the console, consider using a logging framework like SLF4J or Logback for better logging practices.

4. **Configuration**:
   - Ensure that the `JavaMailSender` is properly configured in your `application.properties` or `application.yml` file with the correct SMTP server details.

   Example configuration:
   ```properties
   spring.mail.host=smtp.example.com
   spring.mail.port=587
   spring.mail.username=your-email@example.com
   spring.mail.password=your-email-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

---

### **Improvements**

1. **Externalize Email Templates**:
   - Move the HTML email template to an external file (e.g., `verification-email.html`) and load it dynamically. This makes it easier to update the template without modifying the code.

2. **Use a Logging Framework**:
   - Replace `System.out.println` with a proper logging framework like SLF4J.

3. **Asynchronous Email Sending**:
   - Use `@Async` to send emails asynchronously, improving the performance of the application.

   Example:
   ```java
   @Async
   public void sendEmail(String userEmail, String username, String subject, String... details) throws MessagingException {
       // Email sending logic
   }
   ```

4. **Internationalization**:
   - Add support for sending emails in different languages based on user preferences.

5. **Unit Testing**:
   - Write unit tests to ensure the email content is generated correctly and the email sending logic works as expected.

---

This `VerificationEmailService` class is a well-structured and reusable component for sending verification emails in the **MechaniX** platform. With some enhancements, it can be made even more robust and flexible.