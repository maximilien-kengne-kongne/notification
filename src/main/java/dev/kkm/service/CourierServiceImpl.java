package dev.kkm.service;

import dev.kkm.exception.CourierException;
import dev.kkm.model.CourierDetail;
import jakarta.activation.DataSource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CourierServiceImpl implements CourierService {

    private final ApplicationContext applicationContext;
    private final TemplateEngine templateEngine;

    public CourierServiceImpl( ApplicationContext applicationContext, TemplateEngine templateEngine) {
        this.applicationContext = applicationContext;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendCourier(CourierDetail courierDetail) {

        Calendar calendar = Calendar.getInstance();

            try {
                MimeMessage message = applicationContext.getBean(JavaMailSenderImpl.class).createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setSentDate(calendar.getTime());

                isValidEmail("sender", courierDetail.getSender());

                if (courierDetail.getOrganizationName() != null) {
                    helper.setFrom(new InternetAddress(courierDetail.getSender(), courierDetail.getOrganizationName()));
                }else {
                    helper.setFrom(courierDetail.getSender());
                }

                helper.setSubject(courierDetail.getSubject());

                for (String recipient : courierDetail.getRecipients()) {
                    isValidEmail("recipient", recipient);
                    helper.addTo(recipient);
                }

                if (courierDetail.getCc() != null) {
                    for (String cc : courierDetail.getCc()) {
                        isValidEmail("cc", cc);
                        helper.addCc(cc);
                    }
                }

                if (courierDetail.getBcc() != null) {
                    for (String bcc : courierDetail.getBcc()) {
                        isValidEmail("bcc", bcc);
                        helper.addBcc(bcc);
                    }
                }

                if (courierDetail.getTemplateName() != null) {
                    String processedHtml = processTemplate(courierDetail);
                    helper.setText(processedHtml, true);
                } else {
                    helper.setText(courierDetail.getBody());
                }

                if (courierDetail.getAttachments() != null) {
                    for (Map.Entry<String, DataSource> attachment : courierDetail.getAttachments().entrySet()) {
                        helper.addAttachment(attachment.getKey(), attachment.getValue());
                    }
                }

                applicationContext.getBean(JavaMailSenderImpl.class).send(message);
            } catch (Exception e) {
                throw new CourierException(e.getMessage());
            }
    }

    @Override
    public void sendCourier(List<CourierDetail> courierDetails) {
        courierDetails.forEach(this::sendCourier);
    }


    /**
     * @implNote Processes a template model with the provided variables
     * @param courierDetail content variables and template
     * @return The HTML generated from the template
     */
    private String processTemplate(CourierDetail courierDetail) {
        Context context = new Context(Locale.getDefault());
        context.setVariables(courierDetail.getVariables());
        return templateEngine.process(courierDetail.getTemplateName(), context);
    }

    /**
     * Check if email address is valid
     * @param fieldName name of field to check
     * @param email e-mail to check
     * @throws CourierException when email is invalid
     */
    private void isValidEmail(String fieldName, String email) {
        boolean isValid =  email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        if (!isValid) {
            throw new CourierException(fieldName + " email is invalid");
        }
    }
}
