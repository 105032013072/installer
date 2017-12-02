package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.action.InitDB;
import com.bosssoft.platform.installer.wizard.gui.component.MultiLabel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;
import com.bosssoft.platform.installer.wizard.util.DBConnectionUtil;

public abstract class AbstractDBEditorPanel extends JPanel implements IEditorPanel {
	private String parameter = null;

	protected JTextField tfdUrl = new JTextField();
	protected JLabel lblIP = new JLabel();
	protected JLabel lblUrl = new JLabel();
	protected JLabel lblPort = new JLabel();
	protected JCheckBox chkInitDB = new JCheckBox();
	protected MultiLabel lblTabSpace = new MultiLabel(I18nUtil.getString("DBCONFIG.MSG.TABSPACE"), 4, 2, 3);
	protected JTextField tfdSID = new JTextField();
	protected JLabel lbPassword = new JLabel();
	protected JLabel lblUser = new JLabel();
	protected JLabel lblSID = new JLabel();


	protected JTextField tfdPort = new JTextField();

	protected JButton btnDBTest = new JButton();
	protected JTextField tfdIP = new JTextField();

	protected JTextField tfdInformixName = new JTextField();

	protected JPasswordField tfdPassword = new JPasswordField();
	protected JTextField tfdUser = new JTextField();
	
	protected TxtDocumentListener documentListener = new TxtDocumentListener();
	private EditorActionListener actionListener = new EditorActionListener();
	private IContext context = null;

	public AbstractDBEditorPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() {
		setLayout(null);
		setOpaque(false);
		this.btnDBTest.setText(I18nUtil.getString("DBCONFIG.BUTTON.CONTEST"));
		this.btnDBTest.setMnemonic('T');
		this.btnDBTest.setMargin(new Insets(2, 2, 2, 2));
		this.btnDBTest.setOpaque(false);

		this.tfdUser.setBounds(new Rectangle(133, 88, 237, 21));
		this.tfdPassword.setBounds(new Rectangle(133, 118, 237, 21));
		this.tfdIP.setBounds(new Rectangle(133, 0, 237, 21));
		this.tfdIP.setText("127.0.0.1");
		
		this.btnDBTest.setBounds(new Rectangle(260,180, 120, 21));
		
		this.tfdPort.setBounds(new Rectangle(133, 29, 54, 21));
		
		this.lblSID.setBounds(new Rectangle(0, 60, 94, 16));
		this.lblUser.setBounds(new Rectangle(0, 89, 96, 16));
		this.lbPassword.setBounds(new Rectangle(0, 121, 97, 16));
		this.tfdSID.setBounds(new Rectangle(133, 58, 237, 21));

		this.chkInitDB.setBounds(new Rectangle(0, 175, 200, 25));
		this.lblTabSpace.setBounds(new Rectangle(0, 251, 380, 80));
		this.lblTabSpace.setForeground(Color.red);
		this.lblTabSpace.setVerticalAlignment(1);
		this.lblTabSpace.setVisible(false);

		this.lblPort.setBounds(new Rectangle(0, 31, 70, 16));
		this.lblUrl.setBounds(new Rectangle(0, 149, 101, 16));
		this.lblIP.setBounds(new Rectangle(0, 2, 64, 16));
		this.tfdUrl.setBounds(new Rectangle(133, 147, 237, 21));
        this.tfdUrl.setEnabled(false);
		
		this.lblSID.setText(I18nUtil.getString("DBCONFIG.LABEL.DBNAME"));
		this.lblUser.setText(I18nUtil.getString("DBCONFIG.LABEL.USER"));
		this.lbPassword.setText(I18nUtil.getString("DBCONFIG.LABEL.PASSWORD"));
		this.chkInitDB.setText(I18nUtil.getString("DBCONFIG.LABEL.INITDB"));
		this.chkInitDB.setOpaque(false);
		this.chkInitDB.setMargin(new Insets(0, 0, 0, 0));
		this.lblPort.setText(I18nUtil.getString("DBCONFIG.LABEL.PORT"));
		this.lblUrl.setText(I18nUtil.getString("DBCONFIG.LABEL.URL"));
		this.lblIP.setText(I18nUtil.getString("DBCONFIG.LABEL.IP"));
		
		add(this.lblPort, null);
		add(this.tfdSID, null);
		add(this.tfdPort, null);
		add(this.tfdUrl, null);
		add(this.chkInitDB, null);
		add(this.lblTabSpace, null);
		add(this.tfdPassword, null);
		add(this.btnDBTest, null);
		add(this.lbPassword, null);
		add(this.lblUser, null);
		add(this.lblUrl, null);
		add(this.tfdUser, null);
		
		add(this.lblSID, null);
		add(this.tfdIP, null);
		add(this.lblIP, null);
		
		this.tfdUser.getDocument().addDocumentListener(this.documentListener);
		this.tfdPassword.getDocument().addDocumentListener(this.documentListener);
		this.tfdIP.getDocument().addDocumentListener(this.documentListener);
		this.tfdPort.getDocument().addDocumentListener(this.documentListener);
		this.tfdSID.getDocument().addDocumentListener(this.documentListener);

		this.btnDBTest.addActionListener(this.actionListener);
		this.chkInitDB.addActionListener(this.actionListener);
	}

