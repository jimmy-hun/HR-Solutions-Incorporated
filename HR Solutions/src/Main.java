import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import function.Department;
import function.Employee;
import function.Organisation;
import function.PayLevel;

public class Main extends JFrame
{
	// StringBuilder was used for the pay report... opening payroll.txt from notepad gets issues. MSWord or eclipse will do. 
	
	/* NOTE: JavaDoc style explanations are used especially in Main and Organisation (Department and Employee is quite self explanatory)
	 * 
	 * Packages have been used to make things more neat... they are called files and function. 
	 * files contains the logo and startup and saved files
	 * function contains classes such as employee and department
	 * The default package contains the main class, which is where the system starts and sets up the GUI. 
	 * The notes folder details some of the info found on the Internet (used in assignment)
	 */

	private static final long serialVersionUID = 7505069817118718512L;

	// declarations occur here
	//create tabbed pane
	private JTabbedPane tabbedPane;
	// employee buttons
	private JButton btnAddEmployee, btnEditEmployee, btnDeleteEmployee, btnManageEmployee;
	// department buttons
	private JButton btnAddDepartment, btnEditDepartment, btnDeleteDepartment, btnManageDepartment;
	// management buttons
	private JButton btnDisplayDepEmp, btnPayReport; 	
	// exit button
	private JButton btnSaveAndExit; 
	// home page info
	private JLabel homeNote, homeInst, homeInst1; 	

	private AddEmpListener addEmpListener;			// add listener to btnAddEmployee
	private EditEmpListener editEmpListener;		// edit listener to btnEditEmployee
	private DeleteEmpListener delEmpListener;		// delete listener to btnDeleteEmployee
	private ManageEmpListener manEmpListener;		// manage listener to btnManageEmployee

	private AddDepListener addDepListener;			// add listener to btnAddDepartment
	private EditDepListener editDepListener;		// edit listener to btnEditDepartment
	private DeleteDepListener delDepListener;		// delete listener to btnDeleteDepartment
	private ManageDepListener manDepListener;		// manage listener to btnManageDepartment

	private DisplayDepEmpListener displayDepEmpListener;	// pay report listener to btnDisplayDepEmp
	private PayReportListener payReportListener;			// pay report listener to btnPayReport

	private SaveAndExitListener saveAndExitListener;		// add listener to save and exit

	private PayLevel payLevelClass;	// add class containing pay level costs
	private Organisation organisation;	// add organisation (where employee and department are linked)
	private final static String COMPANY_NAME = "HR Solutions Incorporated";

	// for loading startup files
	// Find resource of the specified name from the search path used to load classes.
	private static final String STARTUP = System.getProperty("user.dir") + "\\src\\files\\startup.txt"; // for a 'clean start'
	private static final String SERIALISE_FILE_NAME = System.getProperty("user.dir") + "\\src\\files\\org.ser"; // save if exiting [SERIALISE]

	private static final double HEAD_BONUS = 5000;

	// Name: 	Main 
	// Purpose:	To create a tabbed pane and add listeners as well as to load the previous session or create a 'clean start'  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. TabbedPane makes a tabbed pane. Works like JPanel but uses JPanels to add tabs
	//			2. Many listeners have been added, which are required for button press functions
	//			3. Organisation is initialised and instantiated as a medium for storing employee and department info
	//			4. File orgCheck is for checking if an org.ser file already exists, it determines it's path, if it does exist 
	//			   it goes to LoadFromSerialisedFile() which loads else it will do a 'clean start' in LoadFreshDataFromFile().
	public Main() 
	{
		payLevelClass = new PayLevel();

		addEmpListener = new AddEmpListener();
		editEmpListener = new EditEmpListener();
		delEmpListener = new DeleteEmpListener();

		addDepListener = new AddDepListener();
		editDepListener = new EditDepListener();
		delDepListener = new DeleteDepListener();

		manDepListener = new ManageDepListener();
		manEmpListener = new ManageEmpListener();
		displayDepEmpListener = new DisplayDepEmpListener();
		payReportListener = new PayReportListener();

		saveAndExitListener = new SaveAndExitListener();

		tabbedPane = new JTabbedPane(); // a tabbed pane can be used to put tabs
		tabbedPane.addTab("Home", homeTab());
		tabbedPane.addTab("Employee", employeeTab());
		tabbedPane.addTab("Department", departmentTab());
		tabbedPane.addTab("Management", managementTab());
		tabbedPane.addTab("Exit & Save", exitTab());
		add(tabbedPane);

		setSize(640, 585);

		organisation = new Organisation(COMPANY_NAME);

		//do check here check if ser file exists or not
		// add orgCheck as a file instance under the directory SERIALISE_FILE_NAME... this does not create the .ser file
		// FileOutputStream fileOut = new FileOutputStream(SERIALISE_FILE_NAME, false); creates the file upon exit (output stream)
		File orgCheck = new File(SERIALISE_FILE_NAME); 

		if (orgCheck.exists())
		{
			LoadFromSerialisedFile();
		}

		else
		{
			LoadFreshDataFromFile();
		}
	}

	// Name: 	LoadFromSerialisedFile 
	// Purpose:	To load from org.ser which is re-written each time the Exit & Save button is pressed  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	FileInputStream restoreIN, ObjectInputStream in, "org.ser"
	// Output: 	Nothing
	// Effect: 	1. FileInputStream restoreIN loads/inputs org.ser
	//			2. ObjectInputStream in allows for loading org.ser by taking restoreIN as the object 
	//			3. organisation = (Organisation) in.readObject(); reads the 'organisation' in org.ser and makes it equal to
	//			   this 'organisation' hence, effectively loading org.ser into the current session
	private void LoadFromSerialisedFile() 
	{
		System.out.println("RESTORING PREVIOUS SESSION...");

		try
		{
			// restore the .ser file... orders of ArrayList Order will read the objects
			FileInputStream restoreIN = new FileInputStream(SERIALISE_FILE_NAME);
			ObjectInputStream in = new ObjectInputStream(restoreIN);

			// de-serialize:
			organisation = (Organisation) in.readObject();
		}

		// IOException occurs when no file is found... if that is the case, LoadDataFromFile() will load a 'clean start'
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("\n**** Restored " + organisation.Departments.size() + " departments into memory.");
		System.out.println("**** Restored " + organisation.Employees.size() + " employees into memory.");
	}

