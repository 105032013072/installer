package com.bosssoft.platform.installer.core.option;

public class ModuleDef extends CompDef
  implements Comparable
{
  private Integer displaySeq = null;
  private String modulePath = null;

  public Integer getDisplaySeq() {
    return this.displaySeq;
  }

  public void setDisplaySeq(Integer displaySeq)
  {
    this.displaySeq = displaySeq;
  }

  public String getModulePath()
  {
    return this.modulePath;
  }

  public void setModulePath(String path)
  {
    this.modulePath = path;
  }

  public int compareTo(Object o) {
    int r = 0;
    if ((o != null) && ((o instanceof ModuleDef))) {
      ModuleDef md = (ModuleDef)o;
      if (getDisplaySeq() == null) {
        return 1;
      }
      if (md.getDisplaySeq() == null) {
        return -1;
      }
      r = getDisplaySeq().compareTo(md.getDisplaySeq());
      if (r == 0) {
        r = getId().compareTo(md.getId());
      }
    }
    return r;
  }

  public static void main(String[] args)
  {
  }
}