package com.example.LogAnalyzer;

import com.example.LogAnalyzer.Configuration.ConfigTest;
import com.example.LogAnalyzer.Controller.LogControllerTest;
import com.example.LogAnalyzer.Entity.LogEntityTest;
import com.example.LogAnalyzer.Helper.ExceltoEsTest;
import com.example.LogAnalyzer.Helper.QueryPrinterTest;
import com.example.LogAnalyzer.Service.LogServiceImp;
import com.example.LogAnalyzer.Service.LogServiceImpTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ConfigTest.class, LogControllerTest.class, LogEntityTest.class, ExceltoEsTest.class, QueryPrinterTest.class, LogServiceImpTest.class})
public class AllTests {

}