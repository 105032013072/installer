package com.bosssoft.platform.installer.core.runtime;

import java.util.ArrayList;
import java.util.List;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.cfg.InstallConfig;
import com.bosssoft.platform.installer.core.event.IListener;
import com.bosssoft.platform.installer.core.gui.IRenderer;
import com.bosssoft.platform.installer.core.gui.MainFrame;
import com.bosssoft.platform.installer.core.navigator.INavigator;

public class InstallRuntime
{
  public static final String RUN_MODE_SWING = "swing";
  public static final String RUN_MODE_SILENT = "silent";
  private String _runMode = null;

  private INavigator _navigator = null;

  private IRenderer _renderer = null;

  private IContext _context = null;

  private InstallConfig _config = null;

  private List<IListener> _listeners = new ArrayList();

  public static InstallRuntime INSTANCE = new InstallRuntime();

  private String _installRoot = null;

  private MainFrame frame = null;

  public void setConfig(InstallConfig installConfig)
  {
    this._config = installConfig;
  }

  public InstallConfig getConfig() {
    return this._config;
  }

  public void setContext(IContext context) {
    this._context = context;
  }

  public IContext getContext() {
    return this._context;
  }

  public void setNavigator(INavigator navigator) {
    this._navigator = navigator;
  }

  public INavigator getNavigator() {
    return this._navigator;
  }

  public void setRunMode(String runmode) {
    this._runMode = runmode;
  }

  public boolean isSilentInstall()
  {
    return this._runMode.equals("silent");
  }

  public void setInstallRoot(String installRootDir) {
    this._installRoot = installRootDir;
  }

  public String getInstallRoot() {
    return this._installRoot;
  }

  public boolean addListener(IListener listener)
  {
    return this._listeners.add(listener);
  }

  public List<IListener> getListeners()
  {
    return this._listeners;
  }

  public MainFrame getMainFrame() {
    return this.frame;
  }

  public void setMainFrame(MainFrame frame) {
    this.frame = frame;
  }

  public void setRenderer(IRenderer renderer) {
    this._renderer = renderer;
  }
}