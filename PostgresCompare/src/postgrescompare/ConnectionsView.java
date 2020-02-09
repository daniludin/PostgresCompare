package postgrescompare;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.internal.CompareAction;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.ide.IDE;

public class ConnectionsView extends ViewPart {
	Canvas baseCanvas;
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

	ControlDecoration decoUrlLeft;
	ControlDecoration decoUrlRight;

	Button btnChooseSaveFile;

	Action addCompareAction;
	String KEY_LEFT_1 = "urlleft";
	String KEY_LEFT_2 = "usernameleft";
	String KEY_LEFT_3 = "passwordleft";
	String KEY_RIGHT_1 = "urlright";
	String KEY_RIGHT_2 = "usernameright";
	String KEY_RIGHT_3 = "passwordright";
	String selectedJdbcJarPath;
	private Text txtAbstract;
	String urlLeft;
	String userLeft;
	String pwLeft;
	String urlRight;
	String userRight;
	String pwRight;
	private boolean saveFileToProject;
	StringBuffer sbLeft = null;
	StringBuffer sbRight = null;
	Object[] result = new Object[10];

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		System.out.println("init");
	}

	@Override
	public void saveState(IMemento memento) {
		savePluginSettings();
	}

	@Override
	public void dispose() {
		System.out.println("dispose");
		super.dispose();
	}

	public ConnectionsView() {
		super();
	}

	public void setFocus() {
		labelLeft1.setFocus();
	}

	public void createPartControl(Composite parent) {
		createActions();

		baseCanvas = new Canvas(parent, SWT.FILL);
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
		txtUrlLeft.addModifyListener(listenerUrlLeft);

		txtUsernameLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataUsernameL = new GridData(GridData.BEGINNING);
		gridDataUsernameL.horizontalAlignment = GridData.FILL;
		gridDataUsernameL.widthHint = 300;
		gridDataUsernameL.minimumWidth = 300;
		txtUsernameLeft.setLayoutData(gridDataUsernameL);
		txtUsernameLeft.addModifyListener(listenerUrlLeft);

		labelLeft3 = new Label(baseCanvas, 0);
		labelLeft3.setText("Passwort");

		txtPasswordLeft = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataPasswordL = new GridData(GridData.BEGINNING);
		gridDataPasswordL.horizontalAlignment = GridData.FILL;
		gridDataPasswordL.widthHint = 300;
		gridDataPasswordL.minimumWidth = 300;
		txtPasswordLeft.setLayoutData(gridDataPasswordL);
		txtPasswordLeft.addModifyListener(listenerUrlLeft);

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
		txtUrlRight.addModifyListener(listenerUrlLeft);

		labelRight2 = new Label(baseCanvas, 0);
		labelRight2.setText("Username");

		txtUsernameRight = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataUsernameR = new GridData(GridData.BEGINNING);
		gridDataUsernameR.horizontalAlignment = GridData.FILL;
		gridDataUsernameR.widthHint = 300;
		gridDataUsernameR.minimumWidth = 300;
		txtUsernameRight.setLayoutData(gridDataUsernameR);
		txtUsernameRight.addModifyListener(listenerUrlLeft);

		labelRight3 = new Label(baseCanvas, 0);
		labelRight3.setText("Passwort");

		txtPasswordRight = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataPasswordR = new GridData(GridData.BEGINNING);
		gridDataPasswordR.horizontalAlignment = GridData.FILL;
		gridDataPasswordR.widthHint = 300;
		gridDataPasswordR.minimumWidth = 300;
		txtPasswordRight.setLayoutData(gridDataPasswordR);
		txtPasswordRight.addModifyListener(listenerUrlLeft);

		Button btnCompare = new Button(baseCanvas, SWT.BORDER);
		btnCompare.setText("Compare");
//		btnCompare.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				switch (e.type) {
//				case SWT.Selection:
//					compareDatabases(addCompareAction, 
//							txtUrlLeft.getText(), 
//							txtUsernameLeft.getText(), 
//							txtPasswordLeft.getText(), 
//							txtUrlRight.getText(), 
//							txtUsernameRight.getText(), 
//							txtPasswordRight.getText());
//					break;
//				}
//			}
//		});
		// btnCompare.addSelectionListener(new
		// MySelectionAdapter(getSite().getShell()));
		btnCompare.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (decoUrlLeft != null) {
					decoUrlLeft.hide();
				}
				if (decoUrlRight != null) {
					decoUrlRight.hide();
				}
				if (saveFileToProject) {

					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getSite().getShell(),
							ResourcesPlugin.getWorkspace().getRoot(), true, "Select a folder");
					dialog.open();
					result = dialog.getResult();
					if (result == null) {
						return;
					}
				}

				Job job = new Job("Postgres Compare running...") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {

						// doLongThing();
						compareDatabases(addCompareAction, result, getUrlLeft(), getUserLeft(), getPwLeft(),
								getUrlRight(), getUserRight(), getPwRight());
						//syncWithUi();
						// use this to open a Shell in the UI thread
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();

			}

		});

		btnChooseSaveFile = new Button(baseCanvas, SWT.CHECK);
		btnChooseSaveFile.setText("Add results to Project");
		btnChooseSaveFile.addSelectionListener(listenerOption);

		Button btnLoadDriver = new Button(baseCanvas, SWT.BORDER);
		btnLoadDriver.setText("Load JDBC Driver");
		btnLoadDriver.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					loadJdbcJar();
					break;
				}
			}
		});

		txtAbstract = new Text(baseCanvas, SWT.BORDER);
		GridData gridDataText = new GridData(GridData.FILL_BOTH);
		gridDataText.verticalAlignment = SWT.FILL;
		gridDataText.grabExcessHorizontalSpace = true;
		gridDataText.horizontalSpan = 2;
		txtAbstract.setLayoutData(gridDataText);

		// createToolbar();

		loadPluginSettings();
	}

	ModifyListener listenerUrlLeft = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			setUrlLeft(txtUrlLeft.getText());
			setUserLeft(txtUsernameLeft.getText());
			setPwLeft(txtPasswordLeft.getText());
			setUrlRight(txtUrlRight.getText());
			setUserRight(txtUsernameRight.getText());
			setPwRight(txtPasswordRight.getText());
		}
	};
	SelectionListener listenerOption = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (btnChooseSaveFile.getSelection()) {
				setSaveFileToProject(true);
			} else {
				setSaveFileToProject(false);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
	};

	private void syncWithUi() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(getSite().getShell(), "Postgres Compare ", "Finished.");
			}
		});

	}

	private void doLongThing() {
		for (int i = 0; i < 10; i++) {
			try {
				// We simulate a long running operation here
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Doing something");
		}
	}

	private void displayConnectionErrorLeft() {
		decoUrlLeft = new ControlDecoration(txtUrlLeft, SWT.TOP | SWT.LEFT);
		decoUrlLeft.setDescriptionText("Connection error");
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage();
		decoUrlLeft.setImage(image);

	}

	private void displayConnectionErrorRight() {
		decoUrlRight = new ControlDecoration(txtUrlRight, SWT.TOP | SWT.LEFT);
		decoUrlRight.setDescriptionText("Connection error");
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage();
		decoUrlRight.setImage(image);

	}

	private void compareDatabases(Action action, Object[] result, String urlL, String userL, String pwL, String urlR,
			String userR, String pwR) {
		System.out.println("testing clicked");
		boolean noConnectionErrorLeft = true;
		boolean noConnectionErrorRight = true;

		try {
			ReadDatabaseStructure rds = new ReadDatabaseStructure();
			sbLeft = rds.readDbStructure(urlL, userL, pwL);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					decoUrlLeft.hide();
				}
			});

		} catch (SQLException e1) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					displayConnectionErrorLeft();
				}
			});
			noConnectionErrorLeft = false;
			//return;
		}
		try {
			ReadDatabaseStructure rdsRight = new ReadDatabaseStructure();
			sbRight = rdsRight.readDbStructure(urlR, userR, pwR);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					decoUrlRight.hide();
				}
			});
		} catch (SQLException e1) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					displayConnectionErrorRight();
				}
			});
			noConnectionErrorRight = false;
			//return;
		}
		if (!noConnectionErrorLeft || !noConnectionErrorRight) {
			return;
		}
