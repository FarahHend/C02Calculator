package com.Hend.FootprintService.entity;

public class CompanyResponse {

    private String companyId;
    private String companyName;
    private Integer employeesNbr;

    // Constructor
    public CompanyResponse(String companyId, String companyName, Integer employeesNbr) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.employeesNbr = employeesNbr;
    }

    // Getters and Setters
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getEmployeesNbr() {
        return employeesNbr;
    }

    public void setEmployeesNbr(Integer employeesNbr) {
        this.employeesNbr = employeesNbr;
    }
}