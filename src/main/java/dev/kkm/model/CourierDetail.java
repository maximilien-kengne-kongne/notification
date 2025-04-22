package dev.kkm.model;

import dev.kkm.exception.CourierException;
import jakarta.activation.DataSource;


import java.util.*;


/**
 * this class content details of courier
 * @author  maximilien kengne kongne
 * @version  1.0.0
 * @date 21/04/2025
 */
public class CourierDetail {
    private final String sender;
    private final List<String> recipients;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String body;
    private final String organizationName;
    private final String templateName;
    /**
     * possibles values 1,3,5 <br/>
     * 1: high
     * 3: normal
     * 5: low
     */
    private final Integer priority;
    private final String replyTo;
    private final Map<String, Object> variables;
    /**
     * {@link jakarta.activation.DataSource}
     */
    private final Map<String, DataSource> attachments;

    public String getSender() {
        return sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Map<String, DataSource> getAttachments() {
        return attachments;
    }

    private CourierDetail(Builder builder) {
        this.sender = builder.sender;
        this.recipients = List.copyOf(builder.recipients);
        this.cc = List.copyOf(builder.cc);
        this.bcc = List.copyOf(builder.bcc);
        this.subject = builder.subject;
        this.body = builder.body;
        this.organizationName = builder.organizationName;
        this.templateName = builder.templateName;
        this.priority = builder.priority;
        this.replyTo = builder.replyTo;
        this.variables = Map.copyOf(builder.variables);
        this.attachments = Map.copyOf(builder.attachments);
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String sender;
        private final List<String> recipients = new ArrayList<>();
        private final List<String> cc = new ArrayList<>();
        private final List<String> bcc = new ArrayList<>();
        private String subject;
        private String body;
        private String organizationName;
        private String templateName;
        private Integer priority;
        private String replyTo;
        private final Map<String, Object> variables = new HashMap<>();
        private final Map<String, DataSource> attachments = new HashMap<>();

        private Builder() {}

        public Builder withSender(String sender) {
            this.sender = Objects.requireNonNull(sender, "Sender cannot be null");
            return this;
        }

        public Builder addRecipient(String recipient) {
            this.recipients.add(Objects.requireNonNull(recipient, "Recipient cannot be null"));
            return this;
        }

        public Builder addRecipients(List<String> recipients) {
            recipients.forEach(this::addRecipient);
            return this;
        }

        public Builder addCc(String ccRecipient) {
            this.cc.add(Objects.requireNonNull(ccRecipient, "CC recipient cannot be null"));
            return this;
        }

        public Builder addCcs(List<String> ccRecipients) {
            ccRecipients.forEach(this::addCc);
            return this;
        }

        public Builder addBcc(String bccRecipient) {
            this.bcc.add(Objects.requireNonNull(bccRecipient, "BCC recipient cannot be null"));
            return this;
        }

        public Builder addBccs(List<String> bccRecipients) {
            bccRecipients.forEach(this::addBcc);
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = Objects.requireNonNull(subject, "Subject cannot be null");
            return this;
        }

        public Builder withBody(String body) {
            this.body = Objects.requireNonNull(body, "Body cannot be null");
            return this;
        }


        public Builder withOrganizationName(String organizationName) {
            this.organizationName = Objects.requireNonNull(organizationName, "Organization name cannot be null");
            return this;
        }

        public Builder withTemplate(String templateName) {
            this.templateName = Objects.requireNonNull(templateName, "Template name cannot be null");
            return this;
        }

        public Builder withPriority(Integer priority) {
            this.priority = Objects.requireNonNull(priority, "Priority cannot be null");
            return this;
        }

        public Builder withReplyTo(String replyTo) {
            this.replyTo = Objects.requireNonNull(replyTo, "Reply TO cannot be null");
            return this;
        }

        public Builder addVariable(String key, Object value) {
            this.variables.put(
                    Objects.requireNonNull(key, "Variable key cannot be null"),
                    Objects.requireNonNull(value, "Variable value cannot be null")
            );
            return this;
        }

        public Builder addVariables(Map<String, Object> variables) {
            variables.forEach(this::addVariable);
            return this;
        }

        public Builder addAttachment(String name, DataSource dataSource) {
            this.attachments.put(
                    Objects.requireNonNull(name, "Attachment name cannot be null"),
                    Objects.requireNonNull(dataSource, "DataSource cannot be null")
            );
            return this;
        }

        public Builder addAttachments(Map<String, DataSource> attachments) {
            attachments.forEach(this::addAttachment);
            return this;
        }

        public CourierDetail build() {

            if (recipients.isEmpty()) {
                throw new CourierException("At least one recipient is required");
            }

            if (sender.isBlank()) {
                throw new CourierException("Sender is required");
            }

            if (subject.isBlank()) {
                throw new CourierException("Subject is required");
            }

            if (body == null && templateName == null) {
                throw new CourierException("Cannot empty both body and template");
            }

            if (body != null && templateName != null) {
                throw new CourierException("Cannot specify both body and template");
            }

            if (templateName != null && variables.isEmpty()) {
                throw new CourierException("Variables are required when using a template");
            }

            if (priority != null && !List.of(1,3,5).contains(priority)) {
                throw new CourierException("Priority must be in ( 1, 3, 5)");
            }

            if (organizationName == null) {
                throw new CourierException("Organization name is required");
            }

            return new CourierDetail(this);
        }
    }


}