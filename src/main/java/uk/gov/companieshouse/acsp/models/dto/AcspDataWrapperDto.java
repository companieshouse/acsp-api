package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.annotation.Id;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AcspDataWrapperDto {
   @Id
    private String id;

    private AcspDataDto data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AcspDataDto getData() {
        return data;
    }

    public void setData(AcspDataDto data) {
        this.data = data;
    }
}
