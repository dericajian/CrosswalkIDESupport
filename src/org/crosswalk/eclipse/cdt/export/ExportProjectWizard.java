/*
 *  Copyright 2014 Intel Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.crosswalk.eclipse.cdt.export;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.crosswalk.eclipse.cdt.CdtConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.crosswalk.eclipse.cdt.CdtPluginLog;
import org.crosswalk.eclipse.cdt.helpers.ExportHelper;
import org.crosswalk.eclipse.cdt.helpers.ProjectHelper;


public class ExportProjectWizard extends Wizard implements IExportWizard {
	private IProject eProject;
	private String targetFormat;
	private File destFile;
	private DebPackageParameters packageParameters;
	private ExportProjectPage exportProjectPage;
	private ProjectSelectionPage projectSelectionPage;
	private static IProject[] crosswalkProjects;
	int runResult = 0;
	public ExportProjectWizard() {
		setWindowTitle("Export Crosswalk Application");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object selected = selection.getFirstElement();	
		if (selected instanceof IProject) {
		eProject = (IProject) selected;
		} 

		targetFormat = CdtConstants.TARGET_FORMATS[0];
		//set destFile here
		packageParameters = new DebPackageParameters(eProject);
		destFile = new File(packageParameters.currentLocation);
		if (eProject != null) {
			packageParameters = new DebPackageParameters(eProject);
			addPage(exportProjectPage = new ExportProjectPage(this));
		}
		else {	//if there is no selected project,a project selection page will be presented
			crosswalkProjects = ProjectHelper.getCrosswalkProjects();
			if (crosswalkProjects.length == 0) {	
				Status status = new Status(IStatus.ERROR, "Export Error", 0, "No project available to export", null);
				ErrorDialog.openError(getShell(), "error", "Export Project Error!", status);
			}
			addPage(projectSelectionPage = new ProjectSelectionPage("Select Project"));
			return;
		}
	}


	private void showResultDialog(int result) {//Show the result of export 
		MessageDialog dialog;
		if (result == 0) {
			dialog = new MessageDialog(getShell(), "Successful", null, 
					"The Crosswalk app has been exported successfully!", 
					0, new String[] {IDialogConstants.OK_LABEL} ,0);
		}
		else {
			dialog = new MessageDialog(getShell(), "Fail", null,
					"The Crosswalk app export fail.",
					1, new String[] {IDialogConstants.OK_LABEL} ,0);
		}

		switch (dialog.open()) {
			case 0:
				break;
		}
	}


	@Override
	public void addPages() {
	}

	@Override
	public boolean performFinish() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
				throws InvocationTargetException {
					try {		
						monitor.beginTask("Exporting app to: "+ destFile.toString(), 20);
						runResult = ExportHelper.doExport(eProject, targetFormat, destFile, packageParameters, monitor);
						
						
						//delete the folder org.crosswalk.appName	
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
						Path sourceManifestFile = FileSystems.getDefault().getPath(eProject.getLocation().toString(), "manifest.json");	//copy the manifest.json file
						String manifestLocation = sourceManifestFile.toString();	
						JSONObject manifest = new JSONObject(new JSONTokener(				//get the manifest file 
								new FileReader(manifestLocation)));
						
						String packageName = CdtConstants.CROSSWALK_PACKAGE_PREFIX + manifest.get("name");
						Path tmpFolderForGenerate = FileSystems.getDefault().getPath(root.getLocation().toString() + File.separator + packageName);
						if(!root.getProject(eProject.getName()).exists()){
							File forDelete = new File(root.getLocation().toString() + File.separator + packageName);
							deleteDirectory(forDelete);
						}
						
						
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
					monitor.done();
				}
			}
		};
		try {
			dialog.run(false, true, op);
		} catch (Exception e) {
			CdtPluginLog.logError("Failed to export project:" + eProject.getName(), e);
			return false;
		}
		showResultDialog(runResult);
		return true;
	}

	IProject getProject() {
		return eProject;
	}

	void setProject(IProject iProject) {
		eProject = iProject;
	}

	String getTargetFormat() {
		return targetFormat;
	}

	void setTargetFormat(String format) {
		targetFormat = format;
	}

	
	DebPackageParameters getPackageParameters(){
		return packageParameters;
	}

	

	void setPackageParameters(DebPackageParameters parameters){
		packageParameters = parameters;
	}
	
	
	File getDestination() {
		return destFile;
	}

	void resetDestination() {
		destFile = null;
	}

	void setDestination(File destinationFile) {
		destFile = destinationFile;
	}
	
	
	static public boolean deleteDirectory(File path) {
		if(path.exists()){
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++){
				if(files[i].isDirectory()){
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	

	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == projectSelectionPage) {
			eProject = projectSelectionPage.exportProject;
			packageParameters = new DebPackageParameters(eProject);
			addPage(exportProjectPage = new ExportProjectPage(this));
			return exportProjectPage;
		}
		else {
			return exportProjectPage;
		}
	}

	public boolean canFinish() {
		if (getContainer().getCurrentPage() == projectSelectionPage)
			return false;
		else {
			if (exportProjectPage.isPageComplete())
				return true;
			else
				return false;
		}
	}
}
