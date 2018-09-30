package de.reinhard.merlin.word.templating;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileDescriptorTest {
    private Logger log = LoggerFactory.getLogger(FileDescriptorTest.class);

    private static final String TEST_DIR = "./target/";

    @Test
    public void matchesTest() {
        FileDescriptor descriptor1 = new FileDescriptor();
        FileDescriptor descriptor2 = new FileDescriptor();
        assertFalse(descriptor1.matches(descriptor2));
        descriptor1.setDirectory("/Users/kai");
        descriptor2.setDirectory("/Users/kai");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor1.setDirectory("Documents/templates");
        descriptor2.setDirectory("Documents/templates");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor1.setFilename("ContractTemplate.docx");
        descriptor2.setFilename("ContractTemplate.xlsx");
        assertTrue(descriptor1.matches(descriptor2));

        descriptor2.setDirectory("/Users/horst");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor2.setDirectory("/Users/kai");
        descriptor2.setDirectory("Documents/templates/test");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor2.setDirectory("Documents/templates");
        descriptor2.setFilename("ContractTemplate2.xlsx");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor2.setFilename("ContractTemplate.xls");
        assertTrue(descriptor1.matches(descriptor2));
    }

    @Test
    public void lastModifiedTest() throws IOException, InterruptedException {
        File file = new File(TEST_DIR, "tmp.txt");
        FileUtils.write(file, "Test", Charset.defaultCharset());
        FileDescriptor descriptor = new FileDescriptor();
        assertTrue(descriptor.isModified(file));
        log.info("Sleeping 1s...");
        Thread.sleep(1000);
        descriptor.setLastUpdate(new Date());
        assertFalse(descriptor.isModified(file));
        log.info("Sleeping 1s...");
        Thread.sleep(1000);
        FileUtils.write(file, "Test", Charset.defaultCharset());
        assertTrue(descriptor.isModified(file));
    }

    @Test
    public void relativizePathTest() {
        File dir = new File("/Users/kai/Documents");
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setDirectory(dir);
        File file = new File("/Users/kai/Documents/templates/template.xls");
        fileDescriptor.setRelativePath(file);
        assertEquals("templates", fileDescriptor.getRelativePath());
        file = new File("/Users/kai/template.xls");
        fileDescriptor.setRelativePath(file);
        assertEquals("..", fileDescriptor.getRelativePath());
        file = new File("/Users/kai/templates/template.xls");
        fileDescriptor.setRelativePath(file);
        assertEquals("../templates", fileDescriptor.getRelativePath());
         file = new File("/Users/kai/Documents/template.xls");
        fileDescriptor.setRelativePath(file);
        assertEquals(".", fileDescriptor.getRelativePath());
    }
}