package System;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import Helper.logManager;
import ServerApp.Server;
import ServerApp.ServerHelper;

public class PlayerClient implements Runnable {
	static String gameServerName = "";
	logManager playerClientLogManager = null;
//	static final String PLAYERCLIENT = "playerClient_LogReport";
	NamingContextExt ncRef = null;
	Server server = null;

	public PlayerClient(NamingContextExt ncRef) {
		super();
		this.ncRef = ncRef;
		playerClientLogManager = new logManager();
	}

	private boolean validateUserName(String userName) {
		if (userName.length() >= 6 && userName.length() <= 15) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validatePassword(String password) {
		if (password.length() >= 6) {

			return true;
		} else {
			return false;
		}
	}

	public boolean validateString(String name) {

		if ((name.length() >= 1) && name.matches("^[a-zA-Z\\s]+")) {

			return true;
		} else {
			return false;

		}

	}

	private boolean validateIpAddress(String ipAddress) {

		try {
			String[] addressPart = ipAddress.split("\\.");

			if (addressPart.length == 4) {
				int firstPart = Integer.parseInt(addressPart[0]);
				int secondPart = Integer.parseInt(addressPart[1]);
				int thridPart = Integer.parseInt(addressPart[2]);
				int fourthPart = Integer.parseInt(addressPart[3]);

				if (!(firstPart == 132 || firstPart == 93 || firstPart == 182)) {
					return false;
				}
				if (!(secondPart >= 0 && secondPart <= 255)) {
					return false;
				}
				if (!(thridPart >= 0 && thridPart <= 255)) {
					return false;
				}
				if (!(fourthPart >= 0 && fourthPart <= 255)) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;

		}

	}

	private int validateAge(String ageString) {
		int age = 0;

		try {
			age = Integer.parseInt(ageString);
		} catch (Exception ex) {

			return 0;
		}

		if (age >= 1) {

			return age;
		} else {
			return 0;
		}
	}

	private Server getServerObject(String currentIpAddress) {
		boolean status = false;

		String ipAddress = "";
		while (!(status)) {
			ipAddress = currentIpAddress;
			status = validateIpAddress(ipAddress);

			if (status == false) {
				System.out.println("Please enter valid Ip Address like below");
				System.out.println(
						"132.xxx.xxx.xxx : IP-addresses starting with 132 indicate a North-American geolocation.");
				System.out.println("93.xxx.xxx.xxx : IP-addresses starting with 93 indicate an European geo-location.");
				System.out.println("182.xxx.xxx.xxx : IP-addresses starting with 182 indicate an Asian geo-location.");

			}
		}
		// Got IP Address from the console
		String currentIPAddress = ipAddress.toString();
		String[] ipValue = currentIPAddress.split("\\.");
		String firstPartAddress = ipValue[0].substring(0, ipValue[0].length());
		int firstPart_IPAddress = Integer.parseInt(firstPartAddress);

		

		if (firstPart_IPAddress == 132) {
			gameServerName = "NorthAmerican_Server";

		} else if (firstPart_IPAddress == 93) {
			gameServerName = "European_Server";

		} else if (firstPart_IPAddress == 182) {
			gameServerName = "Asain_Server";

		}

		try {

			server = ServerHelper.narrow(ncRef.resolve_str(gameServerName));

			status = true;
		} catch (Exception ex) {

			System.out.println(gameServerName + "Server is busy");
		}

		return server;

	}

	public static void main(String[] args) throws Exception {

		// create and initialize the ORB
		ORB orb = ORB.init(args, null);
		// get the root naming context
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		// Use NamingContextExtinstead of NamingContext. This is part of the
		// Interoperable naming Service.
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		PlayerClient playerClient = new PlayerClient(ncRef);

		(new Thread(playerClient)).start();
	}

	private void playerSignOut(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userName = userName.trim().toUpperCase();
			userNameStatus = validateUserName(userName);
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			currentIpAddress = currentIpAddress.trim();
			ipAddressStatus = validateIpAddress(currentIpAddress);
			if (ipAddressStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean status = checkPlayerSignOutStatus(userNameStatus, ipAddressStatus);
		String playerSignOutString = "";

		if (status) {

			playerSignOutString = playerSignOut(userName, currentIpAddress);
		} else {
			System.out.println("Player Sign Out Failed due to Player Sign Out Details are not valid");

		}

		if (!playerSignOutString.equalsIgnoreCase("true")) {
			System.out.println("Player Sign In Failed due to Server");
		}

	}

	private void playerSigIn(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userName = userName.trim().toUpperCase();
			userNameStatus = validateUserName(userName);
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the Password:");
			password = sc.nextLine();
			password = password.trim();
			passwordStatus = validatePassword(password);
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			currentIpAddress = currentIpAddress.trim();
			ipAddressStatus = validateIpAddress(currentIpAddress);
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
			}
		}

		boolean status = checkPlayerSignInStatus(userNameStatus, passwordStatus, ipAddressStatus);
		String playerSigInString = "";
		if (status) {

			playerSigInString = playerSignIn(userName, password, currentIpAddress);

		} else {
			System.out.println("Player Sign In Failed due to Player Sign In Details are not valid");

		}

		if (!playerSigInString.equalsIgnoreCase("true")) {
			System.out.println("Player Sign In Failed due to Server");
		}

	}

	private void createPlayerOperation(Scanner sc) {
		sc.nextLine();
		System.out.println("-------Player Account---------");

		boolean firstNameStatus = false;
		String firstName = "";
		while (!(firstNameStatus)) {
			System.out.println("Please Enter the First Name:");
			firstName = sc.nextLine();
			firstName = firstName.trim().toUpperCase();
			firstNameStatus = validateString(firstName);
			if (firstNameStatus == false) {
				System.out.println("Entered First name is not valid");
			}
		}

		boolean lastNameStatus = false;
		String lastName = "";
		while (!(lastNameStatus)) {
			System.out.println("Please Enter the Last Name:");
			lastName = sc.nextLine();
			lastName = lastName.trim().toUpperCase();
			lastNameStatus = validateString(lastName);
			if (lastNameStatus == false) {
				System.out.println("Entered Last name is not valid");
			}
		}

		int ageint = 0;
		String ageString = "";
		while (ageint == 0) {
			System.out.println("Please Enter the Age:");
			ageString = sc.nextLine();
			ageString = ageString.trim();
			ageint = validateAge(ageString);
			if (ageint == 0) {
				System.out.println("Entered Age is not valid");
			}
		}

		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userName = userName.trim().toUpperCase();
			userNameStatus = validateUserName(userName);
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the Password:");
			password = sc.nextLine();
			password = password.trim();
			passwordStatus = validatePassword(password);
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			currentIpAddress = currentIpAddress.trim();
			ipAddressStatus = validateIpAddress(currentIpAddress);
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
			}
		}

