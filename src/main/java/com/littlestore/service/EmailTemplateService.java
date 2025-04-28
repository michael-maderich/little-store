package com.littlestore.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailTemplateService {

    /**
    * Loads an email template file from resources/email-templates/ and replaces placeholders.
    *
    * @param templateName Template filename (e.g., "order-confirmation.html")
    * @param variables Map of placeholder names and replacement values
    * @return Fully populated HTML content
    * @throws IOException if template file is missing or unreadable
    */
    public String loadTemplate(String templateName, Map<String, String> variables) throws IOException {
        ClassPathResource resource = new ClassPathResource("email-templates/" + templateName);
        String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            template = template.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }

        return template;
    }

    public String loadCss(String cssFileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("email-templates/css/" + cssFileName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Safely convert an object to a String, with a default fallback if it's null.
     *
     * @param value The object to stringify
     * @param fallback The fallback string to use if value is null
     * @return A non-null string
     */
    public static String safeString(Object value, String fallback) {
        return (value == null) ? fallback : value.toString();
    }
 
    public static String safeString(Object value) {
        return safeString(value, "");
    }
}
