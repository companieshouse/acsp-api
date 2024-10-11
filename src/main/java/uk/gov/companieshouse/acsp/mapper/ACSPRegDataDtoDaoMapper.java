package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.dao.AMLSupervisoryBodiesDao;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = EnumTranslator.class)
public interface ACSPRegDataDtoDaoMapper {

      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessStringToEnum")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeStringToEnum")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorStringToEnum")
      @Mapping(source = "amlSupervisoryBodies", target = "amlSupervisoryBodies", qualifiedByName = "StringArrayToAMLSupervisoryBodiesEnumArray")
      AcspDataDto daoToDto(AcspDataDao acspDataDao);

      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessEnumToString")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeEnumToString")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorEnumToString")
      @Mapping(source = "amlSupervisoryBodies", target = "amlSupervisoryBodies", qualifiedByName = "AMLSupervisoryBodiesEnumArrayToStringArray")
      AcspDataDao dtoToDao(AcspDataDto acspDataDto);

      AMLSupervisoryBodiesDto[] map(AMLSupervisoryBodiesDao[] daoArray);
      AMLSupervisoryBodiesDao[] map(AMLSupervisoryBodiesDto[] dtoArray);


}

