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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.crosswalk.eclipse.cdt.project.CrosswalkNature;

public class ImportProjectWizard extends Wizard implements IImportWizard {
	
	private ImportProjectPage importProjectPage;
	private IProject project;
	public ImportProjectWizard() {
		super();
	}

	
	private void cpProject(File src, File dst) throws IOException {
	    if(src.isDirectory()) {
	        if(!dst.exists())
	            dst.mkdir();
	        String[] files = src.list();
	        for(int i = 0; i < files.length; i++) {
	        	cpProject(new File(src, files[i]), new File(dst, files[i]));
	        }
	    }
	    else {
	        InputStream inStream = new FileInputStream(src);
	        OutputStream outStream = new FileOutputStream(dst);
	        try {
	            byte[] buffer = new byte[1024];
	            int len;
	            while((len = inStream.read(buffer)) > 0) {
	            	outStream.write(buffer, 0, len);
	            }
	        }
	        finally {
	        	inStream.close();
	            outStream.close();
	        }
	    }
	}
	
	
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		File projectSrc = new File(importProjectPage.importProjectPath);
		String projectName = projectSrc.getName();
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		
		String projectDstPath = new String(workspacePath);
		File projectDst = new File(projectDstPath, projectName);
		
		if (!projectSrc.getAbsolutePath().equals(projectDst.getAbsolutePath())) {
			try {
				cpProject(projectSrc, projectDst);
			}
			catch (IOException e) {
				System.out.println(e);
			}
		}
			
			// Create project
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject(projectName);
			try {
				project.create(null);
				project.open(IResource.BACKGROUND_REFRESH, null);
				// add crosswalk nature to the project
				CrosswalkNature.setupProjectNatures(project, null);
			}
			catch (Exception e) {
				System.out.println(e);
			}	
			
	return true;	
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(" Import Crosswalk Project"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		importProjectPage = new ImportProjectPage(this); //NON-NLS-1
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(importProjectPage);        
    }

}
