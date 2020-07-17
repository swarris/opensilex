/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.core.ontology.dal;

/**
 *
 * @author vmigot
 */
public class CSVCell {

    private int rowIndex;

    private int colIndex;

    private String header;

    private String value;

    public CSVCell(CSVCell cell) {
        this.rowIndex = cell.getRowIndex();
        this.colIndex = cell.getColIndex();
        this.header = cell.getHeader();
        this.value = cell.getValue();
    }
    
    public CSVCell(int rowIndex, int colIndex, String header, String value) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.header = header;
        this.value = value;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public String getHeader() {
        return header;
    }

    public String getValue() {
        return value;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
