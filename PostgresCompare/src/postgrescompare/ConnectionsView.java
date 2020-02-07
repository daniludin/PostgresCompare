package postgrescompare;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ConnectionsView extends ViewPart {
	Label labelLeftTitle;
	Label labelLeft1;
	Label labelLeft2;
	Label labelLeft3;
	Text txtUrlLeft;
	Text txtUsernameLeft;
	Text txtPasswordLeft;

	Label labelRightTitle;
	Label labelRight1;
	Label labelRight2;
	Label labelRight3;
	Text txtUrlRight;
	Text txtUsernameRight;
	Text txtPasswordRight;
	Action addCompareAction;
	Action addTestConnectionAction;
	String KEY_LEFT_1 = "urlleft";
	String KEY_LEFT_2 = "usernameleft";
	String KEY_LEFT_3 = "passwordleft";
	String KEY_RIGHT_1 = "urlright";
	String KEY_RIGHT_2 = "usernameright";
	String KEY_RIGHT_3 = "passwordright";
	IMemento memento;

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		System.out.println("init");
//		init(site);
//		this.memento = memento; 
	}

	@Override
	public void saveState(IMemento memento) {
		this.memento = memento.createChild("urlsleft", txtUrlLeft.getText());
		savePluginSettings();
	}
	@Override
	public void dispose()
	{
		System.out.println("dispose");
		//savePluginSettings();
	  super.dispose();
	}

	private void restoreState() {
		if (memento == null) {
			return;
		}
		memento = memento.getChild("urlsleft");
		if (memento != null) {
			IMemento descriptors[] = memento.getChildren("descriptor");
			if (descriptors.length > 0) {
				ArrayList objList = new ArrayList(descriptors.length);
				for (int nX = 0; nX < descriptors.length; nX++) {
					String id = descriptors[nX].getID();
//					Word word = input.find(id);
//					if (word != null) {
//						objList.add(word);
//					}
				}
//				viewer.setSelection(new StructuredSelection(objList));
			}
		}
		memento = null;
		updateActionEnablement();
	}

	public ConnectionsView() {
		super();
	}

