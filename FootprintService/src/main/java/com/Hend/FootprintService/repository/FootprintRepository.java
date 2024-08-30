package com.Hend.FootprintService.repository;


import com.Hend.FootprintService.entity.Footprint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FootprintRepository extends MongoRepository<Footprint, String> {

    //Footprint findByCompanyId(String companyId);
    List<Footprint> findByCompanyId(String companyId);
    Optional<Footprint> findById(String footprintId);

    Footprint findFootprintByFootprintId(String footprintId);

}
