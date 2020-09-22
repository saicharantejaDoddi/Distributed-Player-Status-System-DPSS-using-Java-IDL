package System;

import java.sql.Timestamp;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import Helper.logManager;
import ServerApp.Server;
import ServerApp.ServerHelper;

public class AdministratorClient implements Runnable {

	static String gameServerName = "";
	static String ADMIN = "Admin_LogReport";
	logManager adminLogManager = new logManager(ADMIN);
	NamingContextExt ncRef = null;
	Server server = null;

	public AdministratorClient(NamingContextExt ncRef) {

		super();
		this.ncRef = ncRef;
	}

	public static void main(String[] args) throws Exception {
		// create and initialize the ORB
		ORB orb = ORB.init(args, null);
		// get the root naming context
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		// Use NamingContextExtinstead of NamingContext. This is part of the
		// Interoperable naming Service.
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		AdministratorClient admin = new AdministratorClient(ncRef);
		(new Thread(admin)).start();
	}

	public void run() {

		Scanner sc = new Scanner(System.in);

		boolean optionStatus = true;
		while (optionStatus) {
			int option = displayOptions(sc);

			switch (option) {

			case 1:
				getAdminStatus(sc);
				break;
			case 2:
				getSuspendStatus(sc);
				break;
			case 3:
				System.exit(0);
				break;
			default:
				System.out.println("Please enter either 1 or 2");

			}
		}
	}

	private int displayOptions(Scanner sc) {
		// Display Options
		System.out.println("Please Select the one of the below options");
		System.out.println("1.Get Admin Status ");
		System.out.println("2.Suspend Account");
		System.out.println("3.Close the Admin Client Application");

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
			server = null;
			System.out.println(gameServerName + "Server is busy");
		}

