package dev.kkm.service;

import dev.kkm.exception.CourierException;
import dev.kkm.model.CourierDetail;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class CourierServiceImpl implements CourierService {

    private final ApplicationContext applicationContext;
    private final TemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(CourierServiceImpl.class);

    public CourierServiceImpl( ApplicationContext applicationContext, TemplateEngine templateEngine) {
        this.applicationContext = applicationContext;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendCourier(CourierDetail courierDetail) {
        log.info("... init sendCourier ...");
        Calendar calendar = Calendar.getInstance();

            try {
                MimeMessage message = applicationContext.getBean(JavaMailSenderImpl.class).createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setSubject(courierDetail.getSubject());
                helper.setSentDate(calendar.getTime());
                helper.setFrom(new InternetAddress(courierDetail.getSender(), courierDetail.getOrganizationName()));

                isValidEmail("sender", courierDetail.getSender());

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

                if (courierDetail.getPriority() != null) {
                    helper.setPriority(courierDetail.getPriority());
                }

                if (courierDetail.getReplyTo() != null) {
                    isValidEmail("replyTo", courierDetail.getReplyTo());
                    helper.setReplyTo(courierDetail.getReplyTo());
                }

                if (courierDetail.getAttachments() != null) {
                    for (Map.Entry<String, DataSource> attachment : courierDetail.getAttachments().entrySet()) {
                        helper.addAttachment(attachment.getKey(), attachment.getValue());
                    }
                }

                applicationContext.getBean(JavaMailSenderImpl.class).send(message);

                log.info("... Courier sent successfully ...");

            } catch (MessagingException messagingException) {
                throw new CourierException(messagingException.getMessage(),901);
            } catch (MailSendException mailSendException) {
                if (!Objects.requireNonNull(mailSendException.getMessage()).contains("Invalid Addresses")) {
                    throw new CourierException("mail not send",902);
                } else {
                    throw new CourierException("invalid email address", 903);
                }
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                throw new CourierException(unsupportedEncodingException.getMessage(), 904);
            }catch (RuntimeException runtimeException) {
                throw new CourierException(runtimeException.getMessage(), 905);
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
