package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumTranslator {

        // Multiple enums to string array (for DB storage)
        @Named("AMLSupervisoryBodiesEnumArrayToStringArray")
        public List<String> amlSupervisoryBodiesEnumArrayToStringArray(List<AMLSupervisoryBodies> enumList) {
            return enumList != null
                    ? enumList.stream().map(AMLSupervisoryBodies::getLabel).collect(Collectors.toList())
                    : List.of();  // Save list of lowercase values to DB
        }

        // String array to multiple enums (retrieved from DB)
        @Named("StringArrayToAMLSupervisoryBodiesEnumArray")
        public List<AMLSupervisoryBodies> stringArrayToAmlSupervisoryBodiesEnumArray(List<String> stringList) {
            return stringList != null
                    ? stringList.stream().map(AMLSupervisoryBodies::findByLabel).collect(Collectors.toList())
                    : List.of();  // Retrieve list of enums from list of lowercase values
        }



    @Named("TypeOfBusinessEnumToString")
    public String typeOfBusinessEnumToString(TypeOfBusiness inputEnum) {
        return inputEnum.getLabel();
    }

    @Named("TypeOfBusinessStringToEnum")
    public TypeOfBusiness typeOfBusinessStringToEnum(String inputStr) {
        return TypeOfBusiness.findByLabel(inputStr);
    }

    @Named("RoleTypeEnumToString")
    public String roleTypeEnumToString(RoleType inputEnum) {
        return inputEnum != null ? inputEnum.getLabel() : "";
    }

    @Named("RoleTypeStringToEnum")
    public RoleType roleTypeStringToEnum(String inputStr) {
        return RoleType.findByLabel(inputStr);
    }

    @Named("WorkSectorEnumToString")
    public String businessSectorEnumToString(BusinessSector inputEnum) {
        return inputEnum != null ? inputEnum.getLabel() : "";
    }

    @Named("WorkSectorStringToEnum")
    public BusinessSector businessSectorStringToEnum(String inputStr) {
        return BusinessSector.findByLabel(inputStr);
    }

}