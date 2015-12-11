package function;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

// Name: 	Organisation 
// Purpose:	holds an organisation containing departments and employees  
// Passed: 	text
// Returns: Nothing
// Input: 	Nothing
// Output: 	Nothing
// Effect: 	allows for sorting and thus, allows for pay reports
public class Organisation implements Serializable
{
	private static final long serialVersionUID = 209262702127660104L;

	public String Name;

	public Map<Integer, Department> Departments;
	private int AvailableDepartmentID = 30000;

	public Map<Integer, Employee> Employees;
	private int AvailableEmployeeID = 10000;

	private static final int MAXIMUM_EMPLOYEE_ID = 29999;

	private static final String PAYROLL_TEXT_FILE_PATH = System.getProperty("user.dir") + "\\src\\files\\payroll.txt";

	// Name: 	Organisation 
	// Purpose:	composition of organisation  
	// Passed: 	name
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	names the organisation and has deparments and employees added via HashMap
	public Organisation(String name)
	{
		Name = name;
		Departments = new HashMap<Integer, Department>();
		Employees = new HashMap<Integer, Employee>();
	}

	public int GetAvailableDepartmentID()
	{
		return AvailableDepartmentID;
	}

	public int GetAvailableEmployeeID()
	{
		return AvailableEmployeeID;
	}

	// Name: 	GetAllEmployeeIDs 
	// Purpose:	get all employee IDs  
	// Passed: 	Nothing
	// Returns: employeeIDs
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	create a string array by looping ids with Employees map, sort and return
	public String[] GetAllEmployeeIDs()
	{
		String[] employeeIDs = new String[Employees.size()];
		int count = 0;
		for(Integer ids : Employees.keySet())
		{
			employeeIDs[count] = ids.toString();
			count++;
		}
		Arrays.sort(employeeIDs);
		return employeeIDs;
	}

	public Employee GetEmployee(int employeeID)
	{
		Employee employee = null;

		try
		{
			//check to see if the employee id exists
			if (Employees.containsKey(employeeID))
			{
				//if it does get it
				return Employees.get(employeeID);
				//don't return the object like this
				//this is because it will be returning the local object
				//we want the object that is being referenced
				//employee = Employees.get(employeeID);
			}
		}
		catch(Exception ex)
		{
			//don't do anything since employee will be null
		}

		return employee;
	}

	// Name: 	EditEmployee 
	// Purpose:	edit an employee  
	// Passed: 	int employeeID, Employee employee
	// Returns: isSuccessful
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	edit employee details via passed values and return true
	public boolean EditEmployee(int employeeID, Employee employee)
	{
		boolean isSuccessful = false;

		try
		{
			//check to see if the employee id exists
			if(Employees.containsKey(employeeID))
			{
				//if it does update it
				Employees.remove(employeeID);
				Employees.put(employeeID, employee);
			}
		}

		catch(Exception ex)
		{
			//don't do anything isSuccessful will be false
		}

		return isSuccessful;
	}

