
package org.crosswalk.eclipse.cdt.newProject;


import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.crosswalk.eclipse.cdt.CdtPluginLog;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;



public class HostedManifestSettingPage extends WizardPage implements ModifyListener,SelectionListener,FocusListener{

		private static final int FIELD_WIDTH = 300;
		static final int WIZARD_PAGE_WIDTH = 600;
		private final NewProjectWizardState nProjectWizardState;
		private Text mStartUrlText;
		private Text mVersionText;
		public static Text iconPathText;

		private ControlDecoration iconPathDec;
		private ControlDecoration mVersionDec;
		private ControlDecoration mStartUrlDec;
		private Label mHelpNote;
		private Label mTipLabel;
		private Label iconLabel;
		private Button iconPathbrowserButton;
		private Button useDefaultIcon;
		private Boolean mAppNameCanFinish;
		private Boolean mStartUrlCanFinish;
		private Boolean xwalkVersionChanged;
		private Boolean startUrlChanged;
		//private Boolean iconPathChanged;
		private Files iconFile;
		private String iconSourceMessage;
		
		
		
		
		 HostedManifestSettingPage(NewProjectWizardState values) {
			 super("manifestSetting");
			 xwalkVersionChanged = false;
			 startUrlChanged = false;
//			 iconPathChanged = false;
			 iconSourceMessage = "Select the path of your favourite icon.";
			 nProjectWizardState = values;
			 
			 
				setTitle("Set the manifest for application");
				Bundle bundle = Platform.getBundle("org.crosswalk.eclipse.cdt");
						Path path = new Path("images/icon-68.png");
				URL imageUrl = FileLocator.find(bundle, path, null);
				setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
				setPageComplete(false);
				
		 
		 }

		@Override
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			setControl(container);
			GridLayout gl_container = new GridLayout(4, false);
			gl_container.horizontalSpacing = 10;
			container.setLayout(gl_container);
			
			