	// Name: 	LoadFreshDataFromFile 
	// Purpose:	To load a 'clean start' from startup.txt which is occurs if an org.ser file cannot be found  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	File file, "startup.txt"
	// Output: 	Nothing
	// Effect: 	1. File file loads startup.txt
	//			2. Scanner inFile creates a scanner to scan the contents of file (startup.txt)
	//			3. Department department makes a new department under the scanned settings
	//			4. Employee employee makes a new employee under the scanned settings
	private void LoadFreshDataFromFile()
	{
		System.out.println("**** Loading from file in progress ****");

		try 
		{
			File file = new File(STARTUP);		// load startup.txt via URL to URI
			Scanner inFile = new Scanner(file);	// put a scanner for file, which is startup.txt

			// "The first line of the file is an integer stating how many �department� records will immediately follow."
			int totalDep = inFile.nextInt();
			System.out.println("Total number of departments:\t" + totalDep);	// print the total number of departments

			inFile.nextLine();	// integer just reads the number on the first line... specify inFile.nextLine(); to go to second line

			// loop and add departments and assign employees
			int empInDep = 0;
			for (int i = 0; i < totalDep; i++)
			{
				String depName = inFile.nextLine();		// scan line with department name
				String depLocation = inFile.nextLine();	// scan line with department location
				empInDep = inFile.nextInt();			// scan integer from the line with department no. of employee
				inFile.nextLine();						// previously scanned a integer therefore must go next line

				// make a department with these attributes
				// public Department(int departmentID, String name, String location)
				int departmentID = organisation.GetAvailableDepartmentID(); // get an available ID
				Department department = new Department(departmentID, depName, depLocation); // create a department under these settings

				// add to department map in organisation
				organisation.AddDepartment(departmentID, department);

				System.out.println("\n--------- Department Added ---------\n" + department.toString() + "\nEmployees:\t\t" + empInDep);	// print what was added

				// loop and add the employees
				for (int i2 = 0; i2 < empInDep; i2++)
				{
					String empFirstName = inFile.nextLine();	// scan line with employee first name
					String empLastName = inFile.nextLine();		// scan line with employee last name
					String empGender = inFile.nextLine();		// scan line with employee gender
					String empAddress = inFile.nextLine();		// scan line with employee address
					int salaryCode = inFile.nextInt();			// scan integer for the employee salary code
					inFile.nextLine();							// previously scanned a integer therefore must go next line

					char empGenderAdd = empGender.charAt(0); 	// java counts from 0... can not scan char so turn scanned string to char

					// make a employee with these attributes
					// public Employee(int employeeID, int payLevel, String firstName, String surname, char gender, String address)
					int employeeID = organisation.GetAvailableEmployeeID();
					Employee employee = new Employee(employeeID, salaryCode, empFirstName, empLastName, empGenderAdd, empAddress);

					// add employee to map
					organisation.AddEmployee(employeeID, employee);

					//add employee to department
					organisation.AddEmployeeToDepartment(departmentID, employeeID);

					System.out.println("\n--------- Employee Added ---------\n" + employee.toString());	// print what was added
				}
			}

			// afterwards, there is this 'random number' which specifies the number of unassigned employees...
			int unassignedEmp = inFile.nextInt();		// scan int with number of unassigned employees
			inFile.nextLine();							// go to next line as int was used unassigned

			System.out.println("\n--------- UNASSIGNED EMPLOYEES ---------\nTotal Unassigned: " + unassignedEmp);	// print information notifications

			// loop and add the employees
			for (int i2 = 0; i2 < empInDep; i2++)
			{
				String empFirstName = inFile.nextLine();	// scan line with employee first name
				String empLastName = inFile.nextLine();		// scan line with employee last name
				String empGender = inFile.nextLine();		// scan line with employee gender
				String empAddress = inFile.nextLine();		// scan line with employee address
				int salaryCode = inFile.nextInt();			// scan integer for the employee salary code
				inFile.nextLine();							// previously scanned a integer therefore must go next line

				char empGenderAdd = empGender.charAt(0); 	// java counts from 0... can not scan char so turn scanned string to char

				// make a employee with these attributes
				// public Employee(int employeeID, int payLevel, String firstName, String surname, char gender, String address)
				int employeeID = organisation.GetAvailableEmployeeID();
				Employee employee = new Employee(employeeID, salaryCode, empFirstName, empLastName, empGenderAdd, empAddress);

				// add employee to map
				organisation.AddEmployee(employeeID, employee);

				// no department to add to

				System.out.println("\n--------- Employee Added ---------\n" + employee.toString());	// print what was added
			}

			inFile.close();	// disable the inFile scanner for file to avoid unnecessary issues
		} 

		catch (FileNotFoundException exc)	// deals with the exception error regarding finding the file
		{
			System.out.println("\nERROR:\tstartup.txt was not found... no default values will be loaded.");
			System.out.println("\tYou may wish to add a startup.txt file in Major Assignment\\src\\files if the file follows the correct formatting.");
			System.out.println("\tThis format is specified in the Assignment details. Information can be found at Req-12.");
		}

		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// Name: 	LayoutCommandButtons 
	// Purpose:	Sets a default layout for all tabs except homeTab  
	// Passed: 	GridBagConstraints c, ActionListener actionListener, JPanel jPanel, JButton jButton, String buttonLabel, int rowNumber
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. GridBagConstraints c				pass in the GridBagConstraints so it can be edited in LayoutCommandButtons
	//			2. ActionListener actionListener	pass in the appropriate action listener for the button
	//			3. JPanel jPanel					for passing the JLabel of the specific tab
	//			4. JButton jButton					for passing the button object over
	//			5. String buttonLabel				for naming a button
	//			6. int rowNumber					determines which row the component appears on
	private void LayoutCommandButtons(GridBagConstraints c, ActionListener actionListener, JPanel jPanel, JButton jButton, String buttonLabel, int rowNumber)
	{
		// add button
		jButton = new JButton (buttonLabel);
		jButton.addActionListener(actionListener);

		c.fill = GridBagConstraints.HORIZONTAL; // alignment
		c.ipady = 10;		//makes component taller
		c.weightx = 0.0;	// request any extra horizontal space
		c.weighty = 0.0;	// request any extra vertical space
		c.gridwidth = 3;	// 3 columns wide
		c.gridx = 0;		// how far it leans
		c.gridy = rowNumber;// [position in rows]
		jPanel.add(jButton, c);
	}

	// Name: 	homeTab 
	// Purpose:	Create a home page for the program  
	// Passed: 	Nothing
	// Returns: tab0
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. JPanel tab0				create a JPanel for homeTab, everything is added to this via tab0.add
	//			2. new GridBagLayout()		create a GridBagLayout so constraints can be set
	//			3. javax.swing.JLabel logo	enable an image to be seen
	//			4. java.net.URL image		put the directory of the file so it can be set
	//				=> logo.setIcon(new javax.swing.ImageIcon(image)); sets the image 
	private JPanel homeTab()
	{
		JPanel tab0 = new JPanel();
		tab0.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
		GridBagConstraints c = new GridBagConstraints(); // enable editing of grid
		homeNote = new JLabel(String.format("WELCOME TO %s", COMPANY_NAME.toUpperCase()));
		homeInst = new JLabel("******************************************************");
		homeInst1 = new JLabel("Please select one of the tabs to start.");

		// addition of a image as a logo
		javax.swing.JLabel logo = new javax.swing.JLabel();
		java.net.URL image = ClassLoader.getSystemResource("files/Cat-tree.jpg");	// declare image from directory within class paths
		logo.setIcon(new javax.swing.ImageIcon(image));						// add the image
		tab0.add(logo);														// add to the JPamel

		// edit addEmp button
		c.fill = GridBagConstraints.CENTER; // alignment
		c.gridy = 1;		// 1st row [position in rows]
		tab0.add(homeNote, c);

		// edit addEmp button
		c.fill = GridBagConstraints.CENTER; // alignment
		c.gridy = 2;		// 2nd row [position in rows]
		tab0.add(homeInst, c);

		// edit addEmp button
		c.fill = GridBagConstraints.CENTER; // alignment
		c.gridy = 3;		// 3rd row [position in rows]
		tab0.add(homeInst1, c);

		return tab0;		
	}

	// Name: 	employeeTab 
	// Purpose:	Create the employee tab for the program  
	// Passed: 	Nothing
	// Returns: jPanel
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. JPanel jPanel			create a JPanel for employeeTab, components added to this, returned after tab is completed
	//			2. new GridBagLayout()		creates a GridBagLayout so constraints can be set
	//			3. GridBagConstraints c		enable GridBagConstraints c, which will be the medium for setup in LayoutCommandButtons
	//			4. LayoutCommandButtons		this method is called as it edits c, the constraints and returns it
	//										=> jPanel.add(jButton, c); makes sure that a JButton under the specified constraints is created
	//			5. addEmpListener			passed in LayoutCommandButtons as an Action Listener (add employee)
	//			6. editEmpListener			passed in LayoutCommandButtons as an Action Listener (edit employee)
	//			7. delEmpListener			passed in LayoutCommandButtons as an Action Listener (delete employee)
	private JPanel employeeTab()
	{
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout()); // set a GridBagLayout to jPanel
		GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

		LayoutCommandButtons(c, addEmpListener, jPanel, btnAddEmployee, "Add an Employee", 1);
		LayoutCommandButtons(c, editEmpListener, jPanel, btnEditEmployee, "Edit an Employee", 2);
		LayoutCommandButtons(c, delEmpListener, jPanel, btnDeleteEmployee, "Delete an Employee", 3);

		return jPanel;		
	}

	// Name: 	departmentTab 
	// Purpose:	Create the department tab for the program  
	// Passed: 	Nothing
	// Returns: jPanel
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. JPanel jPanel			create a JPanel for departmentTab, components added to this, returned after tab is completed
	//			2. new GridBagLayout()		creates a GridBagLayout so constraints can be set
	//			3. GridBagConstraints c		enable GridBagConstraints c, which will be the medium for setup in LayoutCommandButtons
	//			4. LayoutCommandButtons		this method is called as it edits c, the constraints and returns it
	//										=> jPanel.add(jButton, c); makes sure that a JButton under the specified constraints is created
	//			5. addDepListener			passed in LayoutCommandButtons as an Action Listener (add department)
	//			6. editDepListener			passed in LayoutCommandButtons as an Action Listener (edit department)
	//			7. delDepListener			passed in LayoutCommandButtons as an Action Listener (delete department)
	private JPanel departmentTab()
	{
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout()); // set a GridBagLayout to jPanel
		GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

