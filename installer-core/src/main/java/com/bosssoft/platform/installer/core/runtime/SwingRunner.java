package com.bosssoft.platform.installer.core.runtime;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.ICommandListener;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.action.AbstractAction;
import com.bosssoft.platform.installer.core.action.GroupAction;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.action.UserAction;
import com.bosssoft.platform.installer.core.cfg.InstallConfig;
import com.bosssoft.platform.installer.core.cfg.Step;
import com.bosssoft.platform.installer.core.event.IStepInterceptor;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.gui.IRenderer;
import com.bosssoft.platform.installer.core.gui.InstallFrame;
import com.bosssoft.platform.installer.core.message.MessageManager;
import com.bosssoft.platform.installer.core.track.ActionsTrack;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.ReflectUtil;

public class SwingRunner extends AbstractRunner implements ICommandListener {
	Logger logger = Logger.getLogger(getClass());

	private final Dimension DEFAULT_DIMENSION = new Dimension(640, 460);
	private Dimension size = null;

	private InstallFrame frame = null;

	private IRenderer renderer = null;

	private Map setupPanels = new HashMap();

	private Map controllPanels = new HashMap();

	private AbstractSetupPanel currentSetupPanel = null;

	private AbstractControlPanel currentControlPanel = null;
	private Object currentUIStepID;
	private static final String DEFAULT_CONTROLLPANEL_CLASSNAME = "com.primeton.install.impl.gui.DefaultControlPanel";
	private boolean ignoreActionClassNotFound = true;
	private ActionsRunner actionsThread;

	static {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.indexOf("windows") >= 0) {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				FontUIResource font = new FontUIResource("Dialog", 0, 12);
				UIManager.put("Button.font", font);
				UIManager.put("ToggleButton.font", font);
				UIManager.put("RadioButton.font", font);
				UIManager.put("CheckBox.font", font);
				UIManager.put("ColorChooser.font", font);
				UIManager.put("ToggleButton.font", font);
				UIManager.put("ComboBox.font", font);
				UIManager.put("ComboBoxItem.font", font);
				UIManager.put("InternalFrame.titleFont", font);
				UIManager.put("Label.font", font);
				UIManager.put("List.font", font);
				UIManager.put("MenuBar.font", font);
				UIManager.put("Menu.font", font);
				UIManager.put("MenuItem.font", font);
				UIManager.put("RadioButtonMenuItem.font", font);
				UIManager.put("CheckBoxMenuItem.font", font);
				UIManager.put("PopupMenu.font", font);
				UIManager.put("OptionPane.font", font);
				UIManager.put("Panel.font", font);
				UIManager.put("ProgressBar.font", font);
				UIManager.put("ScrollPane.font", font);
				UIManager.put("Viewport", font);
				UIManager.put("TabbedPane.font", font);
				UIManager.put("Table.font", font);
				UIManager.put("TableHeader.font", font);
				UIManager.put("TextField.font", font);
				UIManager.put("PasswordFiled.font", font);
				UIManager.put("TextArea.font", font);
				UIManager.put("TextPane.font", font);
				UIManager.put("EditorPane.font", font);
				UIManager.put("TitledBorder.font", font);
				UIManager.put("ToolBar.font", font);
				UIManager.put("ToolTip.font", font);
				UIManager.put("Dialog.background", new Color(231, 245, 255));
			}
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public SwingRunner(InstallConfig config) {
		super(config);
		String rendererClassName = InstallRuntime.INSTANCE.getConfig().getRenderer();
		if (rendererClassName != null)
			try {
				this.renderer = ((IRenderer) Class.forName(rendererClassName).newInstance());
			} catch (Exception e) {
				e.printStackTrace();
				throw new InstallException("Renderer Class not found:" + rendererClassName);
			}
	}

	protected void processStep(String stepID) {
		Step step = this.installConfig.getStep(stepID);
		if (step == null) {
			this.logger.error("Not found the step defination as " + stepID);
			return;
		}
		this.currentStep = step;

		if (this.currentStep.hasGUI()) {
			setupUI(this.currentStep);
		}
		if ((this.currentStep.isAuto()) || (!this.currentStep.hasGUI()))
			next();
	}

	public void executeCommand(String command) {
	}

