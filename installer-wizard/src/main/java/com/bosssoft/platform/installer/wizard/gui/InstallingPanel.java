package com.bosssoft.platform.installer.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.message.IProgressReceiver;
import com.bosssoft.platform.installer.core.message.MessageManager;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class InstallingPanel extends AbstractSetupPanel implements IProgressReceiver {
	Logger logger = Logger.getLogger(getClass());
	private BorderLayout borderLayout1 = new BorderLayout();
	private StepTitleLabel line = new StepTitleLabel();

	private JLabel lblInfo1 = new JLabel();
	private JLabel lblInstalling = new JLabel();
	private JLabel lblInstallContent = new JLabel();
	private JProgressBar progressBar = new JProgressBar();
	private JLabel lblInfo2 = new JLabel();
	private ProgressBarAutoRefresh pbar = new ProgressBarAutoRefresh();

	private int progress = 0;

	private int willWork = 1;

	public InstallingPanel() {
		MessageManager.registe(this);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setOpaque(false);

		setLayout(null);
		this.line.setText(I18nUtil.getString("STEP.INSTALLING"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));
		this.lblInfo1.setBounds(new Rectangle(37, 26, 363, 28));
		this.lblInstalling.setText(I18nUtil.getString("INSTALLING.LABEL.INSTALLING"));
		this.lblInstalling.setBounds(new Rectangle(37, 132, 61, 16));
		this.lblInstallContent.setText(" ");
		this.lblInstallContent.setBounds(new Rectangle(37, 150, 363, 16));
		this.progressBar.setBounds(new Rectangle(37, 172, 366, 18));
		this.progressBar.setStringPainted(true);
		this.lblInfo2.setText(I18nUtil.getString("INSTALLING.LABEL.TIPTIMECOST"));
		this.lblInfo2.setBounds(new Rectangle(37, 48, 363, 16));

		add(this.line, null);
		add(this.lblInfo1, null);
		add(this.lblInfo2, null);
		add(this.lblInstalling, null);
		add(this.lblInstallContent, null);
		add(this.progressBar, null);
	}

	public void afterShow() {
	}

	public void beforeNext() {
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("next", false);
		controlPane.setButtonVisible("previous", false);

		controlPane.setButtonEnabled("finish", false);
		controlPane.setButtonEnabled("help", false);
		controlPane.setButtonEnabled("next", false);
		controlPane.setButtonEnabled("previous", false);

		String label = I18nUtil.getString("INSTALLING.LABEL");

		this.lblInfo1.setText(label);
		this.pbar.start();
	}

	public boolean checkInput() {
		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void setProgress(String info, int value) {
		try {
			this.progress += value;

			if (this.progress < 100)
				this.progressBar.setValue(this.progress);
			this.lblInstallContent.setText(info);
			this.progressBar.repaint();
			this.lblInstallContent.repaint();
		} catch (Exception localException) {
		}
	}

	public void afterActions() {
	}

	public void messageChanged(String message) {
		message = cuteMessage(message);
		this.lblInstallContent.setText(message);
		this.lblInstallContent.repaint();
	}

	private String cuteMessage(String message) {
		if (message != null) {
			int len = message.length();

			if (len > 58) {
				message = "..." + message.substring(len - 58, len);
			}
		}

		if (message == null) {
			message = "";
		}
		return message;
	}

	public void beginWork(String message, int count) {
		this.willWork = count;
	}

	public void worked(String message, int count) {
		message = cuteMessage(message);
		this.lblInstallContent.setText(message);
		this.progressBar.repaint();
		worked(count);
	}

	public void worked(int count) {
		try {
			this.progress += count;

			if (this.progress < 100)
				this.progressBar.setValue(this.progress);
			this.progressBar.repaint();
		} catch (Exception localException) {
		}
	}

	public class ProgressBarAutoRefresh extends Thread {
		public ProgressBarAutoRefresh() {
		}

		public void run() {
			while (InstallingPanel.this.progressBar.getPercentComplete() < 0.99D) {
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					return;
				}

				int value = InstallingPanel.this.progressBar.getValue();
				if (value < InstallingPanel.this.progress) {
					value = InstallingPanel.this.progress;
				}
				int wilWorked = InstallingPanel.this.progress + InstallingPanel.this.willWork;
				if (value < wilWorked) {
					InstallingPanel.this.progressBar.setValue(value + 1);
					InstallingPanel.this.progressBar.repaint();
					InstallingPanel.this.lblInstallContent.repaint();
				}
			}
		}
	}
}