	protected void refreshDBUrl() {
		this.tfdUrl.setText(" " + getDBUrl());
	}

	public abstract String getDBUrl();

	public abstract void initUI();

	public abstract String getJDBCDriverClassName();


	public String testDBConnection() {
		String result = check();

		if (result != null) {
			return result;
		}

		int rtn = validateConn();

		if (rtn == 100)
			return null;
		if (rtn == -1)
			return I18nUtil.getString("DBCONFIG.MESSAGE.CONNECTIONFAIL");
		if (rtn == 0) {
			return I18nUtil.getString("DBCONFIG.MESSAGE.CANTCREATETBL");
		}
		return I18nUtil.getString("DBCONFIG.MESSAGE.CONNECTIONFAIL");
	}

	private int validateConn() {
		String jdbcUrl = this.tfdUrl.getText().trim();
		String user = this.tfdUser.getText();
		String password = new String(this.tfdPassword.getPassword());
		String jdbcDriverClass = null;
		String driverFiles = null;

		jdbcDriverClass = getJDBCDriverClassName();

		int rtn = DBConnectionUtil.validateDBConfig(driverFiles, jdbcDriverClass, jdbcUrl, user, password);
		return rtn;
	}

	public void setContext(IContext context) {
		this.context = context;
	}

	public IContext getContext() {
		return this.context;
	}

	public void setParameter(String p) {
		this.parameter = p;
	}

	public String getParameter() {
		return this.parameter;
	}

	public Properties getProperties() {
		Properties p = new Properties();

		p.put("DB_IP", this.tfdIP.getText().trim());
		p.put("DB_NAME", this.tfdSID.getText());
		p.put("DB_USERNAME", this.tfdUser.getText());
		p.put("DB_PASSWORD", new String(this.tfdPassword.getPassword()));
		p.put("DB_SERVER_PORT", this.tfdPort.getText().trim());
		p.put("DB_URL", this.tfdUrl.getText().trim());
		p.put("DB_IS_INIT", Boolean.toString(this.chkInitDB.isSelected()));
		p.put("DB_IS_DEFAULT_JAR",true);

		p.put("DB_DRIVER", getJDBCDriverClassName());
		p.put("DB_JDBC_LIBS", "");
		
		return p;
	}

	public String check() {
		if ((StringUtils.isEmpty(this.tfdIP.getText()))
				|| (!ValidatorHelper.isPatternValid(this.tfdIP.getText(), "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$"))) {
			return I18nUtil.getString("DBCONFIG.MSG.IPNULL");
		}

		if (this.tfdUrl.getText().trim().length() == 0) {
			return I18nUtil.getString("DBCONFIG.MSG.URLNULL");
		}

		if (this.tfdUser.getText().trim().length() == 0) {
			return I18nUtil.getString("DBCONFIG.MSG.USERNULL");
		}

		/*if (this.tfdPassword.getPassword().length == 0) {
			return I18nUtil.getString("DBCONFIG.MSG.PWDNULL");
		}
*/
		String port = this.tfdPort.getText();
		if ((StringUtils.isEmpty(port)) || (StringUtils.isBlank(port))) {
			return I18nUtil.getString("CHOOSEIP.PORT.EMPTY");
		}
		if ((!ValidatorHelper.isInteger(port)) || ((Integer.valueOf(port).intValue() < 1) && (Integer.valueOf(port).intValue() > 65535))) {
			return I18nUtil.getString("CHOOSEIP.PORT.INVALID");
		}


		if (this.tfdSID.getText().trim().length() == 0) {
			return I18nUtil.getString("DBCONFIG.MSG.USERNAMENULL");
		}
		try {
			if ((Long.parseLong(this.tfdPort.getText().trim()) <= 0L) || (Long.parseLong(this.tfdPort.getText().trim()) > 65535L))
				return I18nUtil.getString("DBCONFIG.MSG.PORTERROR");
		} catch (Exception e) {
			return I18nUtil.getString("DBCONFIG.MSG.PORTVALUEERROR");
		}

		return null;
	}

