package com.Hend.FootprintService.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "File")
public class File {
    @Id
    private String fileId;
    private String footprintId;
    private String filename;
    private byte[] content;
    private String contentType;
    private long size;
    private Date uploadDate;

    public File(String pdfFilePath) {
    }
}
