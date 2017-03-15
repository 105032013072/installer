package com.bosssoft.platform.installer.jee.server.spi.impl;

import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.IJEEServerEnv;
import com.bosssoft.platform.installer.jee.server.ProductDefination;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;
import com.bosssoft.platform.installer.jee.server.spi.IJEEServerProvider;

public class WebsphereServerProvider
  implements IJEEServerProvider
{
  public static final int PRIORITY = 1;
  public static final String SERVER_TYPE = "websphere";

  public IJEEServer createServer(IJEEServerEnv env)
  {
    WebsphereEnv websphereEnv = (WebsphereEnv)env;
    WebSphereServerImpl serverImpl = new WebSphereServerImpl(websphereEnv);
    return serverImpl;
  }

  public int getPriority()
  {
    return 1;
  }

  public boolean support(ProductDefination productDefination)
  {
    String name = productDefination.getName();
    String version = productDefination.getVersion();
    if ((name == null) || 
      (version == null)) {
      return false;
    }
    if (name.toLowerCase().equals("websphere".toLowerCase()))
    {
      if (version.startsWith("6.1")) {
        return true;
      }
    }
    return false;
  }
}