	// Name: 	AddEmployee 
	// Purpose:	add an employee  
	// Passed: 	int employeeID, Employee employee
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	add a employee under the passed ID and map key values
	public void AddEmployee(int employeeID, Employee employee) throws Exception
	{
		try
		{
			//add employee
			Employees.put(employeeID, employee);
			//increment employee id
			AvailableEmployeeID++;

			//throw an error if the employee id exceeds 5 digits and not starting with a 1 or 2
			if(AvailableEmployeeID == MAXIMUM_EMPLOYEE_ID)
			{
				throw new Exception("Maximum number of employees added");
			}
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	// Name: 	RemoveEmployee 
	// Purpose:	remove an employee  
	// Passed: 	int employeeID, boolean removeFromDepartment, boolean removeFromDepartmentHead, boolean removeFromOrganisation
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	searches for employee in all aspects and deletes all traces
	public void RemoveEmployee(int employeeID, boolean removeFromDepartment, boolean removeFromDepartmentHead, boolean removeFromOrganisation) throws Exception
	{
		try
		{
			//check if either of these are required so we don't waste time looping
			if(removeFromDepartment || removeFromDepartmentHead)
			{
				//loop through all the departments 
				//department is looped with Departments (the map)
				for (Department department : Departments.values())
				{
					//check to see if removing the head is required
					if(removeFromDepartmentHead)
					{
						//check to see if they are the head
						if(department.getHeadEmployeeID() == employeeID)
						{
							//set the department head id to zero
							department.setHeadEmployeeID(0);
						}
					}

					//check to see if removing from the department is required
					if(removeFromDepartment)
					{
						//check to see if they are in the department
						if(department.IsEmployeeInDepartment(employeeID))
						{
							//remove from being the department
							department.RemoveEmployee(employeeID);
						}
					}
				}
			}

			if(removeFromOrganisation)
			{
				//remove from organisation
				Employees.remove(employeeID);
			}
		}

		catch(Exception ex)
		{
			throw ex;
		}
	}

	public void RemoveAllEmployees()
	{
		//loop through all the departments
		for (Department department : Departments.values())
		{
			department.setHeadEmployeeID(0);
			department.RemoveAllEmployees();
		}

		//clear all employees
		Employees.clear();
	}

	public String[] GetAllDepartmentIDs()
	{
		String[] departmentIDs = new String[Departments.size()];
		int count = 0;
		for(Integer ids : Departments.keySet())
		{
			departmentIDs[count] = ids.toString();
			count++;
		}
		Arrays.sort(departmentIDs);
		return departmentIDs;
	}

	public Department GetDepartment(int departmentID)
	{
		//null by default
		Department department = null;

		try
		{
			//check to see if the department exists
			if(Departments.containsKey(departmentID))
			{
				//if it does get it
				return Departments.get(departmentID);

				//don't return the object like this
				//this is because it will be returning the local object
				//we want the object that is being referenced
				//department = Departments.get(departmentID);
			}
		}
		catch(Exception ex)
		{
			//don't do anything since department will be null
		}

		return department;
	}

	public boolean EditDepartment(int departmentID, Department department)
	{
		boolean isSuccessful = false;

		try
		{
			//check to see if the department id exists
			if(Departments.containsKey(departmentID))
			{
				//if it does update it
				Departments.remove(departmentID);
				Departments.put(departmentID, department);
			}
		}
		catch(Exception ex)
		{
			//don't do anything isSuccessful will be false
		}

		return isSuccessful;
	}

	public void AddDepartment(int departmentID, Department department) throws Exception
	{
		try
		{
			//add department
			Departments.put(departmentID, department);
			//increment id
			AvailableDepartmentID++;
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	public void AddEmployeeToDepartment(int departmentID, int employeeID) throws Exception
	{
		try
		{
			//get department
			Department department = GetDepartment(departmentID);
			//add employee to department
			department.AddEmployee(employeeID);
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	public void RemoveDepartment(int departmentID) throws Exception
	{
		try
		{
			//remove a department from the system
			Departments.remove(departmentID);
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	public void RemoveAllDepartments()
	{
		//remove all departments in the system
		Departments.clear();
	}

	// Name: 	SortDepartmentByName 
	// Purpose:	sort departments  
	// Passed: 	List<Map.Entry> departmentList
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	sorts the passed departmentList
	private void SortDepartmentByName(List<Map.Entry> departmentList)
	{
		// collection sorter for departmentList, creates a Comparator for the Map.Entry keys and values in the list
		// under the rules defined in the {} 
		// NOTE: 	compare(Map.Entry m1, Map.Entry m2) compares two values of the same type and orders by: a-z or 1-9
		//			it also auto 'increments and loops' or goes over all items... map: (a z c b) => s1.compareToIgnoreCase(s2) would do:
		//			a z c b ... a z ... a is before z ... a z c b
		//			z c b ... z c ... c is before z ... a c z b
		//			z b ... z b ... b is before z ... a c b z ... REPEATS AGAIN TILL ALPHABETICAL/NUMERICAL ORDER
		Collections.sort(departmentList, new Comparator<Map.Entry>() {
			public int compare(Map.Entry m1, Map.Entry m2)
			{
				Department d1 = (Department) m1.getValue(); // make d1 have the value of m1
				Department d2 = (Department) m2.getValue(); // make d2 have the value of m2

				String s1 = d1.getName(); // make s1 equal d1's name
				String s2 = d2.getName(); // make s2 equal d2's name

				// compareTo compares the 2 together by alphabetical/numerical order depending type
				return s1.compareToIgnoreCase(s2);
			}
		});
	}

	// Name: 	SortEmployeeByPayLevel 
	// Purpose:	sort employees  
	// Passed: 	List<Map.Entry> employeeList
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	sorts the passed employeeList
	public void SortEmployeeByPayLevel(List<Map.Entry> employeeList)
	{
		// collection sorter for employeeList, creates a Comparator for the Map.Entry keys and values in the list
		// under the rules defined in the {} 
		// NOTE: 	compare(Map.Entry m1, Map.Entry m2) compares two values of the same type and orders by: a-z or 1-9
		//			it also auto 'increments and loops' or goes over all items... map: (a z c b) => s1.compareToIgnoreCase(s2) would do:
		//			a z c b ... a z ... a is before z ... a z c b
		//			z c b ... z c ... c is before z ... a c z b
		//			z b ... z b ... b is before z ... a c b z ... REPEATS AGAIN TILL ALPHABETICAL/NUMERICAL ORDER
		Collections.sort(employeeList, new Comparator<Map.Entry>() {
			public int compare(Map.Entry m1, Map.Entry m2)
			{
				Employee e1 = (Employee) m1.getValue(); // make i1 have the value of m1
				Employee e2 = (Employee) m2.getValue(); // make i2 have the value of m2

				Integer i1 = e1.getPayLevel(); // make i1 equal e1's name
				Integer i2 = e2.getPayLevel(); // make i2 equal e2's name

				// compareTo compares the 2 together by alphabetical/numerical order depending type
				return i2.compareTo(i1);
			}
				});
		
	}
	
	public String WhatDepartmentEmployeeIsIn(int employeeID)
	{
		String departmentName = "";

		//loop to get all employee head ids in departments
		for(Department department : Departments.values())
		{
			for(int departmentEmployeeID : department.GetAllEmployees())
			{
				if(departmentEmployeeID == employeeID)
				{
					departmentName = String.format("%s - %s", department.getDepartmentID(), department.getName());
					break;
				}
			}
		}

		return departmentName;
	}

	public ArrayList<Employee> GetEmployeesNotInDepartments()
	{
		ArrayList<Integer> employeesInDepartments = new ArrayList<Integer>();
		ArrayList<Employee> employeesNotInDepartments = new ArrayList<Employee>();

		//loop to get all employee ids in departments
		for(Department department : Departments.values())
		{
			employeesInDepartments.addAll(department.GetAllEmployees());
		}

		//loop through all the employees in the organisation
		for(Employee employee : Employees.values())
		{
			//check to see if they are in a department
			if(!employeesInDepartments.contains(employee.getEmployeeID()))
			{
				//if not check to see if they are in the array list
				if(!employeesNotInDepartments.contains(employee))
				{
					//add it to the array list
					employeesNotInDepartments.add(employee);
				}
			}
		}

		return employeesNotInDepartments;
	}

	// Name: 	GetFortnightlyPayReport 
	// Purpose:	create a fortnightly report  
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	multiple loops get information which is printed into a string builder
	//			string builder was chosen specifically so that the same report can be printed to payroll.txt
	public void GetFortnightlyPayReport()
	{
		StringBuilder report = new StringBuilder(); // make the string builder
		PayLevel payLevel = new PayLevel(); // create a pay level class (for showing pay levels)

		ArrayList<Integer> employeesInDepartments = new ArrayList<Integer>();

		List<Map.Entry> departmentList = new ArrayList<Map.Entry>(Departments.entrySet()); // get all Departments via Map.Entry and .entrySet()
		SortDepartmentByName(departmentList); // sort this list by name

		double totalForCompany = 0; // total cost
		double totalForNonDepartments = 0; // total cost

		for(Map.Entry e : departmentList) // loops through the department list
		{
			Department department = (Department) e.getValue();
			String departmentName = department.getName();
			double totalForDepartment = 0; // total cost is put in here because you want each new dep to start at 0

			Map<Integer, Employee> depEmp = new HashMap<Integer, Employee>(); // create a HashMap for employees in department

			if(report.length() > 0) 
			{
				report.append("\n\n");
			}

			report.append("--------- " + departmentName + " Department ---------"); // append to the string builder

			for (int employeeID : department.GetAllEmployees()) // loop through the employee list now
			{
				Employee employee = GetEmployee(employeeID);
				if(employee!= null)
				{
					depEmp.put(employeeID, employee);

					if(!employeesInDepartments.contains(employeeID)) // add if ID is not already in the array list
					{
						employeesInDepartments.add(employeeID);
					}
				}
			}

			List<Map.Entry> depEmpList = new ArrayList<Map.Entry>(depEmp.entrySet()); // get all depEmpList via Map.Entry and .entrySet()
			SortEmployeeByPayLevel(depEmpList); // sort this list by name

			for(Map.Entry ee : depEmpList) // loop ee with depEmpList... this is to get employee details
			{
				Employee employee = (Employee) ee.getValue();
				double fortnightlyPay = payLevel.getFortnightlyAmount(employee.getPayLevel());

				if(report.length() > 0)
				{
					report.append("\n\n");
				}
				
				//print the employee details defined by (Employee) ee.getValue()
				//must also print fortnightlyPay seperately
				//String.format was used for the %.2f to round to decimal places
				//%s is for input of a plain string
				report.append(String.format(employee.toString() + "\nFortnightly Pay:\t$%.2f", fortnightlyPay));

				totalForDepartment += fortnightlyPay;
			}

			if(report.length() > 0)
			{
				report.append("\n\n");
			}
			
			// print total pay for department
			report.append(String.format("\nTotal Fortnightly Pay For %s Department:\t$%.2f", departmentName, totalForDepartment));

			totalForCompany += totalForDepartment; // add on to total for total pay
		}

		List<Map.Entry> employeeList = new ArrayList<Map.Entry>(); // makes a new employee list

		for(Map.Entry e : Employees.entrySet()) // loop with Map.Entry e for key and value
		{
			Employee employee = (Employee) e.getValue(); // employee equals the value
			
			if(!employeesInDepartments.contains(employee.getEmployeeID())) // if ID is not in a department... then employee is not in any department (this is outside the dep loop)
			{
				employeeList.add(e); // add to employeeList, which will hold all unassigned employees
			}
		}
		
		SortEmployeeByPayLevel(employeeList); // sort the list via method call

		if(employeeList.size() > 0) // if employees exist
		{
			report.append("\n\n--------- Not in a Department ---------");

			for(Map.Entry e : employeeList) // loop Map.Entry with employeeList to get keys and values
			{
				Employee employee = (Employee) e.getValue(); // employee will hold the value (Map.Entry e uess .getValue to get the data from employeeList)
				double fortnightlyPay = payLevel.getFortnightlyAmount(employee.getPayLevel());

				if(report.length() > 0)
				{
					report.append("\n\n");
				}
				
				// get the fortnightlypay for not in department employees
				report.append(String.format(employee.toString() + "\nFortnightly Pay:\t$%.2f", fortnightlyPay));

				totalForNonDepartments += fortnightlyPay; // add on to total for total pay
			}

			
			// print total pay from all non assigned employees
			report.append(String.format("\n\nTotal Fortnightly Pay For Non Departments:\t$%.2f", totalForNonDepartments));
		}

		// print total of whole system
		report.append(String.format("\nTotal Fortnightly Pay For Organisation %s:\t$%.2f", Name, totalForCompany));

		// print what was printed in report to the console
		// i.e. show the report in the java console
		System.out.println(report.toString());

		try 
		{
			// send the entire report string (reason for string builder) to a method to save to .txt
			WriteToFile(report.toString());
		} 

		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
	}

	// Name: 	WriteToFile 
	// Purpose:	create payroll.txt  
	// Passed: 	text
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	gets passed text from GetFortnightlyPayReport
	//			creates a new payroll.txt file under PAYROLL_TEXT_FILE_PATH
	//			create file if it does not exist
	//			create PrintStream to print the report vis 'PrintStream name'.println(text)
	private void WriteToFile(String text) throws Exception
	{
		try 
		{
			File file = new File(PAYROLL_TEXT_FILE_PATH);

			if(!file.exists())
			{
				file.createNewFile();
			}

			PrintStream saveFile = new PrintStream(file);
			saveFile.println(text);
		}

		catch(Exception ex)
		{
			throw ex;
		}
	}
}
