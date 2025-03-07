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


In your **MechaniX** project, you have two main types of classes: **Entities** and **DTOs (Data Transfer Objects)**. These serve different purposes and are used in different layers of your application. Let's break down what they are, why they are used, and why they are needed.

---

### **1. Entities**

#### **What Are Entities?**
Entities are classes that represent the data model of your application. They are typically mapped to database tables using an ORM (Object-Relational Mapping) framework like **Hibernate** (which is used in Spring Boot with JPA). In your project, the `ServiceRequest` and `User` classes are entities.

#### **Key Features of Entities**
1. **Database Mapping**:
   - Entities are annotated with `@Entity`, which tells the ORM framework to map the class to a database table.
   - Fields in the entity class are mapped to columns in the database table using annotations like `@Id`, `@GeneratedValue`, etc.

   Example:
   ```java
   @Entity
   @Data
   public class ServiceRequest {
       @Id
       private String serviceRequestUUID;
       private String userEmail;
       private String vehicleType;
       private String modelName;
       private String serviceDescription;
       private String serviceType;
       private Float latitude;
       private Float longitude;
       private String status;
       private String date;
       private String time;
       private String createdAt;
       private String assignedTechnician;
       private String shopName;
       private String requestType;
   }
   ```

2. **Persistence**:
   - Entities are used to interact with the database. They represent the data that is stored, retrieved, updated, or deleted.

3. **Business Logic**:
   - Entities can contain business logic related to the data they represent. For example, validation logic or methods to manipulate the data.

#### **Why Are Entities Used?**
- **Database Interaction**: Entities are used to define the structure of your database tables and to perform CRUD (Create, Read, Update, Delete) operations.
- **Data Integrity**: By defining entities, you ensure that the data stored in the database adheres to a specific structure and constraints.
- **ORM Integration**: Entities allow you to work with objects in your code while the ORM framework handles the translation to SQL queries.

---

### **2. DTOs (Data Transfer Objects)**

#### **What Are DTOs?**
DTOs are classes used to transfer data between different layers of your application, such as between the **controller** and the **service** layer, or between the **backend** and the **frontend**. In your project, classes like `ServiceRequestDTO`, `LoginRequestDTO`, and `AvailableTechnicianDTO` are DTOs.

#### **Key Features of DTOs**
1. **Data Transfer**:
   - DTOs are used to encapsulate data and send it across different layers of the application.
   - They typically contain only fields, getters, and setters, and no business logic.

   Example:
   ```java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class LoginRequestDTO {
       private String email;
       private String password;
   }
   ```

2. **Customization**:
   - DTOs allow you to customize the data that is sent or received. For example, you might exclude sensitive fields (like passwords) or include additional fields that are not part of the entity.

3. **Decoupling**:
   - DTOs help decouple the internal data model (entities) from the external API or UI. This makes your application more flexible and easier to maintain.

#### **Why Are DTOs Used?**
- **Security**: DTOs allow you to control what data is exposed to the outside world. For example, you might exclude sensitive fields like passwords from the DTO.
- **Performance**: By transferring only the required data, DTOs can reduce the amount of data sent over the network, improving performance.
- **Flexibility**: DTOs allow you to customize the data structure for different use cases without modifying the underlying entities.
- **Separation of Concerns**: DTOs help separate the data model (entities) from the data representation (DTOs), making the code easier to maintain and extend.

---

### **Comparison Between Entities and DTOs**

| **Aspect**               | **Entities**                                      | **DTOs**                                      |
|--------------------------|--------------------------------------------------|-----------------------------------------------|
| **Purpose**              | Represent the data model and map to the database. | Transfer data between layers or systems.      |
| **Annotations**          | Use JPA annotations like `@Entity`, `@Id`, etc.  | No database-specific annotations.             |
| **Business Logic**       | May contain business logic.                      | Typically contain no business logic.          |
| **Data Exposure**        | Represent the full data model.                   | Expose only the required data.                |
| **Usage**                | Used for database operations.                    | Used for API communication or data transfer.  |
| **Example**              | `ServiceRequest`, `User`                         | `ServiceRequestDTO`, `LoginRequestDTO`        |

---

### **Why Are Both Needed?**

1. **Separation of Concerns**:
   - Entities are tied to the database and represent the internal data model.
   - DTOs are used for communication between layers or systems and represent the external data model.
   - This separation ensures that changes in the database schema do not directly affect the API or UI.

2. **Security**:
   - Entities may contain sensitive data (e.g., passwords) that should not be exposed to the outside world.
   - DTOs allow you to control what data is exposed, improving security.

3. **Flexibility**:
   - DTOs allow you to customize the data structure for different use cases without modifying the underlying entities.
   - For example, you might have multiple DTOs for the same entity, each tailored for a specific use case.

4. **Performance**:
   - By transferring only the required data, DTOs can reduce the amount of data sent over the network, improving performance.

---

### **Example Workflow**

1. **User Registration**:
   - The frontend sends a `SignupDTO` to the backend.
   - The backend converts the `SignupDTO` to a `User` entity and saves it to the database.
   - The backend sends a `UserDTO` (excluding the password) back to the frontend.

2. **Service Request Creation**:
   - The frontend sends a `ServiceRequestDTO` to the backend.
   - The backend converts the `ServiceRequestDTO` to a `ServiceRequest` entity and saves it to the database.
   - The backend sends a response (e.g., a success message or the created `ServiceRequestDTO`) back to the frontend.

3. **Login**:
   - The frontend sends a `LoginRequestDTO` (containing email and password) to the backend.
   - The backend validates the credentials and sends a `LoginResponseDTO` (containing a JWT token) back to the frontend.

