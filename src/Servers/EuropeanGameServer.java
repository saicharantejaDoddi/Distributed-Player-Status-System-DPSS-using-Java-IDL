package Servers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DataStructure.Account;
import Helper.logManager;
import ServerApp.Server;
import ServerApp.ServerHelper;
import ServerApp.ServerPOA;

public class EuropeanGameServer extends ServerPOA implements Runnable {
	logManager europeanGameServerLogManager = null;
	static final String EUROPEAN = "EuropeanGameServer_LogReport";
	static Hashtable<String, List<Account>> ht = new Hashtable<String, List<Account>>();

	protected EuropeanGameServer() {
		super();
		europeanGameServerLogManager = new logManager(EUROPEAN);
		//europeanGameServerLogManager.insertMessage("European Server is started !!!!!!!!!!!!" + "\r\n");

		// Default Data Stored
		List<Account> accountsofN = new ArrayList<Account>();
		String firstUsernamekey = "N";
		Account account1 = new Account("Isaac", "Newton", "84", "NEWTONI", "NEWTONGRAVITY", "93.1.2.3");
		accountsofN.add(account1);
		ht.put(firstUsernamekey, accountsofN);

		List<Account> accountsofE = new ArrayList<Account>();
		String secondUsernamekey = "E";
		Account account2 = new Account("Albert", "Einstein", "76", "EINSTEINA", "Relativity", "93.1.2.3");
		accountsofE.add(account2);
		ht.put(secondUsernamekey, accountsofE);
		
		List<Account> accountsofR = new ArrayList<Account>();
		String thridkey = "R";
		Account account3 = new Account("Rahul", "Dravid", "47", "RAEUROPEAN", "DravidCricket", "93.1.2.3");
		accountsofR.add(account3);
		ht.put(thridkey, accountsofR);
		
		List<Account> accountsofL = new ArrayList<Account>();
		String fourthkey = "L";
		Account account4 = new Account("Laxman", "Venkata", "45", "LAEUROPEAN", "LaxmanV", "93.1.2.3");
		accountsofL.add(account4);
		ht.put(fourthkey, accountsofL);
		
		List<Account> accountsofM = new ArrayList<Account>();
		String fifthkey = "M";
		Account account5 = new Account("Mahandra", "Dhoni", "33", "MDEUROPEAN", "DhoniM", "182.1.2.3");
		accountsofM.add(account5);                                 
		ht.put(fifthkey, accountsofM);

	}

	private ORB europeanORB;

	public void setORB(ORB orb_val) {
		europeanORB = orb_val;
	}

	public void shutDown() {
		europeanORB.shutdown(false);

	}

	public static void main(String[] args) throws Exception {
		System.out.println("European Game Server is Started !!!!");
		EuropeanGameServer obj = new EuropeanGameServer();
		(new Thread(obj)).start();

		try {
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);
			// get reference to rootpoa& activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			EuropeanGameServer europeanGameServer = new EuropeanGameServer();
			europeanGameServer.setORB(orb);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(europeanGameServer);
			Server href = ServerHelper.narrow(ref);

			// get the root naming context
			// NameServiceinvokes the name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			// Use NamingContextExtwhich is part of the Interoperable Naming Service (INS)
			// specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			String name = "European_Server";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			System.out.println("European Server ready and waiting ...");

			// wait for invocations from clients
			orb.run();
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
		System.out.println("European Server Exiting ...");

	}

	public boolean createPlayerAccount(String firstName, String lastName, String age, String userName, String password,
			String ipAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		Account account = new Account(firstName, lastName, age, userName, password, ipAddress);
		userName = userName.toUpperCase();
		String key = userName.substring(0, 1);
		List<Account> accounts = null;
		synchronized (this) {
			europeanGameServerLogManager
					.insertMessage("-------------------------------------------------------" + "\r\n");
			europeanGameServerLogManager.insertMessage("Operation Performed : Create Player Account" + "\r\n");
			europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
			europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
			europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");

			for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

			{
				if (listOfAccounts.getKey().equalsIgnoreCase(key)) {

					accounts = listOfAccounts.getValue();

					for (Account curr : accounts) {
						if (curr.userName.equalsIgnoreCase(userName)) {
							System.out.println(
									"Account is not successfully created ! Due to duplicate username already exists");
							europeanGameServerLogManager.insertMessage(
									"OUTPUT Message: " + "Account is not successfully created !" + "\r\n");
							europeanGameServerLogManager.insertMessage("ERROR Message: "
									+ "Due to Duplicate username already exists in the European Server." + "\r\n");
							return false;
						}
					}
					accounts.add(account);
				}
			}
			if (accounts == null) {
				accounts = new ArrayList<Account>();
				accounts.add(account);
			}
			ht.put(key, accounts);
			System.out.println("Account is successfully created !");

			europeanGameServerLogManager
					.insertMessage("OUTPUT Message: " + "Account is successfully created !" + "\r\n");
			return true;
		}
	}

