package uk.gov.companieshouse.acsp.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.model.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.model.dto.AcspDataDto;

@Component
@Mapper(componentModel = "spring")
public interface ACSPRegDataDtoDaoMapper {

      AcspDataDto daoToDto(AcspDataDao acspDataDao);

      AcspDataDao dtoToDao(AcspDataDto acspDataDto);
}
