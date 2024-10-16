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

    @Named("amlSupervisoryBodiesWebLabelToDbLabel")
    public AMLSupervisoryBodiesDao[] amlSupervisoryBodiesWebLabelToDbLabel(AMLSupervisoryBodiesDto[] nameArray) {
        var amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[0];
        if(nameArray != null){
           amlSupervisoryBodiesDao = new AMLSupervisoryBodiesDao[nameArray.length];
           for(var i=0; i<nameArray.length; i++){
               amlSupervisoryBodiesDao[i] = new AMLSupervisoryBodiesDao();
               amlSupervisoryBodiesDao[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findByweblabel(nameArray[i].getAmlSupervisoryBody()).getDblabel());
               amlSupervisoryBodiesDao[i].setMembershipId(nameArray[i].getMembershipId());
           }
           return amlSupervisoryBodiesDao;
        }
        return amlSupervisoryBodiesDao;
    }

    @Named("amlSupervisoryBodiesDbLabelToWebLabel")
    public AMLSupervisoryBodiesDto[] amlSupervisoryBodiesDbLabelToWebLabel(AMLSupervisoryBodiesDao[] labelArray) {
        var amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[0];
        if(labelArray != null){
            amlSupervisoryBodiesDto = new AMLSupervisoryBodiesDto[labelArray.length];
            for(var i=0; i<labelArray.length; i++){
                amlSupervisoryBodiesDto[i] = new AMLSupervisoryBodiesDto();
                amlSupervisoryBodiesDto[i].setAmlSupervisoryBody(AMLSupervisoryBodies.findBydblabel(labelArray[i].getAmlSupervisoryBody()).getWeblabel());
                amlSupervisoryBodiesDto[i].setAmlAcronym(AMLSupervisoryBodies.findBydblabel(labelArray[i].getAmlSupervisoryBody()).toString());
                amlSupervisoryBodiesDto[i].setMembershipId(labelArray[i].getMembershipId());
            }
            return amlSupervisoryBodiesDto;
        }
        return amlSupervisoryBodiesDto;
    }

}