			Label versionLabel = new Label(container, SWT.NONE);
			versionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
					false, false, 2, 1));
			versionLabel.setText("xwalk_version:");
			
			mVersionText = new Text(container, SWT.BORDER);
			GridData gdVersionText = new GridData(SWT.FILL, SWT.CENTER, true,
					false, 2, 1);
			gdVersionText.widthHint = FIELD_WIDTH;
			mVersionText.setText("0.0.1");
			mVersionText.setLayoutData(gdVersionText);
			mVersionText.addModifyListener(this);
			mVersionText.addFocusListener(this);
			mVersionDec = createFieldDecoration(mVersionText,
					"The version of Crosswalk. You can use the default value.");

			
			Label startUrlLabel = new Label(container, SWT.NONE);
			startUrlLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 2, 1));
			startUrlLabel.setText("start_url:");
			
			mStartUrlText = new Text(container, SWT.BORDER);
			GridData gdStartUrlText = new GridData(SWT.FILL, SWT.CENTER, true,
					false, 2, 1);
			gdStartUrlText.widthHint = FIELD_WIDTH;
			mStartUrlText.setText("index.html");
			mStartUrlText.setLayoutData(gdStartUrlText);
			mStartUrlText.addModifyListener(this);
			mStartUrlText.addFocusListener(this);
			mStartUrlDec = createFieldDecoration(mStartUrlText,
					"The access point of your application for Crosswalk.You may use the default setting.");
		
			
			
			
			//Set the icon for application
			iconLabel = new Label(container, SWT.NONE);
			iconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
					false, 2, 1));
			iconLabel.setText("icon:");
			iconPathText = new Text(container,SWT.BORDER);
			iconPathText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
			iconPathText.addModifyListener(this);
			iconPathText.setText("icon-48.png");
			iconPathText.addFocusListener(this);
			iconPathText.setEnabled(false);
			iconPathDec = createFieldDecoration(iconPathText,
					"Choose your favourite icon for your application.There is an icon prepared for you, so you can use the defalut one.");
			
			iconPathbrowserButton = new Button(container,SWT.NONE);
			iconPathbrowserButton.setText("Browse...");
			iconPathbrowserButton.addSelectionListener(this);
			iconPathbrowserButton.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
			iconPathbrowserButton.setEnabled(false);
			//iconPathbrowserButton.setVisible(false);
			
			
			useDefaultIcon = new Button(container,SWT.CHECK);
			useDefaultIcon.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,false,4,1));
			useDefaultIcon.setText("Use default icon");
			useDefaultIcon.addSelectionListener(this);
			useDefaultIcon.setSelection(true);
			
			
			
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);

			Label horizontalLine = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
			horizontalLine.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

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




		@Override
		public void widgetSelected(SelectionEvent e) {
			Object source = e.getSource();
			if(source == useDefaultIcon){
				if(useDefaultIcon.getSelection()){
					iconPathText.setEnabled(false);
					//iconPathText.setVisible(false);
					//iconPathbrowserButton.setVisible(false);
					iconPathbrowserButton.setEnabled(false);
					//iconLabel.setVisible(false);
				}else{
//					iconPathText.setVisible(true);
//					iconPathbrowserButton.setVisible(true);
					iconPathbrowserButton.setEnabled(true);
					iconPathText.setEnabled(true);
					//iconLabel.setVisible(true);
				}
				
			}
			else if (source == iconPathbrowserButton) {
				String dir = promptUserForLocation(getShell(), iconPathText, "Select the Path of your favoirte icon");
				if (dir != null) {
					iconPathText.setText(dir);
				}
				
				
				 
			 
			 }
			
		}

		
		
		private String promptUserForLocation(Shell shell, Text textWidget,  String message) {
			 FileDialog fd = new FileDialog(getShell());
			fd.setText("choose your icon");
			String curLocation;
			String dir;

			curLocation = textWidget.getText().trim();
			if (!curLocation.isEmpty()) {
				fd.setFilterPath(curLocation);
			}

			dir = fd.open();
			return dir;
		}
		
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void modifyText(ModifyEvent e) {		
			
			Object source = e.getSource();
			if (source == mStartUrlText) {
				onStartUrlChange();
		}
			if (source == iconPathbrowserButton || source == iconPathText) {
				onIconSourceChange();
		}
		
			CanFinish();
		}
		

		
		public void onStartUrlChange(){
			startUrlChanged = true;//TODO:modify manifest.json file
			
		}
		
		public void onXwalkVersionChange(){
			xwalkVersionChanged = true;
			
		}
		
		
		public void onIconSourceChange(){	//TODO:if icon modified,delete the icon,and create icon 
			String location = iconPathText.getText().trim();
			
			java.nio.file.Path iconPath = FileSystems.getDefault().getPath(location);
			//CdtPluginLog.logInfo("The location you chose is  " + location);
			if(!location.equals("icon-48.png")){
				//CdtPluginLog.logInfo("The location is not equals to default.");
				if (location.length() == 0 || (!Files.exists(iconPath))) {
					// reset canFinish in the wizard.
					iconFile = null;
					setPageComplete(false);
					
					//CdtPluginLog.logInfo("location.length = " + location.length());
					CdtPluginLog.logInfo("~~~~!!!onIconSourceChange() is invoked !!!~~~~~~");
					return;
					}
				else{
					nProjectWizardState.iconPathChanged = true;
					nProjectWizardState.favIcon = location;	//Need more attention
					CdtPluginLog.logInfo("The location of your favrite icon is :" + nProjectWizardState.favIcon);
					setPageComplete(true);
				}
			}		
										
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
		
		
		private void CanFinish() {
			int errorCount = 0;
			String errorMessage = new String("");
			String iconLocation = iconPathText.getText().trim();
			String[] iconLocationArray = iconLocation.split(File.separator);
			java.nio.file.Path iconPath = FileSystems.getDefault().getPath(iconLocation);
			

			if(!(mStartUrlText.getText().equals(nProjectWizardState.startUrl)) || mStartUrlText.getText().length()==0){
				errorCount++;
			}
			else{
				//TODO:modify start_url in manifest
			}
			if(mVersionText.getText().length()==0){
				errorCount++;
			}
			else{
				//TODO:modify xwalk_version in manifest
			}
			
			
			if (errorCount == 0) {
				setPageComplete(true);
			}
			else {
				errorMessage = "Please check the field(s) with * mark, which must meet the requirement.";
				setPageComplete(false); 
				setMessage(errorMessage, WARNING);
			}
		}
		
		
		
		
		public boolean canFlipToNextPage() {
			return false;
		}

		@Override
		public void focusGained(FocusEvent e) {
			// handler for focus gained
			Object source = e.getSource();
			String tip = "";
			if (source == mVersionText) {
				tip = mVersionDec.getDescriptionText();
			} else if (source == mStartUrlText) {
				tip = mStartUrlDec.getDescriptionText();
			}else if (source == iconPathText) {
				tip = iconPathDec.getDescriptionText();
			}

			mTipLabel.setText(tip);
			mHelpNote.setVisible(tip.length() > 0);
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			mTipLabel.setText("");
			mHelpNote.setVisible(false);
			
		}
	}

