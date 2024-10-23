package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "applications")
public class AcspDataDao {

    @Id
    private String id;

    @Field("data")
    private DataDao data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataDao getData() {
        return data;
    }

    public void setData(DataDao data) {
        this.data = data;
    }
}
