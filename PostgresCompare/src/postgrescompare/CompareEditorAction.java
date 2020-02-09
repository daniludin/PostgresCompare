package postgrescompare;

import org.eclipse.compare.CompareUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CompareEditorAction implements IWorkbenchWindowActionDelegate {
	
	String sbLeft = new String();
	String sbRight = new String();
	
	public CompareEditorAction(String sbLeft, String sbRight) {
		this.sbLeft = sbLeft;
		this.sbRight = sbRight;
	}

	public void run(IAction action) {
		CompareUI.openCompareEditor(new CompareInput(this.sbLeft, this.sbRight));
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub

	}
}
