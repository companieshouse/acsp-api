package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;

@Component
@Mapper(componentModel = "spring", uses = EnumTranslator.class)
public interface ACSPRegDataDtoDaoMapper {
      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessStringToEnum")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeStringToEnum")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorStringToEnum")
      @Mapping(source = "amlSupervisoryBodies", target = "amlSupervisoryBodies", qualifiedByName = "amlSupervisoryBodiesDbValueToDisplayLabel")
      AcspDataDto daoToDto(AcspDataDao acspDataDao);

      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessEnumToString")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeEnumToString")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorEnumToString")
      @Mapping(source = "amlSupervisoryBodies", target = "amlSupervisoryBodies", qualifiedByName = "amlSupervisoryBodiesDisplayLabelToDbValue")
      AcspDataDao dtoToDao(AcspDataDto acspDataDto);
}
