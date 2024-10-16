package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.dao.AMLSupervisoryBodiesDao;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

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

    @Named("amlSupervisoryBodiesDisplayLabelToDbValue")
    public AMLSupervisoryBodiesDao[] amlSupervisoryBodiesDisplayLabelToDbValue(AMLSupervisoryBodiesDto[] webLabelArray) {
        var amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[0];
        if(webLabelArray != null){
           amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[webLabelArray.length];
           for(var i=0; i<webLabelArray.length; i++){
               amlSupervisoryBodiesDao[i] = new AMLSupervisoryBodiesDao();
               amlSupervisoryBodiesDao[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByDisplayLabel(webLabelArray[i].getAmlSupervisoryBody()).getDbValue());
               amlSupervisoryBodiesDao[i].setMembershipId(webLabelArray[i].getMembershipId());
           }
           return amlSupervisoryBodiesDao;
        }
        return amlSupervisoryBodiesDao;
    }

    @Named("amlSupervisoryBodiesDbValueToDisplayLabel")
    public AMLSupervisoryBodiesDto[] amlSupervisoryBodiesDbValueToDisplayLabel(AMLSupervisoryBodiesDao[] dbLabelArray) {
        var amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[0];
        if(dbLabelArray != null){
            amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[dbLabelArray.length];
            for(var i=0; i<dbLabelArray.length; i++){
                amlSupervisoryBodiesDto[i] = new AMLSupervisoryBodiesDto();
                amlSupervisoryBodiesDto[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByDbValue(dbLabelArray[i].getAmlSupervisoryBody()).getDisplayLabel());
                amlSupervisoryBodiesDto[i].setAmlAcronym(AMLSupervisoryBodies.findByDbValue(dbLabelArray[i].getAmlSupervisoryBody()).toString());
                amlSupervisoryBodiesDto[i].setMembershipId(dbLabelArray[i].getMembershipId());
            }
            return amlSupervisoryBodiesDto;
        }
        return amlSupervisoryBodiesDto;
    }

}