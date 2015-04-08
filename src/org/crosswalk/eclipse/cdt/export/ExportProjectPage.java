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
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;


public class ExportProjectPage extends WizardPage implements ModifyListener,
		SelectionListener {
	
	private static final int WIZARD_PAGE_WIDTH = 300;
	private static final int FIELD_WIDTH = 300;
	private final ExportProjectWizard exportWizard;
	private DebPackageParameters packageParameters;
	private Text destinationPathText;
	private Button destinationbrowserButton;
	private String destinationMessage;
	private Boolean destinationCanFinish;
	private Label packageInfo;
	private Boolean settingCanFinish;


	
	
	

	protected ExportProjectPage(ExportProjectWizard wizard) {
		super("exportCrosswalkApp");                    
		exportWizard = wizard;
		packageParameters = wizard.getPackageParameters();
		destinationMessage = new String("Select a path to export the crosswalk app.");
		setTitle("Export Crosswalk Application");
		setDescription("Export a Crosswalk Application to deb package.");
		Bundle bundle = Platform.getBundle("org.crosswalk.eclipse.cdt");
		Path imagePath = new Path("images/icon-68.png");
		URL imageUrl = FileLocator.find(bundle, imagePath, null);
		setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
		destinationCanFinish = true;
		settingCanFinish = true;
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {   
		Composite mainComposite = new Composite(parent, SWT.NULL);
		setControl(mainComposite);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.horizontalSpacing = 10;
		mainComposite.setLayout(gl_container);


		Label debPackageLabel = new Label(mainComposite, SWT.NONE);
		debPackageLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		debPackageLabel.setText("Package Name:");
		
		
		Label debPackageLabel2 = new Label(mainComposite,SWT.NONE);
		GridData gdDebPackageLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gdDebPackageLabel.widthHint = FIELD_WIDTH;
		debPackageLabel2.setLayoutData(gdDebPackageLabel);
		debPackageLabel2.setText(packageParameters.packageName);
		createFieldDecoration(debPackageLabel2,
				"The package name is used for pack.");

		
		//Set the destination for export
		Label destinationFileLabel = new Label(mainComposite, SWT.NONE);
		destinationFileLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		destinationFileLabel.setText("*Export Destination:");
		destinationPathText = new Text(mainComposite, SWT.BORDER);
		destinationPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, false, 2, 1));
		destinationPathText.setText(packageParameters.currentLocation);
		destinationPathText.addModifyListener(this);
		
		
		
		destinationbrowserButton = new Button(mainComposite, SWT.NONE);
		destinationbrowserButton.setText("Browse...");
		destinationbrowserButton.addSelectionListener(this);
		destinationbrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		destinationbrowserButton.setEnabled(true);
		
		
		Label packageInfoLabel = new  Label(mainComposite, SWT.NONE);
		packageInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 2));
		packageInfo = new Label(mainComposite, SWT.NONE);
		packageInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 2));
		packageInfo.setText("");

		
		CanFinish();

		Label dummy = new Label(mainComposite, SWT.NONE);
		GridData data = new GridData();
		data.horizontalSpan = 4;
		data.widthHint = WIZARD_PAGE_WIDTH;
		dummy.setLayoutData(data);
		
	}


	private ControlDecoration createFieldDecoration(Control control,
			String description) {
		ControlDecoration dec = new ControlDecoration(control, SWT.LEFT);
		dec.setMarginWidth(2);
		FieldDecoration errorFieldIndicator = FieldDecorationRegistry
				.getDefault().getFieldDecoration(
						FieldDecorationRegistry.DEC_INFORMATION);
		dec.setImage(errorFieldIndicator.getImage());
		dec.setDescriptionText(description);
		control.setToolTipText(description);
		return dec;
	}


	void onShow() {
	}

	private void CanFinish() {
		int errorCount;
		Color warningColor = new Color(Display.getCurrent(), 255, 255, 0);
		Color normalColor = new Color(Display.getCurrent(), 255, 255, 255);
		errorCount = 0;
		String errorMessage = new String("");

		
		if (destinationPathText.getText().length() == 0) {
			destinationPathText.setBackground(warningColor);
		}
		else {
			destinationPathText.setBackground(normalColor);
		}

		if (errorCount != 0)
			errorMessage = "The field(s) with * mark can't be empty!";
		
		if ((errorCount == 0) && destinationCanFinish) {
			setPageComplete(true);
			setMessage(destinationMessage);
		}
		else {
			setPageComplete(false); 
			setMessage(destinationMessage + errorMessage, WARNING);
		}
	}
	
	
	// ---- Implements ModifyListener ----

	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();
		if (settingCanFinish == false)
			return;
			if (source == destinationPathText) {
			onDestinationChange();
		}

		CanFinish();
	}

	private void onDestinationChange() {
		String path = destinationPathText.getText().trim();
		if (path.length() == 0) {
			// reset canFinish in the wizard.
			exportWizard.resetDestination();
			packageParameters.targetFolder = "";
			destinationCanFinish = false;
			setPageComplete(false);
			return;
		}

		File file = new File(path);
		if (!file.exists()) {
			destinationMessage = "Selected destination is not available. ";
			// reset canFinish in the wizard.
			exportWizard.resetDestination();
			packageParameters.targetFolder = "";
			destinationCanFinish = true;
			setPageComplete(true);
			return;
		}	
		else {
		
			packageParameters.targetFolder = path;
			exportWizard.setDestination(file);
			destinationCanFinish = true;
			
		}
		
		
	}

	// ---- Implements SelectionListener ----

	@Override
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		 if (source == destinationbrowserButton) {
			String dir = promptUserForLocation(getShell(), destinationPathText, "Select an Export Path");
			if (dir != null) {
				destinationPathText.setText(dir);
			}
		}


	
	}

	

	private String promptUserForLocation(Shell shell, Text textWidget,  String message) {
		DirectoryDialog dd = new DirectoryDialog(getShell());
		dd.setMessage(message);
		String curLocation;
		String dir;

		curLocation = textWidget.getText().trim();
		if (!curLocation.isEmpty()) {
			dd.setFilterPath(curLocation);
		}

		dir = dd.open();
		return dir;
	}



	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public boolean canFlipToNextPage() {
		return false;
	}
}