		return server;

	}

	private void getAdminStatus(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the  Name:");
			userName = sc.nextLine();
			userNameStatus = validateAdminName(userName.trim());
			if (userNameStatus == false) {
				System.out.println("Entered name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the  Password:");
			password = sc.nextLine();
			passwordStatus = validatePassword(password.trim());
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			ipAddressStatus = validateIpAddress(currentIpAddress.trim());
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
				System.out.println("Please enter valid Ip Address like below");
				System.out.println(
						"132.xxx.xxx.xxx : IP-addresses starting with 132 indicate a North-American geolocation.");
				System.out.println("93.xxx.xxx.xxx : IP-addresses starting with 93 indicate an European geo-location.");
				System.out.println("182.xxx.xxx.xxx : IP-addresses starting with 182 indicate an Asian geo-location.");

			}
		}

		boolean status = checkAdminSignInStatus(userNameStatus, passwordStatus, ipAddressStatus);
		String AdminSigInString = "";
		if (status) {

			AdminSigInString = getPlayerStatus(userName, password, currentIpAddress);

		} else {

			System.out.println("Admin Sign In Failed due to Admin Sign In Details are not valid");
		}
		System.out.println(AdminSigInString);
	}

	private boolean validateUserName(String userName) {
		if (userName.length() >= 6 && userName.length() <= 15) {
			return true;
		} else {
			return false;
		}
	}

	private void getSuspendStatus(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the  Name:");
			userName = sc.nextLine();
			userNameStatus = validateAdminName(userName.trim());
			if (userNameStatus == false) {
				System.out.println("Entered name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the  Password:");
			password = sc.nextLine();
			passwordStatus = validatePassword(password.trim());
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			ipAddressStatus = validateIpAddress(currentIpAddress.trim());
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
				System.out.println("Please enter valid Ip Address like below");
				System.out.println(
						"132.xxx.xxx.xxx : IP-addresses starting with 132 indicate a North-American geolocation.");
				System.out.println("93.xxx.xxx.xxx : IP-addresses starting with 93 indicate an European geo-location.");
				System.out.println("182.xxx.xxx.xxx : IP-addresses starting with 182 indicate an Asian geo-location.");

			}
		}

		boolean userNameStatus2 = false;
		String userName2 = "";
		while (!(userNameStatus2)) {
			System.out.println("Please Enter the Account User Name to suspend:");
			userName2 = sc.nextLine();
			userNameStatus2 = validateUserName(userName2.trim());
			if (userNameStatus2 == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean status = checkSuspendUserNameStatus(userNameStatus, passwordStatus, ipAddressStatus, userNameStatus2);
		boolean AdminSuspendUserNameStatus = false;
		if (status) {

			AdminSuspendUserNameStatus = suspendAccount(userName, password, currentIpAddress, userName2);

		} else {

			System.out.println("Admin Sign In Failed.Due to Admin Sign In Details are not valid");
		}

	}
	// Main Method for the Admin

	public String getPlayerStatus(String userName, String password, String ipAddress) {
		String currentIpAddress = ipAddress.trim();
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		adminLogManager.insertMessage("---------------------------------------------\r\n");
		adminLogManager.insertMessage("Operation Performed :  getPlayerStatus" + "\r\n");
		adminLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		adminLogManager.insertMessage("IPAddress Performed :" + currentIpAddress + "\r\n");
		adminLogManager.insertMessage("UserName Performed :" + userName + "\r\n");

		String AdminSigInString = "";
		try {
			currentIpAddress = ipAddress.trim();
			Server gameServer = getServerObject(currentIpAddress);
			AdminSigInString = gameServer.getPlayerStatus(userName, password, currentIpAddress);
			adminLogManager.insertMessage(AdminSigInString + "\r\n");
			System.out.println("Admin successfully got status from Server " + gameServerName);

		} catch (Exception ex) {
			adminLogManager.insertMessage("OUTPUT Message: " + "Admin Sign In Failed due to issue in Server" + "\r\n");
			adminLogManager.insertMessage("ERROR Message: " + AdminSigInString + "\r\n");
			System.out.println("Admin Sign In Failed.Due to issue in Server");

		}

		return AdminSigInString;
	}

	// Main Method for the Admin Done

	// Validations of Admin,Password,Ip Address
	private boolean validateAdminName(String userName) {
		if (userName.equalsIgnoreCase("Admin")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean validatePassword(String password) {
		if (password.equals("Admin")) {

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

	private boolean checkAdminSignInStatus(boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus) {

		if (userNameStatus && passwordStatus && ipAddressStatus) {
			return true;
		} else {
			return false;
		}

	}

	private boolean checkSuspendUserNameStatus(boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus,
			boolean userNameStatus2) {

		if (userNameStatus && passwordStatus && ipAddressStatus && userNameStatus2) {
			return true;
		} else {
			return false;
		}

	}

	public boolean suspendAccount(String userName, String password, String ipAddress, String userNameSuspend) {
		String currentIpAddress = ipAddress.trim();
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		adminLogManager.insertMessage("---------------------------------------------\r\n");
		adminLogManager.insertMessage("Operation Performed :  suspend Account" + "\r\n");
		adminLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		adminLogManager.insertMessage("IPAddress Performed :" + currentIpAddress + "\r\n");
		adminLogManager.insertMessage("UserName Performed :" + userName + "\r\n");

		boolean AdminSuspendStatus = false;
		try {
			currentIpAddress = ipAddress.trim();
			Server gameServer = getServerObject(currentIpAddress);
			AdminSuspendStatus = gameServer.suspendAccount(userName, password, currentIpAddress, userNameSuspend);
			if (AdminSuspendStatus) {
				adminLogManager
						.insertMessage("OUTPUT Message: " + "Admin successfully suspended account in Server" + "\r\n");
				System.out.println("Admin successfully suspended account in Server " + gameServerName);
				return true;
			} else {
				adminLogManager
						.insertMessage("OUTPUT Message: " + "Admin failed to suspend account in Server" + "\r\n");
				adminLogManager.insertMessage(
						"ERROR Message: " + "Due to requested account is not available in Server" + "\r\n");
				System.out.println("Admin Operation Failed.Due to requested account is not available in Server");
			}

		} catch (Exception ex) {
			adminLogManager.insertMessage("OUTPUT Message: " + "Admin failed to suspend account in Server" + "\r\n");
			adminLogManager.insertMessage("ERROR Message: " + "Due to issue in Server" + "\r\n");
			System.out.println("Admin Sign In Failed due to issue in Server");

		}
		return false;
	}

	// Validation Done

}