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

    @Named("amlSupervisoryBodiesNameToLabel")
    public AMLSupervisoryBodiesDao[] amlSupervisoryBodiesNameToLabel(AMLSupervisoryBodiesDto[] nameArray) {
        AMLSupervisoryBodiesDao[] amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[0];
        if(nameArray != null){
           amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[nameArray.length];
           for(int i=0; i<nameArray.length; i++){
               amlSupervisoryBodiesDao[i] = new AMLSupervisoryBodiesDao();
               amlSupervisoryBodiesDao[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByName(nameArray[i].getAmlSupervisoryBody()).getLabel());
               amlSupervisoryBodiesDao[i].setMembershipId(nameArray[i].getMembershipId());
           }
           return amlSupervisoryBodiesDao;
        }
        return amlSupervisoryBodiesDao;
    }

    @Named("amlSupervisoryBodiesLabelToName")
    public AMLSupervisoryBodiesDto[] amlSupervisoryBodiesLabelToName(AMLSupervisoryBodiesDao[] labelArray) {
        AMLSupervisoryBodiesDto[] amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[0];
        if(labelArray != null){
            amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[labelArray.length];
            for(int i=0; i<labelArray.length; i++){
                amlSupervisoryBodiesDto[i] = new AMLSupervisoryBodiesDto();
                amlSupervisoryBodiesDto[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByLabel(labelArray[i].getAmlSupervisoryBody()).getName());
                amlSupervisoryBodiesDto[i].setAmlAcronym(AMLSupervisoryBodies.findByLabel(labelArray[i].getAmlSupervisoryBody()).toString());
                amlSupervisoryBodiesDto[i].setMembershipId(labelArray[i].getMembershipId());
            }
            return amlSupervisoryBodiesDto;
        }
        return amlSupervisoryBodiesDto;
    }

}