package com.bosssoft.platform.installer.core.option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.bosssoft.platform.installer.core.cfg.InstallConfigLoader;

public class DeployDefLoader
{
  public static DeployDef getDeploy(String configFilePath)
    throws FileNotFoundException
  {
    File file = new File(configFilePath);

    if (!file.exists()) {
      throw new FileNotFoundException(configFilePath);
    }
    if (file.isDirectory()) {
      throw new IllegalStateException("Not is a file." + 
        configFilePath);
    }
    InputStream stream = new FileInputStream(file);

    Document doc = null;
    try {
      doc = InstallConfigLoader.getXmlDocument(stream);
    } catch (DocumentException e1) {
      e1.printStackTrace();
      return null;
    }

    DeployDef deploy = new DeployDef();
    Element root = doc.getRootElement();

    Element element = null;

    for (Iterator i = root.elementIterator(); i.hasNext(); ) {
      element = (Element)i.next();
      deploy.addAction(InstallConfigLoader.parseAction(element));
    }
    return deploy;
  }
}