		boolean status = checkPlayerDetailsStatus(firstNameStatus, lastNameStatus, ageint, userNameStatus,
				passwordStatus, ipAddressStatus);

		boolean createPlayerStatus = false;
		if (status) {

			createPlayerStatus = createPlayerAccount(firstName, lastName, ageString, userName, password,
					currentIpAddress);

		} else {
			System.out.println("Create Player Account Failed due to Players Details are not valid");
		}
		if (!createPlayerStatus) {
			System.out.println("Create Player Account Failed in Server");
		}

	}

	public boolean createPlayerAccount(String firstName, String lastName, String age, String userName, String password,
			String currentIpAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);

		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"Operation Performed : Create Player Account" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"userName Performed :" + userName + "\r\n");
		// Get the server object based on IP Address
		Server gameServer = getServerObject(currentIpAddress);

		boolean createPlayerStatus = false;

		try {
			createPlayerStatus = gameServer.createPlayerAccount(firstName, lastName, age, userName, password,
					currentIpAddress);
		}

		catch (Exception ex) {
			System.out.println("Player Creation Player Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Creation of Player un-successfully" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Creation of Player Failed due to issue in Server");

		}
		if (createPlayerStatus) {
			System.out.println(userName+" Create Player Account Done !");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Account is successfully created !" + "\r\n");

		} else {
			System.out.println("Same User Name is already present");
			System.out.println("Create Player Account Failed in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Account is not successfully created !" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Due to Duplicate username already exists in the Server." + "\r\n");
		}
		return createPlayerStatus;

	}

	public String playerSignIn(String userName, String password, String currentIpAddress) {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"Operation Performed :  Player Sign In" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"userName Performed :" + userName + "\r\n");
		Server gameServer = getServerObject(currentIpAddress);
		String playerSigInString = "";
		try {
			playerSigInString = gameServer.playerSignIn(userName, password, currentIpAddress);
		} catch (Exception ex) {
			System.out.println("Player Sign In Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed In" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Player Sign In Failed due to issue in Server");

		}

		if (playerSigInString.equalsIgnoreCase("true")) {
			System.out.println(userName+" Player successfully Signed In");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player successfully Signed In" + "\r\n");

		} else {
			System.out.println("Player un-successfully Signed In");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed In" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "ERROR Message: " + playerSigInString);

		}
		return playerSigInString;

	}

	public String playerSignOut(String userName, String currentIpAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);

		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"Operation Performed : Player Sign Out" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"userName Performed :" + userName + "\r\n");
		// Get the server object based on IP Address
		String playerSignOutString = "";
		try {
			Server gameServer = getServerObject(currentIpAddress);

			playerSignOutString = gameServer.playerSignOut(userName, currentIpAddress);
		}

		catch (Exception ex) {
			System.out.println("Player Sign Out Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed Out" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Player Sign Out Failed due to issue in Server");

		}
		if (playerSignOutString.equalsIgnoreCase("true")) {
			System.out.println(userName+" Player successfully Signed Out");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player successfully Signed Out" + "\r\n");

		} else {
			System.out.println("Player un-successfully Signed Out");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed Out" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + playerSignOutString);

		}

		return playerSignOutString;
	}

	private int displayOptions(Scanner sc) {
		// Display Options
		System.out.println("Please Select the one of the below options");
		System.out.println("1.Create Player Account");
		System.out.println("2.Player SignIn");
		System.out.println("3.Player SignOut");
		System.out.println("4.Transfer the Account");
		System.out.println("5.Close the Player Client Application");

		// Taking Option
		int option = 0;
		try {
			option = sc.nextInt();
		} catch (Exception ex) {
			System.out.println("Please enter only integer value");
			sc.nextLine();
		}
		return option;
	}

	private boolean checkPlayerDetailsStatus(boolean firstNameStatus, boolean lastNameStatus, int age,
			boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus) {

		if (firstNameStatus && lastNameStatus && userNameStatus && passwordStatus && ipAddressStatus && age > 0) {
			return true;
		} else {
			return false;
		}

	}

	private boolean checkPlayerSignInStatus(boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus) {

		if (userNameStatus && passwordStatus && ipAddressStatus) {
			return true;
		} else {
			return false;
		}

	}

	private boolean checkPlayerSignOutStatus(boolean userNameStatus, boolean ipAddressStatus) {

		if (userNameStatus && ipAddressStatus) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void run() {

		Scanner sc = new Scanner(System.in);

		// 1.Getting from the client way

		boolean optionStatus = true;
		while (optionStatus) {
			int option = displayOptions(sc);

			switch (option) {

			case 1:
				createPlayerOperation(sc);
				break;
			case 2:
				playerSigIn(sc);
				break;
			case 3:
				playerSignOut(sc);
				break;
			case 4:
				transferAccount(sc);
				break;
			case 5:
				System.exit(0);
				break;

			default:
				System.out.println("Please enter either 1 or 2 or 3 or 4 or 5");

			}
		}

	}

	public void transferAccount(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userName = userName.trim().toUpperCase();
			userNameStatus = validateUserName(userName);
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the Password:");
			password = sc.nextLine();
			password = password.trim();
			passwordStatus = validatePassword(password);
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			currentIpAddress = currentIpAddress.trim();
			ipAddressStatus = validateIpAddress(currentIpAddress);
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
			}
		}

		boolean ipAddressStatus2 = false;
		String currentIpAddress2 = "";
		while (!(ipAddressStatus2)) {
			System.out.println("Please Enter the new IP Address:");
			currentIpAddress2 = sc.nextLine();
			currentIpAddress2 = currentIpAddress2.trim();
			ipAddressStatus2 = validateIpAddress(currentIpAddress2);
			if (ipAddressStatus2 == false) {
				System.out.println("Entered Ip Address is not valid");
			}
		}

		boolean status = checkPlayerTransferAccountStatus(userNameStatus, passwordStatus, ipAddressStatus,
				ipAddressStatus2);

		boolean transferAccountStatus = false;
		if (status) {

			transferAccountStatus = transferAccount(userName, password, currentIpAddress, currentIpAddress2);

		} else {
			System.out.println("Transfer Account Failed due to Players Details are not valid");
		}
	}

	public boolean transferAccount(String userName, String password, String currentIpAddress,
			String currentIpAddress2) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);

		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"Operation Performed : Transfer Player Account" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
				"userName Performed :" + userName + "\r\n");
		// Get the server object based on IP Address
		Server gameServer = getServerObject(currentIpAddress);

		boolean transferAccountStatus = false;

		try {
			transferAccountStatus = gameServer.transferAccount(userName, password, currentIpAddress, currentIpAddress2);
		}

		catch (Exception ex) {
			System.out.println("Transfer Account Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Transfer Account un-successfully" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Transfer Account Failed due to issue in Server" + "\r\n");

		}
		if (transferAccountStatus) {
			System.out.println("Transfer Account  Done !");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Transfer Account is successfully created !" + "\r\n");

		} else {
			System.out.println("Transfer Account Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Transfer Account un-successfully" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Transfer Account Failed due to issue in Server" + "\r\n");

		}
		return transferAccountStatus;

	}

	private boolean checkPlayerTransferAccountStatus(boolean userNameStatus, boolean passwordStatus,
			boolean ipAddressStatus, boolean ipAddressStatus2) {

		if (userNameStatus && passwordStatus && ipAddressStatus && ipAddressStatus2) {
			return true;
		} else {
			return false;
		}

	}
}