---

### **Conclusion**

- **Entities** are used to represent the data model and interact with the database.
- **DTOs** are used to transfer data between layers or systems, providing flexibility, security, and performance benefits.
- Both are essential for building scalable, maintainable, and secure applications. By separating the internal data model (entities) from the external data representation (DTOs), you can ensure that your application is flexible and easy to maintain.


The conversion of a **DTO (Data Transfer Object)** to an **Entity** is a common task in Spring Boot applications. This process typically occurs in the **service layer**, where the DTO (received from the controller) is transformed into an entity before being saved to the database. Similarly, an entity can be converted back to a DTO before sending a response to the client.

Let’s break down the workflow and provide an example.

---

### **Workflow: DTO to Entity Conversion**

1. **Client Sends a Request**:
   - The client (e.g., frontend or API consumer) sends a request containing data in the form of a DTO.

2. **Controller Receives the DTO**:
   - The controller receives the DTO and passes it to the service layer.

3. **Service Layer Converts DTO to Entity**:
   - The service layer converts the DTO into an entity. This is where the business logic resides.

4. **Entity is Saved to the Database**:
   - The entity is passed to the repository layer, where it is saved to the database.

5. **Entity is Converted Back to DTO (Optional)**:
   - After saving or retrieving data, the entity may be converted back to a DTO to send a response to the client.

---

### **Example: User Registration**

Let’s take the example of a **User Registration** feature. Here’s how the DTO (`SignupDTO`) is converted to an entity (`User`) and saved to the database.

---

#### **1. DTO Class**
This is the `SignupDTO` class, which is used to transfer data from the client to the server.

```java
package com.x.mechanix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
}
```

---

#### **2. Entity Class**
This is the `User` entity class, which represents the data model in the database.

```java
package com.x.mechanix.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "app_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
}
```

---

#### **3. Service Layer**
The service layer is responsible for converting the DTO to an entity and saving it to the database.

```java
package com.x.mechanix.services;

import com.x.mechanix.dto.SignupDTO;
import com.x.mechanix.entities.User;
import com.x.mechanix.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(SignupDTO signupDTO) {
        // Convert DTO to Entity
        User user = new User();
        user.setName(signupDTO.getName());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(signupDTO.getPassword()); // In a real app, password should be hashed

        // Save the entity to the database
        return userRepository.save(user);
    }
}
```

---

#### **4. Repository Layer**
The repository layer interacts with the database. It uses Spring Data JPA to perform CRUD operations.

```java
package com.x.mechanix.repositories;

import com.x.mechanix.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

---

#### **5. Controller Layer**
The controller receives the DTO from the client and passes it to the service layer.

```java
package com.x.mechanix.controllers;

