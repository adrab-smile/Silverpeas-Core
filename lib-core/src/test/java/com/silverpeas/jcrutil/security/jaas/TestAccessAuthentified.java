/**
 * Copyright (C) 2000 - 2011 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.silverpeas.jcrutil.security.jaas;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.ByteArrayInputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.silverpeas.jcrutil.BasicDaoFactory;
import com.silverpeas.jcrutil.JcrConstants;
import com.silverpeas.jcrutil.model.impl.AbstractJcrRegisteringTestCase;
import com.silverpeas.util.MimeTypes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritLocations=false, locations={"classpath:/spring-jaas.xml", "classpath:/spring-domains.xml"})
public class TestAccessAuthentified extends AbstractJcrRegisteringTestCase {

  private static final String FOLDER_NAME = "SimpleTest";
  private static final String SUB_FOLDER_NAME = "SubTest";
  private static final String FILE_NAME = "MyTest";
  private static final String BART_ID = "7";
  private static final String BART_LOGIN = "bsimpson";
  private static final String BART_PASSWORD = "bart";


  @Before
  public void onSetUp() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node rootNode = session.getRootNode();
      rootNode.addNode(FOLDER_NAME, JcrConstants.NT_FOLDER);
      session.save();
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  @Test
  public void testAccessFileOwnable() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node folder = session.getRootNode().getNode(FOLDER_NAME);
      Node fileNode = folder.addNode(FILE_NAME, JcrConstants.NT_FILE);
      fileNode.addMixin(JcrConstants.SLV_OWNABLE_MIXIN);
      fileNode.setProperty(JcrConstants.SLV_PROPERTY_OWNER, BART_ID);
      Node contentNode = fileNode.addNode(JcrConstants.JCR_CONTENT,
          JcrConstants.NT_RESOURCE);
      contentNode.setProperty(JcrConstants.JCR_MIMETYPE,
          MimeTypes.PLAIN_TEXT_MIME_TYPE);
      contentNode.setProperty(JcrConstants.JCR_ENCODING, "");
      contentNode.setProperty(JcrConstants.JCR_DATA, new ByteArrayInputStream(
          "Bonjour le monde".getBytes()));
      Calendar lastModified = Calendar.getInstance();
      contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lastModified);
      session.save();
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getSystemSession();
      validateFile(session, true, true);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getAuthentifiedSession(BART_LOGIN,
          BART_PASSWORD);
      validateFile(session, true, true);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getAuthentifiedSession("lsimpson", "lisa");
      validateFile(session, false, true);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  @Test
  public void testAccessFileNotOwnable() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node folder = session.getRootNode().getNode(FOLDER_NAME);
      Node fileNode = folder.addNode(FILE_NAME, JcrConstants.NT_FILE);
      Node contentNode = fileNode.addNode(JcrConstants.JCR_CONTENT,
          JcrConstants.NT_RESOURCE);
      contentNode.setProperty(JcrConstants.JCR_MIMETYPE,
          MimeTypes.PLAIN_TEXT_MIME_TYPE);
      contentNode.setProperty(JcrConstants.JCR_ENCODING, "");
      contentNode.setProperty(JcrConstants.JCR_DATA, new ByteArrayInputStream(
          "Bonjour le monde".getBytes()));
      Calendar lastModified = Calendar.getInstance();
      contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lastModified);
      session.save();
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getSystemSession();
      validateFile(session, true, false);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getAuthentifiedSession(BART_LOGIN,
          BART_PASSWORD);
      validateFile(session, true, false);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }

    try {
      session = BasicDaoFactory.getAuthentifiedSession("lsimpson", "lisa");
      validateFile(session, true, false);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  protected void validateFile(Session session, boolean isAccessible,
      boolean hasMixin) throws RepositoryException {
    Node folderNode = session.getRootNode().getNode(FOLDER_NAME);
    assertNotNull("Folder not found", folderNode);
    assertEquals("Folder not of correct type", JcrConstants.NT_FOLDER,
        folderNode.getPrimaryNodeType().getName());
    if (isAccessible) {
      Node fileNode = session.getRootNode().getNode(FOLDER_NAME).getNode(
          FILE_NAME);
      assertNotNull("File not found", fileNode);
      assertEquals("File not of correct type", JcrConstants.NT_FILE, fileNode.getPrimaryNodeType().getName());
      assertEquals("File has not the correct mixin", hasMixin, hasMixin(
          JcrConstants.SLV_OWNABLE_MIXIN, fileNode));
    } else {
      assertFalse("File should not be accessible", folderNode.hasNode(FILE_NAME));
    }
  }

  protected void validateFolder(Session session, boolean hasMixin)
      throws RepositoryException {
    Node parentFolderNode = session.getRootNode().getNode(FOLDER_NAME);
    assertNotNull("Folder not found", parentFolderNode);
    assertEquals("Folder not of correct type", JcrConstants.NT_FOLDER,
        parentFolderNode.getPrimaryNodeType().getName());
    Node folderNode = parentFolderNode.getNode(SUB_FOLDER_NAME);
    assertNotNull("Folder not found", folderNode);
    assertEquals("Folder not of correct type", JcrConstants.NT_FOLDER,
        folderNode.getPrimaryNodeType().getName());
    assertEquals(JcrConstants.NT_FOLDER, folderNode.getPrimaryNodeType().getName());
    assertEquals("Folder has not the correct mixin", hasMixin, hasMixin(
        JcrConstants.SLV_OWNABLE_MIXIN, folderNode));
  }

  @Test
  public void testAccessFolderOwnable() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node folder = session.getRootNode().getNode(FOLDER_NAME);
      Node fileNode = folder.addNode(SUB_FOLDER_NAME, JcrConstants.NT_FOLDER);
      fileNode.addMixin(JcrConstants.SLV_OWNABLE_MIXIN);
      fileNode.setProperty(JcrConstants.SLV_PROPERTY_OWNER, BART_ID);
      session.save();
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getSystemSession();
      validateFolder(session, true);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getAuthentifiedSession(BART_LOGIN,
          BART_PASSWORD);
      validateFolder(session, true);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }

    try {
      session = BasicDaoFactory.getAuthentifiedSession("lsimpson", "lisa");
      validateFolder(session, true);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  @Test
  public void testAccessFolderNotOwnable() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node folder = session.getRootNode().getNode(FOLDER_NAME);
      folder.addNode(SUB_FOLDER_NAME, JcrConstants.NT_FOLDER);
      session.save();
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getSystemSession();
      validateFolder(session, false);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
    try {
      session = BasicDaoFactory.getAuthentifiedSession(BART_LOGIN,
          BART_PASSWORD);
      validateFolder(session, false);
    } catch (Exception ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }

    try {
      session = BasicDaoFactory.getAuthentifiedSession("lsimpson", "lisa");
      validateFolder(session, false);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  @Override
  protected IDataSet getDataSet() throws Exception {
    ReplacementDataSet dataSet = new ReplacementDataSet(new FlatXmlDataSet(this.getClass().getResourceAsStream(
        "test-jcrutil-dataset.xml")));
    dataSet.addReplacementObject("[NULL]", null);
    return dataSet;
  }

  @Override
  protected void clearRepository() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node rootNode = session.getRootNode();
      rootNode.getNode(FOLDER_NAME).remove();
      session.save();
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  protected boolean hasMixin(String mixinName, Node node)
      throws RepositoryException {
    NodeType[] types = node.getMixinNodeTypes();
    for (NodeType type : types) {
      if (mixinName.equals(type.getName())) {
        return true;
      }
    }
    return false;
  }
}
