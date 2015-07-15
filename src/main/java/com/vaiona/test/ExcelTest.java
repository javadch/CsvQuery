/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaiona.test;

import com.vaiona.csv.reader.RowBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Javad Chamanara <chamanara@gmail.com>
 */
public class ExcelTest {

    
    public void read(){
        try
        {
            FileInputStream file = new FileInputStream(new File("C:\\Users\\standard\\Downloads\\javaTest.xlsx"));
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheet("Sheet1"); //workbook.getSheet("test");
//            Stream stream = StreamSupport.stream(sheet.spliterator(), false); // maybe it is better to have a limited size stream by passing the sheet.getPhysicalNumberOfRows()
            
            Stream<Row> stream = StreamSupport.stream(
                    Spliterators.spliterator(sheet.iterator(), sheet.getPhysicalNumberOfRows(),Spliterator.ORDERED),false);
                    
            //long cnt = stream.count();
            stream = stream.skip(1);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            //Iterable<Row> iterable = () -> sheet.iterator();
            List<Entity> result = 
                    stream
                            .filter(row -> (!row.getZeroHeight()))
                            .map(row -> RowBuilder.createRowArray(row, evaluator))
                            .map(rowArray -> new Entity(rowArray))
                            .peek(p-> {System.out.println("");})
                            .collect(Collectors.toList());
            long count = result.stream().count();
            System.out.println("Total records: " + count);
            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }
    
    public class Entity{
        String[] values;
        public Entity(String[] values){
            this.values = values;
        }
    }
    
}