import com.x.mechanix.dto.SignupDTO;
import com.x.mechanix.entities.User;
import com.x.mechanix.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody SignupDTO signupDTO) {
        // Pass the DTO to the service layer
        return userService.registerUser(signupDTO);
    }
}
```

---

### **Workflow in Action**

1. **Client Sends a Request**:
   - The client sends a `POST` request to `/api/users/register` with the following JSON payload:
     ```json
     {
       "name": "John Doe",
       "email": "john.doe@example.com",
       "password": "password123"
     }
     ```

2. **Controller Receives the DTO**:
   - The `UserController` receives the `SignupDTO` object from the request body.

3. **Service Converts DTO to Entity**:
   - The `UserService` converts the `SignupDTO` to a `User` entity:
     ```java
     User user = new User();
     user.setName(signupDTO.getName());
     user.setEmail(signupDTO.getEmail());
     user.setPassword(signupDTO.getPassword());
     ```

4. **Entity is Saved to the Database**:
   - The `User` entity is saved to the database using the `UserRepository`.

5. **Response is Sent to the Client**:
   - The saved `User` entity is returned to the client as the response.

---

### **Optional: Entity to DTO Conversion**

If you want to send a response back to the client in the form of a DTO (e.g., excluding sensitive fields like `password`), you can convert the entity back to a DTO.

#### **DTO for Response**
```java
package com.x.mechanix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
}
```

#### **Service Layer (Updated)**
```java
public UserDTO registerUser(SignupDTO signupDTO) {
    // Convert DTO to Entity
    User user = new User();
    user.setName(signupDTO.getName());
    user.setEmail(signupDTO.getEmail());
    user.setPassword(signupDTO.getPassword());

    // Save the entity to the database
    User savedUser = userRepository.save(user);

    // Convert Entity to DTO
    UserDTO userDTO = new UserDTO();
    userDTO.setId(savedUser.getId());
    userDTO.setName(savedUser.getName());
    userDTO.setEmail(savedUser.getEmail());

    return userDTO;
}
```

#### **Controller Layer (Updated)**
```java
@PostMapping("/register")
public UserDTO registerUser(@RequestBody SignupDTO signupDTO) {
    return userService.registerUser(signupDTO);
}
```

---

### **Summary**

- **DTO to Entity Conversion**: Used to transform data received from the client into a format that can be saved to the database.
- **Entity to DTO Conversion**: Used to transform data retrieved from the database into a format that can be sent to the client.
- **Workflow**:
  1. Client sends a DTO.
  2. Controller receives the DTO and passes it to the service.
  3. Service converts the DTO to an entity and saves it to the database.
  4. Service converts the entity back to a DTO (optional) and sends a response to the client.

This separation ensures that your application is modular, secure, and easy to maintain.

The provided code consists of several classes that handle **service requests** in the **MechaniX** project. These classes are responsible for creating, scheduling, accepting, rejecting, and managing service requests. They also include a scheduled job to handle unassigned service requests. Let's break down the functionality of each class and explain the workflow.

---

### **1. `ScheduleServiceRequestHandler`**

#### **Purpose**
This class handles the scheduling of service requests. It implements the `IScheduleServiceRequest` interface and provides methods for scheduling, accepting, and rejecting service requests.

#### **Key Features**
1. **Scheduling a Service Request**:
   - The `scheduleServiceRequest` method creates a new `ServiceRequest` entity from the `ServiceRequestDTO` and saves it to the database.
   - It validates the input data using the `hasValidValuesSchedule` method.
   - The request type is set to `"Schedule"`.

   ```java
   @Override
   public ServiceRequestDTO scheduleServiceRequest(ServiceRequestDTO serviceRequestDTO) {
       ServiceRequest serviceRequest = new ServiceRequest();
       if (hasValidValuesSchedule(serviceRequestDTO)) {
           serviceRequest.setServiceRequestUUID(UUID.randomUUID().toString());
           serviceRequest.setDate(serviceRequestDTO.getDate());
           serviceRequest.setTime(serviceRequestDTO.getTime());
           serviceRequest.setStatus(Status.CREATED.toString());
           serviceRequest.setUserEmail(tenantInfo.getUser().getEmail());
           serviceRequest.setServiceDescription(serviceRequestDTO.getServiceDescription());
           serviceRequest.setModelName(serviceRequestDTO.getModelName());
           serviceRequest.setLatitude(serviceRequestDTO.getLatitude());
           serviceRequest.setLongitude(serviceRequestDTO.getLongitude());
           serviceRequest.setVehicleType(serviceRequestDTO.getVehicleType());
           serviceRequest.setAssignedTechnician(serviceRequestDTO.getAssignedTechnician());
           serviceRequest.setShopName(serviceRequest.getShopName());
           serviceRequest.setCreatedAt(LocalTime.now().toString());
           serviceRequest.setServiceType(serviceRequestDTO.getServiceType());
           serviceRequest.setRequestType("Schedule");
           serviceRequestRepository.save(serviceRequest);
       }
       return E2DTO.convertToServiceRequestDTO(serviceRequest);
   }
   ```

2. **Accepting a Service Request**:
   - The `acceptServiceRequest` method updates the status of a service request to `"ACCEPTED"` and assigns a technician.
   - It validates the input data using the `hasValidValuesAccept` method.

   ```java
   @Override
   public ServiceRequestDTO acceptServiceRequest(ServiceRequestDTO serviceRequestDTO) throws UserMismatchException {
       if (hasValidValuesAccept(serviceRequestDTO)) {
           Optional<ServiceRequest> serviceRequestOptional = serviceRequestRepository.findById(serviceRequestDTO.getServiceRequestUUID());
           if (serviceRequestOptional.isPresent()) {
               ServiceRequest serviceRequest = serviceRequestOptional.get();
               if (!serviceRequest.getStatus().equals(Status.CREATED.toString())) {
                   return new ServiceRequestDTO();
               }
               serviceRequest.setServiceRequestUUID(serviceRequestDTO.getServiceRequestUUID());
               serviceRequest.setAssignedTechnician(serviceRequestDTO.getAssignedTechnician());
               serviceRequest.setStatus(Status.ACCEPTED.toString());
               serviceRequestRepository.save(serviceRequest);
               return E2DTO.convertToServiceRequestDTO(serviceRequest);
           }
       }
       return null;
   }
   ```

3. **Rejecting a Service Request**:
   - The `rejectServiceRequest` method updates the status of a service request to `"REJECTED"`.

   ```java
   @Override
   public ServiceRequestDTO rejectServiceRequest(ServiceRequestDTO serviceRequestDTO) throws UserMismatchException {
       Optional<ServiceRequest> serviceRequestOptional = serviceRequestRepository.findById(serviceRequestDTO.getServiceRequestUUID());
       if (serviceRequestOptional.isPresent()) {
           ServiceRequest serviceRequest = serviceRequestOptional.get();
           if (!serviceRequest.getStatus().equals(Status.CREATED.toString())) {
               return new ServiceRequestDTO();
           }
           serviceRequest.setStatus(Status.REJECTED.toString());
           serviceRequestRepository.save(serviceRequest);
           return E2DTO.convertToServiceRequestDTO(serviceRequest);
       }
       return null;
   }
   ```

---

### **2. `ServiceRequestHandler`**

#### **Purpose**
This class handles immediate service requests (e.g., "Book Now" requests). It implements the `IServiceRequest` interface and provides methods for creating, accepting, and marking service requests as `"NOT_ASSIGNED"`.

#### **Key Features**
1. **Creating a Service Request**:
   - The `createServiceRequest` method creates a new `ServiceRequest` entity from the `ServiceRequestDTO` and saves it to the database.
   - The request type is set to `"Book"`.

   ```java
   @Override
   public ServiceRequestDTO createServiceRequest(ServiceRequestDTO serviceRequestDTO) {
       ServiceRequest serviceRequest = new ServiceRequest();
       if (hasValidValuesCreate(serviceRequestDTO)) {
           serviceRequest.setServiceRequestUUID(UUID.randomUUID().toString());
           serviceRequest.setDate(LocalDate.now().toString());
           serviceRequest.setTime(LocalTime.now().toString());
           serviceRequest.setStatus(Status.CREATED.toString());
           serviceRequest.setUserEmail(tenantInfo.getUser().getEmail());
           serviceRequest.setServiceDescription(serviceRequestDTO.getServiceDescription());
           serviceRequest.setModelName(serviceRequestDTO.getModelName());
           serviceRequest.setLatitude(serviceRequestDTO.getLatitude());
           serviceRequest.setLongitude(serviceRequestDTO.getLongitude());
           serviceRequest.setVehicleType(serviceRequestDTO.getVehicleType());
           serviceRequest.setCreatedAt(LocalTime.now().toString());
           serviceRequest.setServiceType(serviceRequest.getServiceType());
           serviceRequest.setRequestType("Book");
           serviceRequestRepository.save(serviceRequest);
       }
       return E2DTO.convertToServiceRequestDTO(serviceRequest);
   }
   ```

2. **Accepting a Service Request**:
   - Similar to `ScheduleServiceRequestHandler`, this method updates the status of a service request to `"ACCEPTED"`.

3. **Marking a Service Request as `"NOT_ASSIGNED"`**:
   - The `notAssignedServiceRequest` method updates the status of a service request to `"NOT_ASSIGNED"`.

   ```java
   @Override
   public ServiceRequestDTO notAssignedServiceRequest(ServiceRequestDTO serviceRequestDTO) throws UserMismatchException {
       Optional<ServiceRequest> serviceRequestOptional = serviceRequestRepository.findById(serviceRequestDTO.getServiceRequestUUID());
       if (serviceRequestOptional.isPresent()) {
           ServiceRequest serviceRequest = serviceRequestOptional.get();
           if (!serviceRequest.getStatus().equals(Status.CREATED.toString())) {
               return new ServiceRequestDTO();
           }
           serviceRequest.setStatus(Status.NOT_ASSIGNED.toString());
           serviceRequestRepository.save(serviceRequest);
           return E2DTO.convertToServiceRequestDTO(serviceRequest);
       }
       return null;
   }
   ```

---

### **3. `ServiceRequestPollJob`**

#### **Purpose**
This class is a scheduled job that runs every 5 minutes to check for service requests that have not been assigned to a technician within the last 5 minutes. It updates the status of such requests to `"NOT_ASSIGNED"` and notifies the user.

#### **Key Features**
1. **Scheduled Job**:
   - The `processServiceRequests` method runs every 5 minutes (`@Scheduled(fixedRate = 300000)`).
   - It retrieves service requests created within the last 5 minutes using the `findRequestsCreatedWithinLastFiveMinutes` method.

   ```java
   @Scheduled(fixedRate = 300000)
   public void processServiceRequests() throws Exception {
       LocalTime fiveMinutesAgo = LocalTime.now().minusMinutes(5);
       List<ServiceRequest> requests = serviceRequestRepository.findRequestsCreatedWithinLastFiveMinutes(fiveMinutesAgo.toString());
       try {
           for (ServiceRequest serviceRequest : requests) {
               UserDTO userDTO = userDetailsService.loadUserByEmail(serviceRequest.getUserEmail());
               String url = "https://technician.c-09499df.kyma.ondemand.com/api/v1/deleteRequest";
               DeleteServiceRequestDTO deleteServiceRequestDTO = new DeleteServiceRequestDTO();
               deleteServiceRequestDTO.setServiceRequestUUID(serviceRequest.getServiceRequestUUID());
               deleteServiceRequestDTO.setAssignedTechnician("");
               restClient.makePostRequest(url, deleteServiceRequestDTO, String.class);
               serviceRequest.setStatus(Status.NOT_ASSIGNED.toString());
               serviceRequestRepository.save(serviceRequest);
               serviceRequestNotAccepted.sendEmail(serviceRequest.getUserEmail(), userDTO.getName(), "No Technician Available :(", serviceRequest.getServiceRequestUUID(), serviceRequest.getServiceDescription());
           }
       } catch (Exception e) {
           throw new Exception("Error occurred while processing poll jobs. " + e.getMessage());
       }
   }
   ```

2. **Notification**:
   - If a service request is not assigned within 5 minutes, the user is notified via email using the `serviceRequestNotAccepted` email service.

---

### **4. `UserServiceRequest`**

#### **Purpose**
This class handles fetching service requests for a specific user. It retrieves paginated results of service requests from the database.

#### **Key Features**
1. **Fetching Service Requests**:
   - The `getServiceRequest` method retrieves paginated service requests for the logged-in user.
   - It uses Spring Data JPA's `Pageable` and `Page` for pagination.

   ```java
   public Page<ServiceRequestDTO> getServiceRequest(int page, int size) {
       Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDate")));
       Page<ServiceRequest> serviceRequestPage = serviceRequestRepository.findByUserEmail(tenantInfo.getUser().getEmail(), pageable);
       Page<ServiceRequestDTO> serviceRequestDTOPage = serviceRequestPage.map(this::convertToServiceRequestDTO);
       return serviceRequestDTOPage;
   }
   ```

2. **DTO Conversion**:
   - The `convertToServiceRequestDTO` method converts a list of `ServiceRequest` entities to a list of `ServiceRequestDTO` objects.

   ```java
   private List<ServiceRequestDTO> convertToServiceRequestDTO(List<ServiceRequest> serviceRequests) {
       List<ServiceRequestDTO> serviceRequestDTOList = new ArrayList<>();
       for (ServiceRequest serviceRequest : serviceRequests) {
           serviceRequestDTOList.add(E2DTO.convertToServiceRequestDTO(serviceRequest));
       }
       return serviceRequestDTOList;
   }
   ```

---

### **Summary**

- **`ScheduleServiceRequestHandler`**: Handles scheduling, accepting, and rejecting service requests.
- **`ServiceRequestHandler`**: Handles immediate service requests and marks them as `"NOT_ASSIGNED"` if no technician is available.
- **`ServiceRequestPollJob`**: A scheduled job that checks for unassigned service requests and notifies users.
- **`UserServiceRequest`**: Retrieves paginated service requests for a specific user.

These classes work together to manage the lifecycle of service requests in the **MechaniX** platform, ensuring that requests are created, assigned, and handled efficiently.




## Controllers

The provided code consists of four **Spring Boot REST controllers** that handle different functionalities in the **MechaniX** project. Let's break down each controller and explain its purpose, functionality, and workflow.

---

### **1. `AvailableTechnicianController`**

#### **Purpose**
This controller is responsible for fetching a list of available technicians within a specified distance from the user's location.

#### **Key Features**
1. **Endpoint**:
   - `GET /api/v1/u/getAvailableTechnician/{latitude}/{longitude}`: Fetches available technicians based on the user's latitude and longitude.

2. **Workflow**:
   - The controller makes a `POST` request to an external service (`https://technician.c-09499df.kyma.ondemand.com/api/v1/availabletechnicians`) to retrieve the list of available technicians.
   - It uses the `RestClient` class to make the HTTP request.
   - The response from the external service is deserialized into a list of `AvailableTechnicianDTO` objects using `ObjectMapper`.

   ```java
   @GetMapping("/api/v1/u/getAvailableTechnician/{latitude}/{longitude}")
   public ResponseEntity<?> getAvailableTechnician(@PathVariable Float latitude, @PathVariable Float longitude) {
       String url = "https://technician.c-09499df.kyma.ondemand.com/api/v1/availabletechnicians";
       try {
           AvailableTechnicianRequestDTO technicianRequestDTO = new AvailableTechnicianRequestDTO();
           technicianRequestDTO.setLongitude(longitude);
           technicianRequestDTO.setLatitude(latitude);
           technicianRequestDTO.setNumTech(10);
           technicianRequestDTO.setDistance(10000);
           ResponseEntity<String> response = restClient.makePostRequest(url, technicianRequestDTO, String.class);
           String responseBody = response.getBody();
           ObjectMapper objectMapper = new ObjectMapper();
           List<AvailableTechnicianDTO> technicians = objectMapper.readValue(responseBody,
                   objectMapper.getTypeFactory().constructCollectionType(List.class, AvailableTechnicianDTO.class));
           return new ResponseEntity<>(technicians, HttpStatus.OK);
       } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
   ```

