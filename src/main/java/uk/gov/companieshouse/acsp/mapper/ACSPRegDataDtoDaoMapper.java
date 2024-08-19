package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;

@Component
@Mapper(componentModel = "spring")
public interface ACSPRegDataDtoDaoMapper {

      AcspDataDto daoToDto(AcspDataDao acspDataDao);

      @Mapping(source = "type", target = "type", ignore = true)
      AcspDataDao dtoToDao(AcspDataDto acspDataDto);

}
