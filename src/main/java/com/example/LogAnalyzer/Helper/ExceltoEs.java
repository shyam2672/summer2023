package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class ExceltoEs {

//    @Value("${logdata}")
//    public static String file;

    public static String file="/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx";
    private static final String[] EXCEL_EXTENSIONS = { ".xlsx", ".xls" };
    @Autowired
    private  LogRepository logRepository;

public boolean validate(String s){
    String extension = FilenameUtils.getExtension(s);
    return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
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

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                LogEntity logdata=new LogEntity();


                int cellIdx = 0;
//                int intf=0;
                logdata.setID(rowNumber);

                while (cellsInRow.hasNext()) {

//                    System.out.println("ff");
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
//                            System.out.println(currentCell.getStringCellValue());
                            Date timestamp = currentCell.getDateCellValue();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedTimestamp = dateFormat.format(timestamp);
                            logdata.setTimestamp(formattedTimestamp);
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

    public List<LogEntity>  WriteToEs(List<LogEntity> logs){
//            for(LogEntity loge: logs){
//                System.out.println(loge.getID());
//
//                System.out.println(loge.getTimestamp());
//                System.out.println(loge.getSource());
//
//                System.out.println(loge.getMessage());
//            }

try{
   return (List<LogEntity>) logRepository.saveAll(logs);
}
catch (Exception e){
    throw new RuntimeException(e);
}


    }
}
