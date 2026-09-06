package com.example.portalaluno.shared.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class SanitizeHtmlValidator implements ConstraintValidator<SanitizeHtml, String> {

    // Políticas da OWASP: Bloca qualquer tag (HTML, <script>, <iframe>, etc) mantendo apenas texto puro
    private static final PolicyFactory POLICY = new HtmlPolicyBuilder().toFactory();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Deixa o @NotNull/NotBlank cuidar dos nulos
        }

        // Limpa o texto de qualquer tag maliciosa
        String sanitized = POLICY.sanitize(value);

        // Se o texto limpo for diferente do texto enviado, significa que havia HTML/JS no meio
        return value.equals(sanitized);
    }
}