	public String playerSignIn(String userName, String password, String ipAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		userName = userName.toUpperCase();
		synchronized (this) {

			europeanGameServerLogManager
					.insertMessage("-------------------------------------------------------" + "\r\n");
			europeanGameServerLogManager.insertMessage("Operation Performed : Player Sign In" + "\r\n");
			europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
			europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
			europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");

			String signInString = isAccountValidSignIn(userName, password, ipAddress);
			if (signInString.equalsIgnoreCase("true")) {
				System.out.println("Player successfully Signed In");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Player successfully Signed In" + "\r\n");

				return signInString;
			} else {
				System.out.println("Player un-successfully Signed In");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Player un-successfully Signed In" + "\r\n");
				europeanGameServerLogManager.insertMessage("ERROR Message: " + signInString);
				return signInString;
			}

		}

	}

	public String getEuropeanPlayerStatus() {
		synchronized (this) {
			int europeOnline = 0;
			int europeOffline = 0;
			for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

			{

				for (Account account : listOfAccounts.getValue()) {
					String status = account.status;

					if (status.equalsIgnoreCase("ONLINE")) {
						europeOnline = europeOnline + 1;
					} else if (status.equalsIgnoreCase("OFFLINE")) {
						europeOffline = europeOffline + 1;
					}

				}

			}

			return "EU: " + europeOnline + " online, " + europeOffline + " offline";
		}

	}

	public String playerSignOut(String userName, String ipAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		userName = userName.toUpperCase();
		synchronized (this) {
			europeanGameServerLogManager
					.insertMessage("-------------------------------------------------------" + "\r\n");
			europeanGameServerLogManager.insertMessage("Operation Performed : Player Sign Out" + "\r\n");
			europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
			europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
			europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");

			String signOutString = isAccountValidSignOut(userName, ipAddress);
			if (signOutString.equalsIgnoreCase("true")) {
				System.out.println("Player successfully Signed Out");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Player successfully Signed Out" + "\r\n");

				return signOutString;
			} else {
				System.out.println("Player un-successfully Signed Out");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Player un-successfully Signed Out" + "\r\n");
				europeanGameServerLogManager.insertMessage("ERROR Message: " + signOutString);
				return signOutString;
			}
		}

	}

