package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;

@Component
@Mapper(componentModel = "spring", uses = EnumTranslator.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ACSPRegDataDtoDaoMapper {
      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessStringToEnum")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeStringToEnum")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorStringToEnum")
      @Mapping(source = "amlSupervisoryBodies", target = "amlSupervisoryBodies", qualifiedByName = "amlSupervisoryBodiesStringToEnum")
      AcspDataDto daoToDto(AcspDataDao acspDataDao);

      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessEnumToString")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeEnumToString")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorEnumToString")
      @Mapping(source = "amlSupervisoryBodies", target = "amlSupervisoryBodies", qualifiedByName = "amlSupervisoryBodiesEnumToString")
      AcspDataDao dtoToDao(AcspDataDto acspDataDto);
}
