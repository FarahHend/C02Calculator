package com.Hend.FootprintService.repository;

import com.Hend.FootprintService.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, String> {
    File findByFootprintId(String footprintId);
}
