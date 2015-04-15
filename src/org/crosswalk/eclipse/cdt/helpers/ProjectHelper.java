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

package org.crosswalk.eclipse.cdt.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.json.JSONException;
import org.json.JSONObject;
import org.crosswalk.eclipse.cdt.CdtConstants;
import org.crosswalk.eclipse.cdt.CdtPluginLog;
import org.crosswalk.eclipse.cdt.export.DebPackageParameters;
import org.crosswalk.eclipse.cdt.newProject.NewProjectWizardState;

public final class ProjectHelper {
	 NewProjectWizardState nProjectWizardState = new NewProjectWizardState();
	public static JSONObject getManifest(IProject project) {
		IFile manifestFile = project.getFile(CdtConstants.MANIFEST_PATH);
		JSONObject manifestObject = null;
		try {
			manifestObject = new JSONObject(
					stream2String(manifestFile.getContents()));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return manifestObject;
	}
	// get all crosswalk projects in the project explorer
	public static IProject[] getCrosswalkProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		ArrayList<IProject> crosswalkProjects = new ArrayList<IProject>();
		for (int i = 0; i < projects.length; i++) {
			if (isCrosswalkProject(projects[i])) {
				crosswalkProjects.add(projects[i]);
			}
		}
		return crosswalkProjects.toArray(new IProject[crosswalkProjects.size()]);
	}
	
	// get all projects in the project explorer
	public static IProject[] getAllProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		ArrayList<IProject> allProjects = new ArrayList<IProject>();
		for (int i = 0; i < projects.length; i++) {
			allProjects.add(projects[i]);
		}
		return allProjects.toArray(new IProject[allProjects.size()]);
	}

	public static boolean isCrosswalkProject(IProject project) {
		// check if it's a Crosswalk project based on its nature
		try {
			return project.hasNature(CdtConstants.NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String stream2String(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		try {
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toString();
	}

	public static boolean saveStringProperty(IResource resource,
			String propertyName, String value) {
		QualifiedName qname = new QualifiedName(CdtConstants.PLUGIN_ID,
				propertyName);

		try {
			resource.setPersistentProperty(qname, value);
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String loadStringProperty(IResource resource,
			String propertyName) {
		QualifiedName qname = new QualifiedName(CdtConstants.PLUGIN_ID,
				propertyName);

		try {
			String value = resource.getPersistentProperty(qname);
			return value;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean saveBooleanProperty(IResource resource,
			String propertyName, boolean value) {
		return saveStringProperty(resource, propertyName,
				Boolean.toString(value));
	}

	public static boolean loadBooleanProperty(IResource resource,
			String propertyName, boolean defaultValue) {
		String value = loadStringProperty(resource, propertyName);
		if (value != null) {
			return Boolean.parseBoolean(value);
		}
		return defaultValue;
	}

	public static Boolean loadBooleanProperty(IResource resource,
			String propertyName) {
		String value = loadStringProperty(resource, propertyName);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return null;
	}

	public static boolean saveResourceProperty(IResource resource,
			String propertyName, IResource value) {
		if (value != null) {
			IPath iPath = value.getFullPath();
			return saveStringProperty(resource, propertyName, iPath.toString());
		}
		return saveStringProperty(resource, propertyName, ""); //$NON-NLS-1$
	}

	public static IResource loadResourceProperty(IResource resource,
			String propertyName) {
		String value = loadStringProperty(resource, propertyName);

		if (value != null && value.length() > 0) {
			return ResourcesPlugin.getWorkspace().getRoot()
					.findMember(new Path(value));
		}
		return null;
	}
	
	
	public  void resourceHandler(String resourceDir){  //create the app under workspace by tool "crosswalk-app"
		File executionFile = new File(resourceDir);
		final Map<String, String> env = new HashMap<String, String>(
				System.getenv());
		StringBuilder cmd = new StringBuilder();
		String packageName = CdtConstants.CROSSWALK_PACKAGE_PREFIX + nProjectWizardState.applicationName;
		CdtPluginLog.logInfo("packageName: " + packageName);
		cmd.append("crosswalk-app create " + packageName);
		Process process;
		try {
			process = Runtime.getRuntime().exec(cmd.toString(),
					mapToStringArray(env), executionFile);
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
}