	public String isAccountValidSignIn(String userName, String password, String ipAddress) {

		String key = userName.substring(0, 1);
		List<Account> accounts;

		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equalsIgnoreCase(key)) {
				accounts = listOfAccounts.getValue();

				for (Account account : accounts) {
					if (account.userName.equalsIgnoreCase(userName)) {
						if (account.password.equalsIgnoreCase(password)) {

							if (account.status.equalsIgnoreCase("OFFLINE")) {
								System.out.println("UserName and Password is correct.");
								account.status = "ONLINE";
								return "true";
							} else if (account.status.equalsIgnoreCase("ONLINE")) {
								System.out.println("UserName and Password is correct.But Status is already ONLINE!");
								return "Status is already ONLINE!\r\n";

							}
						} else {
							System.out.println("Password is wrong!");
							return "Password is wrong!\r\n";

						}
					}
				}

			}
		}
		System.out.println("UserName is not found in the current Server!");
		return "UserName is not found in the current Server!\r\n";

	}

	public boolean isPasswordValid(String userName, String password, String ipAddress) {

		String key = userName.substring(0, 1);
		List<Account> accounts;

		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equalsIgnoreCase(key)) {
				accounts = listOfAccounts.getValue();

				for (Account account : accounts) {
					if (account.userName.equalsIgnoreCase(userName)) {
						if (account.password.equalsIgnoreCase(password)) {
							return true;

						} else {
							System.out.println("Password is wrong!");
							return false;

						}
					}
				}

			}
		}
		System.out.println("UserName is not found in the current Server!");
		return false;

	}

	public String isAccountValidSignOut(String userName, String ipAddress) {
		String key = userName.substring(0, 1);
		List<Account> accounts;

		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equalsIgnoreCase(key)) {
				accounts = listOfAccounts.getValue();

				for (Account account : accounts) {
					if (account.userName.equalsIgnoreCase(userName)) {
						if (account.status.equalsIgnoreCase("ONLINE")) {
							System.out.println("UserName and Password is correct.");
							account.status = "OFFLINE";
							return "true";

						} else if (account.status.equalsIgnoreCase("OFFLINE")) {
							System.out.println("UserName and Password is correct.Status is already OFFLINE!");
							return "Status is already OFFLINE!\r\n";

						} else {
							System.out.println("UserName is wrong!");
							return "UserName is wrong!\r\n";

						}
					}
				}

			}
		}
		System.out.println("UserName is not found in the current Server!");
		return "UserName is not found in the current Server!\r\n";

	}

	public String getPlayerStatus(String userName, String password, String ipAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		synchronized (this) {
			europeanGameServerLogManager
					.insertMessage("-------------------------------------------------------" + "\r\n");
			europeanGameServerLogManager.insertMessage("Operation Performed : getPlayerStatus" + "\r\n");
			europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
			europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
			europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");

			String message3 = ClientWithData(9001, "NorthAmerican_Server", "Status");
			String message2 = ClientWithData(9003, "Asain_Server", "Status");
			String message1 = getEuropeanPlayerStatus();
			String totalMessage = "";
			if (message3 == null) {
				System.out.println("Exception in the NorthAmerican Game Server");
				totalMessage = "ERROR MESSAGE:Exception in the NorthAmerican Game Server" + "\r\n";
			}
			if (message2 == null) {
				System.out.println("Exception in the Asian Game Server");
				totalMessage = totalMessage + "ERROR MESSAGE:Exception in the Asian Game Server" + "\r\n";
			}
			if (message1 == null) {
				System.out.println("Exception in the European Game Server");
				totalMessage = totalMessage + "ERROR MESSAGE:Exception in the European Game Server" + "\r\n";
			}

			if (message1 != null && message2 != null && message3 != null) {
				totalMessage = message1.trim() + "." + message2.trim() + "." + message3.trim() + ".";
				totalMessage = "OUTPUT Message: " + totalMessage + "\r\n";
				europeanGameServerLogManager.insertMessage(totalMessage);
			} else {
				europeanGameServerLogManager.insertMessage(totalMessage);
			}

			return totalMessage;
		}

	}

	public void run() {
		System.out.println("UDP Port for European Game Server Started");
		DatagramSocket aSocket = null;

		try {

			aSocket = new DatagramSocket(9002);
			byte[] buffer = new byte[1000];

			while (true) {
				buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				String req = new String(request.getData());
				req = req.trim();
				if (req.equalsIgnoreCase("Status")) {
					String dataValue = getEuropeanPlayerStatus();
					byte[] data = dataValue.getBytes();
					DatagramPacket reply = new DatagramPacket(data, data.length, request.getAddress(),
							request.getPort());
					aSocket.send(reply);
				} else {

					String dataValue = addUserName(req);
					byte[] data = dataValue.getBytes();
					DatagramPacket reply = new DatagramPacket(data, data.length, request.getAddress(),
							request.getPort());
					request=null;
					aSocket.send(reply);

				}
			}

		} catch (Exception ex) {
			System.out.println("Exception");
		}

		finally {

			if (aSocket != null) {
				aSocket.close();
			}

		}

	}

	public boolean transferAccount(String Username, String Password, String OldIPAddress, String NewIPAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		synchronized (this) {
			europeanGameServerLogManager
					.insertMessage("-------------------------------------------------------" + "\r\n");
			europeanGameServerLogManager.insertMessage("Operation Performed : Transfer Account" + "\r\n");
			europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
			europeanGameServerLogManager.insertMessage("IPAddress Performed :" + OldIPAddress + "\r\n");
			europeanGameServerLogManager.insertMessage("userName Performed :" + Username + "\r\n");

			Account account = getAccount(Username);

			if (account != null) {

				boolean passwordStatus = isPasswordValid(Username, Password, OldIPAddress);

				if (passwordStatus) {
					String transferAccountString = callServerWithData(NewIPAddress, account.toString()).trim();
					if (transferAccountString.equalsIgnoreCase("Completed")) {
						removeUserName(Username);
						System.out.println("Transfer Account is  successfully !");
						europeanGameServerLogManager
								.insertMessage("OUTPUT Message: " + "Transfer Account is successfully !" + "\r\n");
						return true;
					} else if (transferAccountString.equalsIgnoreCase("Duplicate")) {
						System.out.println("Transfer Account is not successful ! Due to " + transferAccountString);
						europeanGameServerLogManager
								.insertMessage("OUTPUT Message: " + "Transfer Account is not successful !" + "\r\n");
						europeanGameServerLogManager.insertMessage(
								"ERROR Message: " + "Due to same username is already exists in that server\r\n");
						return false;
					} else {
						return false;
					}
				} else {
					System.out.println("Transfer Account is not successful ! Due to password is wrong");
					europeanGameServerLogManager
							.insertMessage("OUTPUT Message: " + "Transfer Account is not successful !" + "\r\n");
					europeanGameServerLogManager.insertMessage("ERROR Message: " + "Due to password is wrong" + "\r\n");
					return false;
				}

			} else {
				System.out.println(
						"Transfer Account is not successful ! Due to username is not available in this server");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Transfer Account is not successful !" + "\r\n");
				europeanGameServerLogManager
						.insertMessage("ERROR Message: " + "Due to username is not available in this server" + "\r\n");
				return false;
			}

		}

	}

	public boolean removeUserName(String Username) {
		String key = Username.substring(0, 1);
		List<Account> accounts = null;
		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equalsIgnoreCase(key)) {

				accounts = listOfAccounts.getValue();

				for (Account curr : accounts) {
					if (curr.userName.equalsIgnoreCase(Username)) {
						accounts.remove(curr);
						return true;
					}
				}

			}
		}
		return false;
	}

	public String addUserName(String fullData) {
		String[] data = fullData.split("\\.");
		String userName = "";
		synchronized (this) {
			Account account = new Account(data[0], data[1], data[2], data[3], data[4], "", data[5]);
			userName = data[3].toUpperCase();
			String key = data[3].substring(0, 1);
			List<Account> accounts = null;
			for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

			{
				if (listOfAccounts.getKey().equalsIgnoreCase(key)) {

					accounts = listOfAccounts.getValue();

					for (Account curr : accounts) {
						if (curr.userName.equalsIgnoreCase(userName)) {

							return "Duplicate";
						}
					}
					accounts.add(account);
				}
			}
			if (accounts == null) {
				accounts = new ArrayList<Account>();
				accounts.add(account);
			}
			ht.put(key, accounts);

		}

		return "Completed";
	}

	public Account getAccount(String Username) {
		String key = Username.substring(0, 1);
		List<Account> accounts = null;
		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equalsIgnoreCase(key)) {

				accounts = listOfAccounts.getValue();

				for (Account curr : accounts) {
					if (curr.userName.equalsIgnoreCase(Username)) {

						return curr;
					}
				}

			}
		}
		return null;
	}

	public String callServerWithData(String currentIPAddress, String sendData) {

		String[] ipValue = currentIPAddress.split("\\.");
		String firstPartAddress = ipValue[0].substring(0, ipValue[0].length());
		int firstPart_IPAddress = Integer.parseInt(firstPartAddress);

		String gameServerName = "";
		int port = 0;
		if (firstPart_IPAddress == 132) {
			gameServerName = "NorthAmericanServer";
			port = 9001;
		} else if (firstPart_IPAddress == 93) {
			gameServerName = "EuropeanServer";
			port = 9002;
		} else if (firstPart_IPAddress == 182) {
			gameServerName = "AsianServer";
			port = 9003;
		}

		String statusString = ClientWithData(port, gameServerName, sendData);

		return statusString;
	}

	public String ClientWithData(int serverPort, String serverName, String sendData) {
		DatagramSocket aSocket = null;
		String resp = null;

		try {

			aSocket = new DatagramSocket();
			byte[] m = sendData.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
			aSocket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			byte[] data = reply.getData();
			resp = new String(data);
		}

		catch (Exception ex) {
			System.out.println("Exception in the " + serverName);
			resp = null;
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return resp;
	}

	public boolean suspendAccount(String userName, String password, String ipAddress, String userNameSuspend) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		synchronized (this) {
			europeanGameServerLogManager
					.insertMessage("-------------------------------------------------------" + "\r\n");
			europeanGameServerLogManager.insertMessage("Operation Performed : Suspend Account" + "\r\n");
			europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
			europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
			europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");

			Account account = getAccount(userNameSuspend);

			if (account != null) {
				boolean statusSuspend = removeUserName(userNameSuspend);
				if (statusSuspend) {
					System.out.println("Suspend Account is  successfully !");
					europeanGameServerLogManager
							.insertMessage("OUTPUT Message: " + "Suspend Account is successfully !" + "\r\n");
					return true;
				} else {
					System.out.println("Suspend Account is not successfully ! Due to internal Function");
					europeanGameServerLogManager
							.insertMessage("OUTPUT Message: " + "Suspend Account is not successfully !" + "\r\n");
					europeanGameServerLogManager.insertMessage("ERROR Message: " + "Due to internal Function" + "\r\n");
					return false;
				}

			} else {
				System.out.println(
						"Suspend Account is not successfully ! Due to Suspend username is not available in this server");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Suspend Account is not successfully !" + "\r\n");
				europeanGameServerLogManager.insertMessage(
						"ERROR Message: " + "Due to Suspend username is not available in this server" + "\r\n");
				return false;
			}

		}

	}
}
