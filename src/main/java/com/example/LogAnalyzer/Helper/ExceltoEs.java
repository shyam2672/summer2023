package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


//helper class to read data from excel file and write it on es
@Component
public class ExceltoEs {

    // Can be injected from properties
//    @Value("${logdata}")
//    public  String file;

    public static String file = "/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx";

    //file type validation
    public boolean
    validate(String s) {
        String extension = FilenameUtils.getExtension(s);
        return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
    }


    //returns an entity if row is valid
    public LogEntity isvalid(Row row) {

        Iterator<Cell> cellsInRow = row.iterator();

        LogEntity logdata = new LogEntity();

        int cellIdx = 0;


        while (cellsInRow.hasNext()) {

            Cell currentCell = cellsInRow.next();

            switch (cellIdx) {
                case 0:
                    // cell should contain some date time value
                    if (DateUtil.isCellDateFormatted(currentCell)) {
                        Date timestamp = currentCell.getDateCellValue();
                        logdata.setTimestamp(timestamp);

                        LocalDate date = timestamp.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        logdata.setDate(date);
                    } else {
                        throw new RuntimeException("invalid date time format");
                    }

                    break;

                case 1:
                    String source = currentCell.getStringCellValue();
                    //source name cannot be null
                    if (source == null || source.equals("")) {
                        throw new RuntimeException("source cannot be null");
                    }
                    logdata.setSource(currentCell.getStringCellValue());
                    break;

                case 2:
                    String message = currentCell.getStringCellValue();
                    //message  cannot be null
                    if (message == null || message.equals("")) {
                        throw new RuntimeException("message cannot be null");
                    }
                    logdata.setMessage(message);
                    break;
                default:
                    break;
            }

            cellIdx++;
        }


        if (cellIdx < 3) {
            throw new RuntimeException("insufficient data expected 3 cells ");
        }

        return logdata;
    }

    //utility function that reads data from excel file an returns a list of LogEntity
    public List<LogEntity> ReadFromExcel() {

        if (!validate(file)) {
            throw new RuntimeException("invalid file type");
        }

        try {
            Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
            //it is assumed that all the data wil be on first sheet
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<LogEntity> logs = new ArrayList<LogEntity>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }


                logs.add(isvalid(currentRow));
                rowNumber++;
            }

            workbook.close();


            return logs;
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    //takes a list of LogEntities and stores them in es
    public List<LogEntity> WriteToEs(LogRepository logRepository, List<LogEntity> logs) {
        List<LogEntity> Logs = new ArrayList<>();
        for (LogEntity loge : logs) {
//                String id=UUID.randomUUID().toString();
//                loge.setID(id);
//            System.out.println(loge.getTimestamp());

            Logs.add(logRepository.save(loge));
        }
        try {
            return Logs;
//   return (List<LogEntity>) logRepository.saveAll(logs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}