3. **Error Handling**:
   - If an exception occurs, it returns an error message with an HTTP status code of `500 (INTERNAL_SERVER_ERROR)`.

---

### **2. `CallMe`**

#### **Purpose**
This is a simple test controller used to verify that the application is running and accessible.

#### **Key Features**
1. **Endpoint**:
   - `GET /callMe`: Returns a simple string response.

   ```java
   @GetMapping("/callMe")
   public ResponseEntity<String> callMe() {
       return new ResponseEntity<>("Ohh Yeah!!!", HttpStatus.OK);
   }
   ```

2. **Use Case**:
   - This endpoint can be used for health checks or to test connectivity to the application.

---

### **3. `LoginController`**

#### **Purpose**
This controller handles user authentication and generates a JWT (JSON Web Token) for authenticated users.

#### **Key Features**
1. **Endpoint**:
   - `POST /api/v1/u/login`: Authenticates the user and returns a JWT in the response header.

2. **Workflow**:
   - The controller uses Spring Security's `AuthenticationManager` to authenticate the user's credentials (email and password).
   - If authentication is successful, it generates a JWT using the `JwtUtil` class.
   - The JWT is added to the response header (`Authorization`).
   - The user's ID is returned in the response body.

   ```java
   @PostMapping("/api/v1/u/login")
   public void createAuthenticationToken(@RequestBody LoginRequestDTO authenticationRequestDTO, HttpServletResponse response) throws IOException, JSONException {
       try {
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getEmail(), authenticationRequestDTO.getPassword()));
       } catch (BadCredentialsException e) {
           throw new BadCredentialsException("Incorrect Email or password");
       } catch (DisabledException disabledException) {
           response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not created");
           return;
       }
       final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.getEmail());
       Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());

       final String jwt = jwtUtil.generateToken(userDetails);

       if (optionalUser.isPresent()) {
           response.getWriter().write(new JSONObject().put("userId", optionalUser.get().getId()).toString());
       }
       response.addHeader("Access-Control-Expose-Headers", "Authorization");
       response.setHeader("Access-Control-Allow-Headers", "Authorization, X-PINGOTHER, X-Requested-With, Content-Type, Accept, X-Custom-header");
       response.setHeader(HEADER_STRING, TOKEN_PREFIX + jwt);
   }
   ```

