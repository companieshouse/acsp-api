package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.dao.AMLSupervisoryBodiesDao;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.enums.*;

@Service
public class EnumTranslator {


    @Named("TypeOfBusinessEnumToString")
    public String typeOfBusinessEnumToString(TypeOfBusiness inputEnum) {
        return inputEnum != null ? inputEnum.getLabel() : "";
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

    @Named("amlSupervisoryBodiesEnumToString")
    public AMLSupervisoryBodiesDao[] amlSupervisoryBodiesEnumToString(AMLSupervisoryBodiesDto[] inputEnumArray) {
        var amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[0];
        if(inputEnumArray != null){
           amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[inputEnumArray.length];
           for(var i=0; i<inputEnumArray.length; i++){
               amlSupervisoryBodiesDao[i] = new AMLSupervisoryBodiesDao();
               amlSupervisoryBodiesDao[i].setAmlSupervisoryBody(inputEnumArray[i].getAmlSupervisoryBody().getLabel());
               amlSupervisoryBodiesDao[i].setMembershipId(inputEnumArray[i].getMembershipId());
           }
           return amlSupervisoryBodiesDao;
        }
        return amlSupervisoryBodiesDao;
    }

    @Named("amlSupervisoryBodiesStringToEnum")
    public AMLSupervisoryBodiesDto[] amlSupervisoryBodiesStringToEnum(AMLSupervisoryBodiesDao[] inputStrArray) {
        var amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[0];
        if(inputStrArray != null){
            amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[inputStrArray.length];
            for(var i=0; i<inputStrArray.length; i++){
                amlSupervisoryBodiesDto[i] = new AMLSupervisoryBodiesDto();
                amlSupervisoryBodiesDto[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByLabel(inputStrArray[i].getAmlSupervisoryBody()));
                amlSupervisoryBodiesDto[i].setMembershipId(inputStrArray[i].getMembershipId());
            }
            return amlSupervisoryBodiesDto;
        }
        return amlSupervisoryBodiesDto;
    }

    @Named("AcspTypeEnumToString")
    public String acspTypeEnumToString(AcspType inputEnum) {
        return inputEnum != null ? inputEnum.getLabel() : "";
    }

    @Named("AcspTypeStringToEnum")
    public AcspType acspTypeStringToEnum(String inputStr) {
        return AcspType.findByLabel(inputStr);
    }

}