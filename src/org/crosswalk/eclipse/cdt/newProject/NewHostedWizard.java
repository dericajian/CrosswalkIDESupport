package org.crosswalk.eclipse.cdt.newProject;

import org.eclipse.jface.wizard.IWizardPage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.crosswalk.eclipse.cdt.CdtConstants;
import org.crosswalk.eclipse.cdt.CdtPluginLog;
import org.crosswalk.eclipse.cdt.helpers.ProjectHelper;
import org.crosswalk.eclipse.cdt.project.CrosswalkNature;

import static java.nio.file.StandardCopyOption.*;

public class NewHostedWizard extends Wizard implements INewWizard {
	private NewProjectWizardState nProjectWizardState;
	NewHostedPage nHostedPage;
	HostedManifestSettingPage hostedManifestSettingPage;
	private IProject nProject;
	String iconName = NewProjectWizardState.favIcon.substring(NewProjectWizardState.favIcon.lastIndexOf('/')+1);
	String launchUrl;
	public NewHostedWizard() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("New Hosted Crosswalk Application");
		nProjectWizardState = new NewProjectWizardState();
		nHostedPage = new NewHostedPage(nProjectWizardState);
		hostedManifestSettingPage = new HostedManifestSettingPage(nProjectWizardState);

	}

	public void addPages() {
		super.addPages();
		addPage(nHostedPage);
		addPage(hostedManifestSettingPage);
	}

	@Override
	public boolean performFinish() {
		try {

			
			// ---- create the project in workspace ----
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			nProject = root.getProject(nProjectWizardState.projectName);
			nProject.create(null);
			nProject.open(IResource.BACKGROUND_REFRESH
					, null);
			//create the folder for generate app by using crosswalk-app tool.It's under the project folder,and hidden.
			StringBuilder  cmd = new StringBuilder();
			String tmpCreateLocation = nProject.getLocation().toString();
			File tmpFolder = new File(tmpCreateLocation);
			cmd.append("mkdir ").append(".tmp");
			final Map<String, String> env = new HashMap<String, String>(
					System.getenv());
			Process process = Runtime.getRuntime().exec(cmd.toString(),
					mapToStringArray(env), tmpFolder);
			process.waitFor();
			ProjectHelper projectHelper = new ProjectHelper();
			projectHelper.resourceHandler(tmpCreateLocation + File.separator + ".tmp");	//generate the app in a hidden folder named ".tmp" by using crosswalk-app tool.
				
			//copy the generated files into workspace
			String packageName = CdtConstants.CROSSWALK_PACKAGE_PREFIX + NewProjectWizardState.applicationName;
			String resourceFolder = tmpCreateLocation + File.separator + ".tmp" + File.separator + packageName + File.separator + "app";
			Path sourceManifestFile = FileSystems.getDefault().getPath(resourceFolder , "manifest.json");		//copy manifest.json file
			Path targetManifestFile = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "manifest.json");
			Files.copy(sourceManifestFile, targetManifestFile, REPLACE_EXISTING);

			String manifestLocation = targetManifestFile.toString();			//get the manifest file 
			JSONObject manifest = new JSONObject(new JSONTokener(				
					new FileReader(manifestLocation)));

			String applicationName = NewProjectWizardState.applicationName;		//start to modify manifest 
			String iconSize = NewProjectWizardState.iconSize;
			JSONArray icons = manifest.getJSONArray("icons");
			manifest.put("name", applicationName);		
			manifest.put("xwalk_version", NewProjectWizardState.xwalkVersion);
			manifest.remove("start_url");
			manifest.put("app", new JSONObject());
			manifest.getJSONObject("app").put("launch", new JSONObject().put("web_url", nProjectWizardState.hostedLaunchUrl));
			
			if(!NewProjectWizardState.iconPathChanged || NewProjectWizardState.useDefaultIcon){//copy the default icon for app if using default icon.
				Path sourceIconFile = FileSystems.getDefault().getPath(resourceFolder , "icon-48.png");
				Path targetIconFile = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "icon-48.png");
				Path sourceIconFile2 = FileSystems.getDefault().getPath(resourceFolder , "icon.png");
				Path targetIconFile2 = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "icon.png");		
				Files.copy(sourceIconFile, targetIconFile, REPLACE_EXISTING);
				Files.copy(sourceIconFile2, targetIconFile2, REPLACE_EXISTING);
			}
			else if(NewProjectWizardState.iconPathChanged && !NewProjectWizardState.useDefaultIcon){    //copy the specified icon by user into workspace.
				iconName = nProjectWizardState.favIcon.substring(NewProjectWizardState.favIcon.lastIndexOf('/')+1);
				Path userIconPath = FileSystems.getDefault().getPath(NewProjectWizardState.favIcon);
				Path targetIconPath = FileSystems.getDefault().getPath(nProject.getLocation().toString(),iconName);
				Files.copy(userIconPath, targetIconPath,REPLACE_EXISTING);
			
				JSONObject newIcon = new JSONObject();   //add the info of user icon into manifest.json
				for(int i=0;i<4;i++){
					newIcon.put("src", iconName);
					newIcon.put("sizes", iconSize);
					newIcon.put("type", "image/png");
					newIcon.put("density", "1.0");	
				}
				icons.put(newIcon);
				
			}

			
			PrintWriter out = new PrintWriter(new FileOutputStream(nProject
					.getLocation().toFile()
					+ File.separator + "manifest.json"));
			out.write(manifest.toString(4));
			out.close();

			// add crosswalk nature to the project
			CrosswalkNature.setupProjectNatures(nProject, null);

		} catch (Exception e) {
			CdtPluginLog.logError(e);
			return false;
		}
		return true;

	}

	public IWizardPage getNextPage(IWizardPage currentPage) {
		return nHostedPage;
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

	public boolean canFinish() {
		if (nHostedPage.isPageComplete())
			return true;
		else
			return false;
	}

}
