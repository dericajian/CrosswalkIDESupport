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

public class NewProjectWizardState {
	public NewProjectWizardState() {

	}

	/** The name of the project */
	public String projectName;

	/** The application name */
	public static String applicationName ="";
	public static String favIcon = "icon-48.png";
	public static String iconSize = "48x48";
	public String applicationDescription;
	public static boolean useDefaultIcon = true;
	public static boolean iconPathChanged = false;
	public static String startUrl = "index.html";
	public static boolean startUrlChanged = false;
	public static String xwalkVersion = "0.0.1";

	/** Whether the project name has been edited by the user */
	public boolean projectModified;

	/** Whether the application name has been edited by the user */
	public boolean applicationModified;

	/** The location of crosswalk, it means crosswalk-app-template currently */
	public String crosswalkLocation;

//	public String entryFile = "index.html";
	public String hostedLaunchUrl = "https://crosswalk-project.org/";
	
	
	public String customizedIcon;
	//default icon for user.User may change it to their favorite one.
	
	public String iconType = "image/png";
	public String iconDensity = "1.0";
	public String applicationVersion = "0.0.1";
	
}
