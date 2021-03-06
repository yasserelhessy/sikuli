/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide.sikuli_test;

import org.sikuli.ide.SikuliIDE;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;


/**
 * A view presenting the test failures as a list.
 */
public class FailureRunView implements TestRunView {
	JList fFailureList;
	TestRunContext fRunContext;
	
	/**
	 * Renders TestFailures in a JList
	 */
	static class FailureListCellRenderer extends DefaultListCellRenderer {
		private Icon fFailureIcon;
		private Icon fErrorIcon;
		
		FailureListCellRenderer() {
	    		super();
	    		loadIcons();
		}
	
		void loadIcons() {
			fFailureIcon= SikuliIDE.getIconResource("/icons/failure.gif");
			fErrorIcon= SikuliIDE.getIconResource("/icons/error.gif");		
		}
						
		public Component getListCellRendererComponent(
			JList list, Object value, int modelIndex, 
			boolean isSelected, boolean cellHasFocus) {
	
		    Component c= super.getListCellRendererComponent(list, value, modelIndex, isSelected, cellHasFocus);
			TestFailure failure= (TestFailure)value;
			String text= failure.failedTest().toString();
			String msg= failure.exceptionMessage();
			if (msg != null) 
				text+= ":" + BaseTestRunner.truncate(msg); 
	 
			if (failure.isFailure()) { 
				if (fFailureIcon != null)
		    		setIcon(fFailureIcon);
			} else {
		    	if (fErrorIcon != null)
		    		setIcon(fErrorIcon);
		    }
			setText(text);
			setToolTipText(text);
			return c;
		}
	}
	
	public FailureRunView(TestRunContext context) {
		fRunContext= context;
		fFailureList= new JList(fRunContext.getFailures());
		fFailureList.setFont(new Font("Dialog", Font.PLAIN, 12));
 
		fFailureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fFailureList.setCellRenderer(new FailureListCellRenderer());
		fFailureList.setVisibleRowCount(5);

		fFailureList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					testSelected();
				}
			}
		);
	}
	
	public Test getSelectedTest() {
		int index= fFailureList.getSelectedIndex();
		if (index == -1)
			return null;
			
		ListModel model= fFailureList.getModel();
		TestFailure failure= (TestFailure)model.getElementAt(index);
		return failure.failedTest();
	}
	
	public void activate() {
		testSelected();
	}
	
	public void addTab(JTabbedPane pane) {
		JScrollPane scrollPane= new JScrollPane(fFailureList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		Icon errorIcon= SikuliIDE.getIconResource("/icons/error.gif");
		pane.addTab("Failures", errorIcon, scrollPane, "The list of failed tests");
	}
		
	public void revealFailure(Test failure) {
		fFailureList.setSelectedIndex(0);
	}
	
	public void aboutToStart(Test suite, TestResult result) {
	}

	public void runFinished(Test suite, TestResult result) {
	}

	protected void testSelected() {
		fRunContext.handleTestSelected(getSelectedTest());
	}
}
