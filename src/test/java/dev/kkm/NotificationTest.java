package dev.kkm;

import dev.kkm.exception.CourierException;
import dev.kkm.model.CourierDetail;
import dev.kkm.service.CourierServiceImpl;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private JavaMailSenderImpl mailSender;

    @InjectMocks
    private CourierServiceImpl courierService;

    @BeforeEach
    void setUp() {
        when(applicationContext.getBean(JavaMailSenderImpl.class)).thenReturn(mailSender);
        when(applicationContext.getBean(JavaMailSenderImpl.class).createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
     void shouldSendSimpleEmailSuccessfully() {
        CourierDetail courierDetail = CourierDetail.builder()
                .withSender("noreply@company.com")
                .addRecipient("client@example.com")
                .withSubject("Your Order Confirmation")
                .withBody("Thank you for your order!")
                .build();

        courierService.sendCourier(courierDetail);
        verify(applicationContext.getBean(JavaMailSenderImpl.class), times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendSimpleEmailWithAttachmentSuccessfully() {
        DataSource mockAttachment = new DataSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("This is a test".getBytes());
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public String getName() {
                return "file.html";
            }
        };

        CourierDetail courierDetail = CourierDetail.builder()
                .withSender("noreply@company.com")
                .addRecipient("client@example.com")
                .withSubject("Your Order Confirmation")
                .withBody("Thank you for your order!")
                .addAttachment("file.html", mockAttachment)
                .build();

        courierService.sendCourier(courierDetail);
        verify(applicationContext.getBean(JavaMailSenderImpl.class), times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendMultipleEmailsSuccessfully() {
        // Arrange
        CourierDetail email1 = CourierDetail.builder()
                .withSender("noreply@company.com")
                .addRecipient("client1@example.com")
                .withSubject("Subject 1")
                .withBody("Body 1")
                .build();

        CourierDetail email2 = CourierDetail.builder()
                .withSender("noreply@company.com")
                .addRecipient("client2@example.com")
                .withSubject("Subject 2")
                .withBody("Body 2")
                .build();

        List<CourierDetail> courierDetails = Arrays.asList(email1, email2);

        // Act
        courierService.sendCourier(courierDetails);

        // Assert
        verify(applicationContext.getBean(JavaMailSenderImpl.class), times(2)).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendEmailWithOrganizationNameSuccessfully() {
        // Arrange
        CourierDetail courierDetail = CourierDetail.builder()
                .withSender("noreply@company.com")
                .withOrganizationName("ACME Corporation")
                .addRecipient("client@example.com")
                .withSubject("Your Order Confirmation")
                .withBody("Thank you for your order!")
                .build();

        // Act
        courierService.sendCourier(courierDetail);

        // Assert
        verify(applicationContext.getBean(JavaMailSenderImpl.class), times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendTemplateEmailSuccessfully() {
        // Arrange
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", "John Doe");
        variables.put("orderNumber", "12345");

        CourierDetail courierDetail = CourierDetail.builder()
                .withSender("noreply@company.com")
                .addRecipient("client@example.com")
                .withSubject("Order Confirmation")
                .withTemplate("order-confirmation")
                .addVariables(variables)
                .build();

        when(templateEngine.process(eq("order-confirmation"), any(Context.class)))
                .thenReturn("<html><body>Processed template content</body></html>");

        // Act
        courierService.sendCourier(courierDetail);

        // Assert
        verify(templateEngine).process(eq("order-confirmation"), any(Context.class));
        verify(applicationContext.getBean(JavaMailSenderImpl.class), times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenSenderEmailIsInvalid() {
        // Arrange
        CourierDetail courierDetail = CourierDetail.builder()
                .withSender("invalid-email")  // Invalid sender email
                .addRecipient("client@example.com")
                .withSubject("Test Subject")
                .withBody("Test Body")
                .build();

        // Act & Assert
        CourierException exception = assertThrows(CourierException.class, () -> courierService.sendCourier(courierDetail));

        assertEquals("sender email is invalid", exception.getMessage());
        verify(applicationContext.getBean(JavaMailSenderImpl.class), never()).send(any(MimeMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenTemplateProcessingFails() {
        // Arrange
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", "John Doe");
        variables.put("orderNumber", "12345");

        CourierDetail courierDetail = CourierDetail.builder()
                .withSender("noreply@company.com")
                .addRecipient("client@example.com")
                .withSubject("Test Subject")
                .addVariables(variables)
                .withTemplate("non-existent-template")
                .build();

        // Simulate an error during processes of template
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenThrow(new RuntimeException("Template not found"));

        // Act & Assert
        CourierException exception = assertThrows(CourierException.class, () -> courierService.sendCourier(courierDetail));

        assertEquals("Template not found", exception.getMessage());
        verify(applicationContext.getBean(JavaMailSenderImpl.class), never()).send(any(MimeMessage.class));
    }


}
