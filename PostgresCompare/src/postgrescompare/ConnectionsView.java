package postgrescompare;

import java.net.MalformedURLException;
import java.net.URL;

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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ConnectionsView extends ViewPart {
	private Label labelLeft1;
	private Label labelLeft2;
	Text txtUrlLeft;
	Action addTestConnectionAction;

	public ConnectionsView() {
		super();
	}

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		// Normally we might do other stuff here.
	}

	public void setFocus() {
		labelLeft1.setFocus();
	}

	public void createPartControl(Composite parent) {

		Canvas baseCanvas = new Canvas(parent, SWT.FILL);
		baseCanvas.setBackground(new Color(Display.getDefault(), 0, 239, 239));
		baseCanvas.setLayout(new GridLayout(2, false));

//		FillLayout glBase = new FillLayout();
//		glBase.type = SWT.VERTICAL;
//		GridData gdBase = new GridData();
//		gdBase.horizontalAlignment = SWT.FILL;
//		gdBase.grabExcessHorizontalSpace = true;
//		baseCanvas.setLayout(glBase);

		Label labelLeftTitle = new Label(baseCanvas, 0);
		labelLeftTitle.setText("Postgres Datenbank A");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		labelLeftTitle.setLayoutData(gridData);

		labelLeft1 = new Label(baseCanvas, 0);
		labelLeft1.setText("Connection URL");

		txtUrlLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataText = new GridData(GridData.BEGINNING);
		gridDataText.horizontalAlignment = GridData.FILL;
		gridDataText.widthHint = 300;
		gridDataText.minimumWidth = 300;
		txtUrlLeft.setLayoutData(gridDataText);

		labelLeft2 = new Label(baseCanvas, 0);
		labelLeft2.setText("Username");

		Text txtUsernameLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataUsername = new GridData(GridData.BEGINNING);
		gridDataUsername.horizontalAlignment = GridData.FILL;
		gridDataUsername.widthHint = 300;
		gridDataUsername.minimumWidth = 300;
		txtUsernameLeft.setLayoutData(gridDataUsername);

		Label labelLeft3 = new Label(baseCanvas, 0);
		labelLeft3.setText("Passwort");

		Text txtPasswordLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataPassword = new GridData(GridData.BEGINNING);
		gridDataPassword.horizontalAlignment = GridData.FILL;
		gridDataPassword.widthHint = 300;
		gridDataPassword.minimumWidth = 300;
		txtPasswordLeft.setLayoutData(gridDataPassword);

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
	}

	private void saveCstProfile() {
		System.out.println("btn1 clicked");
	}

	public void createActions() {
		addTestConnectionAction = new Action("Add...") {
			public void run() {
				addItem();
			}
		};
		//addTestConnectionAction.setImageDescriptor(getImageDescriptor("twowaycompare_co.gif"));
		addTestConnectionAction.setImageDescriptor(
			        AbstractUIPlugin.imageDescriptorFromPlugin("PostgresCompare", "icons/twowaycompare_co.gif"));
		 // Add selection listener.
//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				updateActionEnablement();
//			}
//		});
	}
    /**
     * Create menu.
     */
    
    /**
     * Create toolbar.
     */
    private void createToolbar() {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
            mgr.add(addTestConnectionAction);
            //mgr.add(deleteItemAction);
    }

	private void updateActionEnablement() {
//        IStructuredSelection sel = 
//                (IStructuredSelection)viewer.getSelection();
		// deleteItemAction.setEnabled(sel.size() > 0);
	}
	private void addItem() {
		System.out.println("addItem clicked");

	}
	/**
	 * Returns the image descriptor with the given relative path.
	 */
	private ImageDescriptor getImageDescriptor(String relativePath) {
		String iconPath = "icons/";
		try {
			UIPlugin plugin = UIPlugin.getDefault();
			URL installURL = plugin.getBundle().getEntry("/");
			URL url = new URL(installURL, iconPath + relativePath);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

}
