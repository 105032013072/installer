package com.bosssoft.platform.installer.core.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageManager
{
  private static final List<IProgressReceiver> receivers = new ArrayList();

  public static void registe(IProgressReceiver receiver)
  {
    synchronized (receivers) {
      if (!receivers.contains(receiver))
        receivers.add(receiver);
    }
  }

  public static void syncSendMessage(String message)
  {
    synchronized (receivers) {
      for (Iterator iter = receivers.iterator(); iter.hasNext(); ) {
        IProgressReceiver receiver = (IProgressReceiver)iter.next();
        receiver.messageChanged(message);
      }
    }
  }

  public static void syncSendWorked(String message, int worked)
  {
    synchronized (receivers) {
      for (Iterator iter = receivers.iterator(); iter.hasNext(); ) {
        IProgressReceiver receiver = (IProgressReceiver)iter.next();
        receiver.worked(message, worked);
      }
    }
  }

  public static void syncSendWorked(int worked)
  {
    synchronized (receivers) {
      for (Iterator iter = receivers.iterator(); iter.hasNext(); ) {
        IProgressReceiver receiver = (IProgressReceiver)iter.next();
        receiver.worked(worked);
      }
    }
  }

  public static void syncSendBeginWork(String message, int willWork)
  {
    synchronized (receivers) {
      for (Iterator iter = receivers.iterator(); iter.hasNext(); ) {
        IProgressReceiver receiver = (IProgressReceiver)iter.next();
        receiver.beginWork(message, willWork);
      }
    }
  }
}