package com.bosssoft.platform.installer.core.initdb.script;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;

public class OracleScript extends SqlScript {
	protected String getProcedureStatement(BufferedReader r) throws IOException {
		StringBuffer procedureContent = new StringBuffer();
		String lineContent = null;
		String trimedLine = null;
		int contentPosition = -1;

		while ((lineContent = r.readLine()) != null) {
			if ((contentPosition = lineContent.indexOf("--")) != -1) {
				if (contentPosition == 0)
					lineContent = "";
				else
					lineContent = lineContent.substring(0, contentPosition - 1);
				trimedLine = lineContent.trim();
			}

			if ((contentPosition = lineContent.indexOf("/*")) != -1) {
				lineContent = lineContent.substring(0, contentPosition - 1);
				trimedLine = lineContent.trim();
				if (!trimedLine.equals("")) {
					procedureContent.append(lineContent + "\n");
					do {
						lineContent = r.readLine();
						if (lineContent == null)
							break;
					} while ((contentPosition = lineContent.indexOf("*/")) == -1);

					lineContent = lineContent.substring(contentPosition + 2, lineContent.length());
				}

			}

			trimedLine = lineContent.trim();
			if (!trimedLine.equals("")) {
				procedureContent.append(lineContent + "\n");
			}
		}
		return procedureContent.toString();
	}

	protected String getNextStatement(BufferedReader r) throws IOException {
		boolean isFinish = false;
		boolean inString = false;
		boolean isComment = false;

		int i = 0;
		int count = 0;
		int lineLength = 0;

		String line = null;

		CharArrayWriter caw = new CharArrayWriter();

		while (!isFinish) {
			line = r.readLine();

			if (line == null) {
				isFinish = true;
			} else {
				line = line.trim();
				lineLength = line.length();

				i = 0;

				while (i < lineLength) {
					char c = line.charAt(i);

					if (isComment) {
						if ((c == '*') && (i < lineLength - 1) && (line.charAt(i + 1) == '/')) {
							isComment = false;
							i += 2;
						} else {
							i++;
						}

					} else {
						if (c == '\'') {
							caw.write(c);

							if (inString) {
								count++;

								if (count >= 2)
									count = 0;
							} else {
								inString = true;

								count = 0;
							}
						} else {
							if ((inString) && (count == 1)) {
								inString = false;
							}

							if (!inString) {
								if (c == '/') {
									if (i == lineLength - 1) {
										isFinish = true;
										break;
									}
									if (line.charAt(i + 1) == '*')
										isComment = true;
								} else {
									if (c == ';') {
										isFinish = true;
										break;
									}
									if (c == '-') {
										if ((i < lineLength - 1) && (line.charAt(i + 1) == '-')) {
											break;
										}

										caw.write(c);
									} else {
										caw.write(c);
									}
								}
							} else
								caw.write(c);
						}

						i++;
					}
				}
			}
			caw.write(32);
		}

		if (line == null) {
			return null;
		}

		if (caw.size() < 1) {
			return new String("");
		}
		return caw.toString();
	}
}