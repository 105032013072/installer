package com.bosssoft.platform.installer.wizard.gui.option;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.bosssoft.platform.installer.core.option.CompDef;
import com.bosssoft.platform.installer.core.option.ComponentsDefHelper;

public class CheckBoxNodeRenderer implements TreeCellRenderer {
	private JCheckBox optionRenderer = new JCheckBox();
	private JLabel rootRenderer = new JLabel();
	private JLabel requiredRender = new JLabel();

	private String _nodeKey = null;

	private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

	private Component _currentComponent = null;

	private static ActionListener DONOTHINGLISTENER = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		}
	};

	private JTree _tree = null;

	private Color selectColor = new Color(180, 212, 253);

	private ActionListener chooseChangeListener = null;

	protected Component getCurrentComponent() {
		return this._currentComponent;
	}

	protected JCheckBox getLeafRenderer() {
		return this.optionRenderer;
	}

	protected String getNodeKey() {
		return this._nodeKey;
	}

	public CheckBoxNodeRenderer(JTree tree) {
		this._tree = tree;

		Font fontValue = UIManager.getFont("Label.font");
		if (fontValue != null) {
			this.optionRenderer.setFont(fontValue);
		}
		Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
		this.optionRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
		this.optionRenderer.addActionListener(new NodeActionListener());
		this.chooseChangeListener = DONOTHINGLISTENER;
	}

	public CheckBoxNodeRenderer(JTree tree, ActionListener chooseListener) {
		this(tree);
		this.chooseChangeListener = chooseListener;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if ((value != null) && ((value instanceof DefaultMutableTreeNode))) {
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if ((userObject instanceof CompDef)) {
				CompDef comp = (CompDef) userObject;
				this._nodeKey = comp.getId();
				String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
				this.optionRenderer.setText(stringValue);
				if (comp.isEditable()) {
					this.optionRenderer.setEnabled(true);
					if (this.optionRenderer.isSelected() != comp.isSelected())
						this.optionRenderer.setSelected(comp.isSelected());
					this._currentComponent = this.optionRenderer;
				} else {
					this._currentComponent = getRequiredCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
				}
			} else {
				this._currentComponent = getRootCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
			}
		} else {
			this._currentComponent = this.nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
		if (selected)
			this._currentComponent.setBackground(this.selectColor);
		else {
			this._currentComponent.setBackground(UIManager.getColor("Tree.textBackground"));
		}
		return this._currentComponent;
	}

	private Component getRootCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		this.rootRenderer.setText(value.toString());
		this.rootRenderer.setOpaque(true);

		return this.rootRenderer;
	}

	private Component getRequiredCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		this.requiredRender.setText(value.toString());
		this.requiredRender.setOpaque(true);
		this.requiredRender.setIcon(new ImageIcon(getClass().getResource("checked.gif")));
		return this.requiredRender;
	}

	private void syncParentNode(DefaultMutableTreeNode node, boolean selected) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		if (parentNode.isRoot()) {
			return;
		}
		CompDef parentComp = (CompDef) parentNode.getUserObject();
		if (!parentComp.isEditable()) {
			return;
		}

		if (selected) {
			parentComp.setSelected(Boolean.TRUE.toString());
			return;
		}

		int childrenCount = parentNode.getChildCount();
		if (childrenCount == 0) {
			return;
		}
		DefaultMutableTreeNode childNode = null;
		CompDef childComp = null;
		boolean zeroSelected = true;
		for (int i = 0; i < childrenCount; i++) {
			childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
			childComp = (CompDef) childNode.getUserObject();
			if (childComp.isSelected()) {
				zeroSelected = false;
				break;
			}
		}

		if (zeroSelected)
			parentComp.setSelected(Boolean.FALSE.toString());
	}

	private void syncChildrenNode(DefaultMutableTreeNode node, boolean selected) {
		int childrenCount = node.getChildCount();
		if (childrenCount == 0) {
			return;
		}
		DefaultMutableTreeNode childNode = null;
		CompDef childComp = null;
		for (int i = 0; i < childrenCount; i++) {
			childNode = (DefaultMutableTreeNode) node.getChildAt(i);
			childComp = (CompDef) childNode.getUserObject();
			if (childComp.isEditable())
				childComp.setSelected(Boolean.toString(selected));
		}
	}

	public DefaultMutableTreeNode findUserObject(Object obj) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this._tree.getModel().getRoot();
		Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject().equals(obj))
				return node;
		}
		return null;
	}

	public static void main(String[] args) {
		URL url = CheckBoxNodeRenderer.class.getClassLoader().getResource("com/primeton/install/frame/impl/it.gif");
		File file = new File(url.getPath());
		System.out.println("Exist " + file.exists());
	}

	public class NodeActionListener implements ActionListener {
		public NodeActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			String optionCompId = CheckBoxNodeRenderer.this.getNodeKey();
			CompDef comp = ComponentsDefHelper.getCompDef(optionCompId);

			String refCompKeys = comp.getDependedBy();
			if (refCompKeys != null) {
				StringTokenizer stoken = new StringTokenizer(refCompKeys);
				CompDef refComp = null;
				while (stoken.hasMoreTokens()) {
					refComp = ComponentsDefHelper.getCompDef(stoken.nextToken());
					if (refComp != null) {
						refComp.setSelected(Boolean.toString(comp.isSelected()));
					}
				}
			}

			DefaultMutableTreeNode currentNode = CheckBoxNodeRenderer.this.findUserObject(comp);
			if (currentNode != null) {
				CheckBoxNodeRenderer.this.syncParentNode(currentNode, comp.isSelected());
				CheckBoxNodeRenderer.this.syncChildrenNode(currentNode, comp.isSelected());
			}
			CheckBoxNodeRenderer.this.chooseChangeListener.actionPerformed(e);
		}
	}
}