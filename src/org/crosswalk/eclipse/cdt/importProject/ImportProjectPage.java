/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.crosswalk.eclipse.cdt.importProject;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.crosswalk.eclipse.cdt.helpers.ProjectHelper;


public class ImportProjectPage extends WizardPage implements SelectionListener,ModifyListener{
	
	
	private Text crosswalkProjectPathText;
	private Button crosswalkProjectPathBrowseButton;
	private static IProject[] crosswalkProjects;
	public String importProjectPath;
	public boolean copyImportProjectToWorkspace;

	protected ImportProjectPage(ImportProjectWizard wizard) {
		super("ImportCrosswalkProject");
		importProjectPath = new String();
		crosswalkProjects = ProjectHelper.getAllProjects();
		setTitle("Import Crosswalk Project");
		setDescription("Import A Crosswalk Project from a folder:");
		Bundle bundle = Platform.getBundle("org.crosswalk.eclipse.cdt");
		Path path = new Path("images/icon-68.png");
		URL imageUrl = FileLocator.find(bundle, path, null);
		setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
		setPageComplete(false);
	}

	
	@Override
	public void createControl(Composite parent) {		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		mainComposite.setLayout(layout);
		Label crosswalkProjectPathLabel = new Label(mainComposite, SWT.NONE);
		crosswalkProjectPathLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		crosswalkProjectPathLabel.setText("Crosswalk Project:");
		crosswalkProjectPathText = new Text(mainComposite, SWT.BORDER);
		crosswalkProjectPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		crosswalkProjectPathText.addModifyListener(this);
		crosswalkProjectPathBrowseButton = new Button(mainComposite, SWT.NONE);
		crosswalkProjectPathBrowseButton.setText("Browse...");
		crosswalkProjectPathBrowseButton.addSelectionListener(this);
		crosswalkProjectPathBrowseButton.setEnabled(true);
		crosswalkProjectPathBrowseButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
	}
	
	private boolean validateCrosswalkProject(String dir) {//Validate the imported project to see if it is a CrosswalkProject 
		// Find manifest.json
		File fd;
		try {
			fd = new File(dir + File.separator + "app");
		}
		catch (Exception e) {
			return false;
		}

		if (fd.isDirectory()) {
			File[] fileList;
			fileList = fd.listFiles(new FilenameFilter() 
			{
				public boolean accept(File dir, String name) {
					return name.startsWith("manifest") && name.endsWith("json");
				}
			});
			if (fileList.length > 0) {
				return true;	
			}
		}
		return false;
	}
	
	private boolean CheckProjectExistInWorkspace(final String projectName) {//Check if the project is already in workspace 
		File fd;
		fd = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		File[] fileList;
		fileList = fd.listFiles(new FilenameFilter() 
		{
			public boolean accept(File dir, String name) {
				return name.startsWith(projectName) && name.endsWith(projectName);
			}
		});
		if (fileList.length > 0)
			return true;	
		else 
			return false;
	}
	
	// ---- Implements SelectionListener ----

	@Override
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (source == crosswalkProjectPathBrowseButton) {
			String dir = promptUserForLocation(getShell(), "Select Crosswalk Project Path");
			if (dir != null) {
				crosswalkProjectPathText.setText(dir);
			}
		}
	}
			
	private String promptUserForLocation(Shell shell, String message) {
		DirectoryDialog dd = new DirectoryDialog(getShell());
		dd.setMessage(message);
		String curLocation;
		String dir;

		curLocation = crosswalkProjectPathText.getText().trim();
		if (!curLocation.isEmpty()) {
			dd.setFilterPath(curLocation);
		} 
				
		dir = dd.open();
		return dir;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
				
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();
		if (source == crosswalkProjectPathText) {
			if (validateCrosswalkProject(crosswalkProjectPathText.getText())) {
				for (int i = 0; i < crosswalkProjects.length; i++) {
					if (crosswalkProjects[i].getLocationURI().getPath().equals(crosswalkProjectPathText.getText())) {
						setMessage("Crosswalk project already exist in workspace", WARNING);
						setPageComplete(false);
						return;
					}
				}
				
				File projectFile = new File(crosswalkProjectPathText.getText());
				if (CheckProjectExistInWorkspace(projectFile.getName()) && !projectFile.getParent().equals(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString())) {
					setMessage("Crosswalk project exist in workspace directory", WARNING);
					setPageComplete(false);
					return;
				}
					
				else {
					setMessage("Crosswalk project found to import");
					importProjectPath = crosswalkProjectPathText.getText();
					setPageComplete(true);
				}
			}
			else {
				setMessage("No crosswalk project is found to import", WARNING);
				setPageComplete(false);
			}
		}
	}
}
