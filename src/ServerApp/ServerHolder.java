package ServerApp;

/**
* ServerApp/ServerHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Server.idl
* Thursday, June 11, 2020 6:37:11 o'clock PM EDT
*/

public final class ServerHolder implements org.omg.CORBA.portable.Streamable
{
  public ServerApp.Server value = null;

  public ServerHolder ()
  {
  }

  public ServerHolder (ServerApp.Server initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ServerApp.ServerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ServerApp.ServerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ServerApp.ServerHelper.type ();
  }

}