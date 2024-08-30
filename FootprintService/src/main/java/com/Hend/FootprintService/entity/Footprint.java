package com.Hend.FootprintService.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "Footprint")
public class Footprint {
    @Id
    private String footprintId;
    private Date reportingPeriod;
    private String companyId;
    private String name;
    private String projectId;
    private Scope1 scope1;
    private Scope2 scope2;
    private Scope3 scope3;
    private float totalEmissions;

    public String getFootprintId() { return footprintId; }
    public void setFootprintId(String footprintId) { this.footprintId = footprintId; }
    public Date getReportingPeriod() { return reportingPeriod; }
    public void setReportingPeriod(Date reportingPeriod) { this.reportingPeriod = reportingPeriod; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getProjectId() { return projectId; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public Scope1 getScope1() { return scope1; }
    public void setScope1(Scope1 scope1) { this.scope1 = scope1; }
    public Scope2 getScope2() { return scope2; }
    public void setScope2(Scope2 scope2) { this.scope2 = scope2; }
    public Scope3 getScope3() { return scope3; }
    public void setScope3(Scope3 scope3) { this.scope3 = scope3; }
    public float getTotalEmissions() { return totalEmissions; }
    public void setTotalEmissions(float totalEmissions) { this.totalEmissions = totalEmissions; }



    public static class Scope1 {
        private List<Mobility> mobility;
        private List<OilGas> oilGas;
        private Agriculture agriculture;
        private float scope1Emissions;
        public List<Mobility> getMobility() { return mobility; }
        public void setMobility(List<Mobility> mobility) { this.mobility = mobility; }
        public List <OilGas> getOilGas() {
            return oilGas;
        }

        public Agriculture getAgriculture() {
            return agriculture;
        }

        public void setAgriculture(Agriculture agriculture) {
            this.agriculture = agriculture;
        }
        public float getScope1Emissions() {
            return scope1Emissions;
        }

        public void setScope1Emissions(float scope1Emissions) {
            this.scope1Emissions = scope1Emissions;
        }



        public static class Mobility {
            private VehicleType vehicleType;
            private FuelType fuelType;
            private float mpg;
            private float mValue;

            public VehicleType getVehicleType() { return vehicleType; }
            public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

            public FuelType getFuelType() { return fuelType; }
            public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

            public float getMpg() { return mpg; }
            public void setMpg(float mpg) { this.mpg = mpg; }

            public float getMValue() { return mValue; }
            public void setMValue(float mValue) { this.mValue = mValue; }
            // Getters and Setters
        }

        public static class OilGas {
            private int barrelNumber;
            private float oValue;

            public int getBarrelNumber() {
                return barrelNumber;
            }

            public void setBarrelNumber(int barrelNumber) {
                this.barrelNumber = barrelNumber;
            }

            public float getOValue() {
                return oValue;
            }

            public void setOValue(float oValue) {
                this.oValue = oValue;
            }
        }

        public static class Agriculture {
            private List<Livestock> livestock;
            private List<Fertiliser> fertiliser;

            public List<Fertiliser> getFertiliser() {
                return fertiliser;
            }

            public List<Livestock> getLivestock() {
                return livestock;
            }

            public void setLivestock(List<Livestock> livestock) {
                this.livestock = livestock;
            }

            public void setFertiliser(List<Fertiliser> fertiliser) {
                this.fertiliser = fertiliser;
            }

            public static class Livestock {
                private AnimalType animalType;
                private int animalNumber;
                private float lValue;

                public AnimalType getAnimalType() {
                    return animalType;
                }

                public void setAnimalType(AnimalType animalType) {
                    this.animalType = animalType;
                }

                public int getAnimalNumber() {
                    return animalNumber;
                }

                public void setAnimalNumber(int animalNumber) {
                    this.animalNumber = animalNumber;
                }

                public float getLValue() {
                    return lValue;
                }

                public void setLValue(float lValue) {
                    this.lValue = lValue;
                }
            }

            public static class Fertiliser {
                private FertiliserType fertiliserType;
                private float amount;
                private float fValue;

                public FertiliserType getFertiliserType() {
                    return fertiliserType;
                }

                public void setFertiliserType(FertiliserType fertiliserType) {
                    this.fertiliserType = fertiliserType;
                }

                public float getAmount() {
                    return amount;
                }

                public void setAmount(float amount) {
                    this.amount = amount;
                }

                public float getFValue() {
                    return fValue;
                }

                public void setFValue(float fValue) {
                    this.fValue = fValue;
                }
            }
        }
    }

    public static class Scope2 {

        private Electricity electricity;

        private Heating heating;
        private float scope2Emissions;

        public Electricity getElectricity() {
            return electricity;
        }

        public void setElectricity(Electricity electricity) {
            this.electricity = electricity;
        }

        public Heating getHeating() {
            return heating;
        }

        public void setHeating(Heating heating) {
            this.heating = heating;
        }
        public float getScope2Emissions() {
            return scope2Emissions;
        }

        public void setScope2Emissions(float scope2Emissions) {
            this.scope2Emissions = scope2Emissions;
        }

        public static class Electricity {
            private float usage;
            private float eValue;

            public float getUsage() {
                return usage;
            }

            public void setUsage(float usage) {
                this.usage = usage;
            }

            public float getEValue() {
                return eValue;
            }

            public void setEValue(float eValue) {
                this.eValue = eValue;
            }
        }

        public static class Heating {
            private FuelType fuelType;
            private float ccf;
            private float hValue;

            public FuelType getFuelType() {
                return fuelType;
            }

            // Setter for fuelType
            public void setFuelType(FuelType fuelType) {
                this.fuelType = fuelType;
            }

            // Getter for ccf
            public float getCcf() {
                return ccf;
            }

            // Setter for ccf
            public void setCcf(float ccf) {
                this.ccf = ccf;
            }

            // Getter for hValue
            public float getHValue() {
                return hValue;
            }

            // Setter for hValue
            public void setHValue(float hValue) {
                this.hValue = hValue;
            }
        }
    }

    public static class Scope3 {
        private List<Shipping> shipping;
        private List<BusinessTrip> businessTrip;
        private float scope3Emissions;

        public List<Shipping> getShipping() {
            return shipping;
        }

        public void setShipping(List<Shipping> shipping) {
            this.shipping = shipping;
        }

        public List<BusinessTrip> getBusinessTrip() {
            return businessTrip;
        }

        public void setBusinessTrip(List<BusinessTrip> businessTrip) {
            this.businessTrip = businessTrip;
        }

        public float getScope3Emissions() {
            return scope3Emissions;
        }

        public void setScope3Emissions(float scope3Emissions) {
            this.scope3Emissions = scope3Emissions;
        }

        public static class Shipping {
            private ShipmentType shipmentType;
            private float shipmentWeight;
            private float shipmentDistance;
            private float sValue;

            public ShipmentType getShipmentType() {
                return shipmentType;
            }

            public void setShipmentType(ShipmentType shipmentType) {
                this.shipmentType = shipmentType;
            }

            public float getShipmentWeight() {
                return shipmentWeight;
            }

            public void setShipmentWeight(float shipmentWeight) {
                this.shipmentWeight = shipmentWeight;
            }

            public float getShipmentDistance() {
                return shipmentDistance;
            }

            public void setShipmentDistance(float shipmentDistance) {
                this.shipmentDistance = shipmentDistance;
            }

            public float getsValue() {
                return sValue;
            }

            public void setSValue(float sValue) {
                this.sValue = sValue;
            }

            public float getSValue() {
                return sValue;
            }
        }

        public static class BusinessTrip {
            private BusinessType businessType;
            private float distance;
            private float bValue;
            // BusinessTrip fields and methods
            public BusinessType getBusinessType() {
                return businessType;
            }

            public void setBusinessType(BusinessType businessType) {
                this.businessType = businessType;
            }

            public float getDistance() {
                return distance;
            }

            public void setDistance(float distance) {
                this.distance = distance;
            }

            public float getBValue() {
                return bValue;
            }

            public void setBValue(float bValue) {
                this.bValue = bValue;
            }
        }
    }
}