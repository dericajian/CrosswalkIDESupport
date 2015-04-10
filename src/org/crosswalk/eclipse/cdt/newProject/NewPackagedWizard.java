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

package org.crosswalk.eclipse.cdt.newProject;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.crosswalk.eclipse.cdt.CdtPluginLog;
import org.crosswalk.eclipse.cdt.helpers.ProjectHelper;
import org.crosswalk.eclipse.cdt.project.CrosswalkNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;




public class NewPackagedWizard extends Wizard implements INewWizard {
	static final String DefaultEntryFileContent = "<html><body><p>Welcome to Crosswalk!</p></body></html>";
	private NewProjectWizardState nProjectWizardState;
	PackagedManifestSettingPage packagedManifestSettingPage;
	NewPackagedPage newPage;
	private IProject nProject;
	String iconName = nProjectWizardState.favIcon.substring(nProjectWizardState.favIcon.lastIndexOf('/')+1);

	
	public NewPackagedWizard() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("New Packaged Crosswalk Application");
		nProjectWizardState = new NewProjectWizardState();
		newPage = new NewPackagedPage(nProjectWizardState);
		packagedManifestSettingPage = new PackagedManifestSettingPage(nProjectWizardState);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(newPage);
		addPage(packagedManifestSettingPage);
	}

	@Override
	public boolean performFinish() {	//True to indicate the finish request is accepted.
		try {
			// create the web staff here
			// ---- create the nProject in workspace ----
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			nProject = root.getProject(nProjectWizardState.projectName);
			nProject.create(null);
			nProject.open(IResource.BACKGROUND_REFRESH, null);
			
			ProjectHelper projectHelper = new ProjectHelper();
			projectHelper.resourceHandler(root.getLocation().toString());
			
			// add crosswalk nature to the nProject
			CrosswalkNature.setupProjectNatures(nProject, null);

		} catch (Exception e) {
			CdtPluginLog.logError(e);
			return false;
		}
		return true;
	}

	static String[] mapToStringArray(Map<String, String> map) {
		final String[] strings = new String[map.size()];
		int i = 0;
		for (Map.Entry<String, String> e : map.entrySet()) {
			strings[i] = e.getKey() + '=' + e.getValue();
			i++;
		}
		return strings;
	}
	
	public IWizardPage getNextPage(IWizardPage currentPage) {
		return newPage;
	}

	public boolean canFinish() {	//whether the Finish button is valid 
		if (newPage.isPageComplete())
			return true;
		else
			return false;	
	}
}
