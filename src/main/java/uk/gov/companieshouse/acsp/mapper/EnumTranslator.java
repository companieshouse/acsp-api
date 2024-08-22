package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

@Service
public class EnumTranslator {

    
    public EnumTranslator() {

    }

    @Named("TypeOfBusinessEnumToString")
    public String inputEnumToString(TypeOfBusiness inputEnum) {
        return inputEnum.getLabel();
    }

    @Named("TypeOfBusinessStringToEnum")
    public TypeOfBusiness inputStringToEnum(String inputStr) {
        return TypeOfBusiness.findByLabel(inputStr);
    }


}