package de.reinhard.merlin.excel;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.I18n;
import de.reinhard.merlin.data.PropertiesStorage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcelWorkbookTest {
    private Logger log = LoggerFactory.getLogger(ExcelWorkbookTest.class);

    @Test
    public void configReaderValidationTest() {
        I18n i18n = I18n.setDefault(Locale.ROOT);
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"));
        ExcelConfigReader configReader = new ExcelConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.getSheet().hasValidationErrors());
        Set<ExcelValidationErrorMessage> validationErrors = configReader.getSheet().getAllValidationErrors();
        assertEquals(1, validationErrors.size());
        assertEquals("In sheet 'Config', column A:'Property' and row #5: Cell value isn't unique. It's already used in row #3: 'user'.",
                validationErrors.iterator().next().getMessageWithAllDetails(i18n));
        assertEquals("horst", props.getConfigString("user"));
        assertEquals("Hamburg", props.getConfigString("city"));
    }

    @Test
    public void validationExcelResponseTest() throws IOException {
        I18n i18n = I18n.setDefault(Locale.ROOT);
        validationexcelResponseTest(I18n.getDefault(), "");
        validationexcelResponseTest(I18n.setDefault(Locale.GERMAN), "_de");
    }

    private void validationexcelResponseTest(I18n i18n, String fileSuffix) throws IOException {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"));
        ExcelConfigReader configReader = new ExcelConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.getSheet().hasValidationErrors());
        ExcelWriterContext ctx = new ExcelWriterContext(i18n, excelWorkbook).setAddErrorColumn(true);
        configReader.getSheet().markErrors(ctx);

        ExcelSheet sheet = excelWorkbook.getSheet("Validator-Test");
        sheet.registerColumn("Name", new ExcelColumnValidator().setRequired());
        sheet.registerColumn("Surname", new ExcelColumnValidator().setRequired());
        sheet.registerColumn("Birthday", new ExcelColumnDateValidator());
        sheet.registerColumn("City", new ExcelColumnValidator());
        sheet.registerColumn("E-Mail", new ExcelColumnPatternValidator().setEMailPattern().setRequired().setUnique());
        sheet.registerColumn("Number", new ExcelColumnValidator().setUnique());
        sheet.registerColumn("Country", new ExcelColumnValidator());
        sheet.markErrors(ctx);

        sheet = excelWorkbook.getSheet("Validator-Mass-Test");
        sheet.registerColumn("Name", new ExcelColumnValidator().setUnique());
        sheet.registerColumn("Birthday", new ExcelColumnDateValidator());
        sheet.registerColumn("City", new ExcelColumnValidator());
        sheet.registerColumn("E-Mail", new ExcelColumnPatternValidator().setEMailPattern().setRequired());
        sheet.registerColumn("Country", new ExcelColumnValidator());
        sheet.markErrors(ctx);

        sheet = excelWorkbook.getSheet("Clean-Test");
        sheet.registerColumn("Name", new ExcelColumnValidator().setUnique());
        sheet.registerColumn("Birthday", new ExcelColumnDateValidator());
        sheet.registerColumn("City", new ExcelColumnValidator());
        sheet.registerColumn("E-Mail", new ExcelColumnPatternValidator().setEMailPattern().setRequired());
        sheet.registerColumn("Country", new ExcelColumnValidator());
        sheet.markErrors(ctx);

        sheet = excelWorkbook.getSheet("Empty-sheet");
        sheet.registerColumn("Name", new ExcelColumnValidator().setUnique());
        sheet.registerColumn("E-Mail", new ExcelColumnPatternValidator().setEMailPattern().setRequired());
        sheet.markErrors(ctx);

        sheet = excelWorkbook.getSheet("Empty-sheet2");
        sheet.registerColumn("Name", new ExcelColumnValidator().setUnique());
        sheet.registerColumn("E-Mail", new ExcelColumnPatternValidator().setEMailPattern().setRequired());
        sheet.markErrors(ctx);

        File file = new File(Definitions.OUTPUT_DIR, "Test-result" + fileSuffix + ".xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        excelWorkbook.getPOIWorkbook().write(new FileOutputStream(file));
    }
}
