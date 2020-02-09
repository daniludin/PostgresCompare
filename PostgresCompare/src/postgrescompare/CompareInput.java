package postgrescompare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.IProgressMonitor;

public class CompareInput extends CompareEditorInput {
	String sbLeft = new String();
	String sbRight = new String();
	
    public CompareInput(String sbLeft, String sbRight) {
        super(new CompareConfiguration());
		this.sbLeft = sbLeft;
		this.sbRight = sbRight;

     }
     protected Object prepareInput(IProgressMonitor pm) {
        CompareItem ancestor = 
           new CompareItem("Common", "contents", System.currentTimeMillis());
        CompareItem left = 
           new CompareItem("Left", sbLeft, System.currentTimeMillis());
        CompareItem right = 
           new CompareItem("Right", sbRight, System.currentTimeMillis());
        return new DiffNode(null, Differencer.CONFLICTING, 
           ancestor, left, right);
     }
}