//	public void init(IViewSite site) throws PartInitException {
//		super.init(site);
//		// Normally we might do other stuff here.
//	}

	public void setFocus() {
		labelLeft1.setFocus();
	}

	public void createPartControl(Composite parent) {

		Canvas baseCanvas = new Canvas(parent, SWT.FILL);
		baseCanvas.setBackground(new Color(Display.getDefault(), 0, 239, 239));
		baseCanvas.setLayout(new GridLayout(2, false));

		// Database A
		labelLeftTitle = new Label(baseCanvas, 0);
		labelLeftTitle.setText("Postgres Datenbank A");
		GridData gridDataL = new GridData();
		gridDataL.horizontalAlignment = GridData.FILL;
		gridDataL.horizontalSpan = 2;
		labelLeftTitle.setLayoutData(gridDataL);

		labelLeft1 = new Label(baseCanvas, 0);
		labelLeft1.setText("Connection URL");

		txtUrlLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataTextL = new GridData(GridData.BEGINNING);
		gridDataTextL.horizontalAlignment = GridData.FILL;
		gridDataTextL.widthHint = 300;
		gridDataTextL.minimumWidth = 300;
		txtUrlLeft.setLayoutData(gridDataTextL);

		labelLeft2 = new Label(baseCanvas, 0);
		labelLeft2.setText("Username");

		txtUsernameLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataUsernameL = new GridData(GridData.BEGINNING);
		gridDataUsernameL.horizontalAlignment = GridData.FILL;
		gridDataUsernameL.widthHint = 300;
		gridDataUsernameL.minimumWidth = 300;
		txtUsernameLeft.setLayoutData(gridDataUsernameL);

		labelLeft3 = new Label(baseCanvas, 0);
		labelLeft3.setText("Passwort");

		txtPasswordLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataPasswordL = new GridData(GridData.BEGINNING);
		gridDataPasswordL.horizontalAlignment = GridData.FILL;
		gridDataPasswordL.widthHint = 300;
		gridDataPasswordL.minimumWidth = 300;
		txtPasswordLeft.setLayoutData(gridDataPasswordL);

		// Database B
		labelRightTitle = new Label(baseCanvas, 0);
		labelRightTitle.setText("Postgres Datenbank B");
		GridData gridDataR = new GridData();
		gridDataR.verticalIndent = 30;
		gridDataR.horizontalAlignment = GridData.FILL;
		gridDataR.horizontalSpan = 2;
		labelRightTitle.setLayoutData(gridDataR);

		labelRight1 = new Label(baseCanvas, 0);
		labelRight1.setText("Connection URL");

		txtUrlRight = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataTextR = new GridData(GridData.BEGINNING);
		gridDataTextR.horizontalAlignment = GridData.FILL;
		gridDataTextR.widthHint = 300;
		gridDataTextR.minimumWidth = 300;
		txtUrlRight.setLayoutData(gridDataTextR);

		labelRight2 = new Label(baseCanvas, 0);
		labelRight2.setText("Username");

		txtUsernameRight = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataUsernameR = new GridData(GridData.BEGINNING);
		gridDataUsernameR.horizontalAlignment = GridData.FILL;
		gridDataUsernameR.widthHint = 300;
		gridDataUsernameR.minimumWidth = 300;
		txtUsernameRight.setLayoutData(gridDataUsernameR);

		labelRight3 = new Label(baseCanvas, 0);
		labelRight3.setText("Passwort");

		txtPasswordRight = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataPasswordR = new GridData(GridData.BEGINNING);
		gridDataPasswordR.horizontalAlignment = GridData.FILL;
		gridDataPasswordR.widthHint = 300;
		gridDataPasswordR.minimumWidth = 300;
		txtPasswordRight.setLayoutData(gridDataPasswordR);


//		Canvas buttonCanvas = new Canvas(baseCanvas, SWT.NONE);
//		GridLayout glButtonCanvas = new GridLayout();
//		GridData gdButtonCanvas = new GridData();
//		gdButtonCanvas.horizontalAlignment = SWT.CENTER;
//		gdButtonCanvas.verticalAlignment = SWT.BEGINNING;
//		glButtonCanvas.numColumns = 3;
//		buttonCanvas.setSize(SWT.DEFAULT, 200);
//
//		buttonCanvas.setLayoutData(gdButtonCanvas);
//		buttonCanvas.setLayout(glButtonCanvas);
//
//		Button btn1 = new Button(buttonCanvas, SWT.BORDER);
//		btn1.setText("Test Connection");
//		btn1.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				switch (e.type) {
//				case SWT.Selection:
//					saveCstProfile();
//					break;
//				}
//			}
//		});
		createActions();
		createToolbar();
		
//		restoreState();
		loadPluginSettings();
	}

	private void saveCstProfile() {
		System.out.println("btn1 clicked");
	}

	public void createActions() {
		addCompareAction = new Action("Compare Databases") {
			public void run() {
				compare();
			}
		};
		addCompareAction.setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin("PostgresCompare", "icons/twowaycompare_co.gif"));

		addTestConnectionAction = new Action("Test Database Connections") {
			public void run() {
				testConnections();
			}
		};
		addTestConnectionAction.setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin("PostgresCompare", "icons/ftpconnecting.gif"));
	}

	/**
	 * Create toolbar.
	 */
	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(addCompareAction);
		mgr.add(addTestConnectionAction);
		// mgr.add(deleteItemAction);
	}

	private void updateActionEnablement() {
//        IStructuredSelection sel = 
//                (IStructuredSelection)viewer.getSelection();
		// deleteItemAction.setEnabled(sel.size() > 0);
	}

	private void compare() {
		System.out.println("compare clicked");

	}

	private void testConnections() {
		System.out.println("testConnections clicked");
		savePluginSettings();
		ReadDatabaseStructure rds = new ReadDatabaseStructure();
		StringBuffer sbLeft = rds.readDbStructure(this.txtUrlLeft.getText(), this.txtUsernameLeft.getText(), this.txtPasswordLeft.getText());
		System.out.println("-- Left Database");
		System.out.println(sbLeft.toString());
	}

	

	private void savePluginSettings() {
		// saves plugin preferences at the workspace level
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("PostgresCompare");

		prefs.put(KEY_LEFT_1, this.txtUrlLeft.getText());
		prefs.put(KEY_LEFT_2, this.txtUsernameLeft.getText());
		prefs.put(KEY_LEFT_3, this.txtPasswordLeft.getText());
		prefs.put(KEY_RIGHT_1, this.txtUrlRight.getText());
		prefs.put(KEY_RIGHT_2, this.txtUsernameRight.getText());
		prefs.put(KEY_RIGHT_3, this.txtPasswordRight.getText());

		try {
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void loadPluginSettings() {
		IEclipsePreferences prefs = new InstanceScope().getNode("PostgresCompare");
		// you might want to call prefs.sync() if you're worried about others changing
		// your settings
		this.txtUrlLeft.setText(prefs.get(KEY_LEFT_1, "default url"));
		this.txtUsernameLeft.setText(prefs.get(KEY_LEFT_2, "default url"));
		this.txtPasswordLeft.setText(prefs.get(KEY_LEFT_3, "default url"));
		this.txtUrlRight.setText(prefs.get(KEY_RIGHT_1, "default url"));
		this.txtUsernameRight.setText(prefs.get(KEY_RIGHT_2, "default url"));
		this.txtPasswordRight.setText(prefs.get(KEY_RIGHT_3, "default url"));
	}

}
