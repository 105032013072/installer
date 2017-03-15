package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.util.XmlHelper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

public class Registry
{
  private static final String registryFileName = ".eosreg";
  private String dir;
  private String ROOT = "installed";
  private Document doc;
  protected boolean readOnly = false;
  protected boolean canWrite = true;
  public static final String INSTALL_COUNT = "INSTALL_COUNT";
  public static final String PRODUCT_VEERSION = "product.version";
  transient Logger logger = Logger.getLogger(Registry.class);

  public Registry(String dir) { this.dir = dir.replace('\\', '/');
    File regFile = new File(dir, ".eosreg");
    if ((regFile.exists()) && (regFile.isFile())) {
      try {
        this.doc = XmlHelper.parse(regFile);
      }
      catch (Exception doe) {
        this.doc = XmlHelper.createDocument(this.ROOT);
        this.doc.getRootElement().addAttribute("count", "0");
      }
    } else {
      this.doc = XmlHelper.createDocument(this.ROOT);
      this.doc.getRootElement().addAttribute("count", "0");
      this.logger.info("Create " + regFile);
    }
  }

  public void addInstalledProduct(IContext context)
  {
    String installDir = context.getStringValue("INSTALL_DIR");
    String eosVersion = context.getStringValue("product.version");

    int theLargest = theLargestFlag(eosVersion);
    int k = theLargest + 1;

    String flag = String.valueOf(k);
    context.setValue("INSTALL_COUNT", Integer.valueOf(k));

    Element root = this.doc.getRootElement();
    Element eos = root.addElement("eos").addAttribute("installDir", installDir).addAttribute("type", "standard").addAttribute("version", eosVersion).addAttribute("flag", flag);
    try
    {
      XmlHelper.saveAs(this.dir + "/" + ".eosreg", this.doc, "GB2312");
    } catch (IOException e) {
      this.logger.error(e.getMessage(), e);
    }

    this.logger.info("save .eosreg file");
  }

  public int theLargestFlag(String version)
  {
    List nl = this.doc.selectNodes("/" + this.ROOT + "/eos[@version=\"" + version + "\"]");
    int realCount = nl.size();
    String str = null;
    int k = 0;
    this.logger.info("Length: " + realCount);
    for (Iterator iter = nl.iterator(); iter.hasNext(); ) {
      Element eos = (Element)iter.next();
      String flag = eos.attributeValue("flag");

      int j = Integer.parseInt(flag);
      if (j > k) {
        k = j;
        str = flag;
      }
    }

    this.logger.info("the largest number is :" + str);
    return k;
  }

  public void removed(String ins_dir, String installCount)
  {
  }

  public static void main(String[] args)
  {
  }
}