	private void next() {
		if (this.currentStep.hasGUI()) {
			//合法性检验  
			if ((this.currentSetupPanel != null) && (!this.currentSetupPanel.checkInput()))
				return;
			//将该步骤配置的信息操作到context中
			if (this.currentSetupPanel != null)
				this.currentSetupPanel.beforeNext();
		}
		try {
			executeStepActions(this.currentStep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeStepActions(Step step) throws Exception {
		boolean isAuto = step.isAuto();
		if (isAuto)
			doActionsInNewThread();
		else
			doActions();
	}

	private void doActionsInNewThread() {
		List actions = this.currentStep.getActions();

		if ((actions == null) || (actions.size() == 0)) {
			actionsFinished();
			return;
		}

		this.actionsThread = new ActionsRunner(actions);
		this.actionsThread.start();
	}

	protected void doActions() {
		IContext context = InstallRuntime.INSTANCE.getContext();
		List actionsList = this.currentStep.getActions();

		Map parameters = null;
		AbstractAction action = null;

		int scaleSum = 0;
		int i = 0;
		for (int j = actionsList.size(); i < j; i++) {
			action = (AbstractAction) actionsList.get(i);
			scaleSum += action.getScale();
		}

		int progressPercent = 1;
		i = 0;
		for (int j = actionsList.size(); i < j; i++) {
			action = (AbstractAction) actionsList.get(i);
			progressPercent = action.getScale() / scaleSum * 100;

			if (progressPercent < 1)
				progressPercent = 1;
			try {
				if ((action instanceof UserAction))
					parameters = parseParameters(context, ((UserAction) action).getParameters());
				else {
					parameters = null;
				}
				this.actionsTrack.addAction(action, parameters);
				action.execute(context, parameters);
			} catch (Throwable ex) {
				ex.printStackTrace();
				if (((ex.getCause() instanceof ClassNotFoundException)) && (this.ignoreActionClassNotFound)) {
					this.actionsTrack.removeLast();
				} else {
					String strategy = action.getStrategy();

					if (!strategy.equals("ignore")) {
						ex.printStackTrace();
					}
					String msg = null;
					if ((ex instanceof InstallException)) {
						msg = ex.getMessage();
						if (strategy.equals("retry")) {
							msg = msg + "\n" + I18nUtil.getString("DIALOG.MSG.RETRYOREXIT");
							if (1 == showConfirmMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"))) {
								this.frame.repaint();

								rollback();
								System.exit(0);
							} else {
								this.actionsTrack.removeLast();
								i--;
							}
						} else if (strategy.equals("quit")) {
							msg = msg + "\n" + I18nUtil.getString("SYSTEM.MSG.WILLEXIT");
							showMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
							this.frame.repaint();

							rollback();
							System.exit(0);
						} else if (strategy.equals("ignore")) {
							msg = msg + "\n" + I18nUtil.getString("DIALOG.MSG.IGNOREOREXIT");
							if (1 == showConfirmMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"))) {
								this.frame.repaint();

								rollback();
								System.exit(0);
							} else {
								this.actionsTrack.removeLast();
							}
						}
					} else {
						msg = I18nUtil.getString("SYSTEM.EXCEPTION");
						msg = msg + "\n" + I18nUtil.getString("SYSTEM.MSG.WILLEXIT");
						showMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
						rollback();
						this.frame.repaint();
						System.exit(0);
					}
				}
			}
		}

		actionsFinished();
	}

	private void showMessage(String msg, String title, int msgType) {
		JOptionPane.showMessageDialog(this.frame, msg, title, msgType);
	}

	private int showConfirmMessage(String msg, String title) {
		JPanel jpanel = new JPanel();
		JTextArea textArea = new JTextArea(4, 40);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText(msg);
		textArea.setEditable(false);
		jpanel.add(new JScrollPane(textArea));
		return JOptionPane.showConfirmDialog(this.frame, jpanel, title, 0);
	}

	private void rollback() {
		
	}

	private void setupUI(Step step) {
		if ((this.currentUIStepID != null) && (this.currentUIStepID.equals(step.getID()))) {
			return;
		}
		this.currentStep = step;
		this.currentUIStepID = this.currentStep.getID();

		this.frame.reset();

		AbstractControlPanel controlPanel = getControlPanel(step);
		if ((controlPanel != null) && (this.currentControlPanel != controlPanel)) {
			this.frame.setControlPanel(controlPanel);
			this.currentControlPanel = controlPanel;
		}

		AbstractSetupPanel setupPanel = getSetupPanel(step);
		if ((setupPanel != null) && (this.currentSetupPanel != setupPanel)) {
			this.frame.setSetupPanel(setupPanel);
			this.currentSetupPanel = setupPanel;
		}

		if (setupPanel != null) {
			setupPanel.beforeShow();
			controlPanel.beforeShow();
			if (step.isRender()) {
				this.frame.setSize(this.size);
				centerScreen(this.frame);
			}

			if (!this.frame.isVisible()) {
				this.frame.setVisible(true);
			} else {
				this.frame.validate();
				this.frame.repaint();
			}
		}
	}

	protected void init() {
		this.frame = new InstallFrame();//构造函数中初始化UI
		MainFrameController.setFrameInstance(this.frame);

		if (this.installConfig.getDimension() == null)
			this.size = this.DEFAULT_DIMENSION;
		else {
			this.size = this.installConfig.getDimension();
		}
		this.frame.setSize(this.size);

		centerScreen(this.frame);

		this.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				SwingRunner.this.cancel();
			}
		});
	}

