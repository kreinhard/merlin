package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelCell {
    private Cell cell;
    private ExcelCellType type;

    ExcelCell(Cell cell, ExcelCellType type) {
        this.cell = cell;
        this.type = type;
        //BuiltinFormats.
    }

    public void setCellValue(String str) {
        this.cell.setCellValue(str);
    }
}