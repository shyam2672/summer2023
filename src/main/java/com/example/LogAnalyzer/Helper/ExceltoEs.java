package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class ExceltoEs {

//    @Value("${logdata}")
//    public static String file;

    public static String file="/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx";
    private static final String[] EXCEL_EXTENSIONS = { ".xlsx", ".xls" };

public boolean validate(String s){
    String extension = FilenameUtils.getExtension(s);
    return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
}

public boolean isvalid(Row row){
    //logic to be discussed
//
//    Required field validation - Check that required fields have some value. This ensures no rows have missing data.
//
//            Data type validation - Validate that fields have the expected data type, like string, integer, float, boolean, etc. This catches type mismatches.
//
//    Range validation - Ensure numeric values fall within an acceptable range. This catches out-of-range values.
//
//            List validation - Validate fields against a finite list of acceptable values. For example, a "status" field may only allow "open", "in progress" and "closed".
//
//            Regular expression validation - Use a regex to validate string fields match a certain pattern. For example, validate an email field matches an email regex.
//
//            Unique field validation - Check that values in unique identifier fields are actually unique. This ensures no duplicate records.
//
//            Format validation - Validate that date/time fields match a specified format. You can parse the values and catch invalid formats.
//
//    Length validation - Ensure string fields are within a minimum and maximum length. This catches strings that are too long or short.
//
//            Checksum validation - Calculate a checksum for the entire row and ensure it matches a provided checksum value. This detects corrupted data.
//
//    Cross-field validation - Compare values across multiple fields to detect inconsistencies. For example, an "end date" can't be before a "start date".

    return true;
}

    public  List<LogEntity> ReadFromExcel(){

       if(!validate(file)){
           throw new RuntimeException("invalid file type");
       }

        try {
            Workbook workbook= WorkbookFactory.create(new FileInputStream(file));
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<LogEntity> logs = new ArrayList<LogEntity>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if(!isvalid(currentRow)){
                    throw new RuntimeException("Invalid data");
                }

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                LogEntity logdata=new LogEntity();


                int cellIdx = 0;
//                int intf=0;
//                logdata.setID(rowNumber);

                while (cellsInRow.hasNext()) {

//                    System.out.println("ff");
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
//                            System.out.println(currentCell.getStringCellValue());
                            Date timestamp = currentCell.getDateCellValue();
                            System.out.println(timestamp);
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date formattedTimestamp = dateFormat.format(timestamp);
                            logdata.setTimestamp(timestamp);
                            break;

                        case 1:
//
//                            System.out.println(currentCell.getStringCellValue());
                            logdata.setSource(currentCell.getStringCellValue());
                            break;

                        case 2:
//                            System.out.println(currentCell.getStringCellValue());
//                            logdata.setMessage(currentCell.getStringCellValue());
                            String message=currentCell.getStringCellValue();
//                            System.out.println(message);
//                            String escapedMessage = message.replace("\"", "\\\"\\\"\\\"");
//                            System.out.println(escapedMessage);
                            logdata.setMessage(message);
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }
//                System.out.println("f");

                logs.add(logdata);
                rowNumber++;
            }

            workbook.close();


            return logs;
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    public List<LogEntity>  WriteToEs(LogRepository logRepository, List<LogEntity> logs){
            for(LogEntity loge: logs){
                String id=UUID.randomUUID().toString();
                loge.setID(id);
            }
try{
   return (List<LogEntity>) logRepository.saveAll(logs);
}
catch (Exception e){
    throw new RuntimeException(e);
}


    }
}
