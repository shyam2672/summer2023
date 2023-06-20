package com.example.LogAnalyzer.Configuration.Helper;

import com.example.LogAnalyzer.Configuration.Entity.LogEntity;
import com.example.LogAnalyzer.Configuration.Repository.LogRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private  LogRepository logRepository;



    public  List<LogEntity> ReadFromExcel(){
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
                            System.out.println(currentCell.getStringCellValue());
                            logdata.setSource(currentCell.getStringCellValue());
                            break;

                        case 2:
//                            System.out.println(currentCell.getStringCellValue());
//                            logdata.setMessage(currentCell.getStringCellValue());
                            String message=currentCell.getStringCellValue();
                            System.out.println(message);
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
//            System.out.println(logs);


//            WriteToEs(logs);
            return logs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public  void WriteToEs(List<LogEntity> logs){
            for(LogEntity loge: logs){
                System.out.println(loge.getID());

                System.out.println(loge.getTimestamp());
                System.out.println(loge.getSource());

                System.out.println(loge.getMessage());
            }


           logRepository.saveAll(logs);
//for( LogEntity logg:logs){
//            LogEntity fogg=new LogEntity();
//        fogg.setMessage("hi");
//    logRepository.save(fogg);
////    logRepository.save(logg);
//
//}

//
//List<LogEntity> l=new ArrayList<LogEntity>();
//
//LogEntity l1=new LogEntity();
//        LogEntity l2=new LogEntity();
//
//        LogEntity l3=new LogEntity();
//        l1.setID(1);
//
//l1.setSource("1");
//        l2.setID(2);
//
//        l2.setSource("2");
//        l3.setID(3);
//        l3.setSource("3");
////        System.out.println("ffff");
//        logRepository.save(l1);
//        logRepository.save(l2);
//        logRepository.save(l3);

//         List<LogEntity> fog= (List<LogEntity>) logRepository.findAll();
//        LogEntity logg=new LogEntity();
//        logg.setMessage("hi");
//
//        logRepository.save(logg);
//        System.out.println(fog);
    }
}
