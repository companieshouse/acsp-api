package uk.gov.companieshouse.acsp.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(
        collection = "#{@environment.getProperty('spring.data.mongodb.collection')}"
)
public class ACSPData {

}
