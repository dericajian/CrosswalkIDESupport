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
import org.json.JSONObject;
import org.json.JSONTokener;
import org.crosswalk.eclipse.cdt.CdtConstants;
import org.crosswalk.eclipse.cdt.CdtPluginLog;
import org.crosswalk.eclipse.cdt.export.DebPackageParameters;
import org.crosswalk.eclipse.cdt.helpers.ProjectHelper;
import org.crosswalk.eclipse.cdt.project.CrosswalkNature;

import static java.nio.file.StandardCopyOption.*;

public class NewHostedWizard extends Wizard implements INewWizard {
	private NewProjectWizardState nProjectWizardState;
	NewHostedPage nHostedPage;
	ManifestSettingPage manifestSettingPage;
	private IProject nProject;
	
	
	public NewHostedWizard() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("New Hosted Crosswalk Application");
		nProjectWizardState = new NewProjectWizardState();
		nHostedPage = new NewHostedPage(nProjectWizardState);
		manifestSettingPage = new ManifestSettingPage(nProjectWizardState);

	}

	public void addPages() {
		super.addPages();
		addPage(nHostedPage);
		addPage(manifestSettingPage);

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

			ProjectHelper projectHelper = new ProjectHelper();
			projectHelper.resourceHandler(root.getLocation().toString());

			
			//move the generated files into workspace
			String packageName = CdtConstants.CROSSWALK_PACKAGE_PREFIX + nProjectWizardState.applicationName;
			String resourceFolder = root.getLocation().toString() + File.separator + packageName + File.separator + "app";
			Path sourceIndexFile = FileSystems.getDefault().getPath(resourceFolder , "index.html");
			Path sourceIconFile1 = FileSystems.getDefault().getPath(resourceFolder , "icon-48.png");
			Path sourceIconFile2 = FileSystems.getDefault().getPath(resourceFolder , "icon.png");
			Path sourceManifestFile = FileSystems.getDefault().getPath(resourceFolder , "manifest.json");
			Path targetManifestFile = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "manifest.json");
			Path targetIndexFile = FileSystems.getDefault().getPath(nProject.getLocation().toString(),"index.html");
			Path targetIconFile1 = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "icon-48.png");
			Path targetIconFile2 = FileSystems.getDefault().getPath(nProject.getLocation().toString(), "icon.png");
			Files.move(sourceIndexFile, targetIndexFile, REPLACE_EXISTING);
			Files.move(sourceManifestFile, targetManifestFile, REPLACE_EXISTING);
			Files.move(sourceIconFile1, targetIconFile1, REPLACE_EXISTING);
			Files.move(sourceIconFile2, targetIconFile2, REPLACE_EXISTING);
			
			
			//copy the user specified icon into workspace
			
			Path userIconPath = FileSystems.getDefault().getPath(nProjectWizardState.favIcon);
			//CdtPluginLog.logInfo("The location of your favrite icon is :" + nProjectWizardState.favIcon);
			String iconName = nProjectWizardState.favIcon.substring(nProjectWizardState.favIcon.lastIndexOf('/')+1);
			//CdtPluginLog.logInfo("@@@@@@@@@@@@@@@@@@@@@@@@@@icon name is :" + iconName);
			Path targetIconPath = FileSystems.getDefault().getPath(nProject.getLocation().toString(),iconName);
			Files.copy(userIconPath, targetIconPath,REPLACE_EXISTING);
			
			//modify  manifest.json 
			String manifestLocation = targetManifestFile.toString();
			JSONObject manifest = new JSONObject(new JSONTokener(
					new FileReader(manifestLocation)));

			String applicationName = nProjectWizardState.applicationName;
			manifest.put("name", applicationName);
			manifest.put("app", new JSONObject());
			manifest.getJSONObject("app").put(
					"launch",
					new JSONObject().put("web_url",
							nProjectWizardState.hostedLaunchUrl));
			manifest.remove("start_url");
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
