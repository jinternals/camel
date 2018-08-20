package com.jinternals.camel.file.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class FileCopyRouteTest {
    private static final long DURATION_MILIS = 5000;
    private static final String SOURCE_FOLDER = "src/test/resources/data/inbox";
    private static final String DESTINATION_FOLDER = "src/test/resources/data/outbox";
    private File sourceFolder;
    private File sourceFile;
    private File destinationFolder;
    private File destinationFolderFile;
    private String CONTENT ="content";
    private CamelContext camelContext;
    @Before
    public void setUp() throws Exception {
        sourceFolder = new File(SOURCE_FOLDER);
        sourceFolder.mkdirs();

        sourceFile = new File(SOURCE_FOLDER + "/1.txt");
        sourceFile.createNewFile();
        writeToFile(sourceFile, CONTENT);

        destinationFolder = new File(DESTINATION_FOLDER);
        destinationFolder.mkdirs();

        destinationFolderFile = new File(destinationFolder+"/1.txt");

    }


    @After
    public void tearDown() throws Exception {
        cleanFolder(sourceFolder);
        cleanFolder(destinationFolderFile);
    }

    private void cleanFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {

                    System.out.println("folder = [" + folder + "]");
                    file.delete();

                }
            }
        }
    }
    @Test
    public void shouldExecuteFileCopyRoute() throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new FileCopyRoute(SOURCE_FOLDER,DESTINATION_FOLDER));


        camelContext.start();

        sleep(DURATION_MILIS);

        assertThat(sourceFile.exists()).isFalse();

        assertThat(destinationFolderFile.exists()).isTrue();

        assertThat(readFile(destinationFolderFile)).isEqualTo(CONTENT.toUpperCase());

        camelContext.stop();
    }

    public void writeToFile(File file,String content)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }

    static String readFile(File file) throws IOException
    {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded);
    }

}