3. **Error Handling**:
   - If the credentials are invalid, it throws a `BadCredentialsException`.
   - If the user is not found, it returns an HTTP status code of `404 (NOT FOUND)`.

---

### **4. `UserServiceRequestController`**

#### **Purpose**
This controller handles fetching the service request history for a logged-in user.

#### **Key Features**
1. **Endpoint**:
   - `GET /api/v1/u/getBookingHistory`: Retrieves a paginated list of service requests for the logged-in user.

2. **Workflow**:
   - The controller uses the `UserServiceRequest` service to fetch the service requests.
   - The results are paginated using Spring Data JPA's `Pageable` and `Page`.

   ```java
   @GetMapping("/api/v1/u/getBookingHistory")
   public ResponseEntity<?> getUserServiceRequest() {
       try {
           Page<ServiceRequestDTO> serviceRequestList = userServiceRequest.getServiceRequest(page, size);
           return new ResponseEntity<>(Objects.requireNonNullElseGet(serviceRequestList, ArrayList::new), HttpStatus.OK);
       } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
       }
   }
   ```

3. **Error Handling**:
   - If an exception occurs, it returns an error message with an HTTP status code of `200 (OK)`.

---

### **Summary of Controllers**

| **Controller**                 | **Endpoint**                                      | **Purpose**                                                                 |
|--------------------------------|--------------------------------------------------|-----------------------------------------------------------------------------|
| `AvailableTechnicianController` | `GET /api/v1/u/getAvailableTechnician/{lat}/{lon}` | Fetches available technicians near the user's location.                     |
| `CallMe`                        | `GET /callMe`                                    | A simple test endpoint to verify the application is running.                |
| `LoginController`               | `POST /api/v1/u/login`                           | Authenticates the user and returns a JWT.                                   |
| `UserServiceRequestController`   | `GET /api/v1/u/getBookingHistory`                | Retrieves the service request history for the logged-in user.               |

