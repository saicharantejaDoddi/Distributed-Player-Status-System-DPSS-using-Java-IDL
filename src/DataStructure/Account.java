package DataStructure;

public class Account {
	public String firstName;
	public String lastName;
	public String age;
	public String password;
	public String ipAddress;
	public String userName;
	public String status;

	public Account(String firstName, String lastName, String age, String userName, String password, String ipAddress) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.userName = userName;
		this.password = password;
		this.ipAddress = ipAddress;
		this.status = "OFFLINE";
	}

	public Account(String firstName, String lastName, String age, String userName, String password, String ipAddress,
			String status) {

		this.firstName = firstName.trim();
		this.lastName = lastName.trim();
		this.age = age;
		this.userName = userName.trim();
		this.password = password.trim();
		this.ipAddress = ipAddress.trim();
		this.status = status.trim();
	}

	public String toString() {

		return this.firstName + "." + this.lastName + "." + this.age + "." + this.userName + "." + this.password + "."
				+ this.status;

	}

}
