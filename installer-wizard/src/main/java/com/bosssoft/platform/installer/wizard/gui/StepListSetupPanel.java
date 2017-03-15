package com.bosssoft.platform.installer.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.wizard.gui.component.BlankIcon;

public class StepListSetupPanel extends AbstractSetupPanel {
	private AbstractSetupPanel subPanel = null;
	private List<StepListRenderer.StepItem> stepList = null;
	private String currentStepId = null;
	private StepsListPane stepsListPane = null;

	public StepListSetupPanel(String stepId, AbstractSetupPanel panel, List<StepListRenderer.StepItem> stepList) {
		this.subPanel = panel;
		this.stepList = stepList;
		this.currentStepId = stepId;
		setLayout(null);
	}

	public void afterActions() {
		this.subPanel.afterActions();
	}

	public void beforeNext() {
		this.subPanel.beforeNext();
	}

	public void beforePrevious() {
		this.subPanel.beforePrevious();
	}

	public void beforeShow() {
		this.subPanel.beforeShow();
	}

	public boolean checkInput() {
		return this.subPanel.checkInput();
	}

	public void initialize(String[] parameters) {
		this.subPanel.initialize(parameters);

		this.stepsListPane = new StepsListPane();
		this.stepsListPane.setBounds(new Rectangle(0, 0, 180, 400));

		this.subPanel.setBounds(new Rectangle(180, 0, 452, 392));
		add(this.subPanel);
		add(this.stepsListPane);
	}

	public void setContext(IContext c) {
		super.setContext(c);
		this.subPanel.setContext(c);
	}

	public class StepsListPane extends JPanel {
		private BorderLayout borderLayout = new BorderLayout();
		private JLabel lblLogo = new JLabel();
		private Border border;
		private JListStepItem[] stepItems;
		private String[] steps = null;
		private JList lstSteps = new JList();
		private Color stepsListColor = new Color(178, 227, 242);

		public StepsListPane() {
			try {
				jbInit();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public Rectangle getRectangle() {
			return new Rectangle(0, 0, 180, 400);
		}

		void jbInit() throws Exception {
			this.border = BorderFactory.createEmptyBorder(10, 0, 20, 0);
			this.lblLogo.setVerifyInputWhenFocusTarget(true);
			this.lblLogo.setHorizontalAlignment(0);
			String logoPath = InstallerFileManager.getImageDir() + "/logo.gif";
			this.lblLogo.setIcon(new ImageIcon(logoPath));
			this.lblLogo.setVerticalAlignment(0);

			setOpaque(false);
			this.lblLogo.setBorder(this.border);
			setLayout(this.borderLayout);
			add(this.lblLogo, "North");

			this.stepItems = getJListItems();

			this.lstSteps = new JList(this.stepItems);

			this.lstSteps.setOpaque(false);
			LabelListCellRenderer renderer = new LabelListCellRenderer();
			this.lstSteps.setCellRenderer(renderer);

			add(this.lstSteps, "Center");
			setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		}

		public void setCurrentStep(String step) {
			if (step == null)
				return;
			int position = -1;
			for (int i = 0; i < this.steps.length; i++) {
				if (this.steps[i].equals(step)) {
					position = i;
					break;
				}
			}
			if (position < 0) {
				return;
			}
			for (int i = 0; i < position; i++) {
				this.stepItems[i].setStatus(3);
			}

			this.stepItems[position].setStatus(2);
			this.lstSteps.repaint();
		}

		private JListStepItem[] getJListItems() {
			if (StepListSetupPanel.this.stepList.size() == 0) {
				return null;
			}
			JListStepItem[] items = new JListStepItem[StepListSetupPanel.this.stepList.size()];
			int status = 3;
			for (int i = 0; i < StepListSetupPanel.this.stepList.size(); i++) {
				items[i] = new JListStepItem(((StepListRenderer.StepItem) StepListSetupPanel.this.stepList.get(i)).toString(), status);
				if (((StepListRenderer.StepItem) StepListSetupPanel.this.stepList.get(i)).getStepId().equals(StepListSetupPanel.this.currentStepId)) {
					items[i].setStatus(2);
					status = 1;
				}
			}
			return items;
		}

		public class JListStepItem {
			public static final int TOINSTALL = 1;
			public static final int INSTALLING = 2;
			public static final int INSTALLED = 3;
			private int _status = 1;
			private String _name = null;

			public JListStepItem(String stepName, int status) {
				this._name = stepName;
				this._status = status;
			}

			public void setStatus(int s) {
				this._status = s;
			}

			public int getStatus() {
				return this._status;
			}

			public String toString() {
				return this._name;
			}
		}

		public class LabelListCellRenderer implements ListCellRenderer {
			private Icon iconToInstall = new BlankIcon(new Dimension(15, 15));
			private ImageIcon iconInstalling = new ImageIcon(StepListSetupPanel.this.getClass().getResource("installing.gif"));
			private ImageIcon iconInstalled = new ImageIcon(StepListSetupPanel.this.getClass().getResource("installed.gif"));

			protected Border m_noFocusBorder = BorderFactory.createEmptyBorder(5, 10, 5, 0);

			public LabelListCellRenderer() {
			}

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel();
				label.setOpaque(true);
				label.setBorder(this.m_noFocusBorder);
				label.setText(value.toString());
				label.setFont(new Font("Dialog", 0, 12));

				label.setOpaque(false);
				StepListSetupPanel.StepsListPane.JListStepItem data = (StepListSetupPanel.StepsListPane.JListStepItem) value;
				if (data.getStatus() == 1)
					label.setIcon(this.iconToInstall);
				else if (data.getStatus() == 2)
					label.setIcon(this.iconInstalling);
				else if (data.getStatus() == 3) {
					label.setIcon(this.iconInstalled);
				}
				return label;
			}
		}
	}
}