package com.Hend.CompanyService.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection  = "Company")
@Data
public class Company {
    @Id
    private String companyId;
    private String name;
    private Sector sector;
    private Country country;
    private Integer employeesNbr;
    private String userId;
}