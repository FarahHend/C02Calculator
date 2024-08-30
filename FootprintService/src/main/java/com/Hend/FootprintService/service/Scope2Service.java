package com.Hend.FootprintService.service;

import com.Hend.CompanyService.entity.Company;
import com.Hend.CompanyService.entity.UserResponse;
import com.Hend.CompanyService.service.UserServiceClient;
import com.Hend.FootprintService.entity.*;
import com.Hend.FootprintService.repository.FootprintRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Scope2Service {

    @Autowired
    private CompanyServiceClient companyServiceClient;

    @Autowired
    private FootprintRepository footprintRepository;

   // public ResponseEntity<CompanyResponse> getCompanyById(String companyId) {
       // return companyServiceClient.getCompanyById(companyId);
   // }
   public CompanyResponse getCompanyById(String companyId) {
       return companyServiceClient.getCompanyById(companyId);
   }
    public List<Footprint> getFootprintsByCompanyId(String companyId) {
        return footprintRepository.findByCompanyId(companyId);
    }

    public Footprint getFootprintById(String footprintId) {
        Optional<Footprint> footprint = footprintRepository.findById(footprintId);
        return footprint.orElse(null);
    }

    public Footprint getFootprintByIdId(String footprintId) {
        return footprintRepository.findFootprintByFootprintId(footprintId);
    }

    public List<Footprint> getAllFootprints() {
        return footprintRepository.findAll();
    }

    public void calculateElectricityEmissions(Footprint.Scope2.Electricity electricity) {

        float emissionFactor = 0.1f; //kg CO2e per kwh  per year

        float emissions = electricity.getUsage() * emissionFactor;

        electricity.setEValue(emissions);
    }

    public void calculateHeatingEmissions(Footprint.Scope2.Heating heating) {
        float emissionFactor;
        FuelType fuelType = heating.getFuelType();
        switch (fuelType) {
            case NATURAL_GAS:
                emissionFactor = 5.45f;
                break;
            case PROPANE:
            case OIL:
                emissionFactor = 61.9f;
                break;
            default:
                throw new IllegalArgumentException("Unknown fuel type: " + fuelType);
        }

        float emissions = heating.getCcf() * emissionFactor;

        heating.setHValue(emissions);
    }

    public void calculateShippingEmissions(Footprint.Scope3.Shipping shipping) {
        float emissionFactor;
        ShipmentType shipmentType = shipping.getShipmentType();
        switch (shipmentType) {
            case Air:
                emissionFactor = 0.5f; // kg CO2e per kg per km per year
                break;
            case Sea:
                emissionFactor = 0.016f; // kg CO2e per kg per km
                break;
            case Rail:
                emissionFactor = 0.02f; // kg CO2e per kg per km
                break;
            case Car:
                emissionFactor = 0.15f; // kg CO2e per kg per km
                break;
            default:
                throw new IllegalArgumentException("Unknown shipment type: " + shipmentType);
        }

        float emissions = shipping.getShipmentWeight() * shipping.getShipmentDistance() * emissionFactor;

        shipping.setSValue(emissions);
    }

    public void calculateOilGasEmissions(Footprint.Scope1.OilGas oilGas) {
        // (kg CO2e per barrel)
        float emissionFactor = 400.0f;

        float emissions = oilGas.getBarrelNumber() * emissionFactor;

        oilGas.setOValue(emissions);
    }

    public void calculateLivestockEmissions(Footprint.Scope1.Agriculture.Livestock livestock) {
        float emissionFactor;
        switch (livestock.getAnimalType()) {
            case Beef_cattle:
                emissionFactor = 70.0f; // kg CO2e per 1 animal per year
                break;
            case Sheep:
            case Goats:
                emissionFactor = 9.0f;
                break;
            case Pigs:
                emissionFactor = 7.0f;
                break;
            case Horses:
                emissionFactor = 22.0f;
                break;
            default:
                throw new IllegalArgumentException("Unknown animal type: " + livestock.getAnimalType());
        }

        float emissions = livestock.getAnimalNumber() * emissionFactor;

        livestock.setLValue(emissions);
    }

    public void calculateFertiliserEmissions(Footprint.Scope1.Agriculture.Fertiliser fertiliser) {
        float emissionFactor;

        switch (fertiliser.getFertiliserType()) {
            case Dolomite:
                emissionFactor = 0.5f; // kg CO2e per kg
                break;
            case Urea_with_urease_inhibitor:
                emissionFactor = 1.5f; // kg CO2e per kg
                break;
            case Lime:
                emissionFactor = 0.8f; // kg CO2e per kg
                break;
            case Urea_without_urease_inhibitor:
                emissionFactor = 1.5f; // kg CO2e per kg
                break;
            case Non_urea_nitrogen_1:
                emissionFactor = 2.5f; // kg CO2e per kg
                break;
            default:
                throw new IllegalArgumentException("Unknown fertiliser type");
        }


        float emissions = fertiliser.getAmount() * emissionFactor;

        fertiliser.setFValue(emissions);
    }

    public void calculateBusinessTripEmissions(Footprint.Scope3.BusinessTrip businessTrip) {
        float emissionFactor;
        BusinessType businessType = businessTrip.getBusinessType();
        switch (businessType) {
            case Air_Travel:
                emissionFactor = 0.12f; // kg CO2e per km
                break;
            case Rail_Travel:
                emissionFactor = 0.017f; // kg CO2e per km
                break;
            default:
                throw new IllegalArgumentException("Unknown business type: " + businessType);
        }

        float emissions = businessTrip.getDistance() * emissionFactor;

        businessTrip.setBValue(emissions);
    }

    public void calculateMobilityEmissions(Footprint.Scope1.Mobility mobility) {
        float emissionFactor;
        switch (mobility.getVehicleType()) {
            case Car:
                emissionFactor = mobility.getFuelType() == FuelType.Gasoline ? 0.25f : 0.24f;
                break;
            case Motorcycle:
                emissionFactor = mobility.getFuelType() == FuelType.Gasoline ? 0.20f : 0.0f;
                break;
            case Bus:
                emissionFactor = mobility.getFuelType() == FuelType.Gasoline ? 0.35f : 0.32f;
                break;
            case Truck:
                emissionFactor = mobility.getFuelType() == FuelType.Gasoline ? 0.50f : 0.45f;
                break;
            case Bicycle:
                emissionFactor = 0.0f;
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + mobility.getVehicleType());
        }
        float emissions = mobility.getMpg() * emissionFactor;
        mobility.setMValue(emissions);
    }

    public Footprint saveFootprint(Footprint footprint, String companyId) {
        float totalScope1Emissions = 0.0f;
        float totalScope2Emissions = 0.0f;
        float totalScope3Emissions = 0.0f;

        // Set the companyId on the Footprint
        footprint.setCompanyId(companyId);

        try {
            // Fetch the company name using the companyId
            ResponseEntity<String> responseEntity = companyServiceClient.getCompanyNameById(companyId);
            String companyName = responseEntity.getBody();

            if (companyName != null) {
                footprint.setName(companyName);
            } else {
                throw new IllegalArgumentException("Company not found with ID: " + companyId);
            }
        } catch (FeignException.NotFound e) {
            // Handle 404 Not Found
            System.err.println("Company not found: " + e.getMessage());
            throw new RuntimeException("Company not found with ID: " + companyId, e);
        } catch (FeignException.BadRequest e) {
            // Handle 400 Bad Request
            System.err.println("Bad request: " + e.getMessage());
            throw new RuntimeException("Invalid request for company details", e);
        } catch (FeignException e) {
            // Handle other Feign exceptions
            System.err.println("Feign exception: " + e.getMessage());
            throw new RuntimeException("Failed to fetch company details", e);
        } catch (Exception e) {
            // Handle general exceptions
            System.err.println("Exception: " + e.getMessage());
            throw new RuntimeException("An error occurred while processing the request", e);
        }

        // Fetch the company details using the companyId
        // Calculate emissions if scope1 is not null
        if (footprint.getScope1() != null) {
            if (footprint.getScope1().getOilGas() != null) {
                for (Footprint.Scope1.OilGas oilGas : footprint.getScope1().getOilGas()) {
                    calculateOilGasEmissions(oilGas);
                    totalScope1Emissions += oilGas.getOValue();
                    //footprint.getScope1().setScope1Emissions(totalScope1Emissions);
                }
            }
            if (footprint.getScope1().getAgriculture() != null) {
                Footprint.Scope1.Agriculture agriculture = (Footprint.Scope1.Agriculture) footprint.getScope1().getAgriculture();
                if (agriculture.getLivestock() != null) {
                    for (Footprint.Scope1.Agriculture.Livestock livestock : agriculture.getLivestock()) {
                        calculateLivestockEmissions(livestock);
                        totalScope1Emissions += livestock.getLValue();
                        //footprint.getScope1().setScope1Emissions(totalScope1Emissions);
                    }
                }
                if (agriculture.getFertiliser() != null) {
                    for (Footprint.Scope1.Agriculture.Fertiliser fertiliser : agriculture.getFertiliser()) {
                        calculateFertiliserEmissions(fertiliser);
                        totalScope1Emissions += fertiliser.getFValue();
                       // footprint.getScope1().setScope1Emissions(totalScope1Emissions);
                    }
                }
            }
            if (footprint.getScope1().getMobility() != null) {
                for (Footprint.Scope1.Mobility mobility : footprint.getScope1().getMobility()) {
                    calculateMobilityEmissions(mobility);
                    totalScope1Emissions += mobility.getMValue();
                    //footprint.getScope1().setScope1Emissions(totalScope1Emissions);
                }
            }
            footprint.getScope1().setScope1Emissions(totalScope1Emissions);
        }

        // Calculate emissions if scope2 is not null
        if (footprint.getScope2() != null) {
            if (footprint.getScope2().getElectricity() != null) {
                calculateElectricityEmissions(footprint.getScope2().getElectricity());
                totalScope2Emissions += footprint.getScope2().getElectricity().getEValue();
                //footprint.getScope2().setScope2Emissions(totalScope2Emissions);
            }
            if (footprint.getScope2().getHeating() != null) {
                calculateHeatingEmissions(footprint.getScope2().getHeating());
                totalScope2Emissions += footprint.getScope2().getHeating().getHValue();
               // footprint.getScope2().setScope2Emissions(totalScope2Emissions);
            }
            footprint.getScope2().setScope2Emissions(totalScope2Emissions);
        }

        // Calculate emissions if scope3 is not null
        if (footprint.getScope3() != null) {
            if (footprint.getScope3().getShipping() != null) {
                for (Footprint.Scope3.Shipping shipping : footprint.getScope3().getShipping()) {
                    calculateShippingEmissions(shipping);
                    totalScope3Emissions += shipping.getSValue();
                    //footprint.getScope3().setScope3Emissions(totalScope3Emissions);
                }
            }
            if (footprint.getScope3().getBusinessTrip() != null) {
                for (Footprint.Scope3.BusinessTrip businessTrip : footprint.getScope3().getBusinessTrip()) {
                    calculateBusinessTripEmissions(businessTrip);
                    totalScope3Emissions += businessTrip.getBValue();
                   // footprint.getScope3().setScope3Emissions(totalScope3Emissions);
                }
            }
            footprint.getScope3().setScope3Emissions(totalScope3Emissions);
        }

        // Calculate total emissions
        float totalEmissions = totalScope1Emissions + totalScope2Emissions + totalScope3Emissions;
        footprint.setTotalEmissions(totalEmissions);

        // Save the footprint
        Footprint savedFootprint = footprintRepository.save(footprint);

        return savedFootprint;
    }


    public Footprint saveFootprintSave(Footprint footprint, String companyId) {
        // Set the companyId on the Footprint
        footprint.setCompanyId(companyId);

        try {
            // Fetch the company name using the companyId
            ResponseEntity<String> responseEntity = companyServiceClient.getCompanyNameById(companyId);
            String companyName = responseEntity.getBody();

            if (companyName != null) {
                footprint.setName(companyName);
            } else {
                throw new IllegalArgumentException("Company not found with ID: " + companyId);
            }
        } catch (FeignException.NotFound e) {
            // Handle 404 Not Found
            System.err.println("Company not found: " + e.getMessage());
            throw new RuntimeException("Company not found with ID: " + companyId, e);
        } catch (FeignException.BadRequest e) {
            // Handle 400 Bad Request
            System.err.println("Bad request: " + e.getMessage());
            throw new RuntimeException("Invalid request for company details", e);
        } catch (FeignException e) {
            // Handle other Feign exceptions
            System.err.println("Feign exception: " + e.getMessage());
            throw new RuntimeException("Failed to fetch company details", e);
        } catch (Exception e) {
            // Handle general exceptions
            System.err.println("Exception: " + e.getMessage());
            throw new RuntimeException("An error occurred while processing the request", e);
        }

        // Calculate emissions and save the footprint
        return footprintRepository.save(footprint);
    }


}