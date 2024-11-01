package uk.gov.companieshouse.acsp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.acsp.models.dao.Acsp;

@Repository
//For now datatype is String, Change the datatype later accordingly
public interface AcspRepository extends MongoRepository<Acsp, String> {

    int countById(String id);

}
