package com.bosssoft.platform.installer.core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class UrlUtil
{
  public static final String FILE_URL_PREFIX = "file:";

  public static URL getURL(String resourceLocation)
    throws RuntimeException
  {
    return getURL(resourceLocation, null);
  }

  public static URL getURL(String resourceLocation, ClassLoader loader)
    throws RuntimeException
  {
    if (resourceLocation.startsWith("file:")) {
      resourceLocation = resourceLocation.substring("file:".length());
    }
    File file = new File(resourceLocation);
    if (file.exists()) {
      try {
        return new URL(resourceLocation);
      } catch (MalformedURLException ex) {
        try {
          return new URL("file:" + resourceLocation);
        } catch (MalformedURLException ex2) {
          throw new RuntimeException("The [resourceLocation=" + resourceLocation + "] is neither a URL not a well-formed file path.", ex2);
        }
      }
    }
    URL url = null;
    if (loader != null) {
      url = loader.getResource(resourceLocation);
    }
    if (url == null) {
      url = Thread.currentThread().getContextClassLoader().getResource(resourceLocation);
      if (url == null) {
        url = UrlUtil.class.getClassLoader().getResource(resourceLocation);
        if (url == null) {
          throw new RuntimeException("Class path resource [" + resourceLocation + "] cannot be resolved to URL because it does not exist.");
        }
      }
    }
    return url;
  }
}