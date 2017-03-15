package com.bosssoft.platform.installer.wizard.gui.validate.impl;

public class LengthValidator extends AbstractStringValidator {
	private boolean checkMin = true;

	private boolean checkMax = true;

	private Integer min = null;

	private Integer max = null;

	public LengthValidator(int min, int max) {
		this.min = Integer.valueOf(min);
		this.max = Integer.valueOf(max);
	}

	public LengthValidator(boolean r_CheckMin, boolean r_CheckMax, int r_Min, int r_Max) {
		this.checkMin = r_CheckMin;
		this.checkMax = r_CheckMax;
		this.min = Integer.valueOf(r_Min);
		this.max = Integer.valueOf(r_Max);
	}

	protected boolean onValidate(String r_Value) {
		if (r_Value == null) {
			return false;
		}

		return validateLength(r_Value.trim().length());
	}

	public boolean validateLength(int length) {
		boolean t_Result = true;
		if (this.min != null) {
			t_Result = length >= this.min.intValue();
		}

		if ((t_Result) && (this.max != null) && ((this.min == null) || (this.max.intValue() > this.min.intValue()))) {
			t_Result = (t_Result) && (this.checkMax ? length <= this.max.intValue() : length < this.max.intValue());
		}

		return t_Result;
	}

	public int getMax() {
		return this.max.intValue();
	}

	public void setMax(int r_Max) {
		this.max = Integer.valueOf(r_Max);
	}

	public int getMin() {
		return this.min.intValue();
	}

	public void setMin(int r_Min) {
		this.min = Integer.valueOf(r_Min);
	}

	public boolean isCheckMax() {
		return this.checkMax;
	}

	public void setCheckMax(boolean r_CheckMax) {
		this.checkMax = r_CheckMax;
	}

	public boolean isCheckMin() {
		return this.checkMin;
	}

	public void setCheckMin(boolean r_CheckMin) {
		this.checkMin = r_CheckMin;
	}
}