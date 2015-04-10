package org.crosswalk.eclipse.cdt.newProject;

import java.net.URL;


import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

public class NewHostedPage extends WizardPage implements ModifyListener,SelectionListener,FocusListener{

	private static final int FIELD_WIDTH = 300;
	static final int WIZARD_PAGE_WIDTH = 600;
	private final NewProjectWizardState nHostedWizardState;
	private Text mProjectText;
	private Text mApplicationText;
	private boolean mIgnore;
	private ControlDecoration mApplicationDec;
	private ControlDecoration mProjectDec;
	private Label mHelpNote;
	private Label mTipLabel;
	private Boolean mAppNameCanFinish;
	private Boolean mProjectNameCanFinish;
//	private HostedManifestSettingPage hostedManifestSettingPage;
	
	 NewHostedPage(NewProjectWizardState values) {
		 super("newHostedCrosswalkApp");
			nHostedWizardState = values;
			setTitle("New Hosted Crosswalk Application");
			setDescription("Creates a new Hosted Crosswalk Application");
			Bundle bundle = Platform.getBundle("org.crosswalk.eclipse.cdt");
			Path path = new Path("images/icon-68.png");
			URL imageUrl = FileLocator.find(bundle, path, null);
			setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
			setPageComplete(false);
			mAppNameCanFinish = false;
			mProjectNameCanFinish = false;
			
	}


public void createControl(Composite parent) {
	Composite container = new Composite(parent, SWT.NULL);
	setControl(container);
	GridLayout gl_container = new GridLayout(4, false);
	gl_container.horizontalSpacing = 10;
	container.setLayout(gl_container);

	Label applicationLabel = new Label(container, SWT.NONE);
	applicationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
			false, false, 2, 1));
	applicationLabel.setText("Application Name:");

	mApplicationText = new Text(container, SWT.BORDER);
	GridData gdApplicationText = new GridData(SWT.FILL, SWT.CENTER, true,
			false, 2, 1);
	gdApplicationText.widthHint = FIELD_WIDTH;
	mApplicationText.setLayoutData(gdApplicationText);
	mApplicationText.addModifyListener(this);
	mApplicationText.addFocusListener(this);
	mApplicationDec = createFieldDecoration(mApplicationText,
			"The application name is shown in the Play Store, as well as in the "
					+ "Manage Application list in Settings.");

	
	Label projectLabel = new Label(container, SWT.NONE);
	projectLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
			false, 2, 1));
	projectLabel.setText("Project Name:");
	mProjectText = new Text(container, SWT.BORDER);
	GridData gdProjectText = new GridData(SWT.FILL, SWT.CENTER, true,
			false, 2, 1);
	gdProjectText.widthHint = FIELD_WIDTH;
	mProjectText.setLayoutData(gdProjectText);
	mProjectText.addModifyListener(this);
	mProjectText.addFocusListener(this);
	setMessage("The application name must contain 2 characters at least.");
	
	mProjectDec = createFieldDecoration(
			mProjectText,
			"The project name is only used by Eclipse, but must be unique within the "
					+ "workspace. This can typically be the same as the application name.");

	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);
	new Label(container, SWT.NONE);

	Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
	label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

	mHelpNote = new Label(container, SWT.NONE);
	mHelpNote.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
			1, 1));
	mHelpNote.setText("Note:");
	mHelpNote.setVisible(false);

	mTipLabel = new Label(container, SWT.WRAP);
	mTipLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
			1));

	// Reserve space for 4 lines
	mTipLabel.setText("\n\n\n\n"); //$NON-NLS-1$

	// Reserve enough width to accommodate the various wizard pages up front (since they are
	// created lazily, and we don't want the wizard to dynamically resize itself for small
	// size adjustments as each successive page is slightly larger)
	
	Label dummy = new Label(container, SWT.NONE);
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

// ---- Implements ModifyListener ----




@Override
public void modifyText(ModifyEvent e) {
	if (mProjectText.getText().length() == 0) {
		mProjectNameCanFinish = false;
	}
	else {
		mProjectNameCanFinish = true;
	}

	
	if ( !isAppNameLegal(mApplicationText.getText())) {
		mAppNameCanFinish = false;
		setMessage("Application name must  contain 2 characters at least.");	
	}
	else {
		mAppNameCanFinish = true;
	}
	if (mProjectNameCanFinish && mAppNameCanFinish)
		setPageComplete(true);
	else
		setPageComplete(false);
	if (mIgnore) {
		return;
	}
	Object source = e.getSource();
	if (source == mProjectText) {
		nHostedWizardState.projectName = mProjectText.getText();
		updateProjectLocation(nHostedWizardState.projectName);
		nHostedWizardState.projectModified = true;

		try {
			mIgnore = true;
			if (!nHostedWizardState.applicationModified) {
				nHostedWizardState.applicationName = nHostedWizardState.projectName;
				mApplicationText.setText(nHostedWizardState.projectName);
			}
			// update nHostedWizardState.projectName;
		} finally {
			mIgnore = false;
		}
	} else if (source == mApplicationText) {
		nHostedWizardState.applicationName = mApplicationText.getText();
		nHostedWizardState.applicationModified = true;

		try {
			mIgnore = true;
			if (!nHostedWizardState.projectModified) {
				nHostedWizardState.projectName = appNameToProjectName(nHostedWizardState.applicationName);
				mProjectText.setText(nHostedWizardState.projectName);
				updateProjectLocation(nHostedWizardState.projectName);
			}
			// updateActivityNames(nHostedWizardState.applicationName);
		} finally {
			mIgnore = false;
		}
	}
}




private String appNameToProjectName(String appName) {
	// Strip out whitespace (and capitalize subsequent words where spaces
	// were removed
	boolean upcaseNext = false;
	StringBuilder sb = new StringBuilder(appName.length());
	for (int i = 0, n = appName.length(); i < n; i++) {
		char c = appName.charAt(i);
		if (c == ' ') {
			upcaseNext = true;
		} else if (upcaseNext) {
			sb.append(Character.toUpperCase(c));
			upcaseNext = false;
		} else {
			sb.append(c);
		}
	}

	appName = sb.toString().trim();
	return appName;
}

/**
 * The project should be created in the workspace, then update the
 * project location based on the project name.
 */
private void updateProjectLocation(String projectName) {
	if (projectName == null) {
		projectName = "";
	}
	
}

// ---- Implements SelectionListener ----

@Override
public void widgetSelected(SelectionEvent e) {

}


@Override
public void widgetDefaultSelected(SelectionEvent e) {
	// TODO:
}

// ---- Implements FocusListener ----

@Override
public void focusGained(FocusEvent e) {
	// handler for focus gained
	Object source = e.getSource();
	String tip = "";
	if (source == mApplicationText) {
		tip = mApplicationDec.getDescriptionText();
	} else if (source == mProjectText) {
		tip = mProjectDec.getDescriptionText();
	}

	mTipLabel.setText(tip);
	mHelpNote.setVisible(tip.length() > 0);
}

@Override
public void focusLost(FocusEvent e) {
	mTipLabel.setText("");
	mHelpNote.setVisible(false);
}


public boolean isAppNameLegal(String inputString)
{	
	int leastNameLength = 2;
	if(inputString.length() < leastNameLength)
		return false;
	else 
		return true;
}

public IWizardPage getNextPage()
{    		
	
	HostedManifestSettingPage page = ((NewHostedWizard)getWizard()).hostedManifestSettingPage;
	return page;
}


@Override
public boolean canFlipToNextPage() {
	if(isAppNameLegal(mApplicationText.getText())){
		return true;
	}
	else{
		return false;
	}
	
}


}