		// Notification label...  apparently JLabel handles HTML
		JLabel notification = new JLabel("<html>Departments may only be deleted<br>if no employees are assigned to it.<html>");
		jPanel.add(notification);
		
		LayoutCommandButtons(c, addDepListener, jPanel, btnAddDepartment, "Add a Department", 1);
		LayoutCommandButtons(c, editDepListener, jPanel, btnEditDepartment, "Edit a Department", 2);
		LayoutCommandButtons(c, delDepListener, jPanel, btnDeleteDepartment, "Delete a Department", 3);

		return jPanel;	
	}

	// Name: 	managementTab 
	// Purpose:	Create the management tab for the program  
	// Passed: 	Nothing
	// Returns: jPanel
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. JPanel jPanel			create a JPanel for managementTab, components added to this, returned after tab is completed
	//			2. new GridBagLayout()		creates a GridBagLayout so constraints can be set
	//			3. GridBagConstraints c		enable GridBagConstraints c, which will be the medium for setup in LayoutCommandButtons
	//			4. LayoutCommandButtons		this method is called as it edits c, the constraints and returns it
	//										=> jPanel.add(jButton, c); makes sure that a JButton under the specified constraints is created
	//			5. manDepListener			passed in LayoutCommandButtons as an Action Listener (manage departments)
	//			6. manEmpListener			passed in LayoutCommandButtons as an Action Listener (manage employees)
	//			7. displayDepEmpListener	passed in LayoutCommandButtons as an Action Listener (show employees in department)
	//			8. payReportListener		passed in LayoutCommandButtons as an Action Listener (produce report in console)
	private JPanel managementTab()
	{
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout()); // set a GridBagLayout to jPanel
		GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

		// Notification label...  apparently JLabel handles HTML
		JLabel notification = new JLabel("<html>Manage employees into department first<br>before managing the department's head.<html>");
		
		jPanel.add(notification);
		
		LayoutCommandButtons(c, manEmpListener, jPanel, btnManageEmployee, "Manage Employee", 1);
		LayoutCommandButtons(c, manDepListener, jPanel, btnManageDepartment, "Manage Department", 2);
		LayoutCommandButtons(c, displayDepEmpListener, jPanel, btnDisplayDepEmp, "Display Employees in Departments", 3);
		LayoutCommandButtons(c, payReportListener, jPanel, btnPayReport, "Produce Pay Report", 4);

		return jPanel;	
	}
	
	// Name: 	exitTab 
	// Purpose:	Create the exit tab for the program  
	// Passed: 	Nothing
	// Returns: jPanel
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. JPanel jPanel			create a JPanel for exitTab, components added to this, returned after tab is completed
	//			2. new GridBagLayout()		creates a GridBagLayout so constraints can be set
	//			3. GridBagConstraints c		enable GridBagConstraints c, which will be the medium for setup in LayoutCommandButtons
	//			4. LayoutCommandButtons		this method is called as it edits c, the constraints and returns it
	//										=> jPanel.add(jButton, c); makes sure that a JButton under the specified constraints is created
	//			5. saveAndExitListener		passed in LayoutCommandButtons as an Action Listener (exit system and save)
	private JPanel exitTab()
	{
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
		GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

		// Notification label... apparently, java is capable of some HTML 
		JLabel notification = new JLabel("<html>If you don't want to save, click the cross.</html>");
		jPanel.add(notification);
		
		LayoutCommandButtons(c, saveAndExitListener, jPanel, btnSaveAndExit, "Exit & Save", 1);

		return jPanel;
	}

	// Name: 	main(String[] args) 
	// Purpose:	The main method i.e. where the program begins  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. Main mainFrame			create a new 'Main' which will be the JFrame (public class Main extends JFrame)
	//			2. .addWindowListener		creates a listener so that clicking the cross on the window closes (without saving)
	public static void main(String[] args) 
	{
		Main mainFrame = new Main();
		mainFrame.setTitle("Main Menu"); // give the frame a name
		mainFrame.addWindowListener // window close function
		( 
				new WindowAdapter() 
				{
					public void windowClosing (WindowEvent e) 
					{
						System.exit(0);
					}
				}
		);

		mainFrame.setVisible(true); // make the frame visible
		mainFrame.setLocationRelativeTo(null); //center frame
		mainFrame.setResizable(false); //fixed size
	}

	// Name: 	GetIntFromComboBox 
	// Purpose:	To turn a String containing numbers into an int  
	// Passed: 	selection
	// Returns: number
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. String selection			a string is passed
	//			2. selection.split(" - ")	delete all " - " in the string
	//			3. return number			after splitting and parseInt (so string turns to int)
	//										parseInt(splits[0]) will be the first string which is the ID 
	private int GetIntFromComboBox(String selection)
	{
		int number = 0;

		// .split() requires an array list as, because:
		// if selection = "a - b - c"... selection.split(" - ") will get [a,b,c]
		String[] splits = selection.split(" - ");

		// if the length of the array is more than 1
		if (splits.length > 1)
		{
			// get the first integer by: Integer.parseInt(splits[0])
			// 1. Integer can be turned to String/int
			// 2. parseInt turns int to String
			// 3. [0] because you want the first no. (the ID)
			number = Integer.parseInt(splits[0]);
		}

		return number;
	}

	// Name: 	GetAllPayLevelsForComboBox 
	// Purpose:	To get numbers for PayLevels when using ComboBox  
	// Passed: 	Nothing
	// Returns: payLevelList
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. ArrayList<String> payLevelList			create a array list which will contain the pay level values
	//			2. Map<Integer, Double> payLevelMap			creates a map containing the keys and values of payLevelClass
	//			3. payLevelList.add(matchKey.toString())	add to array list, which is returned
	private ArrayList<String> GetAllPayLevelsForComboBox()
	{
		ArrayList<String> payLevelList = new ArrayList<String>();
		Map<Integer, Double> payLevelMap = payLevelClass.getAllPayLevels();

		// Integer is used as it is compatible with toString()
		// make Integer matchKey and loop repetitively with PayLevelMap's keys
		// advanced for loop... no i++ is required as it loops ??? with ???
		for(Integer matchKey : payLevelMap.keySet())
		{
			payLevelList.add(matchKey.toString()); // add from map to the array list
			//payLevelList.add(String.format("%s - $%s", pl, NumberFormat.getInstance().format(payLevelMap.get(pl))));
		}

		// Sorts the specified list into ascending order, according to the natural ordering of its elements
		// ArrayList was used as it can sort...
		Collections.sort(payLevelList);

		return payLevelList;
	}

	// Name: 	GetAllEmployeesForComboBox 
	// Purpose:	Gets the employees when using ComboBox  
	// Passed: 	Nothing
	// Returns: employeeList
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. ArrayList<String> employeeList		create a array list which will contain the employees
	//			2. payLevelList.add()					add to array list, which is returned
	private ArrayList<String> GetAllEmployeesForComboBox()
	{
		ArrayList<String> employeeList = new ArrayList<String>();

		// loop employeeID Integers with the Employees map in organisation 
		for (Integer employeeID : organisation.Employees.keySet())
		{
			Employee employee = organisation.GetEmployee(employeeID);
			employeeList.add(employeeID + " - " + employee.getFirstName() + " " + employee.getSurname());
		}

		Collections.sort(employeeList); // sort employeeList
		employeeList.add(0, "Please select"); // add initial text on array index 0, ArrayList pushes others forward

		return employeeList;
	}

	// Name: 	GetAllDepartmentsForComboBox 
	// Purpose:	Gets the employees when using ComboBox  
	// Passed: 	Nothing
	// Returns: employeeList
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. ArrayList<String> departmentList		create a array list which will contain the employees
	//			2. departmentList()						add to array list, which is returned
	private ArrayList<String> GetAllDepartmentsForComboBox()
	{
		ArrayList<String> departmentList = new ArrayList<String>();

		// loop departmentID Integers with the Departments map in organisation
		for(Integer departmentID : organisation.Departments.keySet())
		{
			Department department = organisation.GetDepartment(departmentID);
			departmentList.add(departmentID + " - " + department.getName());
		}

		Collections.sort(departmentList); // sort departmentList 
		departmentList.add(0, "Please select"); // add initial text on array index 0, ArrayList pushes others forward

		return departmentList;
	}

	// Name: 	AddEmpListener 
	// Purpose:	Act as an ActionListener for AddEmp  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. actionPerformed(ActionEvent event)			make/open frame event when AddEmp is clicked
	//			2. ButtonHandler implements ActionListener		action event for 'add' feature
	//			3. also contains the declarations for many swing features
	public class AddEmpListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnAdd, btnCancel;
		private JLabel lblEmployeeID, txtEmployeeID, lblFirstName, lblSurname, lblGender, lblAddress, lblPayLevel;
		private JTextField txtFirstName, txtSurname, txtAddress;
		private JRadioButton rblGenderMale, rblGenderFemale;
		private ButtonGroup btnGroup = new ButtonGroup();
		private JComboBox cbPayLevel;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	Act as an ActionListener for AddEmp  
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	1. if AddEmp is clicked, this opens
		//			2. jFrame makes a new window/frame
		//			3. new GridBagLayout() determines the type of layout
		//			4. GridBagConstraints c enables for editing position of components
		//			5. .addActionListener(btnHandler) enables the action listener for designated button
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Add an Employee"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Employee ID label
			lblEmployeeID = new JLabel("Employee ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblEmployeeID, c);

			// Employee ID label
			txtEmployeeID = new JLabel(String.format("%d", organisation.GetAvailableEmployeeID()));
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 3;		// how far it leans
			c.gridy = 1;		// 2nd row [position in rows]
			jFrame.add(txtEmployeeID, c);

			// First Name Label
			lblFirstName = new JLabel("First Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblFirstName, c);

			// First Name Text Input
			txtFirstName = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(txtFirstName, c);

			// Surname Label
			lblSurname = new JLabel("Surname:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblSurname, c);

			// Surname Text Input
			txtSurname = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(txtSurname, c);

			// Gender Label
			lblGender = new JLabel("Gender:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(lblGender, c);

			// Gender ComboBox
			rblGenderMale = new JRadioButton("M");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 3;		// how far it leans
			c.gridy = 4;		// [position in rows]
			rblGenderMale.setSelected(true);
			jFrame.add(rblGenderMale, c);
			btnGroup.add(rblGenderMale);

			// Gender ComboBox
			rblGenderFemale = new JRadioButton("F");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 4;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(rblGenderFemale, c);
			btnGroup.add(rblGenderFemale);

			// Address Label
			lblAddress = new JLabel("Address:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(lblAddress, c);

			// Address Text Input
			txtAddress = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(txtAddress, c);


			// Pay Level Label
			lblPayLevel = new JLabel("Pay Level:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(lblPayLevel, c);

			// ComboBox
			ArrayList<String> payLevels = GetAllPayLevelsForComboBox();
			cbPayLevel = new JComboBox(payLevels.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(cbPayLevel, c);

			// Add button
			btnAdd = new JButton ("Add");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.ipadx = 20;		// makes component wider
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 7;		// [position in rows]
			jFrame.add(btnAdd, c);
			btnAdd.addActionListener(btnHandler);

			// Cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.ipadx = 0;		// makes component wider
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 3 columns wide
			c.gridx = 4;		// how far it leans
			c.gridy = 7;		// [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(300, 300);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	ButtonHandler 
		// Purpose:	Act as an ActionListener for the buttons within AddEmp  
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	Add or Cancel will carry out their task
		private class ButtonHandler implements ActionListener 
		{
			// Name: 	actionPerformed 
			// Purpose:	Define the action performed  
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	If btnAdd is pressed, it adds to employee and is kept by organisation.AddEmployee
			//			If btnCancel, the frame is disposed and nothing is saved
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == btnAdd) 
				{
					try
					{
						int employeeID = Integer.parseInt(txtEmployeeID.getText()); // get int to string
						int payLevel = Integer.parseInt(cbPayLevel.getSelectedItem().toString()); // get int to string
						String firstName = txtFirstName.getText();
						String surname = txtSurname.getText();
						char gender = 'M'; // default gender, char uses ''

						if (rblGenderFemale.getModel() == btnGroup.getSelection()) // if female is selected
						{
							gender = 'F';
						}

						String address = txtAddress.getText();

						Employee employee = new Employee(employeeID, payLevel, firstName, surname, gender, address);

						organisation.AddEmployee(employeeID, employee);
						jFrame.dispose(); // exit frame
					}

					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	EditEmpListener 
	// Purpose:	Act as an ActionListener for EditEmp  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	1. actionPerformed(ActionEvent event)		make/open frame event when EditEmp is clicked
	//			2. SetEmployeeDetails						setting employee details
	//			3. ButtonHandler							action event for 'edit' feature
	//			4. also contains the declarations for many swing features
	public class EditEmpListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnEdit, btnCancel;
		private JLabel lblEmployeeID, lblFirstName, lblSurname, lblGender, lblAddress, lblPayLevel;
		private JTextField txtFirstName, txtSurname, txtAddress;
		private JComboBox cbEmployeeID, cbPayLevel;
		private JRadioButton rblGenderMale, rblGenderFemale;
		private ButtonGroup btnGroup = new ButtonGroup();
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	Create frame when edit emp is pressed
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	a GUI with buttons/text boxes/radio boxes/etc. which responds to input 
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Edit an Employee"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Employee ID label
			lblEmployeeID = new JLabel("Employee ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblEmployeeID, c);

			// Employee ID combo box
			ArrayList<String> employeeList = GetAllEmployeesForComboBox();
			cbEmployeeID = new JComboBox(employeeList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbEmployeeID, c);
			cbEmployeeID.addActionListener(btnHandler);

			// First Name Label
			lblFirstName = new JLabel("First Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblFirstName, c);

			// First Name Text Input
			txtFirstName = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(txtFirstName, c);

			// Surname Label
			lblSurname = new JLabel("Surname:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblSurname, c);

			// Surname Text Input
			txtSurname = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(txtSurname, c);

			// String[] genders = { "M", "F" };

			// Gender Label
			lblGender = new JLabel("Gender:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(lblGender, c);

			// Gender ComboBox
			rblGenderMale = new JRadioButton("M");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 4;		// [position in rows]
			rblGenderMale.setSelected(true);
			jFrame.add(rblGenderMale, c);
			btnGroup.add(rblGenderMale);

			// Gender ComboBox
			rblGenderFemale = new JRadioButton("F");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(rblGenderFemale, c);
			btnGroup.add(rblGenderFemale);

			// Address Label
			lblAddress = new JLabel("Address:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(lblAddress, c);

			// Address Text Input
			txtAddress = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(txtAddress, c);

			// Pay Level Label
			lblPayLevel = new JLabel("Pay Level:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 7;		// [position in rows]
			jFrame.add(lblPayLevel, c);

			// ComboBox
			ArrayList<String> payLevels = GetAllPayLevelsForComboBox();
			cbPayLevel = new JComboBox(payLevels.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 7;		// [position in rows]
			jFrame.add(cbPayLevel, c);

			// Edit button
			btnEdit = new JButton ("Edit");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 8;		// 2nd row [position in rows]
			jFrame.add(btnEdit, c);
			btnEdit.addActionListener(btnHandler);
			//hide the button since can not edit at this time
			btnEdit.setVisible(false);

			// Cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 8;		// 3rd row [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(320, 300);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	SetEmployeeDetails 
		// Purpose:	Show details when an employee is selected
		// Passed: 	employeeID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	When an employee id is chosen, his/her details will show as well as the edit button 
		private void SetEmployeeDetails(int employeeID)
		{
			rblGenderMale.setSelected(true);	// default
			rblGenderFemale.setSelected(false);	// not default

			if(employeeID > 0)
			{
				Employee employee = organisation.GetEmployee(employeeID);

				// pre-populate data
				cbPayLevel.setSelectedItem(String.valueOf(employee.getPayLevel()));
				txtFirstName.setText(employee.getFirstName());
				txtSurname.setText(employee.getSurname());

				if(employee.getGender() == 'F')
				{
					rblGenderMale.setSelected(false);
					rblGenderFemale.setSelected(true);
				}

				txtAddress.setText(employee.getAddress());

				// display the edit button as something is now selected
				btnEdit.setVisible(true);
			}

			// so you cannot change/edit false or black information
			else
			{
				cbPayLevel.setSelectedIndex(0);
				txtFirstName.setText("");
				txtSurname.setText("");
				txtAddress.setText("");
				//hide the button since can not edit at this time
				btnEdit.setVisible(false);
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	ActionListener for EditEmp buttons
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	When an employee id is chosen, his/her details will show as well as the edit button
		private class ButtonHandler implements ActionListener 
		{
			// Name: 	actionPerformed 
			// Purpose:	ActionListener for EditEmp buttons
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	if an employee ID is selected, SetEmployeeDetails is called and their details will appear
			//			if edit is pressed, the new details will save and exit will dispose
			public void actionPerformed( ActionEvent event ) 
			{
				// if the event involves the EmployeeID combo box
				if (event.getSource() == cbEmployeeID) 
				{
					int employeeID = 0;
					if(cbEmployeeID.getSelectedIndex() != 0)
					{
						// employeeID is the one selected from ComboBox
						employeeID = GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString());
					}

					// call SetEmployeeDetails(???) method with 
					// employeeID sent is the one that is selected
					SetEmployeeDetails(employeeID);
				}

				if (event.getSource() == btnEdit) 
				{
					try
					{
						if(cbEmployeeID.getSelectedIndex() != 0)
						{
							int employeeID = GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString());
							int payLevel = Integer.parseInt(cbPayLevel.getSelectedItem().toString());
							String firstName = txtFirstName.getText();
							String surname = txtSurname.getText();
							char gender = 'M';

							if(rblGenderFemale.getModel() == btnGroup.getSelection())
							{
								gender = 'F';
							}

							String address = txtAddress.getText();

							Employee employee = organisation.GetEmployee(employeeID);
							employee.SetDetails(payLevel, firstName, surname, gender, address);

							organisation.EditEmployee(employeeID, employee);
						}

						jFrame.dispose();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	DeleteEmpListener 
	// Purpose:	Enable the function for DeleteEmp
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	deletes the employee and makes sure the same ID is not re-usable
	public class DeleteEmpListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnDelete, btnCancel;
		private JComboBox cbEmployeeID;
		private JLabel lblEmployeeID, lblFirstName, lblSurname, lblGender, lblAddress, lblPayLevel;
		private JLabel lblFirstNameVal, lblSurnameVal, lblGenderVal, lblAddressVal, lblPayLevelVal;

		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	Enable the function for DeleteEmp
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	create the frame when DeleteEmp is clicked
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Delete an Employee"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Employee ID label
			lblEmployeeID = new JLabel("Employee ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblEmployeeID, c);

			// Employee ID combo box
			ArrayList<String> employeeList = GetAllEmployeesForComboBox();
			cbEmployeeID = new JComboBox(employeeList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbEmployeeID, c);
			cbEmployeeID.addActionListener(btnHandler);

			// First Name Label
			lblFirstName = new JLabel("First Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblFirstName, c);

			// First Name Value
			lblFirstNameVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblFirstNameVal, c);

			// Surname Label
			lblSurname = new JLabel("Surname:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblSurname, c);

			// Surname Value
			lblSurnameVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblSurnameVal, c);

			// Gender Label
			lblGender = new JLabel("Gender:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(lblGender, c);

			// Gender Value
			lblGenderVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(lblGenderVal, c);

			// Address Label
			lblAddress = new JLabel("Address:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(lblAddress, c);

			// Address Value
			lblAddressVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(lblAddressVal, c);

			// Pay Level Label
			lblPayLevel = new JLabel("Pay Level:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(lblPayLevel, c);

			// Pay Level Value
			lblPayLevelVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(lblPayLevelVal, c);

			// delete button
			btnDelete = new JButton ("Delete");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 0;		// how far it leans
			c.gridy = 7;		// [position in rows]
			jFrame.add(btnDelete, c);
			btnDelete.addActionListener(btnHandler);
			//hide the button since can not edit at this time
			btnDelete.setVisible(false);

			// cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 7;		// [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(320, 300);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	SetEmployeeDetails 
		// Purpose:	Enable the function for DeleteEmp
		// Passed: 	employeeID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	the passed employeeID is processed to automatically show the details
		private void SetEmployeeDetails(int employeeID)
		{
			if(employeeID > 0)
			{
				Employee employee = organisation.GetEmployee(employeeID);

				// pre-populate data
				lblPayLevelVal.setText(String.valueOf(employee.getPayLevel()));
				lblFirstNameVal.setText(employee.getFirstName());
				lblSurnameVal.setText(employee.getSurname());
				lblGenderVal.setText(String.valueOf(employee.getGender()));
				lblAddressVal.setText(employee.getAddress());
				//display the edit button
				btnDelete.setVisible(true);
			}

			else
			{
				lblPayLevelVal.setText("");
				lblFirstNameVal.setText("");
				lblSurnameVal.setText("");
				lblGenderVal.setText("");
				lblAddressVal.setText("");
				//hide the button since can not edit at this time
				btnDelete.setVisible(false);
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	ActionListener for DeleteEmp
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString()) method splits selected string
		//			btnDelete deletes the selected employee, btnCancel will dispose
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == cbEmployeeID) 
				{
					int employeeID = 0;
					if(cbEmployeeID.getSelectedIndex() != 0)
					{
						// call GetIntFromComboBox to split the employeeID (10000 - Martin Wright)
						// you only want the number...
						employeeID = GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString());
					}

					// auto fill details of selected employeeID
					SetEmployeeDetails(employeeID);
				}

				if (event.getSource() == btnDelete) 
				{
					try
					{
						if(cbEmployeeID.getSelectedIndex() != 0)
						{
							int employeeID = GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString());
							organisation.RemoveEmployee(employeeID, true, true, true); // remove in organisation
						}

						jFrame.dispose();
					}

					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	AddDepListener 
	// Purpose:	ActionListener for AddDep
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	adds a department, declarations are made
	public class AddDepListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnAdd, btnCancel;
		private JLabel lblDepartmentID, txtDepartmentID, lblName, lblLocation;
		private JTextField txtName, txtLocation;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	create frame for AddDep
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	adds the GUI components and adds listeners
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Add a Department"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Department ID label
			lblDepartmentID = new JLabel("Department ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblDepartmentID, c);

			// Department ID label
			txtDepartmentID = new JLabel(String.format("%d", organisation.GetAvailableDepartmentID()));
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(txtDepartmentID, c);

			// Name Label
			lblName = new JLabel("Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblName, c);

			// Name Text Input
			txtName = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(txtName, c);

			// Location Label
			lblLocation = new JLabel("Location:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblLocation, c);

			// Location Text Input
			txtLocation = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(txtLocation, c);

			// Add button
			btnAdd = new JButton ("Add");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 0;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(btnAdd, c);
			btnAdd.addActionListener(btnHandler);

			// Cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(300, 160);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	ButtonHandler 
		// Purpose:	ActionListener when button is pressed
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	a department will be added or the frame will be disposed
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event

			// Name: 	actionPerformed 
			// Purpose:	to add departments
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	a department will be added or the frame will be disposed
			//			Department department makes a new department with the current status
			//			organisation.AddDepartment adds the department
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == btnAdd) 
				{
					try
					{
						int departmentID = Integer.parseInt(txtDepartmentID.getText()); // conversion required
						String name = txtName.getText();
						String location = txtLocation.getText();

						Department department = new Department(departmentID, name, location);

						organisation.AddDepartment(departmentID, department);
						jFrame.dispose();
					}

					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	EditDepListener 
	// Purpose:	ActionListener for EditDep
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	edits a department, declarations are made
	public class EditDepListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnEdit, btnCancel;
		private JComboBox cbDepartmentID;
		private JLabel lblDepartmentID, lblName, lblLocation;
		private JTextField txtName, txtLocation;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	creates GUI components for EditDep
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	a new frame that allows for editing
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Edit a Department"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Department ID label
			lblDepartmentID = new JLabel("Department ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblDepartmentID, c);

			// Department ID combo box
			ArrayList<String> departmentList = GetAllDepartmentsForComboBox();
			cbDepartmentID = new JComboBox(departmentList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 3;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbDepartmentID, c);
			cbDepartmentID.addActionListener(btnHandler);

			// Name Label
			lblName = new JLabel("Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblName, c);

			// Name Text Input
			txtName = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 3;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(txtName, c);

			// Location Label
			lblLocation = new JLabel("Location:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblLocation, c);

			// Location Text Input
			txtLocation = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 3;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(txtLocation, c);

			// Edit button
			btnEdit = new JButton ("Edit");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 0;		// how far it leans
			c.gridy = 4;		// 4th row [position in rows]
			jFrame.add(btnEdit, c);
			btnEdit.addActionListener(btnHandler);
			//hide the button since can not edit at this time
			btnEdit.setVisible(false);

			// Cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 4;		// 4th row [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(300, 160);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	SetDepartmentDetails 
		// Purpose:	sets department details when the ID is selected
		// Passed: 	departmentID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	the selected ID from the combo box will get its corresponding values
		private void SetDepartmentDetails(int departmentID)
		{
			// if you are wondering what > 0 is for
			// it is because 0 is already set for "Please Select"
			// or to enable an if else statement
			if(departmentID > 0)
			{
				// get the passed ID from organisation
				Department department = organisation.GetDepartment(departmentID);

				// pre-populate data
				txtName.setText(department.getName());
				txtLocation.setText(department.getLocation());
				//display the edit button
				btnEdit.setVisible(true);
			}

			else
			{
				txtName.setText("");
				txtLocation.setText("");

				//hide the button since can not edit at this time
				btnEdit.setVisible(false);
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	enables the function when clicked
		// Passed: 	departmentID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	department will be edited
		//			.getSelectedItem() refers to the currently selected item
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event

			// Name: 	actionPerformed 
			// Purpose:	action listener
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	edit department, cancel edit
			//			organisation.GetDepartment and organisation.EditDepartment are used to alter
			public void actionPerformed( ActionEvent event ) 
			{
				// if dep ID combo box is selected...
				if (event.getSource() == cbDepartmentID) 
				{
					int departmentID = 0;

					if(cbDepartmentID.getSelectedIndex() != 0) // != 0 because that is "Please Select"/default text
					{
						// shows details of selected item
						departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString());
					}

					// a call to the method which puts the corresponding values
					SetDepartmentDetails(departmentID);
				}

				if (event.getSource() == btnEdit)
				{
					try
					{
						if(cbDepartmentID.getSelectedIndex() != 0)
						{
							int departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString());
							String name = txtName.getText();
							String location = txtLocation.getText();

							// change the department details
							Department department = organisation.GetDepartment(departmentID);
							department.setName(name);
							department.setLocation(location);

							// call organisations EditDepartment method
							// department is the detail changes and departmentID is the ID from the combo box
							organisation.EditDepartment(departmentID, department);
						}

						jFrame.dispose();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	DeleteDepListener 
	// Purpose:	to delete departments
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	deletes a department, declarations are made
	public class DeleteDepListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnDelete, btnCancel;
		private JComboBox cbDepartmentID;
		private JLabel lblDepartmentID, lblName, lblLocation, lblNameVal, lblLocationVal;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	create GUI frame for delete function
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	makes an interface and adds action listeners
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Delete a Department"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Department ID label
			lblDepartmentID = new JLabel("Department ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 3 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblDepartmentID, c);

			// Department ID combo box
			ArrayList<String> departmentList = DepartmentsThatCanBeDeleted();
			cbDepartmentID = new JComboBox(departmentList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbDepartmentID, c);
			cbDepartmentID.addActionListener(btnHandler);

			// Name Label
			lblName = new JLabel("Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 3 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblName, c);

			// Name Text Value
			lblNameVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblNameVal, c);

			// Location Label
			lblLocation = new JLabel("Location:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 3 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblLocation, c);

			// Location Text Value
			lblLocationVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblLocationVal, c);

			// Delete button
			btnDelete = new JButton ("Delete");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 0;		// how far it leans
			c.gridy = 4;		// 2nd row [position in rows]
			jFrame.add(btnDelete, c);
			btnDelete.addActionListener(btnHandler);
			//hide the button since can not edit at this time
			btnDelete.setVisible(false);

			// Cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 3;	// 3 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 4;		// 3rd row [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(300, 160);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	DepartmentsThatCanBeDeleted 
		// Purpose:	only departments without members can be deleted
		// Passed: 	Nothing
		// Returns: departmentList
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	returns a new department list with no employees in them
		private ArrayList<String> DepartmentsThatCanBeDeleted()
		{
			ArrayList<String> departmentList = new ArrayList<String>();
			departmentList.add("Please select"); // initial

			String[] departmentIDs = organisation.GetAllDepartmentIDs(); // get all IDs
			if(departmentIDs.length > 0) // 0 is taken as "Please select"
			{
				for(String departmentID : departmentIDs) // enhance loop the new array with a new String
				{
					int dID = Integer.parseInt(departmentID); // convert the string array to int via parse
					Department department = organisation.GetDepartment(dID); // match via dID

					// if no one exists in the department
					if(department.GetAllEmployees().size() == 0 && department.getHeadEmployeeID() == 0)
					{
						departmentList.add(departmentID + " - " + department.getName());
					}
				}
			}

			return departmentList;
		}

		// Name: 	SetDepartmentDetails 
		// Purpose:	show department details when selected
		// Passed: 	departmentID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	shows the details of the selected department, if no match, show nothing
		private void SetDepartmentDetails(int departmentID)
		{
			if(departmentID > 0)
			{
				Department department = organisation.GetDepartment(departmentID);

				// pre-populate data
				lblNameVal.setText(department.getName());
				lblLocationVal.setText(department.getLocation());
				//display the edit button
				btnDelete.setVisible(true);
			}

			else
			{
				lblNameVal.setText("");
				lblLocationVal.setText("");
				//hide the button since can not edit at this time
				btnDelete.setVisible(false);
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	action listener
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	deletes the selected department
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event

			// Name: 	actionPerformed 
			// Purpose:	action listener
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	deletes the selected department, shows dep info if combo box is selected, dispose at will
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == cbDepartmentID) 
				{
					int departmentID = 0;

					if(cbDepartmentID.getSelectedIndex() != 0)
					{
						// departmentID = currently selected
						departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString());
					}

					// send departmentID to SetDepartmentDetails for showing details
					SetDepartmentDetails(departmentID);
				}

				if (event.getSource() == btnDelete) 
				{
					try
					{
						int departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString());
						organisation.RemoveDepartment(departmentID);
						jFrame.dispose();
					}

					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	ManageDepListener 
	// Purpose:	set department head or remove all employees
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	described in purpose
	public class ManageDepListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnUpdateHead, btnRemoveAllEmployees, btnCancel;
		private JComboBox cbDepartmentID, cbHeadEmployeeID;
		private JLabel lblDepartmentID, lblName, lblLocation, lblNameVal, lblLocationVal;
		private JLabel lblHeadEmployeeID;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	create GUI frame
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	GUI with options
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Manage a Department"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Department ID label
			lblDepartmentID = new JLabel("Department ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblDepartmentID, c);

			// Department ID combo box
			ArrayList<String> departmentList = GetAllDepartmentsForComboBox();
			cbDepartmentID = new JComboBox(departmentList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbDepartmentID, c);
			cbDepartmentID.addActionListener(btnHandler);

			// Name Label
			lblName = new JLabel("Name:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblName, c);

			// Name Text Value
			lblNameVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblNameVal, c);

			// Location Label
			lblLocation = new JLabel("Location:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblLocation, c);

			// Location Text Value
			lblLocationVal = new JLabel();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblLocationVal, c);

			// Head ID label
			lblHeadEmployeeID = new JLabel("Department Head:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(lblHeadEmployeeID, c);
			lblHeadEmployeeID.setVisible(false);

			// Head ID combo box
			cbHeadEmployeeID = new JComboBox();
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 4;		// [position in rows]
			jFrame.add(cbHeadEmployeeID, c);
			cbHeadEmployeeID.setVisible(false);

			// Update Head button
			btnUpdateHead = new JButton ("Update Head");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(btnUpdateHead, c);
			btnUpdateHead.addActionListener(btnHandler);
			btnUpdateHead.setVisible(false);

			// Remove All button
			btnRemoveAllEmployees = new JButton ("Remove All Employees");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 6;		// [position in rows]
			jFrame.add(btnRemoveAllEmployees, c);
			btnRemoveAllEmployees.addActionListener(btnHandler);
			btnRemoveAllEmployees.setVisible(false);

			// Cancel button
			btnCancel = new JButton ("Cancel");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 3;		// how far it leans
			c.gridy = 5;		// [position in rows]
			jFrame.add(btnCancel, c);
			btnCancel.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(350, 240);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	SetEligibleHeads 
		// Purpose:	set up eligible employees to be head
		// Passed: 	departmentID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	employees can only be potential heads of the department they are assigned to
		private void SetEligibleHeads(int departmentID)
		{
			lblHeadEmployeeID.setVisible(false);
			cbHeadEmployeeID.setVisible(false);
			btnUpdateHead.setVisible(false);
			btnRemoveAllEmployees.setVisible(false);

			cbHeadEmployeeID.removeAllItems(); // emptied as you do not want ALL employees (department only)
			cbHeadEmployeeID.insertItemAt("Please select", 0);

			if(departmentID > 0)
			{
				Department department = organisation.GetDepartment(departmentID); // passed dep ID
				ArrayList<Integer> employees = department.GetAllEmployees(); // get all emp in that dep

				if(employees.size() != 0) // show if there are employees
				{
					lblHeadEmployeeID.setVisible(true);
					cbHeadEmployeeID.setVisible(true);
					btnUpdateHead.setVisible(true);
					btnRemoveAllEmployees.setVisible(true);

					int position = 1; // 0 is for "Please select"

					for(int employeeID : employees)
					{
						Employee employee = organisation.GetEmployee(employeeID);
						String option = employeeID + " - " + employee.getFirstName() + " " + employee.getSurname();
						cbHeadEmployeeID.insertItemAt(option, position);
						position++;
					}

					if(department.getHeadEmployeeID() != 0) // if a head exists (head cannot be 0)
					{
						Employee headEmployee = organisation.GetEmployee(department.getHeadEmployeeID());
						String head = headEmployee.getEmployeeID() + " - " + headEmployee.getFirstName() + " " + headEmployee.getSurname();
						cbHeadEmployeeID.setSelectedItem(head);
					}

					else cbHeadEmployeeID.setSelectedIndex(0); // if no head, set 0 as default
				}
			}
		}

		// Name: 	SetDepartmentDetails 
		// Purpose:	set up details of selected department
		// Passed: 	departmentID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	shows department details if method is called
		private void SetDepartmentDetails(int departmentID)
		{
			if(departmentID > 0)
			{
				Department department = organisation.GetDepartment(departmentID);

				// pre-populate data
				lblNameVal.setText(department.getName());
				lblLocationVal.setText(department.getLocation());
			}

			else
			{
				lblNameVal.setText("");
				lblLocationVal.setText("");
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	action listener
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	sets the department head or deletes all employees in a department or cancels
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event
			
			// Name: 	actionPerformed 
			// Purpose:	set head, remove all employees, cancel
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	method calls are used to update/remove, feature to show selected details added via method call as well
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == cbDepartmentID) 
				{
					int departmentID = 0;
					if(cbDepartmentID.getSelectedIndex() != 0)
					{
						departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString());
					}

					SetDepartmentDetails(departmentID); // method call
					SetEligibleHeads(departmentID); // method call
				}

				if (event.getSource() == btnUpdateHead) 
				{
					try
					{
						if(cbDepartmentID.getSelectedIndex() != 0)
						{
							int departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString()); // get selected
							int headEmployeeID = GetIntFromComboBox(cbHeadEmployeeID.getSelectedItem().toString());
							Department department = organisation.GetDepartment(departmentID); // get dep id
							department.setHeadEmployeeID(headEmployeeID); // set dep head in specified dep
							organisation.EditDepartment(departmentID, department); // set in organisation.EditDepartment
						}

						jFrame.dispose();
					}

					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnRemoveAllEmployees) 
				{
					try
					{
						// turn selected combo box ids into a int as they are strings by calling the GetIntFromComboBox method which splits the string
						// and gets only the first array index, which is a int
						int departmentID = GetIntFromComboBox(cbDepartmentID.getSelectedItem().toString());
						Department department = organisation.GetDepartment(departmentID);
						department.RemoveAllEmployees(); // call method
						organisation.EditDepartment(departmentID, department); // update in organisation
						jFrame.dispose();
					}
					
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

				if (event.getSource() == btnCancel) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	DisplayDepEmpListener 
	// Purpose:	displays employees in a department
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	shows all employees in a department and the head
	public class DisplayDepEmpListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnClose;
		private JComboBox cbDepartment;
		private JLabel lblDepartment;
		private JTextArea txtEmployeeDetails;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	create GUI for the button
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	shows department choices, and a text box
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Display Employees in Department"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Department label
			lblDepartment = new JLabel("Department:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblDepartment, c);

			// Department ID combo box
			ArrayList<String> departmentList = GetAllDepartmentsForComboBox();
			cbDepartment = new JComboBox(departmentList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 3 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbDepartment, c);
			cbDepartment.addActionListener(btnHandler);

			txtEmployeeDetails = new JTextArea();
			txtEmployeeDetails.setWrapStyleWord(true); // enable word wrap
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 200;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 2 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 2;		// [position in rows]
			
			JScrollPane scrollPane = new JScrollPane(txtEmployeeDetails);	// JTextArea is placed in a JScrollPane.
			jFrame.getContentPane().add(scrollPane, c);	// add to frame with constraints

			// Cancel button
			btnClose = new JButton ("Close");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 2;	// 1 column wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// 3rd row [position in rows]
			jFrame.add(btnClose, c);
			btnClose.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(320, 320);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	SetDepartmentDetails 
		// Purpose:	create GUI for the button
		// Passed: 	departmentID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	gets employees from selected department, puts in a hash map then array list and prints it in text area
		private void SetDepartmentDetails(int departmentID)
		{
			if(departmentID > 0)
			{
				StringBuilder report = new StringBuilder(); // create a string builder to build on to the shown string via .append() - (more convienient)
				Department department = organisation.GetDepartment(departmentID); // get the department ID

				Map<Integer, Employee> map = new HashMap<Integer, Employee>(); // create a hash map to hold employees

				for (int employeeID : department.GetAllEmployees()) // get all employees from the department (department = organisation.GetDepartment(departmentID))
				{
					Employee employee = organisation.GetEmployee(employeeID); // make a new employee which contains the index/IF from the loop

					if (employee != null) // if an employee exists
					{
						map.put(employeeID, employee); // add in hash map
					}
				}

				// makes a new array list using map (the previously made hash map)
				// utilises list collection so List<Map.Entry> must be used
				// The 'name of Map'.entrySet method returns a collection-view of the map, whose elements are of this class.
				// Map.Entry and .entrySet() gets the entire 'map' created previously, it contains the key AND value
				// System.out.println(employeeList.toString()); will therefore print everything in employeeList
				List<Map.Entry> employeeList = new ArrayList<Map.Entry>(map.entrySet());
				organisation.SortEmployeeByPayLevel(employeeList); // calls a method in organisation that sorts List<Map.Entry> types
				
				report.append("------ EMPLOYEES IN DEPARTMENT ------");

				for (Map.Entry e : employeeList) 
				{
					Employee employee = (Employee) e.getValue(); // 
					double annualPay = payLevelClass.getAnnualAmount(employee.getPayLevel()); // get annual pay via emp pay level

					// create the report
					report.append("\n");
					report.append(String.format("\nEmployee ID:\t\t%s", employee.getEmployeeID()));
					report.append(String.format("\nFirst Name:\t\t%s", employee.getFirstName()));
					report.append(String.format("\nSurname:\t\t%s", employee.getSurname()));
					report.append(String.format("\nAnnual Pay:\t\t$%.2f", annualPay));
					
					// if the employee is a head
					if(employee.getEmployeeID() == department.getHeadEmployeeID())
					{
						report.append(String.format("\nHead Bonus:\t\t$%.2f", HEAD_BONUS));
						report.append(String.format("\nTotal Annual Pay:\t$%.2f", annualPay + HEAD_BONUS));
					}
				}
				
				// print the report in the text area
				txtEmployeeDetails.setText(report.toString());
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	action listener for button
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	matches employees to selected department and prints in text area or exit
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == cbDepartment) 
				{
					int departmentID = 0;
					if(cbDepartment.getSelectedIndex() != 0)
					{
						departmentID = GetIntFromComboBox(cbDepartment.getSelectedItem().toString());
					}
					SetDepartmentDetails(departmentID);
				}

				if (event.getSource() == btnClose) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	PayReportListener 
	// Purpose:	displays a pay report
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	shows a pay report of all dep and emps
	public class PayReportListener implements ActionListener
	{

		// Name: 	actionPerformed 
		// Purpose:	action listener
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	calls GetFortnightlyPayReport method from organisation, which shows report in console
		public void actionPerformed(ActionEvent event)
		{
			organisation.GetFortnightlyPayReport(); // done via method call in organisation
		}
	}

	// Name: 	ManageEmpListener 
	// Purpose:	add employees to department
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	allows program to add employees to departments
	public class ManageEmpListener implements ActionListener
	{
		private JFrame jFrame;
		private JButton btnSave, btnClose;
		private JComboBox cbEmployeeID, cbDepartment;
		private JLabel lblEmployeeID, lblDepartment, lblNotification;
		private ButtonHandler btnHandler;

		// Name: 	actionPerformed 
		// Purpose:	create the interface when managing employees
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	shows an interface which allows for choosing an employee and setting them in a department
		public void actionPerformed(ActionEvent event)
		{
			jFrame = new JFrame();
			btnHandler = new ButtonHandler();
			jFrame.setTitle("Add Employees to Department"); // name the frame

			jFrame.setLayout(new GridBagLayout());  // set a GridBagLayout to tab0
			GridBagConstraints c = new GridBagConstraints(); // enable editing of grid

			// Employee ID label
			lblEmployeeID = new JLabel("Employee ID:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 1;		// 1st row [position in rows]
			jFrame.add(lblEmployeeID, c);

			// Employee ID combo box
			ArrayList<String> employeeList = GetAllEmployeesForComboBox();
			cbEmployeeID = new JComboBox(employeeList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 1;		// [position in rows]
			jFrame.add(cbEmployeeID, c);
			cbEmployeeID.addActionListener(btnHandler);

			// Notification label... apparently, java is capable of some HTML 
			lblNotification = new JLabel("<html>Please note: This employee is a department head.<br>You can not move the head of a department to another.</html>");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 2;		// [position in rows]
			jFrame.add(lblNotification, c);
			lblNotification.setVisible(false);

			// Department label
			lblDepartment = new JLabel("Department:");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(lblDepartment, c);
			lblDepartment.setVisible(false);

			// Department ID combo box
			ArrayList<String> departmentList = GetAllDepartmentsForComboBox();
			cbDepartment = new JComboBox(departmentList.toArray());
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 3;		// [position in rows]
			jFrame.add(cbDepartment, c);
			cbDepartment.setVisible(false);

			// Save Button
			btnSave = new JButton ("Save");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 1;		// how far it leans
			c.gridy = 4;		// 3rd row [position in rows]
			jFrame.add(btnSave, c);
			btnSave.addActionListener(btnHandler);
			btnSave.setVisible(false);

			// Cancel button
			btnClose = new JButton ("Close");
			c.fill = GridBagConstraints.HORIZONTAL; // alignment
			c.ipady = 10;		// makes component taller
			c.weightx = 0.5;	// request any extra horizontal space
			c.weighty = 0.5;	// request any extra vertical space
			c.gridwidth = 1;	// 1 columns wide
			c.gridx = 2;		// how far it leans
			c.gridy = 4;		// 3rd row [position in rows]
			jFrame.add(btnClose, c);
			btnClose.addActionListener(btnHandler);

			//set frame size
			jFrame.setSize(520, 220);
			//display frame
			jFrame.setVisible(true);
			//center frame
			jFrame.setLocationRelativeTo(null);
			//fixed size
			jFrame.setResizable(false);
		}

		// Name: 	SetEmployeeDetails 
		// Purpose:	show details of selected employee
		// Passed: 	employeeID
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	employeeID is processed to determine his/her allocated department
		//			also makes add and select department functions visible if ID is valid	
		private void SetEmployeeDetails(int employeeID)
		{
			//set to nothing by default
			cbDepartment.setSelectedIndex(0);
			//set as invisible
			lblNotification.setVisible(false);
			cbDepartment.setVisible(false);
			lblDepartment.setVisible(false);
			btnSave.setVisible(false);

			if(employeeID > 0)
			{
				cbDepartment.setVisible(true);
				lblDepartment.setVisible(true);
				btnSave.setVisible(true);

				String departmentName = organisation.WhatDepartmentEmployeeIsIn(employeeID);
				cbDepartment.setSelectedItem(departmentName);
				
				int departmentID = GetIntFromComboBox(departmentName);
				if(departmentID > 0)
				{
					Department department = organisation.GetDepartment(departmentID);
					
					// lblNotification is a notice if selected is a head
					if(department.getHeadEmployeeID() == employeeID)
					{
						lblNotification.setVisible(true);
						btnSave.setVisible(false); // Req-6: hey must remain assigned to that department for as long as they are the head
					}
				}
			}
		}

		// Name: 	ButtonHandler 
		// Purpose:	action listener for manage emp button
		// Passed: 	Nothing
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	employeeID is processed to determine his/her allocated department
		//			also makes add and select department functions visible if ID is valid
		private class ButtonHandler implements ActionListener 
		{
			// implements button press event
			
			// Name: 	actionPerformed 
			// Purpose:	action listener for manage emp button
			// Passed: 	event
			// Returns: Nothing
			// Input: 	Nothing
			// Output: 	Nothing
			// Effect: 	employeeID is processed to determine his/her allocated department
			//			also makes add and select department functions visible if ID is valid via method call
			//			add employee to department and update in organisation
			public void actionPerformed( ActionEvent event ) 
			{
				if (event.getSource() == cbEmployeeID) 
				{
					int employeeID = 0;
					if(cbEmployeeID.getSelectedIndex() != 0)
					{
						// if ??? id is selected from combo box
						employeeID = GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString());
					}
					
					// send it to SetEmployeeDetails for processing
					SetEmployeeDetails(employeeID);
				}

				if (event.getSource() == btnSave) 
				{
					if(cbEmployeeID.getSelectedIndex() != 0)
					{
						try
						{
							int employeeID = GetIntFromComboBox(cbEmployeeID.getSelectedItem().toString());
							int departmentID = GetIntFromComboBox(cbDepartment.getSelectedItem().toString());
							
							// remove employee from department and/or being head
							organisation.RemoveEmployee(employeeID, true, true, false);

							Department department = organisation.GetDepartment(departmentID);
							// add employee to department
							department.AddEmployee(employeeID);
							// update department in organisation
							organisation.EditDepartment(departmentID, department);
						} 
						
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					
					jFrame.dispose(); // exit after saving
				}

				if (event.getSource() == btnClose) 
				{
					jFrame.dispose(); // dispose() specifically exits the current frame (nothing is saved)
				}
			}
		}
	}

	// Name: 	SaveAndExitListener 
	// Purpose:	to save and exit
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	saves and exits program
	// Name: 	SaveAndExitListener 
	// Purpose:	to save and exit
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	described in purpose
	public class SaveAndExitListener implements ActionListener
	{
		// Name: 	actionPerformed 
		// Purpose:	to save and exit
		// Passed: 	event
		// Returns: Nothing
		// Input: 	Nothing
		// Output: 	Nothing
		// Effect: 	SERIALISE_FILE_NAME is the linke fore File/ObjectOutputStream
		//			FileOutputStream loads or creates org.ser if it does or does not exist
		//			ObjectOutputStream prints or saves all data
		public void actionPerformed(ActionEvent event)
		{
			System.out.println("\n**** Saving Orders to file...");

			try 
			{
				// serialise and create .ser file
				// putting true after the directory to append made it unable to be re-written and to stay fixed
				// putting false allows for continuation
				// **** use false because you already reload it at the start... 
				//		meaning what you do will be re-saved afterwards anyway
				// **** logically, using true would also work but it just keeps the file the way it is
				FileOutputStream fileOut = new FileOutputStream(SERIALISE_FILE_NAME, false);

				ObjectOutputStream out = new ObjectOutputStream(fileOut);

				// Serialises
				out.writeObject(organisation); 
				System.out.println("\n**** Data has been serialised");			
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
}