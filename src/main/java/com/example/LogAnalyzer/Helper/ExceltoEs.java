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


@Component
public class ExceltoEs {

    // Can be injected from properties
//    @Value("${logdata}")
//    public  String file;

    public static String file = "/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx";

    public boolean
    validate(String s) {
        String extension = FilenameUtils.getExtension(s);
        return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
    }

    public LogEntity
    isvalid(Row row) {

        Iterator<Cell> cellsInRow = row.iterator();

        LogEntity logdata = new LogEntity();

        int cellIdx = 0;


        while (cellsInRow.hasNext()) {

//                    System.out.println("ff");
            Cell currentCell = cellsInRow.next();

            switch (cellIdx) {
                case 0:

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
                    if (source == null || source.equals("")) {
                        throw new RuntimeException("source cannot be null");
                    }
                    logdata.setSource(currentCell.getStringCellValue());
                    break;

                case 2:
                    String message = currentCell.getStringCellValue();
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

    public List<LogEntity>
    ReadFromExcel() {

        if (!validate(file)) {
            throw new RuntimeException("invalid file type");
        }

        try {
            Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
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

<<<<<<< HEAD
    public List<LogEntity>  WriteToEs(LogRepository logRepository, List<LogEntity> logs){
            for(LogEntity loge: logs){
//                String id=UUID.randomUUID().toString();
//                loge.setID(id);
                System.out.println(logRepository.save(loge).getDate());
            }
try{
    return logs;
=======
    public List<LogEntity> WriteToEs(LogRepository
                                             logRepository, List<LogEntity> logs) {
        List<LogEntity> Logs = new ArrayList<>();
        for (LogEntity loge : logs) {
//                String id=UUID.randomUUID().toString();
//                loge.setID(id);
            System.out.println(loge.getTimestamp());
            // Print query

            Logs.add(logRepository.save(loge));
        }
        try {
            return Logs;
>>>>>>> 58dc878 (dynamic termsfilter)
//   return (List<LogEntity>) logRepository.saveAll(logs);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }


    }

}


