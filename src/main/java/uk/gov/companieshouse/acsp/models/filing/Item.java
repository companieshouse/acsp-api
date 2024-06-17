package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {

    @JsonProperty("data")
    private ACSP data;
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("submission_language")
    private String submissionLanguage;
    @JsonProperty("submission_id")
    private String submissionId;

    public ACSP getData() {
        return data;
    }

    public void setData(ACSP data) {
        this.data = data;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSubmissionLanguage() {
        return submissionLanguage;
    }

    public void setSubmissionLanguage(String submissionLanguage) {
        this.submissionLanguage = submissionLanguage;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
}
