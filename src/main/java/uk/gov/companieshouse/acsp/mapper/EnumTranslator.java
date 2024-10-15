package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.dao.AMLSupervisoryBodiesDao;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.sql.Array;

@Service
public class EnumTranslator {


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

    @Named("amlSupervisoryBodiesEnumToString")
    public AMLSupervisoryBodiesDao[] amlSupervisoryBodiesEnumToString(AMLSupervisoryBodiesDto[] inputEnum) {
        AMLSupervisoryBodiesDao[] amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[0];
       if(inputEnum != null){
           amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[inputEnum.length];
           for(int i=0; i<inputEnum.length; i++){
               amlSupervisoryBodiesDao[i] = new AMLSupervisoryBodiesDao();
               amlSupervisoryBodiesDao[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByName(inputEnum[i].getAmlSupervisoryBody()).getLabel());
               amlSupervisoryBodiesDao[i].setMembershipId(inputEnum[i].getMembershipId());
           }
           return amlSupervisoryBodiesDao;
       }
       return amlSupervisoryBodiesDao;
    }

    @Named("amlSupervisoryBodiesStringToEnum")
    public AMLSupervisoryBodiesDto[] amlSupervisoryBodiesStringToEnum(AMLSupervisoryBodiesDao[] inputStr) {
        AMLSupervisoryBodiesDto[] amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[0];
        if(inputStr != null){
            amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[inputStr.length];
            for(int i=0; i<inputStr.length; i++){
                amlSupervisoryBodiesDto[i] = new AMLSupervisoryBodiesDto();
                amlSupervisoryBodiesDto[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByLabel(inputStr[i].getAmlSupervisoryBody()).getName());
                amlSupervisoryBodiesDto[i].setMembershipId(inputStr[i].getMembershipId());
            }
            return amlSupervisoryBodiesDto;
        }
        return amlSupervisoryBodiesDto;
    }

}