package com.littlestore.config;

import java.io.UncheckedIOException;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Load file: validation.properties
        messageSource.setBasename("classpath:validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver() {
            private final Logger logger = LoggerFactory.logger(WebConfiguration.class);

            @Override
			public void cleanupMultipart(MultipartHttpServletRequest request) {
                try {
                    super.cleanupMultipart(request);
                } catch (UncheckedIOException ex) {
                    // Log and ignore any temp-file deletion errors
                    logger.warn("Could not delete multipart temp file", ex);
                }
            }
        };
    }
}