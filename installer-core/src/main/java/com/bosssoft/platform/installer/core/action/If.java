package com.bosssoft.platform.installer.core.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.ActionsUtil;
import com.bosssoft.platform.installer.core.util.ExpressionParser;

public class If extends AbstractAction {
	private String leftValue = null;
	private String rightValue = null;

	private List<IAction> thenActions = new ArrayList();
	private List<IAction> elseActions = new ArrayList();

	public void execute(IContext context, Map parameters) throws InstallException {
		String lvalue = ExpressionParser.parseString(this.leftValue);
		String rvalue = ExpressionParser.parseString(this.rightValue);
		if (lvalue.equals(rvalue))
			ActionsUtil.run(context, this.thenActions);
		else
			ActionsUtil.run(context, this.elseActions);
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void addThenAction(IAction action) {
		this.thenActions.add(action);
	}

	public void addElseAtion(IAction action) {
		this.elseActions.add(action);
	}

	public void setLeftValue(String v) {
		this.leftValue = v;
	}

	public void setRightValue(String v) {
		this.rightValue = v;
	}
}