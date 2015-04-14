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
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.crosswalk.eclipse.cdt.CdtConstants;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;




public class NewPackagedWizard extends Wizard implements INewWizard {
	static final String DefaultEntryFileContent = "<html><body><p>Welcome to Crosswalk!</p></body></html>";
	private NewProjectWizardState nProjectWizardState;
	PackagedManifestSettingPage packagedManifestSettingPage;
	NewPackagedPage newPage;
	private IProject nProject;
	String iconName;

	
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
			

			//copy the generated files into workspace
			String packageName = CdtConstants.CROSSWALK_PACKAGE_PREFIX + nProjectWizardState.applicationName;
			String resourceFolder = root.getLocation().toString() + File.separator + packageName + File.separator + "app";
			Path sourceIndexFile = FileSystems.getDefault().getPath(resourceFolder , "index.html");
			if(!nProjectWizardState.iconPathChanged || nProjectWizardState.useDefaultIcon){//copy the default icons for app if using default icon.
				Path sourceIconFile1 = FileSystems.getDefault().getPath(resourceFolder , "icon-48.png");
				Path sourceIconFile2 = FileSystems.getDefault().getPath(resourceFolder , "icon.png");
				Path targetIconFile1 = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "icon-48.png");
				Path targetIconFile2 = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "icon.png");
				Files.copy(sourceIconFile1, targetIconFile1, REPLACE_EXISTING);
				Files.copy(sourceIconFile2, targetIconFile2, REPLACE_EXISTING);
			}
			
			
			Path sourceManifestFile = FileSystems.getDefault().getPath(resourceFolder , "manifest.json");
			Path targetManifestFile = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "manifest.json");
			Path targetIndexFile = FileSystems.getDefault().getPath(nProject.getLocation().toString(),"index.html");
			
			Files.copy(sourceIndexFile, targetIndexFile, REPLACE_EXISTING);
			Files.copy(sourceManifestFile, targetManifestFile, REPLACE_EXISTING);
			
			
			
			//copy the user specified icon into workspace
			
			
			
			//modify  manifest.json 
			String manifestLocation = targetManifestFile.toString();
			JSONObject manifest = new JSONObject(new JSONTokener(
					new FileReader(manifestLocation)));

			String applicationName = nProjectWizardState.applicationName;
			manifest.put("name", applicationName);		
			manifest.put("xwalk_version", nProjectWizardState.xwalkVersion);
			manifest.put("start_url", nProjectWizardState.startUrl);
			
			
			
//			manifest.getJSONObject("app").put(
//					"launch",
//					new JSONObject().put("web_url",
//							nProjectWizardState.hostedLaunchUrl));
//			manifest.remove("start_url");
			
			String iconSize = nProjectWizardState.iconSize;
			JSONArray icons = manifest.getJSONArray("icons");
			
			if(nProjectWizardState.iconPathChanged && !nProjectWizardState.useDefaultIcon){
				CdtPluginLog.logInfo("Not use the default icon");
				iconName = nProjectWizardState.favIcon.substring(nProjectWizardState.favIcon.lastIndexOf('/')+1);
				Path userIconPath = FileSystems.getDefault().getPath(nProjectWizardState.favIcon);
				//CdtPluginLog.logInfo("The location of your favorite icon is :" + nProjectWizardState.favIcon);
				
				Path targetIconPath = FileSystems.getDefault().getPath(nProject.getLocation().toString(),iconName);
				Files.copy(userIconPath, targetIconPath,REPLACE_EXISTING);
			
				
				JSONObject newIcon = new JSONObject();
				for(int i=0;i<4;i++){
					newIcon.put("src", iconName);
					newIcon.put("sizes", iconSize);
					newIcon.put("type", "image/png");
					newIcon.put("density", "1.0");	
				}
				icons.put(newIcon);
				//icons.remove(0);
				//icons.remove(1);
			
			
			}

			
			
			
			
			
			PrintWriter out = new PrintWriter(new FileOutputStream(nProject
					.getLocation().toFile()
					+ File.separator + "manifest.json"));
			out.write(manifest.toString(4));
			out.close();
			
			
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
		if (newPage.isPageComplete() && packagedManifestSettingPage.isPageComplete()){
			return true;
		}
		else
			return false;	
	}
}
