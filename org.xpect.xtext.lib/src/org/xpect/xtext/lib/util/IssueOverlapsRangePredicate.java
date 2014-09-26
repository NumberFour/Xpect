/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.xtext.lib.util;

import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.validation.Issue;

import com.google.common.base.Predicate;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class IssueOverlapsRangePredicate implements Predicate<Issue> {
	private final String document;
	private final int end;
	private final Severity severity;
	private final int start;

	public IssueOverlapsRangePredicate(String document, int start, int end, Severity severity) {
		super();
		this.document = document;
		this.start = start;
		this.end = end;
		this.severity = severity;
	}

	public IssueOverlapsRangePredicate(XtextResource resource, int offset, Severity severity) {
		this.document = resource.getParseResult().getRootNode().getText();
		this.start = this.document.lastIndexOf('\n', offset);
		int endIndex = this.document.indexOf('\n', offset);
		this.end = endIndex >= 0 ? endIndex : this.document.length();
		this.severity = severity;
	}

	public boolean apply(Issue issue) {
		if (severity != null && severity != issue.getSeverity())
			return false;
		Integer offset = issue.getOffset();
		Integer length = issue.getLength();
		if (offset != null && length != null && offset < end && offset + length > start)
			return true;
		return false;
	}

	public String getDocument() {
		return document;
	}

	public int getEnd() {
		return end;
	}

	public Severity getSeverity() {
		return severity;
	}

	public int getStart() {
		return start;
	}

	@Override
	public String toString() {
		String before = this.document.substring(0, start);
		String selection = this.document.substring(start + 1, end);
		String after = this.document.substring(end + 1);
		return before + "\n>>>" + selection + "<<<\n" + after;
	}

}