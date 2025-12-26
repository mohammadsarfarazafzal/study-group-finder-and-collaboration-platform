package com.studygroup.backend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.studygroup.backend.entity.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            
            System.out.println("Email sent to: " + toEmail + " | Status: " + response.getStatusCode());
        } catch (IOException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = "https://edunion.onrender.com/reset-password?token=" + resetToken;
        
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2563EB;">Password Reset Request</h2>
                <p>You requested to reset your password for the Edunion Platform.</p>
                <p>Click the button below to reset your password:</p>
                <a href="%s" style="display: inline-block; padding: 12px 24px; background-color: #2563EB; color: white; text-decoration: none; border-radius: 8px; margin: 16px 0;">Reset Password</a>
                <p style="color: #666; font-size: 14px;">This link will expire in 24 hours.</p>
                <p style="color: #666; font-size: 14px;">If you didn't request this, please ignore this email.</p>
                <hr style="margin: 24px 0; border: none; border-top: 1px solid #ddd;">
                <p style="color: #999; font-size: 12px;">Best regards,<br>Team Edunion</p>
            </div>
            """, resetLink);

        sendEmail(toEmail, "Password Reset - Edunion", htmlContent);
    }

    public void sendEventNotification(String toEmail, Event event) {
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2563EB;">🎓 New Study Session</h2>
                <div style="background-color: #F3F4F6; padding: 20px; border-radius: 8px; margin: 16px 0;">
                    <h3 style="margin-top: 0;">%s</h3>
                    <p><strong>Description:</strong> %s</p>
                    <p><strong>📅 Date:</strong> %s</p>
                    <p><strong>⏰ Time:</strong> %s to %s</p>
                    <p><strong>📍 Location:</strong> %s</p>
                    <p><strong>👥 Group:</strong> %s</p>
                    <p><strong>👤 Created by:</strong> %s</p>
                </div>
                <p>You're receiving this notification because you're a member of this study group.</p>
                <p>Happy Studying! 🎓</p>
                <hr style="margin: 24px 0; border: none; border-top: 1px solid #ddd;">
                <p style="color: #999; font-size: 12px;">Best regards,<br>Team Edunion</p>
            </div>
            """,
            event.getTitle(),
            event.getDescription(),
            event.getStartTime().toLocalDate(),
            event.getStartTime().toLocalTime(),
            event.getEndTime().toLocalTime(),
            event.getLocation(),
            event.getGroup().getName(),
            event.getCreatedBy().getName()
        );

        sendEmail(toEmail, "New Study Session: " + event.getTitle(), htmlContent);
    }

    public void sendEventReminder(String toEmail, Event event) {
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #F59E0B;">⏰ Session Reminder</h2>
                <p><strong>Your study session is starting soon!</strong></p>
                <div style="background-color: #FEF3C7; padding: 20px; border-radius: 8px; margin: 16px 0; border-left: 4px solid #F59E0B;">
                    <h3 style="margin-top: 0;">%s</h3>
                    <p><strong>Description:</strong> %s</p>
                    <p><strong>⏰ Time:</strong> %s to %s</p>
                    <p><strong>📍 Location:</strong> %s</p>
                    <p><strong>👥 Group:</strong> %s</p>
                </div>
                <p>Don't forget to join on time! 🎓</p>
                <hr style="margin: 24px 0; border: none; border-top: 1px solid #ddd;">
                <p style="color: #999; font-size: 12px;">Best regards,<br>Team Edunion</p>
            </div>
            """,
            event.getTitle(),
            event.getDescription(),
            event.getStartTime().toLocalTime(),
            event.getEndTime().toLocalTime(),
            event.getLocation(),
            event.getGroup().getName()
        );

        sendEmail(toEmail, "Reminder: " + event.getTitle() + " starts soon", htmlContent);
    }
}
