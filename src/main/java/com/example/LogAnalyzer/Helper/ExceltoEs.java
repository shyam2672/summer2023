package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Entity.LoggerEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import com.example.LogAnalyzer.Repository.LoggerRepository;
import com.example.LogAnalyzer.Service.LogServiceImp;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


//helper class to read data from excel file and write it on es
@Component
public class ExceltoEs {

    private static final Logger logger = Logger.getLogger(LogServiceImp.class.getName());


    @Autowired
    private LoggerRepository loggerRepository;


    List<LoggerEntity> validloggers;

    public static String file = "/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/new data.xls";

    //file type validation
    public boolean
    validate(String s) {
        String extension = FilenameUtils.getExtension(s);
        return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
    }

    public void fetchValidLoggers() {
        validloggers = loggerRepository.findAll();
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
                    String timestamp = currentCell.getStringCellValue();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    formatter.setLenient(false);
                    Date tsp;
                    try {
                        tsp = formatter.parse(timestamp);
                        logdata.setTimestamp(tsp);
                    } catch (ParseException e) {
                        logger.log(Level.SEVERE, "An error occurred", e);

                        throw new RuntimeException(e);
                    }

                    LocalDate date = tsp.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    logdata.setDate(date);
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
                case 3:
                    String loglevel = currentCell.getStringCellValue();
                    if (!loglevel.equals("ERROR")) {
                        return null;
                    }
                    //loglevel  cannot be null
                    if (loglevel == null || loglevel.equals("")) {
                        throw new RuntimeException("loglevel cannot be null");
                    }
                    logdata.setLoglevel(loglevel);
                    break;
                case 4:
                    String logger = currentCell.getStringCellValue();
                    if (!filterLogger(logger))
                        return null;

                    //logger  cannot be null
                    if (logger == null || logger.equals("")) {
                        throw new RuntimeException("logger cannot be null");
                    }

                    logdata.setLogger(logger);
                    break;
                case 5:
                    String partnerid = String.valueOf(currentCell.getNumericCellValue());
                    //partnerid  cannot be null
                    if (partnerid == null || partnerid.equals("")) {
                        throw new RuntimeException("partnerid cannot be null");
                    }
                    logdata.setPartnerid(partnerid);
                    break;
                default:
                    break;
            }

            cellIdx++;
        }

        //some fileds are not present
        if (cellIdx < 5) {
            throw new RuntimeException("insufficient data, expected 6 cells ");
        }

        return logdata;
    }

    private boolean filterLogger(String logger) {
        //


        for (LoggerEntity logger1 : validloggers) {

            if (logger1.getlogger().equals(logger)) {
                return true;
            }
        }

        if (logger.equals("testlogger")) return true;
        return false;
    }

    //utility function that reads data from excel file an returns a list of LogEntity
    public List<LogEntity> ReadFromExcel() {


        if (!validate(file)) {
            logger.info("invalid file type");

            throw new RuntimeException("invalid file type");
        }

        fetchValidLoggers();

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

                LogEntity logg = isvalid(currentRow);

                if (logg != null)
                    logs.add(logg);
                rowNumber++;
            }

            workbook.close();


            return logs;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred", e);

            throw new RuntimeException(e);
        }

    }

    //takes a list of LogEntities and stores them in es
    public List<LogEntity> WriteToEs(LogRepository logRepository, List<LogEntity> logs) {
        List<LogEntity> Logs = new ArrayList<>();
        for (LogEntity loge : logs) {

            Logs.add(logRepository.save(loge));
        }
        try {
            return Logs;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred", e);

            throw new RuntimeException(e);
        }

    }

}


