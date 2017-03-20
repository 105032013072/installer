package com.bosssoft.platform.installer.core.initdb.script;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;

public class SQLServerScript extends SqlScript {
	protected String getProcedureStatement(BufferedReader r) throws IOException {
		return "";
	}

	protected String getNextStatement(BufferedReader r) throws IOException {
		boolean isFinish = false;
		boolean inString = false;
		boolean isComment = false;

		int i = 0;
		int count = 0;
		int countOfBracket = 0;
		int lineLength = 0;

		String line = null;

		CharArrayWriter caw = new CharArrayWriter();

		while (!isFinish) {
			line = r.readLine();
			if ((line == null) || ("go".equalsIgnoreCase(line.trim()))) {
				isFinish = true;
			} else {
				line.trim();
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
									if (line.charAt(i + 1) == '*') {
										isComment = true;
									}
								} else if (c == ';') {
									if (countOfBracket == 0) {
										isFinish = true;
										break;
									}
									caw.write(c);
								} else if (c == '-') {
									if ((i < lineLength - 1) && (line.charAt(i + 1) == '-')) {
										break;
									}

									caw.write(c);
								} else if (c == '(') {
									countOfBracket++;
									caw.write(c);
								} else if (c == ')') {
									countOfBracket--;
									caw.write(c);
								} else {
									caw.write(c);
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