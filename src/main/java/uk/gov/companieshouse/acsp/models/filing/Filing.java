package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;

public class Filing {

    @JsonProperty("presenter")
    private Presenter presenter;

    @JsonProperty("submission")
    private Submission submission;

    @JsonProperty("items")
    private FilingApi[] filingItems;

    public Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public FilingApi[] getFilingItems() {
        return filingItems;
    }

    public void setFilingItems(FilingApi[] filingItems) {
        this.filingItems = filingItems;
    }
}