	private void cancel() {
		if ((this.currentStep.getNextStepDiscriminator() == null) && (this.currentStep.getDefaultNextStepID() == null)) {
			finish();
		} else {
			String message = null;
			if ((this.installConfig.getType() != null) && (this.installConfig.getType().equals("uninstall")))
				message = I18nUtil.getString("EXIT_UNINSTALL");
			else {
				message = I18nUtil.getString("EXIT_INSTALL");
			}

			if ((this.currentStep.isAuto()) && (this.actionsThread != null) && (this.actionsThread.isAlive())) {
				this.actionsThread.requestSuspend();
				if (this.frame.showConfirmMessage(message, I18nUtil.getString("DIALOG.TITLE.INFO")) == 0) {
					rollback();
					System.exit(0);
				} else {
					this.actionsThread.requestResume();
					return;
				}

			}

			if (this.frame.showConfirmMessage(message, I18nUtil.getString("DIALOG.TITLE.INFO")) == 0)
				System.exit(0);
		}
	}

	public void actionsFinished() {
		if (this.currentStep.hasGUI())
			this.currentSetupPanel.afterActions();
		gotoNextStep(this.currentStep);
	}
	//读取该步骤的拦截器，使用拦截器判断是否满足某种情况，若是不满足则跳过该步骤，直接进入下一步
	private void gotoNextStep(Step step) {
		String nextStepID = getNextStepID(step);

		if (nextStepID == null) {
			return;
		}
		Step nextStep = this.installConfig.getStep(nextStepID);
		String interceptorClassName = nextStep.getInterceptorClassName();
		if (interceptorClassName != null) {
			IStepInterceptor interceptor = (IStepInterceptor) ReflectUtil.newInstanceBy(interceptorClassName);
			if (interceptor.isIgnoreThis(this.context)) {
				gotoNextStep(nextStep);
				return;
			}
			interceptor.beforeStep(this.context);
		}

		if (this.currentStep.getID().equals(nextStepID)) {
			return;
		}

		nextStep.setCallerID(this.currentStep.getID());
		processStep(nextStepID);
	}

	public void stepBegin(Step step) {
	}

	private AbstractSetupPanel getSetupPanel(Step step) {
		AbstractSetupPanel panel = null;
		Object object = this.setupPanels.get(step.getID());
		if ((object != null) && (!step.getReflesh())) {
			panel = (AbstractSetupPanel) object;
		} else {
			if ((this.renderer != null) && (step.isRender())) {
				panel = this.renderer.getSetupPanel(step);
			} else {
				if (step.getSetupPanelClassName() == null)
					return null;
				String clazzName = step.getSetupPanelClassName();
				Object instance = ReflectUtil.newInstanceBy(clazzName);

				if (instance == null) {
					return null;
				}
				panel = (AbstractSetupPanel) instance;
			}
			panel.initialize(null);
			this.setupPanels.put(step.getID(), panel);
		}
		panel.setContext(InstallRuntime.INSTANCE.getContext());
		return panel;
	}

	public AbstractControlPanel getControlPanel(Step step) {
		AbstractControlPanel panel = null;
		String clazzName = step.getControlPanelClassName();
		if (clazzName == null)
			clazzName = "com.primeton.install.impl.gui.DefaultControlPanel";
		Object object = this.controllPanels.get(clazzName);
		if (object != null) {
			panel = (AbstractControlPanel) object;
		} else {
			if ((this.renderer != null) && (step.isRender())) {
				panel = this.renderer.getControlPanel(step);
			} else {
				Object instance = ReflectUtil.newInstanceBy(clazzName);
				if (instance == null) {
					return null;
				}
				panel = (AbstractControlPanel) instance;
			}
			panel.setCommandListener(this);
			this.controllPanels.put(step.getID(), panel);
		}

		return panel;
	}