//		try {
//			if (!noConnectionErrorLeft && this.decoUrlLeft != null) {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						decoUrlLeft.hide();
//					}
//				});
//
//			}
//			if (!noConnectionErrorRight && this.decoUrlRight != null) {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						decoUrlRight.hide();
//					}
//				});
//			}
//			if (!noConnectionErrorRight || !noConnectionErrorRight) {
//				return;
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		if (isSaveFileToProject()) {

			for (int i = 0; i < result.length; i++) {
				IPath path = (IPath) result[i];
				System.out.println("class: " + path.getClass());
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				java.net.URI uri = root.findMember((IPath) result[0]).getLocationURI();

				IContainer[] folders = root.findContainersForLocationURI(uri);
				if (folders != null && folders.length > 0) {
					IFolder folder = (IFolder) folders[0];

					IFile fileLeft = folder.getFile(getDatabaseNameLeft() + ".sql");
					if (!fileLeft.exists()) {
						try {
							fileLeft.create(new ByteArrayInputStream(sbLeft.toString().getBytes("UTF-8")), true, null);
						} catch (CoreException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					IFile fileRight = folder.getFile(getDatabaseNameRight() + ".sql");
					if (!fileRight.exists()) {
						try {
							fileRight.create(new ByteArrayInputStream(sbRight.toString().getBytes("UTF-8")), true,
									null);
						} catch (CoreException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				CompareEditorAction compareAction = new CompareEditorAction(sbLeft.toString(), sbRight.toString());
				compareAction.run(action);
			}
		});
	}

//	private String getFileName() {
//		DateFormat df = new DateFormat();
//		df.se
//	}

	private String getDatabaseNameLeft() {
		String[] bits = getUrlLeft().split("/");
		return bits[bits.length - 1];
	}

	private String getDatabaseNameRight() {
		String[] bits = getUrlRight().split("/");
		return bits[bits.length - 1];
	}

	private void compare() {
		System.out.println("compare clicked");
		// File fileToOpen = new File("C:\\temp\\UserTest.xml");
		File fileToOpen = new File("C:\\temp\\sometext.txt");

		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			try {
				IDE.openEditorOnFileStore(page, fileStore);
				// IDE.openInternalEditorOnFileStore(page, fileStore);
				// IDE.openEditor(page, fileToOpen, "tested.editors.MultiPageEditor");

			} catch (PartInitException e) {
				// Put your exception handler here if you wish to
			}
		} else {
			// Do something if the file does not exist
		}

	}

	private void testConnections() {
		System.out.println("testConnections clicked");
		savePluginSettings();
		ReadDatabaseStructure rds = new ReadDatabaseStructure();
		StringBuffer sbLeft = null;
		try {
			sbLeft = rds.readDbStructure(this.txtUrlLeft.getText(), this.txtUsernameLeft.getText(),
					this.txtPasswordLeft.getText());
			System.out.println("-- Left Database");
			System.out.println(sbLeft.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();
		IEditorDescriptor[] editors = registry.getEditors("filename.txt");
		for (IEditorDescriptor desc : editors) {
			System.out.println("editor: " + desc.getId() + "\t" + desc.getLabel());
		}

		URI uri = URI.createPlatformResourceURI("/myProject/folder/deep/myFile.ext", true);

//		IResource iResource = UriUtils.toIResource(uri);
//
//		PackageExplorerPart part= PackageExplorerPart.getFromActivePerspective();
//		IResource resource = ;
//
//		part.selectAndReveal(resource);

		// UntitledTextFileWizard obj = new UntitledTextFileWizard();

	}

	private void loadJdbcJar() {
		FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN);
		// dialog.setFilterExtensions(new String[] { "*.html" });
		dialog.setFilterPath("D:\\Begasoft\\workspaces\\ws4tmp\\substidoc\\libext\\");
		selectedJdbcJarPath = dialog.open();
		if (selectedJdbcJarPath == null) {
			return;
		}
		System.out.println("selected File: " + this.selectedJdbcJarPath);

		try {
			File jarFile = new File(selectedJdbcJarPath);
			System.out.println(jarFile.toURI());
			System.out.println(jarFile.toURL());
			URL[] urls = new URL[] { new URL("jar", "", "file:" + selectedJdbcJarPath + "!/") };
			// URL[] urls = new URL[]{ new URL("jar", "",
			// jarFile.toURI().toURL().toString())};
			URLClassLoader cl = URLClassLoader.newInstance(urls, Activator.getDefault().getClass().getClassLoader());
			Class<?> loadedClass = cl.loadClass("org.postgresql.Driver");

//			Constructor<?> constructor = loadedClass.getConstructor();
//	        Object beanObj = constructor.newInstance();
//			
//	        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
//	        Class myClass = bundle.loadClass("org.postgresql.Driver"); 

			try {
				// addURL(urls[0]);
				// Class.forName("org.postgresql.Driver");
				Class.forName("org.postgresql.Driver", false, cl);
				Connection connection = null;
				try {
					connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
							"postgres");
				} catch (SQLException e) {
					System.out.println("Connection Failed! Check output console");
					e.printStackTrace();
				}

			} catch (ClassNotFoundException e) {
				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}

	}
//	public  void addURL ( URL theURL ) throws IOException {
//	    //CPTest  lclsTest = new CPTest();
//	    if ( getUrlCount () == 0 ) { 
//	        getfQCNUrls()[getUrlCount()] = theURL;
//	        setUrlCount ( 1 );
//	    }
//	    else { 
//	        boolean lisThere = false;
//	        for (int i = 0; i < getUrlCount(); i++) {
//	            if (getfQCNUrls()[i].toString().equalsIgnoreCase(theURL.toString())) {
//	                lisThere = true;
//	            }
//	        }
//	        if ( lisThere ) { 
//	            System.out.println ( "File URL [" + theURL.toString () + "] already on the CLASSPATH!" );
//	        }
//	        else { 
//	            getfQCNUrls()[getUrlCount()] = theURL;
//	            setUrlCount ( getUrlCount ()+1 );
//	        }
//
//	    }
//
//	    // CLEAR : Null out the classloader...
//	    this.fQCNLoader = null;
//	    // BUILD/RE-BUILD : the classloader...
//	    this.fQCNLoader = new URLClassLoader ( getfQCNUrls(), this.getClass ().getClassLoader () );
//
//	    //try {
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//	    //    System.out.println ( "Current Working Directory.............[" + System.getProperty ( "user.dir" ) + "]" );
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//	    //    System.out.println ( "this.classes []! " );
//	    //    lclsTest.dumpClasses ( this.getClass ().getClassLoader () );
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//	    //
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//	    //    System.out.println ( "File URL [" + theURL.toString () + "] added! " );
//	    //    lclsTest.dumpClasses ( this.FQCNLoader );
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//	    //    System.out.println ( "ClassLoader.getSystemClassLoader() " );
//	    //    lclsTest.dumpClasses ( ClassLoader.getSystemClassLoader() );
//	    //    System.out.println ( "---------------------------------------------------------------------------" );
//
//	    //    Class  cls = this.FQCNLoader.loadClass ( "com.lmig.RRFECF.pso.security.nonproduction.CM_RRFECF_development_securitykey" );
//	    //  Class  cls = Class.forName(theFQCN, false, theClsLoader);
//	    //    theObjectKey = ( Object )  theClsLoader.loadClass(theFQCN);
//
//
//	    //}
//	    //catch ( Exception e ) {
//	    //// TODO Auto-generated catch block
//	    //    e.printStackTrace();
//	    //}
//
////	    Class sysclass = URLClassLoader.class;
////	    try {
////	        Method method = sysclass.getDeclaredMethod("addURL", parameters);
////	        method.setAccessible(true);
////	        method.invoke(getfQCNLoader(), new Object[] {
////	            theURL
////	        });
////	    }
////	    catch (Throwable t) {
////	        t.printStackTrace();
////	        throw new IOException(
////	            "Error, could not add URL to system classloader");
////	    }
//	}

	public void createActions() {

		addCompareAction = new Action("Testing") {
			public void run() {

				compareDatabases(addCompareAction, null, getUrlLeft(), getUserLeft(), getPwLeft(), getUrlRight(),
						getUserRight(), getPwRight());
			}
		};
		addCompareAction
				.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("PostgresCompare", "icons/test.gif"));
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

//		String[] vorschlaege = new String[]{"Vorschlag1", "Vorschlag2"};
//		prefs.put("vorschlaege", vorschlaege);

		try {
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void loadPluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("PostgresCompare");
		// you might want to call prefs.sync() if you're worried about others changing
		// your settings
		this.txtUrlLeft.setText(prefs.get(KEY_LEFT_1, "jdbc:postgresql://localhost:5432/postgres1"));
		this.txtUsernameLeft.setText(prefs.get(KEY_LEFT_2, ""));
		this.txtPasswordLeft.setText(prefs.get(KEY_LEFT_3, ""));
		this.txtUrlRight.setText(prefs.get(KEY_RIGHT_1, "jdbc:postgresql://localhost:5432/postgres2"));
		this.txtUsernameRight.setText(prefs.get(KEY_RIGHT_2, ""));
		this.txtPasswordRight.setText(prefs.get(KEY_RIGHT_3, ""));
	}

	public String getUrlLeft() {
		return urlLeft;
	}

	public void setUrlLeft(String urlLeft) {
		this.urlLeft = urlLeft;
	}

	public String getUserLeft() {
		return userLeft;
	}

	public void setUserLeft(String userLeft) {
		this.userLeft = userLeft;
	}

	public String getPwLeft() {
		return pwLeft;
	}

	public void setPwLeft(String pwLeft) {
		this.pwLeft = pwLeft;
	}

	public String getUrlRight() {
		return urlRight;
	}

	public void setUrlRight(String urlRight) {
		this.urlRight = urlRight;
	}

	public String getUserRight() {
		return userRight;
	}

	public void setUserRight(String userRight) {
		this.userRight = userRight;
	}

	public String getPwRight() {
		return pwRight;
	}

	public void setPwRight(String pwRight) {
		this.pwRight = pwRight;
	}

	public boolean isSaveFileToProject() {
		return saveFileToProject;
	}

	public void setSaveFileToProject(boolean saveFileToProject) {
		this.saveFileToProject = saveFileToProject;
	}

}
