package com.bosssoft.platform.installer.core.option;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ComponentsDefLoader
{
  private static final String CONFIG_FILE_NAME = "module_info";
  private static XStream xStream = null;

  static { xStream = new XStream(new DomDriver());

    xStream.alias("module", ModuleDef.class);
    xStream.alias("component", CompDef.class);

    xStream.useAttributeFor("id", String.class);
    xStream.useAttributeFor("nameKey", String.class);
    xStream.useAttributeFor("descKey", String.class);
    xStream.useAttributeFor("size", String.class);
    xStream.useAttributeFor("displaySeq", Integer.class);
    xStream.useAttributeFor("depends", String.class);
    xStream.useAttributeFor("dependedBy", String.class);
    xStream.useAttributeFor("filesPath", String.class);
    xStream.useAttributeFor("selected", String.class);
    xStream.useAttributeFor("editable", String.class);

    xStream.addImplicitCollection(ModuleDef.class, "comps", CompDef.class);
    xStream.addImplicitCollection(CompDef.class, "comps", CompDef.class);
  }

  public static List<ModuleDef> loadOptionComps(String path)
  {
    String edition = InstallRuntime.INSTANCE.getContext().getStringValue("EDITION").toLowerCase();
    String configFileName = "module_info_" + edition + ".xml";

    List options = new ArrayList();

    if (path == null) {
      return options;
    }
    File dir = new File(path);
    if (!dir.exists()) {
      return options;
    }

    File[] files = dir.listFiles();
    if (files == null) {
      return options;
    }
    File configFile = null;
    ModuleDef option = null;
    for (File f : files) {
      configFile = getConfigFile(f, configFileName);
      if (configFile.exists())
      {
        option = loadOptionDef(configFile);
        option.setModulePath(f.getAbsolutePath());
        options.add(option);
      }
    }
    return options;
  }

  private static File getConfigFile(File parent, String defaultName) {
    File f = new File(parent, defaultName);
    if (!f.exists()) {
      defaultName = "module_info.xml";
      f = new File(parent, defaultName);
    }

    return f;
  }

  private static ModuleDef loadOptionDef(File configFile)
  {
    try {
      InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile));
      return (ModuleDef)xStream.fromXML(reader);
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args)
  {
    String path = "E:/options_info.xml";
    File file = new File(path);
    ModuleDef m = loadOptionDef(file);

    System.out.println(m.getComps().size());
  }
}