package function;

import java.io.Serializable;

// Name: 	Employee 
// Purpose:	To create employees  
// Passed: 	Nothing
// Returns: Nothing
// Input: 	Nothing
// Output: 	Nothing
// Effect: 	creates employees with set attributes
public class Employee implements Serializable
{
	private static final long serialVersionUID = 4659424145861337291L;

	private int EmployeeID;
	private int PayLevel;

	private String FirstName;
	private String Surname;
	private char Gender;
	private String Address;

	// Name: 	Employee 
	// Purpose:	constructor for Employee
	// Passed: 	int employeeID, int payLevel, String firstName, String surname, char gender, String address
	// Returns: Nothing
	// Input: 	Nothing
	// Output: 	Nothing
	// Effect: 	defines a employee by the passed values
	public Employee(int employeeID, int payLevel, String firstName, String surname, char gender, String address)
	{
		EmployeeID = employeeID;
		PayLevel = payLevel;
		FirstName = firstName;
		Surname = surname;
		Gender = gender;
		Address = address;
	}

	public void SetDetails(int payLevel, String firstName, String surname, char gender, String address)
	{
		PayLevel = payLevel;
		FirstName = firstName;
		Surname = surname;
		Gender = gender;
		Address = address;
	}

	public String toString()
	{
		//format the string for retrieval
		return "Employee ID:\t\t" + EmployeeID + "\nFirst Name:\t\t" + FirstName + "\nSurname:\t\t" + 
				Surname + "\nGender:\t\t\t" + Gender + "\nAddress:\t\t" + Address + "\nPay Level:\t\t" + PayLevel;
	}

	public int getEmployeeID()
	{
		return EmployeeID;
	}

	public int getPayLevel()
	{
		return PayLevel;
	}

	public String getFirstName()
	{
		return FirstName;
	}

	public String getSurname()
	{
		return Surname;
	}

	public char getGender()
	{
		return Gender;
	}

	public String getAddress()
	{
		return Address;
	}

	public void setEmployeeID(int employeeID) 
	{
		EmployeeID = employeeID;
	}

	public void setPayLevel(int payLevel) 
	{
		PayLevel = payLevel;
	}

	public void setFirstName(String firstName) 
	{
		FirstName = firstName;
	}

	public void setSurname(String surname) 
	{
		Surname = surname;
	}

	public void setSurname(char gender) 
	{
		Gender = gender;
	}

	public void setAddress(String address) 
	{
		Address = address;
	}
}
