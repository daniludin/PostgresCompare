package postgrescompare;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class ConnectionsView extends ViewPart  {
	private Label label;
    Text txtAbstract;
    Action addTestConnectionAction;

	public ConnectionsView() {
		super();
	}

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		// Normally we might do other stuff here.
	}

	public void setFocus() {
		label.setFocus();
	}

	public void createPartControl(Composite parent) {
		
		Canvas baseCanvas = new Canvas(parent, SWT.FILL);
		baseCanvas.setBackground(new Color(Display.getDefault(), 0, 239, 239));

		FillLayout glBase = new FillLayout();
		glBase.type = SWT.VERTICAL;

		GridData gdBase = new GridData();
		gdBase.horizontalAlignment = SWT.FILL;
		gdBase.grabExcessHorizontalSpace = true;
		baseCanvas.setLayout(glBase);

		
		label = new Label(baseCanvas, 0);
		label.setText("Connection URL");
		
		Canvas buttonCanvas = new Canvas(baseCanvas, SWT.NONE);
		GridLayout glButtonCanvas = new GridLayout();
		GridData gdButtonCanvas = new GridData();
		gdButtonCanvas.horizontalAlignment = SWT.CENTER;
		gdButtonCanvas.verticalAlignment = SWT.BEGINNING;
		glButtonCanvas.numColumns = 3;
		buttonCanvas.setSize(SWT.DEFAULT, 40);

		buttonCanvas.setLayoutData(gdButtonCanvas);
		buttonCanvas.setLayout(glButtonCanvas);

		Button btn1 = new Button(buttonCanvas, SWT.BORDER);
		btn1.setText("Test Connection");
		btn1.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					saveCstProfile();
					break;
				}
			}
		});

		txtAbstract = new Text(baseCanvas,  SWT.BORDER);
		GridData gridDataText = new GridData(GridData.FILL_BOTH);
		gridDataText.verticalAlignment = SWT.FILL;
		//gridDataText.grabExcessVerticalSpace = true;
		gridDataText.grabExcessHorizontalSpace = true;
		//gridDataText.heightHint = 40;
		//gridDataText.widthHint = 600;

		txtAbstract.setLayoutData(gridDataText);

		
	}
	private void saveCstProfile() {
		System.out.println("btn1 clicked");
	}
//    public void createActions() {
//    	addTestConnectionAction = new Action("Add...") {
//                public void run() { 
//                           addItem();
//                   }
//           };
//           // Add selection listener.
//        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//                   public void selectionChanged(SelectionChangedEvent event) {
//                           updateActionEnablement();
//                   }
//           });
//   }
//    private void updateActionEnablement() {
////        IStructuredSelection sel = 
////                (IStructuredSelection)viewer.getSelection();
//        //deleteItemAction.setEnabled(sel.size() > 0);
//}

}
