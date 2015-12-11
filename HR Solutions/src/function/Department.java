package function;

import java.io.Serializable;
import java.util.ArrayList;

//Name: 	Department 
//Purpose:	To create departments and add/remove assigned employees  
//Passed: 	Nothing
//Returns: 	Nothing
//Input: 	Nothing
//Output: 	Nothing
//Effect: 	creates departments with set attributes and sort employees
public class Department implements Serializable
{
	private static final long serialVersionUID = -1507374393664013870L;
	
	private int DepartmentID;
	private String Name;
	private String Location;
	private int HeadEmployeeID;
	private ArrayList<Integer> Employees;
	
	// Name: 	Department 
	// Purpose:	constructor for Department
	// Passed: 	int departmentID, String name, String location
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	defines a employee by the passed values
	public Department(int departmentID, String name, String location)
	{
		DepartmentID = departmentID;
		Name = name;
		Location = location;
		HeadEmployeeID = 0;
		Employees = new ArrayList<Integer>();
	}
	
	// Name: 	IsEmployeeInDepartment 
	// Purpose:	check if employee exists in department
	// Passed: 	int employeeID
	// Returns: employeeInDepartment
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	checks employeeID and determines whether he/she is in department
	public boolean IsEmployeeInDepartment(int employeeID)
	{
		boolean employeeInDepartment = false;
		try
		{
			//check to see if they are apart of the department
			employeeInDepartment = Employees.contains(employeeID);
		}
		catch(Exception ex)
		{
			//if there is an error they aren't in the department
			employeeInDepartment = false;
		}
		
		return employeeInDepartment;
	}
	
	// Name: 	AddEmployee 
	// Purpose:	adds employee to department
	// Passed: 	int employeeID
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	checks employeeID and adds in department if he/she does not exist in it
	public void AddEmployee(int employeeID) throws Exception
	{
		try
		{
			//check to see if they are apart of the department
			if(!Employees.contains(employeeID))
			{
				//if not add them
				Employees.add(employeeID);
			}
		}
		
		catch(Exception ex)
		{
			throw ex;
		}
	}
	
	// not really used in assignment
	public void RemoveHead()
	{
		// given that 0 can not be used as an ID, set to 0 to remove
		// ID 0 in other cases is the default "Please select" text which is made & overwrites
		HeadEmployeeID = 0;
	}
	
	// Name: 	RemoveEmployee 
	// Purpose:	removes an employee from department
	// Passed: 	int employeeID
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	checks employeeID and deletes from department
	public void RemoveEmployee(int employeeID) throws Exception
	{
		try
		{
			//get the index of the employee in the array
			int index = Employees.indexOf(employeeID);
			
			if(index >= 0)
			{
				//if the employee is found delete them
				Employees.remove(index);
			}
		}
		
		catch(Exception ex)
		{
			throw ex;
		}
	}
	
	// Name: 	GetAllEmployees 
	// Purpose:	removes an employee from department
	// Passed: 	int employeeID
	// Returns: Employees
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	returns Employees as an Integer ArrayList
	public ArrayList<Integer> GetAllEmployees()
	{
		//get all employees
		return Employees;
	}
	
	// Name: 	RemoveAllEmployees 
	// Purpose:	removes all employees from department
	// Passed: 	Nothing
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	clears the ArrayList Employee and sets head on index 0
	public void RemoveAllEmployees()
	{
		//if we are removing all employees then there will be no head
		HeadEmployeeID = 0;
		//clear all employees
		Employees.clear();
	}
	
	public String toString()
	{
		//there may not be a head so set this value to N/A
		String empHead = "N/A";
		if(HeadEmployeeID != 0)
			empHead = Integer.toString(HeadEmployeeID);
		
		//format the string for retrieval
		return "DepartmentID:\t\t" + DepartmentID + "\nName:\t\t\t" + Name + "\nLocation:\t\t" + Location + "\nHead:\t\t\t" + empHead;
	}

	public int getDepartmentID()
	{
		return DepartmentID;
	}
	
	public String getName()
	{
		return Name;
	}
	
	public String getLocation()
	{
		return Location;
	}
	
	public int getHeadEmployeeID()
	{
		return HeadEmployeeID;
	}
	
	public void setDepartmentID(int departmentID) 
	{
		DepartmentID = departmentID;
	}
	
	public void setName(String name) 
	{
		Name = name;
	}

	public void setLocation(String location) 
	{
		Location = location;
	}
	
	public void setHeadEmployeeID(int headEmployeeID) 
	{
		HeadEmployeeID = headEmployeeID;
	}
}
