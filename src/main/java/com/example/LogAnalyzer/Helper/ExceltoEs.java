package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class ExceltoEs {

//    @Value("${logdata}")
//    public  String file;

    public static String file="/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx";

public boolean validate(String s){
    String extension = FilenameUtils.getExtension(s);
    return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
}

public LogEntity isvalid(Row row){
    //logic to be discussed


//
//    Required field validation - Check that required fields have some value. This ensures no rows have missing data.


    Iterator<Cell> cellsInRow = row.iterator();

    LogEntity logdata=new LogEntity();

    int cellIdx = 0;
//                int intf=0;
//                logdata.setID(rowNumber);

    while (cellsInRow.hasNext()) {

//                    System.out.println("ff");
        Cell currentCell = cellsInRow.next();

        switch (cellIdx) {
            case 0:
//                            System.out.println(currentCell.getStringCellValue());Da

                if(DateUtil.isCellDateFormatted(currentCell)){
                    Date timestamp = currentCell.getDateCellValue();
                    System.out.println(timestamp);
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date formattedTimestamp = dateFormat.format(timestamp);
                    logdata.setTimestamp(timestamp);
                }
                else{
                    throw new RuntimeException("invalid date time format");
                }

                break;

            case 1:
//
//                            System.out.println(currentCell.getStringCellValue());
                String source=currentCell.getStringCellValue();
                if(source==null || source.equals("")){
                    throw new RuntimeException("source cannot be null");
                }
                logdata.setSource(currentCell.getStringCellValue());
                break;

            case 2:
                String message=currentCell.getStringCellValue();
                if(message==null || message.equals("")){
                    throw new RuntimeException("source cannot be null");
                }
                logdata.setMessage(message);
                break;
            default:
                break;
        }

        cellIdx++;
    }






    return logdata;
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



//                System.out.println("f");

                logs.add(isvalid(currentRow));
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
