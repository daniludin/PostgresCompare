package postgrescompare;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class WordView extends ViewPart 
{
        WordFile input;
        ListViewer viewer;
        Action addItemAction, deleteItemAction, selectAllAction;
        IMemento memento;
        
        /**
         * Constructor
         */
     public WordView() {
                super();
                input = new WordFile(new File("list.lst"));
        }

        /**
         * @see IViewPart.init(IViewSite)
         */
     public void init(IViewSite site) throws PartInitException {
                super.init(site);
                // Normally we might do other stuff here.
        }

        /**
         * @see IWorkbenchPart#createPartControl(Composite)
         */
     public void createPartControl(Composite parent) {
                // Create viewer.
             viewer = new ListViewer(parent);
                viewer.setContentProvider(new WordContentProvider());
                viewer.setLabelProvider(new LabelProvider());
                viewer.setInput(input);

                // Create menu and toolbars.
             createActions();
                createMenu();
                createToolbar();
                createContextMenu();
                hookGlobalActions();
                
                // Restore state from the previous session.
             restoreState();
        }
        
        /**
         * @see WorkbenchPart#setFocus()
         */
        public void setFocus() {
                viewer.getControl().setFocus();
        }

}