	public void doCommand(String cmd, Object[] parameters) {
		if (cmd.equals("NEXT"))
			next();
		else if (cmd.equals("CANCEL"))
			cancel();
		else if (cmd.equals("PREVIOUS"))
			previous();
		else if (cmd.equals("FINISH"))
			finish();
		else if (cmd.equals("HELP"))
			help();
	}

	private void help() {
	}

	private void previous() {
		if (this.currentStep.isAuto()) {
			return;
		}
		String previousStepID = this.currentStep.getCallerID();
		if (previousStepID == null) {
			return;
		}

		processStep(previousStepID);
	}

	private void finish() throws InstallException {
		if (!this.currentSetupPanel.checkInput())
			return;
		doActions();
		System.exit(0);
	}

	private void centerScreen(Window w) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = w.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		w.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}

	protected class ActionsRunner extends Thread {
		private boolean suspendRequested = false;

		private List actions = null;

		Logger runLogger = Logger.getLogger(ActionsRunner.class.getName());

		public ActionsRunner(List actions) {
			this.actions = actions;
		}

		public void requestSuspend() {
			this.suspendRequested = true;
		}

		private synchronized void checkSuspended() throws InterruptedException {
			while (this.suspendRequested)
				wait();
		}

		public synchronized void requestResume() {
			this.suspendRequested = false;
			notify();
		}

		//执行安装步骤中的一系列动作
		public void run() {
			AbstractAction action = null;
			Map parameters = null;
			IContext context = InstallRuntime.INSTANCE.getContext();
			
			try {
				double scaleSum = 0.0D;
				ArrayList allActions = new ArrayList();
				int i = 0;
				for (int j = this.actions.size(); i < j; i++) {
					action = (AbstractAction) this.actions.get(i);
					if ((action instanceof GroupAction)) {
						List<IAction> subActions = ((GroupAction) action).getActions();
						for (IAction a : subActions) {
							scaleSum += ((AbstractAction) a).getScale();
							allActions.add(a);
						}
					} else {
						allActions.add(action);
						scaleSum += action.getScale();
					}
				}

				int progressPercent = 1;

				i = 0;
				for (int j = allActions.size(); i < j; i++) {
					checkSuspended();
					action = (AbstractAction) allActions.get(i);
					progressPercent = (int) (action.getScale() * 100.0D / scaleSum);
					if (progressPercent < 1)
						progressPercent = 1;
					try {
						if ((action instanceof UserAction))
							parameters = SwingRunner.parseParameters(context, ((UserAction) action).getParameters());
						else {
							parameters = null;
						}
						MessageManager.syncSendBeginWork(action.toString(), progressPercent);
						SwingRunner.this.actionsTrack.addAction(action, parameters);
						action.execute(context, parameters);
						MessageManager.syncSendWorked(action.toString(), progressPercent);
					} catch (Throwable ex) {
						ex.printStackTrace();
						this.runLogger.error(ex.getMessage(), ex);
						String msg = null;

						if ((ex instanceof InstallException))
							msg = ex.getMessage();
						else {
							msg = I18nUtil.getString("SYSTEM.EXCEPTION");
						}

						if (action.getStrategy().equals("retry")) {
							msg = msg + "\n" + I18nUtil.getString("DIALOG.MSG.RETRYOREXIT");

							if (1 == SwingRunner.this.showConfirmMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"))) {
								SwingRunner.this.frame.repaint();
								SwingRunner.this.rollback();
								System.exit(0);
							} else {
								SwingRunner.this.actionsTrack.removeLast();
								i--;
							}
						} else if (action.getStrategy().equals("quit")) {
							msg = msg + "\n" + I18nUtil.getString("SYSTEM.MSG.WILLEXIT");

							SwingRunner.this.showMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
							SwingRunner.this.frame.repaint();
							SwingRunner.this.rollback();
							System.exit(0);
						} else if (action.getStrategy().equals("ignore")) {
							msg = msg + "\n" + I18nUtil.getString("DIALOG.MSG.IGNOREOREXIT");
							if (1 == SwingRunner.this.showConfirmMessage(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"))) {
								SwingRunner.this.frame.repaint();
								SwingRunner.this.rollback();
								System.exit(0);
							} else {
								SwingRunner.this.actionsTrack.removeLast();
							}
						}
					}
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			SwingRunner.this.actionsFinished();
		}
	}
}