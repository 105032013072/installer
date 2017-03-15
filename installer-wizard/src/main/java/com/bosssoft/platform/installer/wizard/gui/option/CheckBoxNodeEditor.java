package com.bosssoft.platform.installer.wizard.gui.option;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import com.bosssoft.platform.installer.core.option.CompDef;
import com.bosssoft.platform.installer.core.option.ComponentsDefHelper;

public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
	CheckBoxNodeRenderer renderer = null;

	ChangeEvent changeEvent = null;
	JTree tree;

	public CheckBoxNodeEditor(JTree tree) {
		this.tree = tree;
		this.renderer = new CheckBoxNodeRenderer(tree);
	}

	public CheckBoxNodeEditor(JTree tree, ActionListener chooseListener) {
		this.tree = tree;
		this.renderer = new CheckBoxNodeRenderer(tree, chooseListener);
	}

	public Object getCellEditorValue() {
		CompDef optionComp = null;
		Component comp = this.renderer.getCurrentComponent();
		if ((comp instanceof JCheckBox)) {
			JCheckBox checkbox = (JCheckBox) comp;

			optionComp = ComponentsDefHelper.getCompDef(this.renderer.getNodeKey());
			optionComp.setSelected(Boolean.toString(checkbox.isSelected()));
		} else if ((comp instanceof JLabel)) {
			JLabel label = (JLabel) comp;
			optionComp = ComponentsDefHelper.getCompDef(this.renderer.getNodeKey());
		}
		return optionComp;
	}

	public boolean isCellEditable(EventObject event) {
		boolean returnValue = false;
		if ((event instanceof MouseEvent)) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = this.tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null) {
				Object node = path.getLastPathComponent();
				if ((node != null) && ((node instanceof DefaultMutableTreeNode))) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
					Object userObject = treeNode.getUserObject();
					if ((userObject instanceof CompDef)) {
						CompDef comp = (CompDef) userObject;
						returnValue = comp.isEditable();
					}
				}
			}
		}
		return returnValue;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
		Component editor = this.renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);

		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				if (CheckBoxNodeEditor.this.stopCellEditing())
					CheckBoxNodeEditor.this.fireEditingStopped();
			}
		};
		if ((editor instanceof JCheckBox)) {
			((JCheckBox) editor).addItemListener(itemListener);
		}
		return editor;
	}
}