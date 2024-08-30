package com.Hend.FootprintService.controller;

import com.Hend.FootprintService.entity.*;
import com.Hend.FootprintService.repository.FootprintRepository;
import com.Hend.FootprintService.service.CompanyServiceClient;
import com.Hend.FootprintService.service.FileService;
import com.Hend.FootprintService.service.Scope2Service;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/footprint/create")
public class Scope2Controller {

    private final Scope2Service scope2Service;

    @Autowired
    private FileService fileService;

    private final CompanyServiceClient companyServiceClient;

    @Autowired
    private FootprintRepository footprintRepository;


    @Autowired
    public Scope2Controller(Scope2Service scope2Service, CompanyServiceClient companyServiceClient) {
        this.scope2Service = scope2Service;
        //this.companyServiceClient = companyServiceClient;
        this.companyServiceClient = companyServiceClient;
    }

    @PostMapping("/addd/{companyId}")
    public ResponseEntity<?> saveFootprintSave(@PathVariable String companyId, @RequestBody Footprint footprint) {
        try {
            // Fetch the company name using the companyId
            ResponseEntity<String> responseEntity = companyServiceClient.getCompanyNameById(companyId);
            String companyName = responseEntity.getBody();

            // Set the company name in the Footprint entity
            if (companyName != null) {
                footprint.setName(companyName);
            } else {
                throw new IllegalArgumentException("Company not found with ID: " + companyId);
            }

            // Save the footprint using the service
            Footprint savedFootprint = scope2Service.saveFootprintSave(footprint, companyId);

            return ResponseEntity.ok(savedFootprint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company not found: " + e.getMessage());
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company not found: " + e.getMessage());
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request for company details: " + e.getMessage());
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch company details: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request: " + e.getMessage());
        }
    }


    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Footprint>> getFootprintsByCompanyId(@PathVariable String companyId) {
        List<Footprint> footprints = scope2Service.getFootprintsByCompanyId(companyId);
        return ResponseEntity.ok(footprints);
    }

    @GetMapping("/footprint/{footprintId}/download")
    public ResponseEntity<byte[]> downloadFootprint(@PathVariable String footprintId) {
        try {
            File file = fileService.generateCsvFile(footprintId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(file.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @GetMapping("/footprint/{footprintId}/generatePdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable String footprintId) {
        try {
            Optional<Footprint> optionalFootprint = footprintRepository.findById(footprintId);
            if (optionalFootprint.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }

            Footprint footprint = optionalFootprint.get();
            byte[] pdfContent = fileService.generatePdf(footprint);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"footprint_" + footprintId + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/add/{companyId}")
    public ResponseEntity<Footprint> saveFootprint(@PathVariable String companyId, @RequestBody Footprint footprint) {
        try {
            // Fetch the company name using the companyId
            ResponseEntity<String> responseEntity = companyServiceClient.getCompanyNameById(companyId);
            String companyName = responseEntity.getBody();

            // Set the company name in the Footprint entity
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

        // Save the footprint using the service
        Footprint savedFootprint = scope2Service.saveFootprint(footprint, companyId);

        // Initialize StringBuilder for the response message
        StringBuilder responseMessage = new StringBuilder();

        // Append company name to the response message
        responseMessage.append("Company Name: ").append(footprint.getName()).append("\n");


        // Retrieve company details
      //  ResponseEntity<CompanyResponse> companyResponse = companyServiceClient.getCompanyById(companyId);
        //if (companyResponse.getStatusCode().is2xxSuccessful() && companyResponse.getBody() != null) {
           // String companyName = companyResponse.getBody().getName();
           // responseMessage.append("Company Name: ").append(companyName).append("\n");
        //} else {
           // responseMessage.append("Company not found.\n");
       // }
        float totalScope1Emissions = savedFootprint.getScope1() != null ? savedFootprint.getScope1().getScope1Emissions() : 0.0f;
        float totalScope2Emissions = savedFootprint.getScope2() != null ? savedFootprint.getScope2().getScope2Emissions() : 0.0f;
        float totalScope3Emissions = savedFootprint.getScope3() != null ? savedFootprint.getScope3().getScope3Emissions() : 0.0f;
        float totalEmissions = savedFootprint.getTotalEmissions();

        // Append totals to the response message
        responseMessage.append("Total Scope 1 Emissions: ").append(totalScope1Emissions).append(" kg CO2e\n");
        responseMessage.append("Total Scope 2 Emissions: ").append(totalScope2Emissions).append(" kg CO2e\n");
        responseMessage.append("Total Scope 3 Emissions: ").append(totalScope3Emissions).append(" kg CO2e\n");
        responseMessage.append("Total Emissions: ").append(totalEmissions).append(" kg CO2e\n");

        // Calculate the percentage of each scope from the total emissions
        float percentScope1 = totalEmissions != 0 ? (totalScope1Emissions / totalEmissions) * 100 : 0.0f;
        float percentScope2 = totalEmissions != 0 ? (totalScope2Emissions / totalEmissions) * 100 : 0.0f;
        float percentScope3 = totalEmissions != 0 ? (totalScope3Emissions / totalEmissions) * 100 : 0.0f;

        // Append percentages to the response message
        responseMessage.append("Percentage of Scope 1 Emissions: ").append(String.format("%.2f", percentScope1)).append("%\n");
        responseMessage.append("Percentage of Scope 2 Emissions: ").append(String.format("%.2f", percentScope2)).append("%\n");
        responseMessage.append("Percentage of Scope 3 Emissions: ").append(String.format("%.2f", percentScope3)).append("%\n");


        // Generate the formatted emissions string for electricity
        if (savedFootprint.getScope2() != null && savedFootprint.getScope2().getElectricity() != null) {
            Footprint.Scope2.Electricity electricity = savedFootprint.getScope2().getElectricity();
            float usage = electricity.getUsage();
            float emissionFactor = 0.1f;
            float emissions = usage * emissionFactor;
            float emissionsInTonnes = emissions / 1000;
            String formattedEmissions = String.format(
                    "Electricity Emissions = %.2f kWh × %.1f kg CO2e/kWh = %.2f kg CO2e = %.2f tonnes CO2e",
                    usage, emissionFactor, emissions, emissionsInTonnes
            );
            responseMessage.append(formattedEmissions).append("\n");
        }

        // Generate the formatted emissions string for heating
        if (savedFootprint.getScope2() != null && savedFootprint.getScope2().getHeating() != null) {
            Footprint.Scope2.Heating heating = savedFootprint.getScope2().getHeating();
            float ccf = heating.getCcf();
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
            float emissions = ccf * emissionFactor;
            float emissionsInTonnes = emissions / 1000;
            String formattedEmissions = String.format(
                    "Heating Emissions = %.2f CCF of %s × %.2f kg CO2e/CCF = %.2f kg CO2e = %.2f tonnes CO2e",
                    ccf, fuelType.name(), emissionFactor, emissions, emissionsInTonnes
            );
            responseMessage.append(formattedEmissions).append("\n");
        }

        // Generate the formatted emissions string for shipping
        if (savedFootprint.getScope3() != null && savedFootprint.getScope3().getShipping() != null) {
            for (Footprint.Scope3.Shipping shipping : savedFootprint.getScope3().getShipping()) {
                float shipmentWeight = shipping.getShipmentWeight();
                float shipmentDistance = shipping.getShipmentDistance();
                float emissionFactor;
                ShipmentType shipmentType = shipping.getShipmentType();
                switch (shipmentType) {
                    case Air:
                        emissionFactor = 0.5f;
                        break;
                    case Sea:
                        emissionFactor = 0.016f;
                        break;
                    case Rail:
                        emissionFactor = 0.02f;
                        break;
                    case Car:
                        emissionFactor = 0.15f;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown shipment type: " + shipmentType);
                }
                float emissions = shipmentWeight * shipmentDistance * emissionFactor;
                float emissionsInTonnes = emissions / 1000;
                String formattedEmissions = String.format(
                        "Shipping Emissions = %.2f kg of %s × %.2f kg CO2e/kg/km × %.2f km = %.2f kg CO2e = %.2f tonnes CO2e",
                        shipmentWeight, shipmentType.name(), emissionFactor, shipmentDistance, emissions, emissionsInTonnes
                );
                responseMessage.append(formattedEmissions).append("\n");
            }
        }

        // Generate the formatted emissions string for oil and gas
        if (savedFootprint.getScope1() != null && savedFootprint.getScope1().getOilGas() != null) {
            for (Footprint.Scope1.OilGas oilGas : savedFootprint.getScope1().getOilGas()) {
                int barrelNumber = oilGas.getBarrelNumber();
                float emissionFactor = 400.0f; // kg CO2e per barrel
                float emissions = barrelNumber * emissionFactor;
                float emissionsInTonnes = emissions / 1000;
                String formattedEmissions = String.format(
                        "Oil and Gas Emissions = %d barrels × %.2f kg CO2e/barrel = %.2f kg CO2e = %.2f tonnes CO2e",
                        barrelNumber, emissionFactor, emissions, emissionsInTonnes
                );
                responseMessage.append(formattedEmissions).append("\n");
            }
        }

        // Generate the formatted emissions string for livestock
        if (savedFootprint.getScope1() != null && savedFootprint.getScope1().getAgriculture() != null) {
            Footprint.Scope1.Agriculture agriculture = (Footprint.Scope1.Agriculture) savedFootprint.getScope1().getAgriculture();
            if (agriculture.getLivestock() != null) {
                for (Footprint.Scope1.Agriculture.Livestock livestock : agriculture.getLivestock()) {
                    float animalNumber = livestock.getAnimalNumber();
                    float emissionFactor;
                    switch (livestock.getAnimalType()) {
                        case Beef_cattle:
                            emissionFactor = 70.0f;
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
                    float emissions = animalNumber * emissionFactor;
                    float emissionsInTonnes = emissions / 1000;
                    String formattedEmissions = String.format(
                            "Livestock Emissions = %.2f animals of %s × %.2f kg CO2e/animal = %.2f kg CO2e = %.2f tonnes CO2e",
                            animalNumber, livestock.getAnimalType().name(), emissionFactor, emissions, emissionsInTonnes
                    );
                    responseMessage.append(formattedEmissions).append("\n");
                }
            }
        }

        // Generate the formatted emissions string for fertiliser
        if (savedFootprint.getScope1() != null && savedFootprint.getScope1().getAgriculture() != null) {
            Footprint.Scope1.Agriculture agriculture = (Footprint.Scope1.Agriculture) savedFootprint.getScope1().getAgriculture();
            if (agriculture.getFertiliser() != null) {
                for (Footprint.Scope1.Agriculture.Fertiliser fertiliser : agriculture.getFertiliser()) {
                    float amount = fertiliser.getAmount();
                    float emissionFactor;
                    switch (fertiliser.getFertiliserType()) {
                        case Dolomite:
                            emissionFactor = 0.5f;
                            break;
                        case Urea_with_urease_inhibitor:
                            emissionFactor = 1.5f;
                            break;
                        case Lime:
                            emissionFactor = 0.8f;
                            break;
                        case Urea_without_urease_inhibitor:
                            emissionFactor = 1.5f;
                            break;
                        case Non_urea_nitrogen_1:
                            emissionFactor = 2.5f;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown fertiliser type: " + fertiliser.getFertiliserType());
                    }
                    float emissions = amount * emissionFactor;
                    float emissionsInTonnes = emissions / 1000;
                    String formattedEmissions = String.format(
                            "Fertiliser Emissions = %.2f kg of %s × %.2f kg CO2e/kg = %.2f kg CO2e = %.2f tonnes CO2e",
                            amount, fertiliser.getFertiliserType().name(), emissionFactor, emissions, emissionsInTonnes
                    );
                    responseMessage.append(formattedEmissions).append("\n");
                }
            }
        }

        // Generate the formatted emissions string for business trips
        if (savedFootprint.getScope3() != null && savedFootprint.getScope3().getBusinessTrip() != null) {
            for (Footprint.Scope3.BusinessTrip businessTrip : savedFootprint.getScope3().getBusinessTrip()) {
                float distance = businessTrip.getDistance();
                float emissionFactor;
                BusinessType businessType = businessTrip.getBusinessType();
                switch (businessType) {
                    case Air_Travel:
                        emissionFactor = 0.12f;
                        break;
                    case Rail_Travel:
                        emissionFactor = 0.017f;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown business type: " + businessType);
                }
                float emissions = distance * emissionFactor;
                float emissionsInTonnes = emissions / 1000;
                String formattedEmissions = String.format(
                        "Business Trip Emissions = %.2f km of %s × %.3f kg CO2e/km = %.2f kg CO2e = %.2f tonnes CO2e",
                        distance, businessType.name(), emissionFactor, emissions, emissionsInTonnes
                );
                responseMessage.append(formattedEmissions).append("\n");
            }
        }

        // Generate the formatted emissions string for mobility
        if (savedFootprint.getScope1() != null && savedFootprint.getScope1().getMobility() != null) {
            for (Footprint.Scope1.Mobility mobility : savedFootprint.getScope1().getMobility()) {
                float distance = mobility.getMpg();
                float emissionFactor;
                VehicleType vehicleType = mobility.getVehicleType();
                switch (vehicleType) {
                    case Car:
                        emissionFactor = 0.2f; // Example factor for car
                        break;
                    case Motorcycle:
                        emissionFactor = 0.1f; // Example factor for motorcycle
                        break;
                    case Bus:
                        emissionFactor = 0.05f; // Example factor for bus
                        break;
                    case Truck:
                        emissionFactor = 0.03f; // Example factor for train
                        break;
                    case Bicycle:
                        emissionFactor = 0.00f; // Example factor for train
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
                }
                float emissions = distance * emissionFactor;
                float emissionsInTonnes = emissions / 1000;
                String formattedEmissions = String.format(
                        "Mobility Emissions = %.2f km of %s × %.2f kg CO2e/km = %.2f kg CO2e = %.2f tonnes CO2e",
                        distance, vehicleType.name(), emissionFactor, emissions, emissionsInTonnes
                );
                responseMessage.append(formattedEmissions).append("\n");
            }
        }

        //return ResponseEntity.ok(responseMessage.toString());
        return ResponseEntity.ok(savedFootprint);
    }

    @GetMapping("/{footprintId}")
    public Footprint getFootprintByIdId(@PathVariable String footprintId) {
        return scope2Service.getFootprintByIdId(footprintId);
    }
    @GetMapping("/getall")
    public List<Footprint> getAllFootprints() {
        return scope2Service.getAllFootprints();
    }
}
