/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.silverpeas.util;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ehugonnet
 */
public class ZipManagerTest {

  private static String base = System.getProperty("basedir");

  public ZipManagerTest() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    File tempDir = new File(base + File.separatorChar + "target" + File.separatorChar + "temp");
    tempDir.mkdirs();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    File tempDir = new File(base + File.separatorChar + "target" + File.separatorChar + "temp");
    FileUtils.forceDelete(tempDir);
  }

  /**
   * Test of compressPathToZip method, of class ZipManager.
   */
  @Test
  public void testCompressPathToZip() throws Exception {
    assertNotNull(base);
    String path = base + File.separatorChar + "target" + File.separatorChar
            + "test-classes" + File.separatorChar + "ZipSample";
    String outfilename = base + File.separatorChar + "target" + File.separatorChar
            + "temp" + File.separatorChar + "testCompressPathToZip.zip";
    long expResult = 806L;
    long result = ZipManager.compressPathToZip(path, outfilename);
    assertEquals(expResult, result);
    ZipFile zipFile = new ZipFile(new File(outfilename));
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    int nbEntries = 0;
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      nbEntries++;
    }
    assertEquals(5, nbEntries);
    assertNotNull(zipFile.getEntry("ZipSample" + File.separatorChar + "simple.txt"));
    assertNotNull(zipFile.getEntry("ZipSample" + File.separatorChar + "level1" + File.separatorChar + "simple.txt"));
    assertNotNull(zipFile.getEntry("ZipSample" + File.separatorChar + "level1" + File.separatorChar + "level2b" + File.separatorChar + "simple.txt"));
    assertNotNull(zipFile.getEntry("ZipSample" + File.separatorChar + "level1" + File.separatorChar + "level2a" + File.separatorChar + "simple.txt"));
    assertNotNull(zipFile.getEntry("ZipSample" + File.separatorChar + "level1" + File.separatorChar + "level2a" + File.separatorChar + "sïmplifié.txt"));
    assertNull(zipFile.getEntry("ZipSample" + File.separatorChar + "level1" + File.separatorChar + "level2c" + File.separatorChar));
    zipFile.close();
  }

  /**
   * Test of compressStreamToZip method, of class ZipManager.
   */
  @Test
  public void testCompressStreamToZip() throws Exception {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("FrenchScrum.odp");
    String filePathNameToCreate = File.separatorChar + "dir1" + File.separatorChar + "dir2" + File.separatorChar + "FrenchScrum.odp";
    String outfilename = base + File.separatorChar + "target" + File.separatorChar + "temp" + File.separatorChar + "testCompressStreamToZip.zip";
    ZipManager.compressStreamToZip(inputStream, filePathNameToCreate, outfilename);
    File file = new File(outfilename);
    assertNotNull(file);
    assertTrue(file.exists());
    assertTrue(file.isFile());
    int result = ZipManager.getNbFiles(new File(outfilename));
    assertEquals(1, result);
    ZipFile zipFile = new ZipFile(file);
    assertNotNull(zipFile.getEntry(File.separatorChar + "dir1" + File.separatorChar + "dir2" + File.separatorChar + "FrenchScrum.odp"));
    zipFile.close();
  }

  /**
   * Test of extract method, of class ZipManager.
   */
  @Test
  public void testExtract() throws Exception {
    System.out.println("extract");
    File source = new File(base + File.separatorChar + "target" + File.separatorChar + "test-classes" + File.separatorChar + "testExtract.zip");
    File dest = new File(base + File.separatorChar + "target" + File.separatorChar + "temp" + File.separatorChar + "extract");
    dest.mkdirs();
    ZipManager.extract(source, dest);
    assertNotNull(dest);
  }

  /**
   * Test of getNbFiles method, of class ZipManager.
   */
  @Test
  public void testGetNbFiles() throws Exception {
    assertNotNull(base);
    String path = base + File.separatorChar + "target" + File.separatorChar
            + "test-classes" + File.separatorChar + "ZipSample";
    String outfilename = base + File.separatorChar + "target" + File.separatorChar + "temp" + File.separatorChar
            + "testGetNbFiles.zip";
    ZipManager.compressPathToZip(path, outfilename);
    File file = new File(outfilename);
    assertNotNull(file);
    assertTrue(file.exists());
    assertTrue(file.isFile());
    int result = ZipManager.getNbFiles(new File(outfilename));
    assertEquals(5, result);
  }
}