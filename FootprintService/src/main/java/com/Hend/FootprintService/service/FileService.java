package com.Hend.FootprintService.service;

import com.Hend.FootprintService.entity.CompanyResponse;
import com.Hend.FootprintService.entity.File;
import com.Hend.FootprintService.entity.Footprint;
import com.Hend.FootprintService.repository.FileRepository;
import com.Hend.FootprintService.repository.FootprintRepository;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FootprintRepository footprintRepository;

    @Autowired
    private CompanyServiceClient companyServiceClient;

    //@Autowired
    //private CompanyRepository companyRepository;

    //public ResponseEntity<CompanyResponse> getCompanyById(String companyId) {
       // return companyServiceClient.getCompanyById(companyId);
    //}
    public File generateCsvFile(String footprintId) throws Exception {
        Footprint footprint = footprintRepository.findById(footprintId).orElseThrow(() -> new Exception("Footprint not found"));
       // Company company = companyRepository.findById(footprint.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));

        // Write CSV headers
        writer.writeNext(new String[]{
                "Footprint ID", "Reporting Period", "Company ID", "Project ID", "Total Emissions",
                // Scope 1
                "Scope1_Mobility_VehicleType", "Scope1_Mobility_FuelType", "Scope1_Mobility_MPG", "Scope1_Mobility_MValue",
                "Scope1_OilGas_BarrelNumber", "Scope1_OilGas_OValue",
                "Scope1_Agriculture_Livestock_AnimalType", "Scope1_Agriculture_Livestock_AnimalNumber", "Scope1_Agriculture_Livestock_LValue",
                "Scope1_Agriculture_Fertiliser_FertiliserType", "Scope1_Agriculture_Fertiliser_Amount", "Scope1_Agriculture_Fertiliser_FValue",
                // Scope 2
                "Scope2_Electricity_Usage", "Scope2_Electricity_EValue",
                "Scope2_Heating_FuelType", "Scope2_Heating_CCF", "Scope2_Heating_HValue",
                // Scope 3
                "Scope3_Shipping_ShipmentType", "Scope3_Shipping_ShipmentWeight", "Scope3_Shipping_ShipmentDistance", "Scope3_Shipping_SValue",
                "Scope3_BusinessTrip_BusinessType", "Scope3_BusinessTrip_Distance", "Scope3_BusinessTrip_BValue"
        });

        // Flatten Scope1 data
        String scope1Mobility = footprint.getScope1() != null ? footprint.getScope1().getMobility().stream()
                .map(m -> m.getVehicleType() + ";" + m.getFuelType() + ";" + m.getMpg() + ";" + m.getMValue())
                .collect(Collectors.joining("|")) : "";

        String scope1OilGas = footprint.getScope1() != null ? footprint.getScope1().getOilGas().stream()
                .map(o -> o.getBarrelNumber() + ";" + o.getOValue())
                .collect(Collectors.joining("|")) : "";

        String scope1AgricultureLivestock = footprint.getScope1() != null ? footprint.getScope1().getAgriculture().getLivestock().stream()
                .map(l -> l.getAnimalType() + ";" + l.getAnimalNumber() + ";" + l.getLValue())
                .collect(Collectors.joining("|")) : "";

        String scope1AgricultureFertiliser = footprint.getScope1() != null ? footprint.getScope1().getAgriculture().getFertiliser().stream()
                .map(f -> f.getFertiliserType() + ";" + f.getAmount() + ";" + f.getFValue())
                .collect(Collectors.joining("|")) : "";

        // Flatten Scope2 data
        String scope2Electricity = footprint.getScope2() != null ? footprint.getScope2().getElectricity().getUsage() + ";" + footprint.getScope2().getElectricity().getEValue() : "";
        String scope2Heating = footprint.getScope2() != null ? footprint.getScope2().getHeating().getFuelType() + ";" + footprint.getScope2().getHeating().getCcf() + ";" + footprint.getScope2().getHeating().getHValue() : "";

        // Flatten Scope3 data
        String scope3Shipping = footprint.getScope3() != null ? footprint.getScope3().getShipping().stream()
                .map(s -> s.getShipmentType() + ";" + s.getShipmentWeight() + ";" + s.getShipmentDistance() + ";" + s.getSValue())
                .collect(Collectors.joining("|")) : "";

        String scope3BusinessTrip = footprint.getScope3() != null ? footprint.getScope3().getBusinessTrip().stream()
                .map(b -> b.getBusinessType() + ";" + b.getDistance() + ";" + b.getBValue())
                .collect(Collectors.joining("|")) : "";

        // Write footprint data
        writer.writeNext(new String[]{
                footprint.getFootprintId(),
                footprint.getReportingPeriod().toString(),
                footprint.getCompanyId(),
                footprint.getProjectId(),
                String.valueOf(footprint.getTotalEmissions()),
                // Scope 1
                scope1Mobility,
                scope1OilGas,
                scope1AgricultureLivestock,
                scope1AgricultureFertiliser,
                // Scope 2
                scope2Electricity,
                scope2Heating,
                // Scope 3
                scope3Shipping,
                scope3BusinessTrip
        });

        writer.close();

        byte[] content = baos.toByteArray();

        File file = File.builder()
                .footprintId(footprintId)
                .filename("footprint_" + footprintId + ".csv")
                .content(content)
                .contentType("text/csv")
                .size(content.length)
                .uploadDate(new Date())
                .build();

        return fileRepository.save(file);
    }

    public byte[] generatePdf(Footprint footprint) throws IOException {
       // Company company = companyRepository.findById(footprint.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        // Add page number event handler
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new MyFooterEventHandler(document));

        try {
            // Format date
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            // Add Company Name to PDF
            document.add(new Paragraph("Company Name: " + footprint.getName())
                    .setFontSize(15)
                    .setBold());
            // Add Company Name, Sector, and Country to PDF
           // document.add(new Paragraph("Company Name: " + company.getName()));
           // document.add(new Paragraph("Sector: " + company.getSector().toString()));
           // document.add(new Paragraph("Country: " + company.getCountry().toString()));

            // Create the header paragraph with tabs
            Paragraph headerParagraph = new Paragraph()
                    .add(timestamp)
                    .addTabStops(new TabStop(1000, TabAlignment.RIGHT))
                    .add(new Tab())
                    .add("Calculate your CO2 emissions | Horizop Energy");

            // Add the header paragraph to the document
            document.add(headerParagraph);

            // Add a line break after the header
            document.add(new Paragraph("\n"));

            // Load the static image from resources
            ClassPathResource imageResource = new ClassPathResource("BN - White BG.png");
            try (InputStream imageInputStream = imageResource.getInputStream()) {
                byte[] imageBytes = imageInputStream.readAllBytes();
                ImageData imageData = ImageDataFactory.create(imageBytes);
                Image image = new Image(imageData);
                // Resize the image
                float width = 150;
                float height = 50;
                image.scaleToFit(width, height);
                // Align the image to the left
                image.setHorizontalAlignment(HorizontalAlignment.LEFT);
                document.add(image);
            }

            document.add(new Paragraph("\n"));

            // Add a line break
            Paragraph title = new Paragraph("Calculate your CO2 emissions")
                    .setFontSize(25)
                    .setBold();
            document.add(title);

            // Add the main paragraph with clickable link
            Link link = new Link("U.S. Energy Information Administration (EIA)",
                    PdfAction.createURI("https://www.eia.gov/environment/emissions/co2_vol_mass.php"));
            Paragraph mainParagraph = new Paragraph("This calculator can provide you with a simple estimate of the greenhouse gases emitted and absorbed by your farm. It uses the same methods and emission factors as the ")
                    .add(link);
            document.add(mainParagraph);

            // Add last updated date
            Paragraph lastUpdated = new Paragraph("Last updated: 07 September 2023")
                    .setFontSize(10)
                    .setItalic();
            document.add(lastUpdated);

            // Add another line break
            document.add(new Paragraph("\n"));

            // Add the horizontal line
            document.add(new LineSeparator(new SolidLine()));

            // Add another line break
            document.add(new Paragraph("\n"));

            Paragraph usingCalculator = new Paragraph("About our calculator")
                    .setFontSize(18)
                    .setBold();
            document.add(usingCalculator);

            Paragraph mainParagraphh = new Paragraph("Our calculator provides a straightforward method to estimate your company's greenhouse gas emissions. It categorizes emissions into three scopes: Scope 1 covers direct emissions from your company's owned or controlled sources, such as on-site fuel combustion. Scope 2 includes indirect emissions from purchased electricity, heating, and cooling. Scope 3 encompasses indirect emissions from your value chain, including business travel, waste management, and employee commuting.")
                    .add(". We are committed to helping you understand and mitigate your carbon footprint, contributing to a more sustainable future.");
            document.add(mainParagraphh);

            Paragraph note = new Paragraph("Note: Record will be kept of the data you enter. You can print or download your results from the results area of the calculator.")
                    .setItalic();
            document.add(note);

            // Add another line break
            document.add(new Paragraph("\n"));

            // Add the total emissions in bold
            document.add(new Paragraph("Total")
                    .setFontSize(20)
                    .setBold());
            document.add(new Paragraph(String.format("Total Emissions (kg CO2e): %.2f", footprint.getTotalEmissions()))
                    .setFontSize(15));

            // Add the horizontal line
            document.add(new LineSeparator(new SolidLine()));

            // Add Scope 1 details
            document.add(new Paragraph("Scope 1 Details")
                    .setFontSize(20)
                    .setBold());

            for (Footprint.Scope1.Mobility mobility : footprint.getScope1().getMobility()) {
                document.add(new Paragraph("Vehicle Type " + mobility.getVehicleType())
                        .setFontSize(15));
                document.add(new Paragraph("Fuel Type: " + mobility.getFuelType())
                        .setFontSize(15));
                document.add(new Paragraph("MPG: " + mobility.getMpg())
                        .setFontSize(15));
                document.add(new Paragraph("Emissions (kg CO2e): " + mobility.getMValue())
                        .setFontSize(15));
            }

            for (Footprint.Scope1.OilGas oilGas : footprint.getScope1().getOilGas()) {
                document.add(new Paragraph("Barrel Number: " + oilGas.getBarrelNumber())
                        .setFontSize(15));
                document.add(new Paragraph("Emissions (kg CO2e): " + oilGas.getOValue())
                        .setFontSize(15));
            }

            for (Footprint.Scope1.Agriculture.Livestock livestock : footprint.getScope1().getAgriculture().getLivestock()) {
                document.add(new Paragraph("Animal Type: " + livestock.getAnimalType())
                        .setFontSize(15));
                document.add(new Paragraph("Animal Number: " + livestock.getAnimalNumber())
                        .setFontSize(15));
                document.add(new Paragraph("Emissions (kg CO2e): " + livestock.getLValue())
                        .setFontSize(15));
            }

            for (Footprint.Scope1.Agriculture.Fertiliser fertiliser : footprint.getScope1().getAgriculture().getFertiliser()) {
                document.add(new Paragraph("Fertiliser Type: " + fertiliser.getFertiliserType())
                        .setFontSize(15));
                document.add(new Paragraph("Amount: " + fertiliser.getAmount())
                        .setFontSize(15));
                document.add(new Paragraph("Emissions (kg CO2e): " + fertiliser.getFValue())
                        .setFontSize(15));
            }

            // Add a line break
            document.add(new Paragraph("\n"));
            // Add Scope 1 total
            document.add(new Paragraph("Scope 1 Total Emissions (kg CO2e): " + footprint.getScope1().getScope1Emissions())
                    .setFontSize(15)
                    .setBold());

            // Add the horizontal line
            document.add(new LineSeparator(new SolidLine()));

            // Add Scope 2 details
            document.add(new Paragraph("Scope 2 Details")
                    .setFontSize(20)
                    .setBold());

            document.add(new Paragraph("Electricity Usage (kWh): " + footprint.getScope2().getElectricity().getUsage())
                    .setFontSize(15));
            document.add(new Paragraph("Electricity Emissions (kg CO2e): " + footprint.getScope2().getElectricity().getEValue())
                    .setFontSize(15));

            document.add(new Paragraph("Heating Fuel Type: " + footprint.getScope2().getHeating().getFuelType())
                    .setFontSize(15));
            document.add(new Paragraph("Heating CCF: " + footprint.getScope2().getHeating().getCcf())
                    .setFontSize(15));
            document.add(new Paragraph("Heating Emissions (kg CO2e): " + footprint.getScope2().getHeating().getHValue())
                    .setFontSize(15));

            document.add(new Paragraph("Scope 2 Total Emissions (kg CO2e): " + footprint.getScope2().getScope2Emissions())
                    .setFontSize(15)
                    .setBold());

            // Add the horizontal line
            document.add(new LineSeparator(new SolidLine()));
            // Add a line break
            document.add(new Paragraph("\n"));

            // Add Scope 3 details
            document.add(new Paragraph("Scope 3 Details:")
                    .setFontSize(20)
                    .setBold());

            for (Footprint.Scope3.Shipping shipping : footprint.getScope3().getShipping()) {
                document.add(new Paragraph("Shipment Type: " + shipping.getShipmentType())
                        .setFontSize(15));
                document.add(new Paragraph("Shipment Weight: " + shipping.getShipmentWeight())
                        .setFontSize(15));
                document.add(new Paragraph("Shipment Distance: " + shipping.getShipmentDistance())
                        .setFontSize(15));
                document.add(new Paragraph("Emissions (kg CO2e): " + shipping.getSValue())
                        .setFontSize(15));
            }

            for (Footprint.Scope3.BusinessTrip businessTrip : footprint.getScope3().getBusinessTrip()) {
                document.add(new Paragraph("Business Trip Type: " + businessTrip.getBusinessType())
                        .setFontSize(15));
                document.add(new Paragraph("Distance: " + businessTrip.getDistance())
                        .setFontSize(15));
                document.add(new Paragraph("Emissions (kg CO2e): " + businessTrip.getBValue())
                        .setFontSize(15));
            }

            document.add(new Paragraph("Scope 3 Total Emissions (kg CO2e): " + footprint.getScope3().getScope3Emissions())
                    .setFontSize(15)
                    .setBold());

            // Add the horizontal line
            document.add(new LineSeparator(new SolidLine()));

            document.add(new Paragraph("\n"));

        } catch (IOException e) {
            logger.error("Error while generating PDF", e);
            throw e;
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }



    public File getFileByFootprintId(String footprintId) {
        return fileRepository.findByFootprintId(footprintId);
    }
}
