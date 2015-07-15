/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaiona.csv.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Javad Chamanara <chamanara@gmail.com>
 */
public class RowBuilder {
    // add a collection of column indexes or names to make the function process a projection of needed columns only
    public static String[] createRowArray(Row row, FormulaEvaluator evaluator){
        String[] cellValues = new String[row.getLastCellNum()+1];
        for(int cellIndex =0;  cellIndex <= row.getLastCellNum(); cellIndex++){
            CellValue cellValue = evaluator.evaluate(row.getCell(cellIndex));
            if(cellValue != null){
                switch (cellValue.getCellType())
                {
                    // what about the DATE type
                    case Cell.CELL_TYPE_NUMERIC:
                        //System.out.print(cellValue.getNumberValue() + "\t");
                        cellValues[cellIndex] = String.valueOf(cellValue.getNumberValue());
                        break;
                    case Cell.CELL_TYPE_STRING:
                        //System.out.print(cellValue.getStringValue()  + "\t");
                        cellValues[cellIndex] = cellValue.getStringValue();
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        //System.out.println(cellValue.getBooleanValue()  + "\t");
                        cellValues[cellIndex] = String.valueOf(cellValue.getBooleanValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA: // should not happen. It is evaluated by the evaluator
                    case Cell.CELL_TYPE_BLANK:
                    case Cell.CELL_TYPE_ERROR:
                        cellValues[cellIndex] = "";
                        break;
                } 
            }
        }                    
     return cellValues;   
    }    
}
