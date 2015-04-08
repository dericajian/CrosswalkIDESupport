package org.crosswalk.eclipse.cdt.export;

import java.io.File;

import org.crosswalk.eclipse.cdt.CdtConstants;
import org.crosswalk.eclipse.cdt.helpers.*;
import org.eclipse.core.resources.IProject;
import org.json.JSONObject;

public class DebPackageParameters {
	public String packageName;
	public String targetFolder;
	public String appName;
	public String supportedArch;
	public boolean useDefaultIcon = true;
	public String appVersion;
	public String currentLocation;

	public DebPackageParameters(IProject project) {

		JSONObject manifest = ProjectHelper.getManifest(project);
		appName = manifest.getString("name");
		appVersion = manifest.getString("xwalk_version");
		packageName = CdtConstants.CROSSWALK_PACKAGE_PREFIX + appName;
		
		// default targetFolder is the subdirectory in project location named pkg
		targetFolder = project.getLocation().toFile().toString();
		supportedArch = "amd64";
		currentLocation = project.getLocation().toString();
	}
}