---

### **Key Points**

1. **Separation of Concerns**:
   - Each controller handles a specific functionality, making the code modular and easy to maintain.

2. **Error Handling**:
   - All controllers include error handling to ensure that exceptions are caught and appropriate responses are returned.

3. **Use of DTOs**:
   - DTOs are used to transfer data between the client and the server, ensuring that only the required data is exposed.

4. **Integration with External Services**:
   - The `AvailableTechnicianController` integrates with an external service to fetch available technicians.

5. **Security**:
   - The `LoginController` uses Spring Security for authentication and JWT for authorization.

---

### **Improvements**

1. **Pagination Parameters**:
   - The `UserServiceRequestController` should accept `page` and `size` as query parameters to allow dynamic pagination.

   ```java
   @GetMapping("/api/v1/u/getBookingHistory")
   public ResponseEntity<?> getUserServiceRequest(@RequestParam int page, @RequestParam int size) {
       // Implementation
   }
   ```

2. **Logging**:
   - Add logging (e.g., using SLF4J) to track requests, responses, and errors.

3. **Validation**:
   - Add input validation (e.g., using `@Valid`) to ensure that the request data is valid.

4. **Asynchronous Processing**:
   - Consider using `@Async` for long-running tasks (e.g., fetching available technicians) to improve performance.

5. **Global Exception Handling**:
   - Use a global exception handler (`@ControllerAdvice`) to centralize error handling and reduce code duplication.

---

These controllers form the backbone of the **MechaniX** platform's REST API, providing essential functionalities for users, technicians, and service requests.


The provided code consists of two **Spring Boot REST controllers** that handle **service requests** and **scheduled service requests** in the **MechaniX** project. These controllers interact with the service layer to create, accept, reject, and manage service requests. They also send email notifications to users and technicians at various stages of the request lifecycle. Let's break down each controller and explain its purpose, functionality, and workflow.

---

### **1. `ServiceRequestController`**

#### **Purpose**
This controller handles **immediate service requests** (e.g., "Book Now" requests). It provides endpoints for creating, accepting, and marking service requests as `"NOT_ASSIGNED"`.

#### **Key Features**
1. **Endpoints**:
   - `POST /api/v1/u/createServiceRequest`: Creates a new service request.
   - `POST /api/v1/u/acceptServiceRequest`: Accepts a service request.
   - `POST /api/v1/u/notAssignedServiceRequest`: Marks a service request as `"NOT_ASSIGNED"`.

