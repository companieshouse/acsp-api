package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "applications")
public class Acsp {

    @Id
    private String id;

    @Field("data")
    private AcspDataDao acspDataDao;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AcspDataDao getAcspDataDao() {
        return acspDataDao;
    }

    public void setAcspDataDao(AcspDataDao acspDataDao) {
        this.acspDataDao = acspDataDao;
    }
}
