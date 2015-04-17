package org.crosswalk.eclipse.cdt.newProject;


import java.awt.Font;
import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;






import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class PackagedManifestSettingPage extends WizardPage implements ModifyListener,SelectionListener,FocusListener{ 
	
	private static final int FIELD_WIDTH = 300;
	static final int WIZARD_PAGE_WIDTH = 600;
	private final NewProjectWizardState nProjectWizardState;
	private Text startUrlText;
	private Text xwalkVersionText;
	public static Text iconPathText;
	//public static Text iconSizeText;
	public static Text iconHeightText;
	public static Text iconWidthText;
	private ControlDecoration iconPathDec;
	private ControlDecoration versionDec;
	private ControlDecoration startUrlDec;
	private ControlDecoration iconSizeDec1;
	private ControlDecoration iconSizeDec2;
	private Label helpNote;
	private Label tipLabel;
	private Label iconLabel;
	private Label iconSizeLabel;
	private Button iconPathBrowser;
	private Button useDefaultIcon;
	private Button startUrlBrowser;
	private Boolean appNameCanFinish;
	private Boolean startUrlCanFinish;
	private Boolean xwalkVersionChanged;
	private Boolean startUrlChanged;
	private Files iconFile;
	private String iconSourceMessage;
	
	
	
	
	 PackagedManifestSettingPage(NewProjectWizardState values) {
		 super("manifestSetting");
		 xwalkVersionChanged = false;
		 startUrlChanged = false;
		 iconSourceMessage = "Select the path of your favourite icon.";
		 nProjectWizardState = values;
		 
		 
			setTitle("Set the manifest for application");
			Bundle bundle = Platform.getBundle("org.crosswalk.eclipse.cdt");
					Path path = new Path("images/icon-68.png");
			URL imageUrl = FileLocator.find(bundle, path, null);
			setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
			setPageComplete(true);
			
	 
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
		
		xwalkVersionText = new Text(container, SWT.BORDER);
		GridData gdVersionText = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		gdVersionText.widthHint = FIELD_WIDTH;
		xwalkVersionText.setText("0.0.1");
		xwalkVersionText.setLayoutData(gdVersionText);
		xwalkVersionText.addModifyListener(this);
		xwalkVersionText.addFocusListener(this);
		versionDec = createFieldDecoration(xwalkVersionText,
				"The version of Crosswalk. It must be between 1-4 dot-separated integers each between 0 and 65535");

		
		Label startUrlLabel = new Label(container, SWT.NONE);
		startUrlLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 2, 1));
		startUrlLabel.setText("start_url:");
		
		startUrlText = new Text(container, SWT.BORDER);
		GridData gdStartUrlText = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gdStartUrlText.widthHint = FIELD_WIDTH;
		startUrlText.setText("index.html");
		startUrlText.setLayoutData(gdStartUrlText);
		startUrlText.addModifyListener(this);
		startUrlText.addFocusListener(this);
		startUrlText.setEnabled(true);
		startUrlDec = createFieldDecoration(startUrlText,
				"The access point of your application.It must be a html file.");
	
		startUrlBrowser = new Button(container,SWT.NONE);
		startUrlBrowser.addSelectionListener(this);
		startUrlBrowser.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		startUrlBrowser.setEnabled(true);
		startUrlBrowser.setText("Browse...");
		
		//Set the icon for application
		iconLabel = new Label(container, SWT.NONE);
		iconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 2, 1));
		iconLabel.setText("icon:");
		iconPathText = new Text(container,SWT.BORDER);
		iconPathText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		iconPathText.setText("icon-48.png");
		iconPathText.addModifyListener(this);
		iconPathText.addFocusListener(this);
		iconPathText.setEnabled(false);
		iconPathDec = createFieldDecoration(iconPathText,
				"Icon of your application.There is an icon prepared for you, so you can use the defalut one.");
		
		iconPathBrowser = new Button(container,SWT.NONE);
		iconPathBrowser.setText("Browse...");
		iconPathBrowser.addSelectionListener(this);
		iconPathBrowser.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		iconPathBrowser.setEnabled(false);
		
		iconSizeLabel = new Label(container, SWT.NONE);
		iconSizeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 2, 1));
		iconSizeLabel.setText("icon size:");

		Composite container2 = new Composite(container,SWT.NULL);
		setControl(container2);
		container2.setLayout(gl_container);
		iconHeightText = new Text(container2,SWT.BORDER);
		iconHeightText.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,false,1,2));
		iconHeightText.setText("48");
		iconHeightText.addModifyListener(this);
		iconHeightText.addFocusListener(this);
		iconHeightText.setEnabled(false);
		
		Label sizeOperator = new Label(container2,SWT.NONE);
		sizeOperator.setText("x");
		
		iconWidthText = new Text(container2,SWT.BORDER);
		iconWidthText.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,2));
		iconWidthText.setText("48");
		iconWidthText.addModifyListener(this);
		iconWidthText.addFocusListener(this);
		iconWidthText.setEnabled(false);
		iconSizeDec1 = createFieldDecoration(iconHeightText,"The height of your icon.The value must be between 10 and 999).");
		iconSizeDec2 = createFieldDecoration(iconWidthText,"The width of your icon.The value must be between 10 and 999)");
		
		
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

		helpNote = new Label(container, SWT.NONE);
		helpNote.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1));
		helpNote.setText("Note:");
		helpNote.setVisible(false);

		tipLabel = new Label(container, SWT.WRAP);
		tipLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));

		// Reserve space for 4 lines
		tipLabel.setText("\n\n\n\n"); //$NON-NLS-1$

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
	public void widgetSelected(SelectionEvent e) {			//in answer to Buttons(useDefaultIcon/iconPathBrowser/startUrlBrowser)
		Object source = e.getSource();
		if(source == useDefaultIcon ){
			if(useDefaultIcon.getSelection()){	//use default icon
					iconPathText.setEnabled(false);
					iconPathBrowser.setEnabled(false);
					iconHeightText.setEnabled(false);
					iconWidthText.setEnabled(false);
					nProjectWizardState.useDefaultIcon = true;
			}
			else{	//not use default icon
					iconPathBrowser.setEnabled(true);
					iconPathText.setEnabled(true);
					iconHeightText.setEnabled(true);
					iconWidthText.setEnabled(true);
					nProjectWizardState.useDefaultIcon = false;	
				}
			
		}        
		else if (source == iconPathBrowser) {
			String dir = promptUserForLocation(getShell(), iconPathText, "Select the Path of your favoirte icon");
			if (dir != null) {
				iconPathText.setText(dir);
				onIconSourceChange();
			}		 
		 }
		else if (source == startUrlBrowser) {
			String dir = promptUserForLocation(getShell(), startUrlText, "Select the launch file of your app");
			if (dir != null) {
				startUrlText.setText(dir);
				onStartUrlChange();
			}		 
		 }
		
		CanFinish();
	}

	
	
	private String promptUserForLocation(Shell shell, Text textWidget,  String message) {
		 FileDialog fd = new FileDialog(getShell());
		fd.setText("Specify your own file ");
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
	public void modifyText(ModifyEvent e) {		//in answer to Text(startUrlText/xwalkVersionText/iconSizeText/iconPathText)
		
		Object source = e.getSource();
		if ( source == startUrlText ) {
			onStartUrlChange();
	}
		else if ( source == iconPathText ) {
			onIconSourceChange();
	}
		else if( source == iconHeightText || source == iconWidthText){
			onIconSizeChange();
		}
		else if ( source == xwalkVersionText ){
			onXwalkVersionChange();
		}
	
		CanFinish();
	}
	

	
	
	
	public void onIconSizeChange(){
		if(!isIconSizeValid()){
			setPageComplete(false);
		}
		else{
			nProjectWizardState.iconSize = iconHeightText.getText().trim() + "x" + iconWidthText.getText().trim();	
		}
	}
	
	public void onStartUrlChange(){		
		if(!isStartUrlValid()){
			setPageComplete(false);
		}
		else{
			nProjectWizardState.startUrl = startUrlText.getText().trim();	
			nProjectWizardState.startUrlChanged = true;//TODO:modify manifest.json file
		}
	}
	
	public void onXwalkVersionChange(){
		if(!isXwalkVersionValid()){
				setPageComplete(false);
				
			}
		else{
				nProjectWizardState.xwalkVersion = xwalkVersionText.getText().trim();
				xwalkVersionChanged = true;
			}
		 	
		}

	public void onIconSourceChange(){	//TODO:if icon modified,delete the icon,and create icon 
		if(!isIconPathValid()){
			setPageComplete(false);
		}
		else{
			String location = iconPathText.getText().trim();
			java.nio.file.Path iconPath = FileSystems.getDefault().getPath(location);
			nProjectWizardState.iconPathChanged = true;
			nProjectWizardState.useDefaultIcon = false;
			nProjectWizardState.favIcon = location;	//Need more work			
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

		if(!isIconSizeValid()){
			errorCount++;
			setMessage("Size of icon is not valid,please check the Note", WARNING);
		}
		else if(!isStartUrlValid()){
			errorCount++;
			setMessage("The start_url you specified is not correct,please check the Note", WARNING);
		}
		else if(!isIconPathValid()){
			errorCount++;
			setMessage("The path you specified for icon is not correct,please check the Note", WARNING);
		}
		else if(!isXwalkVersionValid()){
			errorCount++;
			setMessage("The xwalk_version you specified is not correct,please check the Note", WARNING);
		}
		
		if (errorCount != 0) {
			setPageComplete(false); 
		}
		else{
			setMessage("");
			setPageComplete(true);
		}
	}
	
	
	
	
	
	public boolean isIconPathValid(){
		String iconLocation = iconPathText.getText().toString();
		java.nio.file.Path iconPath = FileSystems.getDefault().getPath(iconLocation);
		if(iconLocation.endsWith(".png")){
			if(!iconLocation.equals( "icon-48.png") && (!Files.exists(iconPath)))
				return false;
			else
				return true;
		}
			
		else 
			return false;
	} 
	
	public boolean isStartUrlValid(){
		String startUrlFileLocation = startUrlText.getText().toString();
		java.nio.file.Path startUrlFile = FileSystems.getDefault().getPath(startUrlFileLocation);	
		if(startUrlFileLocation.endsWith(".html")){
			if(!startUrlFileLocation.equals("index.html") && (!Files.exists(startUrlFile)))
				return false;
			else
				return true;
		}
			
		else 
			return false;
	} 
	
	public boolean isXwalkVersionValid(){
		String versionString = xwalkVersionText.getText().toString();		
		String[] versionNumber = versionString.split(".");
		boolean result = false;
		if(versionNumber.length == 3 || versionNumber.length == 4){	//make sure there are 3 segments in xwalk_version,
			for(int i=0; i<versionNumber.length; i++){				//since the tool may goes wrong if it lower than 0.0.1(default value)
				if( (Integer.parseInt(versionNumber[i]) < 0) || (Integer.parseInt(versionNumber[i]) > 65535) ){
										break; 
				}	       
				if(i == versionNumber.length)//all versionNumber are right
					result=true;		
			}
		}		
		return result;				
	}
	
	public boolean isIconSizeValid(){
		String heightInput = iconHeightText.getText().toString();
		String widthInput = iconWidthText.getText().toString();
		Pattern pattern = Pattern.compile("[0-9]{1,3}");
		Matcher matcher1 = pattern.matcher(heightInput);
		Matcher matcher2 = pattern.matcher(widthInput);
		if(!matcher1.matches() || (!matcher2.matches()) || heightInput.equals("0") || widthInput.equals("0")){
			return false;
		}
		else{
			return true;
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
		String tip2 = "You can't change the size of default icon." + "\n" + "You must specify the location of your icon before editing this value .";
		
		if (source == xwalkVersionText) {
			tip = versionDec.getDescriptionText();
		} else if (source == startUrlText) {
			tip = startUrlDec.getDescriptionText();
		}else if (source == iconPathText) {
			tip = iconPathDec.getDescriptionText();
		}else if (source == iconHeightText) {
			tip = iconSizeDec1.getDescriptionText() + "\n" + tip2;
		}else if (source == iconWidthText) {
			tip = iconSizeDec2.getDescriptionText() + "\n" + tip2;
		}

		tipLabel.setText(tip);
		helpNote.setVisible(tip.length() > 0);
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		tipLabel.setText("");
		helpNote.setVisible(false);
		
	}
}

