package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dao.DataDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.dto.DataMapper;

@Component
@Mapper(componentModel = "spring", uses = EnumTranslator.class)
public interface ACSPRegDataDtoDaoMapper {
      @Mapping(source = "typeOfBusiness", target = "typeOfBusiness", qualifiedByName = "TypeOfBusinessStringToEnum")
      @Mapping(source = "roleType", target = "roleType", qualifiedByName = "RoleTypeStringToEnum")
      @Mapping(source = "workSector", target = "workSector", qualifiedByName = "WorkSectorStringToEnum")
      AcspDataDto daoToDto(DataDao dataDao);

      @Mapping(source = "data.typeOfBusiness", target = "data.typeOfBusiness", qualifiedByName = "TypeOfBusinessEnumToString")
      @Mapping(source = "data.roleType", target = "data.roleType", qualifiedByName = "RoleTypeEnumToString")
      @Mapping(source = "data.workSector", target = "data.workSector", qualifiedByName = "WorkSectorEnumToString")
      AcspDataDao dtoToDao(DataMapper dataMapper);
}