2. **Workflow**:
   - **Create Service Request**:
     - The `createServiceRequest` method creates a new service request using the `ServiceRequestHandler` service.
     - It sends the request to the technician service using the `RestClient`.
     - It sends an email notification to the user using the `serviceRequestCreatedEmail` service.

     ```java
     @PostMapping("/api/v1/u/createServiceRequest")
     public ResponseEntity<String> createServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDTO) {
         try {
             ServiceRequestDTO serviceRequestBody = serviceRequest.createServiceRequest(serviceRequestDTO);
             if (serviceRequestBody != null && serviceRequestBody.getServiceRequestUUID() != null) {
                 serviceRequestBody.setDistance(10000);
                 serviceRequestBody.setNumTech(10);
                 String url = "https://technician.c-09499df.kyma.ondemand.com/api/v1/serviceRequest";
                 restClient.makePostRequest(url, serviceRequestBody, HttpStatus.class);
                 serviceRequestCreatedEmail.sendEmail(tenantInfo.getUser().getEmail(), tenantInfo.getUser().getName(), "Request Created!!!", serviceRequestBody.getServiceRequestUUID(), serviceRequestDTO.getServiceDescription());
                 return new ResponseEntity<>("Service Request Created.", HttpStatus.OK);
             } else {
                 return new ResponseEntity<>("Some details were missing.", HttpStatus.INTERNAL_SERVER_ERROR);
             }
         } catch (Exception e) {
             return new ResponseEntity<>("CreateServiceRequest -> " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

   - **Accept Service Request**:
     - The `acceptServiceRequest` method updates the status of a service request to `"ACCEPTED"`.
     - It sends an email notification to the user using the `serviceRequestAcceptedEmail` service.

     ```java
     @PostMapping("/api/v1/u/acceptServiceRequest")
     public ResponseEntity<String> acceptServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDTO) {
         UserDTO userDTO;
         try {
             userDTO = userDetailsService.loadUserByEmail(serviceRequestDTO.getUserEmail());
             ServiceRequestDTO serviceRequestD = serviceRequest.acceptServiceRequest(serviceRequestDTO);
             if (serviceRequestD == null) {
                 return new ResponseEntity<>("No request present with provided uuid.", HttpStatus.NOT_FOUND);
             } else if (serviceRequestD.getServiceRequestUUID() == null) {
                 return new ResponseEntity<>("Status of this request is already accepted", HttpStatus.CONFLICT);
             } else if (userDTO == null) {
                 return new ResponseEntity<>("No user present with provided uuid.", HttpStatus.NOT_FOUND);
             }
             serviceRequestAcceptedEmail.sendEmail(userDTO.getEmail(), userDTO.getName(), "Request Accepted!!!", serviceRequestD.getServiceRequestUUID().toString(), serviceRequestD.getShopName());
             return new ResponseEntity<>("Request Accepted.", HttpStatus.OK);
         } catch (UserMismatchException ex) {
             return new ResponseEntity<>("User who created this request is different from user modifying this request.", HttpStatus.CONFLICT);
         } catch (Exception e) {
             return new ResponseEntity<>("AcceptServiceRequest -> " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

   - **Mark Service Request as `"NOT_ASSIGNED"`**:
     - The `notAssignedServiceRequest` method updates the status of a service request to `"NOT_ASSIGNED"`.
     - It sends an email notification to the user using the `serviceRequestNotAccepted` service.

     ```java
     @PostMapping("/api/v1/u/notAssignedServiceRequest")
     public ResponseEntity<String> notAssignedServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDTO) {
         UserDTO userDTO;
         try {
             userDTO = userDetailsService.loadUserByEmail(serviceRequestDTO.getUserEmail());
             ServiceRequestDTO serviceRequestD = serviceRequest.notAssignedServiceRequest(serviceRequestDTO);
             if (serviceRequestD == null) {
                 return new ResponseEntity<>("No request present with provided uuid.", HttpStatus.NOT_FOUND);
             } else if (serviceRequestD.getServiceRequestUUID() == null) {
                 return new ResponseEntity<>("Status of this request is already not assigned", HttpStatus.CONFLICT);
             } else if (userDTO == null) {
                 return new ResponseEntity<>("No user present with provided uuid.", HttpStatus.NOT_FOUND);
             }
             serviceRequestNotAccepted.sendEmail(userDTO.getEmail(), userDTO.getName(), "No Technician Available :(", serviceRequestDTO.getServiceRequestUUID().toString(), serviceRequestD.getServiceDescription());
             return new ResponseEntity<>(HttpStatus.OK);
         } catch (UserMismatchException ex) {
             return new ResponseEntity<>("User who created this request is different from user modifying this request.", HttpStatus.CONFLICT);
         } catch (Exception e) {
             return new ResponseEntity<>("NotAssignedServiceRequest -> " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

3. **Error Handling**:
   - Each method includes error handling to catch exceptions and return appropriate HTTP status codes and error messages.

---

### **2. `ScheduleServiceRequestController`**

#### **Purpose**
This controller handles **scheduled service requests** (e.g., requests booked for a future date and time). It provides endpoints for scheduling, accepting, and rejecting scheduled service requests.

#### **Key Features**
1. **Endpoints**:
   - `POST /api/v1/u/scheduleServiceRequest`: Schedules a new service request.
   - `POST /api/v1/u/acceptScheduleServiceRequest`: Accepts a scheduled service request.
   - `POST /api/v1/u/rejectScheduleServiceRequest`: Rejects a scheduled service request.

2. **Workflow**:
   - **Schedule Service Request**:
     - The `scheduleServiceRequest` method creates a new scheduled service request using the `ScheduleServiceRequestHandler` service.
     - It sends the request to the technician service using the `RestClient`.
     - It sends an email notification to the user using the `serviceRequestCreatedEmail` service.

     ```java
     @PostMapping("/api/v1/u/scheduleServiceRequest")
     public ResponseEntity<String> scheduleServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDTO) {
         try {
             ServiceRequestDTO serviceRequest = scheduleServiceRequest.scheduleServiceRequest(serviceRequestDTO);
             if (serviceRequest != null) {
                 serviceRequest.setDistance(10000);
                 serviceRequest.setNumTech(10);
                 String url = "https://technician.c-09499df.kyma.ondemand.com/api/v1/scheduleRequest";
                 restClient.makePostRequest(url, serviceRequest, HttpStatus.class);
                 serviceRequestCreatedEmail.sendEmail(tenantInfo.getUser().getEmail(), tenantInfo.getUser().getName(), "Request Scheduled!!!", serviceRequest.getServiceRequestUUID().toString(), serviceRequest.getServiceDescription(), serviceRequest.getDate(), serviceRequest.getTime());
                 return new ResponseEntity<>("Service Request Created.", HttpStatus.OK);
             } else {
                 return new ResponseEntity<>("Some details were missing.", HttpStatus.INTERNAL_SERVER_ERROR);
             }
         } catch (Exception e) {
             return new ResponseEntity<>("ScheduleServiceRequest -> ", HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

   - **Accept Scheduled Service Request**:
     - The `acceptScheduleServiceRequest` method updates the status of a scheduled service request to `"ACCEPTED"`.
     - It sends an email notification to the user using the `serviceRequestAcceptedEmail` service.

     ```java
     @PostMapping("/api/v1/u/acceptScheduleServiceRequest")
     public ResponseEntity<String> acceptScheduleServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDTO) {
         UserDTO userDTO;
         try {
             userDTO = userDetailsService.loadUserByEmail(serviceRequestDTO.getUserEmail());
             ServiceRequestDTO serviceRequest = scheduleServiceRequest.acceptServiceRequest(serviceRequestDTO);
             if (serviceRequest == null) {
                 return new ResponseEntity<>("Some details were missing.", HttpStatus.INTERNAL_SERVER_ERROR);
             } else if (serviceRequest.getServiceRequestUUID() == null) {
                 return new ResponseEntity<>("Status of this request is already accepted", HttpStatus.CONFLICT);
             } else if (userDTO == null) {
                 return new ResponseEntity<>("No user present with provided uuid.", HttpStatus.NOT_FOUND);
             }
             serviceRequestAcceptedEmail.sendEmail(userDTO.getEmail(), userDTO.getName(), "Request Accepted!!!", serviceRequest.getServiceRequestUUID().toString(), serviceRequest.getAssignedTechnician(), serviceRequest.getDate(), serviceRequest.getTime());
             return new ResponseEntity<>("Request Accepted.", HttpStatus.OK);
         } catch (UserMismatchException ex) {
             return new ResponseEntity<>("User who created this request is different from user modifying this request.", HttpStatus.CONFLICT);
         } catch (Exception e) {
             return new ResponseEntity<>("AcceptScheduleServiceRequest -> ", HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

   - **Reject Scheduled Service Request**:
     - The `rejectServiceRequest` method updates the status of a scheduled service request to `"REJECTED"`.
     - It sends an email notification to the user using the `serviceRequestNotAccepted` service.

     ```java
     @PostMapping("/api/v1/u/rejectScheduleServiceRequest")
     public ResponseEntity<String> rejectServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDTO) {
         UserDTO userDTO;
         try {
             userDTO = userDetailsService.loadUserByEmail(serviceRequestDTO.getUserEmail());
             ServiceRequestDTO serviceRequest = scheduleServiceRequest.rejectServiceRequest(serviceRequestDTO);
             if (serviceRequest == null) {
                 return new ResponseEntity<>("No request present with provided uuid.", HttpStatus.INTERNAL_SERVER_ERROR);
             } else if (serviceRequest.getServiceRequestUUID() == null) {
                 return new ResponseEntity<>("Status of this request is already rejected", HttpStatus.CONFLICT);
             } else if (userDTO == null) {
                 return new ResponseEntity<>("No user present with provided uuid.", HttpStatus.NOT_FOUND);
             }
             serviceRequestNotAccepted.sendEmail(userDTO.getEmail(), userDTO.getName(), "Technician rejected your request :(", serviceRequestDTO.getServiceRequestUUID().toString(), serviceRequest.getServiceDescription(), serviceRequest.getAssignedTechnician(), serviceRequest.getDate(), serviceRequest.getTime());
             return new ResponseEntity<>(HttpStatus.OK);
         } catch (UserMismatchException ex) {
             return new ResponseEntity<>("User who created this request is different from user modifying this request.", HttpStatus.CONFLICT);
         } catch (Exception e) {
             return new ResponseEntity<>("RejectServiceRequest -> " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

3. **Error Handling**:
   - Each method includes error handling to catch exceptions and return appropriate HTTP status codes and error messages.

---

### **Summary of Controllers**

| **Controller**                 | **Endpoints**                                      | **Purpose**                                                                 |
|--------------------------------|--------------------------------------------------|-----------------------------------------------------------------------------|
| `ServiceRequestController`      | `POST /api/v1/u/createServiceRequest`             | Creates an immediate service request.                                       |
|                                 | `POST /api/v1/u/acceptServiceRequest`             | Accepts a service request.                                                  |
|                                 | `POST /api/v1/u/notAssignedServiceRequest`        | Marks a service request as `"NOT_ASSIGNED"`.                                |
| `ScheduleServiceRequestController` | `POST /api/v1/u/scheduleServiceRequest`           | Schedules a service request for a future date and time.                     |
|                                 | `POST /api/v1/u/acceptScheduleServiceRequest`     | Accepts a scheduled service request.                                        |
|                                 | `POST /api/v1/u/rejectScheduleServiceRequest`     | Rejects a scheduled service request.                                        |

---

### **Key Points**

1. **Separation of Concerns**:
   - Each controller handles a specific type of service request (immediate or scheduled), making the code modular and easy to maintain.

2. **Email Notifications**:
   - Email notifications are sent to users at various stages of the request lifecycle (e.g., request created, accepted, rejected).

3. **Integration with External Services**:
   - Both controllers integrate with an external technician service to send service requests.

4. **Error Handling**:
   - All methods include error handling to ensure that exceptions are caught and appropriate responses are returned.

5. **Use of DTOs**:
   - DTOs are used to transfer data between the client and the server, ensuring that only the required data is exposed.

---

### **Improvements**

1. **Logging**:
   - Add logging (e.g., using SLF4J) to track requests, responses, and errors.

2. **Validation**:
   - Add input validation (e.g., using `@Valid`) to ensure that the request data is valid.

3. **Asynchronous Processing**:
   - Consider using `@Async` for long-running tasks (e.g., sending emails or making external API calls) to improve performance.

4. **Global Exception Handling**:
   - Use a global exception handler (`@ControllerAdvice`) to centralize error handling and reduce code duplication.

5. **Pagination**:
   - Add pagination support for endpoints that return lists of service requests.

---

These controllers form the backbone of the **MechaniX** platform's REST API, providing essential functionalities for managing service requests and scheduled service requests.