	public boolean checkInput() {
		String message = check();
		if ((message != null) && (this.chkInitDB.isSelected())) {
			showError(message);
			return false;
		}

		if (this.chkInitDB.isSelected()) {
			String result = testDBConnection();
			if (result != null) {
				showError(result);
				return false;
			}

			if (InitDB.isInitialized(DBConnectionUtil.getConnection(this.context))) {
				int ans = MainFrameController.showConfirmDialog(I18nUtil.getString("DBCONFIG.MSG.DB.ALREADY.INIT"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 1, 2);
				if (1 == ans) {
					this.context.setValue("DB_IS_FORCE_INIT", "false");
				}
				if (2 == ans) {
					return false;
				}
			}
		}

		if (message != null) {
			message = I18nUtil.getString("DBCONFIG.MSG.CONNECTERROR") + message + I18nUtil.getString("DBCONFIG.MSG.CONTINUE");
			int result = MainFrameController.showConfirmDialog(message, I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2);
			return result == 0;
		}
		String test_conn_result = testDBConnection();
		if (test_conn_result != null) {
			String msg = I18nUtil.getString("DBCONFIG.MSG.CONNECTERROR") + I18nUtil.getString("DBCONFIG.MSG.CONTINUE");
			int dialog_result = MainFrameController.showConfirmDialog(msg, I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2);
			return dialog_result == 0;
		}
		
		//确认数据库是否进行初始化
		if(chkInitDB.isSelected()){
			int dialog_result=MainFrameController.showConfirmDialog(I18nUtil.getString("INITDB.SURE"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2);
		    return dialog_result==0;
		}

		return true;
	}

	protected void showError(String message) {
		MainFrameController.showMessageDialog(message, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
	}

	public void resetTabSpaceText() {
		String db_type = getClass().getSimpleName().toLowerCase();

		this.lblTabSpace.setVisible(false);
	}

	class EditorActionListener implements ActionListener {
		EditorActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			 if (AbstractDBEditorPanel.this.chkInitDB == source) {
				AbstractDBEditorPanel.this.resetTabSpaceText();
			} else if (AbstractDBEditorPanel.this.btnDBTest == source) {
				String message = AbstractDBEditorPanel.this.testDBConnection();
				if (message != null)
					MainFrameController.showMessageDialog(message, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
				else
					MainFrameController.showMessageDialog(I18nUtil.getString("DBCONFIG.MESSAGE.CONNECTIONOK"), I18nUtil.getString("DIALOG.TITLE.INFO"), 1);
			}
		}
	}

	class IPAddressFormatter extends DefaultFormatter {
		IPAddressFormatter() {
		}

		public String valueToString(Object value) throws ParseException {
			if (!(value instanceof byte[]))
				throw new ParseException("Not a byte[]", 0);
			byte[] a = (byte[]) value;
			if (a.length != 4)
				throw new ParseException("Length != 4", 0);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < 4; i++) {
				int b = a[i];
				if (b < 0)
					b += 256;
				buffer.append(String.valueOf(b));
				if (i < 3)
					buffer.append('.');
			}
			return buffer.toString();
		}

		public Object stringToValue(String text) throws ParseException {
			StringTokenizer tokenizer = new StringTokenizer(text, ".");
			byte[] a = new byte[4];
			for (int i = 0; i < 4; i++) {
				int b = 0;
				if (!tokenizer.hasMoreTokens())
					throw new ParseException("Too few bytes", 0);
				try {
					b = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e) {
					throw new ParseException("Not an Integer", 0);
				}
				if ((b < 0) || (b >= 256))
					throw new ParseException("Byte out of range", 0);
				a[i] = ((byte) b);
			}
			if (tokenizer.hasMoreTokens())
				throw new ParseException("Too many bytes", 0);
			return a;
		}
	}

	class PortFormatter extends DefaultFormatter {
		PortFormatter() {
		}

		public String valueToString(Object value) throws ParseException {
			if (!(value instanceof Long))
				throw new ParseException("Not a Long", 0);
			long l = ((Long) value).longValue();
			if ((l < 0L) || (l > 65535L))
				throw new ParseException("Not a Valid Port", 0);
			return value.toString();
		}

		public Object stringToValue(String text) throws ParseException {
			long value = 0L;
			try {
				value = Long.parseLong(text);
			} catch (Exception e) {
				throw new ParseException("Not a Long", 0);
			}

			if ((value < 0L) || (value > 65535L)) {
				throw new ParseException("Not a Valid Port", 0);
			}
			return new Long(value);
		}
	}

	protected class TxtDocumentListener implements DocumentListener {
		protected TxtDocumentListener() {
		}

		public void insertUpdate(DocumentEvent evt) {
			AbstractDBEditorPanel.this.refreshDBUrl();
		}

		public void removeUpdate(DocumentEvent evt) {
			AbstractDBEditorPanel.this.refreshDBUrl();
		}

		public void changedUpdate(DocumentEvent evt) {
			AbstractDBEditorPanel.this.refreshDBUrl();
		}
	}
}