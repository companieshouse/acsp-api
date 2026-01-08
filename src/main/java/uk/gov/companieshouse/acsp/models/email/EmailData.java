package uk.gov.companieshouse.acsp.models.email;

public interface EmailData {

    String getTo();

    String getSubject();

    void setTo(String to);

    void setSubject(String subject);
}
