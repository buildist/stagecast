/* FTPSession - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class FTPSession
    implements Debug.Constants, ResourceIDs.SplashScreenIDs,
	       ResourceIDs.WorldIDs
{
    private static final byte[] CRLF = "\r\n".getBytes();
    private String _host;
    private int _port;
    private Socket _controlSocket;
    private InputStream _in;
    private OutputStream _out;
    private String _lastResultString;
    private Socket _dataSocket;
    private ProgressDialog _progress;
    private int _progressIncr;
    
    class FTPException extends RecoverableException
    {
	FTPException(String msg) {
	    super(msg, false);
	}
    }
    
    public FTPSession(String host, int port) {
	_host = host;
	_port = port;
	_controlSocket = null;
	_dataSocket = null;
    }
    
    public void setProgress(ProgressDialog dlg) {
	_progress = dlg;
    }
    
    public void setProgressIncr(int incr) {
	_progressIncr = incr;
    }
    
    public void connect() throws FTPException {
	boolean connected = false;
	try {
	    _controlSocket = new Socket(_host, _port);
	    _controlSocket.setSoTimeout(60000);
	    _in = _controlSocket.getInputStream();
	    _out = _controlSocket.getOutputStream();
	    int result = getResult();
	    throwIfNot(result == 220, result, "Can't connect to " + _host);
	    connected = true;
	} catch (java.net.UnknownHostException unknownhostexception) {
	    throw new FTPException("Can't find host: " + _host);
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	} finally {
	    if (!connected && _controlSocket != null) {
		try {
		    _controlSocket.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
		_controlSocket = null;
	    }
	}
    }
    
    public void login(String user, String password) {
	boolean loggedIn = false;
	try {
	    sendCommand("USER " + user);
	    int result = getResult();
	    throwIfNot(result == 230 || result == 331, result,
		       "User " + user + " not recognized");
	    sendCommand("PASS " + password);
	    result = getResult();
	    throwIfNot(result == 230, result,
		       "Invalid login; user or password unknown");
	    loggedIn = true;
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	}
    }
    
    public void setDirectory(String dir) throws FTPException {
	try {
	    sendCommand("CWD " + dir);
	    int result = getResult();
	    throwIfNot(result / 100 == 2, result,
		       "Can't set directory to " + dir);
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	}
    }
    
    public void transmit(File file, boolean binary, String name)
	throws FTPException {
	try {
	    transmit(new FileInputStream(file), binary, name);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	} catch (FTPException ftp) {
	    throw ftp;
	}
    }
    
    public void transmit(InputStream is, boolean binary, String name)
	throws FTPException {
	OutputStream os = null;
	try {
	    prepareToTransmit(name, binary);
	    os = _dataSocket.getOutputStream();
	    Util.streamCopy(is, os, _progress, _progressIncr);
	    os = null;
	    _dataSocket.close();
	    _dataSocket = null;
	    int result = getResult();
	    throwIfNot(result == 226, result, "File transfer aborted");
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	} finally {
	    if (os != null) {
		try {
		    os.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    if (is != null) {
		try {
		    is.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    if (_dataSocket != null) {
		try {
		    _dataSocket.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
    }
    
    public void transmit(World world) throws FTPException {
	OutputStream os = null;
	try {
	    prepareToTransmit(world.getName() + Resource.getText("W ex"),
			      true);
	    os = _dataSocket.getOutputStream();
	    world.saveObjects(os, false);
	    os.close();
	    os = null;
	    _dataSocket.close();
	    _dataSocket = null;
	    int result = getResult();
	    throwIfNot(result == 226, result, "File transfer aborted");
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	} finally {
	    if (os != null) {
		try {
		    os.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    if (_dataSocket != null) {
		try {
		    _dataSocket.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
    }
    
    private void prepareToTransmit(String name, boolean binary)
	throws FTPException {
	try {
	    sendCommand("TYPE " + (binary ? "I" : "A"));
	    int result = getResult();
	    throwIfNot(result / 100 == 2, result, "Remote command failed");
	    setPort();
	    sendCommand("STOR " + name);
	    result = getResult();
	    throwIfNot(result >= 125 && result <= 200, result,
		       "Store request failed");
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	}
    }
    
    public void receive(String name, boolean binary, File file)
	throws FTPException {
	try {
	    receive(name, binary, new FileOutputStream(file));
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	} catch (FTPException ftp) {
	    throw ftp;
	}
    }
    
    public void receive(String name, boolean binary, OutputStream os)
	throws FTPException {
	InputStream is = null;
	try {
	    sendCommand("TYPE " + (binary ? "I" : "A"));
	    int result = getResult();
	    throwIfNot(result / 100 == 2, result, "Remote command failed");
	    setPort();
	    sendCommand("RETR " + name);
	    result = getResult();
	    throwIfNot(result >= 125 && result <= 200, result,
		       "Retrieve request failed");
	    is = _dataSocket.getInputStream();
	    Util.streamCopy(is, os, _progress, _progressIncr);
	    is = null;
	    os = null;
	    _dataSocket.close();
	    _dataSocket = null;
	    result = getResult();
	    throwIfNot(result == 226, result, "File transfer aborted");
	} catch (FTPException ftp) {
	    throw ftp;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new FTPException(e.getMessage());
	} finally {
	    if (os != null) {
		try {
		    os.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    if (is != null) {
		try {
		    is.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    if (_dataSocket != null) {
		try {
		    _dataSocket.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
    }
    
    public void disconnect() {
	try {
	    sendCommand("QUIT");
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println(e);
	} finally {
	    setProgress(null);
	    if (_controlSocket != null) {
		try {
		    _controlSocket.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
    }
    
    private void sendCommand(String command) throws IOException {
	Debug.print("debug.ftp", "FTP Send: ", command);
	_out.write(command.getBytes());
	_out.write(CRLF);
	_out.flush();
    }
    
    private int getResult() throws IOException {
	StringBuffer result = new StringBuffer(80);
	boolean cr = false;
	do {
	    int ch = _in.read();
	    if (ch != -1) {
		result.append((char) ch);
		if (!cr || ch != 10) {
		    cr = ch == 13;
		    continue;
		}
	    }
	    _lastResultString = result.toString();
	    result = new StringBuffer(80);
	    Debug.print("debug.ftp", "FTP Response: ", _lastResultString);
	} while (_lastResultString.charAt(0) == ' '
		 || _lastResultString.charAt(3) == '-');
	return Integer.parseInt(_lastResultString.substring(0, 3));
    }
    
    private void setPort() throws IOException, FTPException {
	sendCommand("PASV");
	int result = getResult();
	throwIfNot(result == 227, result, "Set passive mode failed");
	defineDataSocket(_lastResultString);
    }
    
    private void defineDataSocket(String socketMsg) throws IOException {
	char ch = '\0';
	int lastPos;
	for (lastPos = socketMsg.length() - 1; lastPos > 0; lastPos--) {
	    ch = socketMsg.charAt(lastPos);
	    if (Character.isDigit(ch))
		break;
	}
	int firstPos = lastPos;
	for (/**/; Character.isDigit(ch) || ch == ',';
	     ch = socketMsg.charAt(firstPos))
	    firstPos--;
	String portInfo = socketMsg.substring(firstPos + 1, lastPos + 1);
	StringTokenizer st = new StringTokenizer(portInfo, ",");
	String ipAddr = "";
	for (int i = 0; i < 4; i++)
	    ipAddr = ipAddr + st.nextToken() + (i == 3 ? "" : ".");
	int port = Integer.parseInt(st.nextToken());
	port <<= 8;
	port += Integer.parseInt(st.nextToken());
	Debug.print("debug.ftp",
		    "Creating data socket at " + ipAddr + ":" + port);
	_dataSocket = new Socket(InetAddress.getByName(ipAddr), port);
    }
    
    private void throwIfNot(boolean test, int result, String msg)
	throws FTPException {
	if (!test) {
	    StringBuffer buf = new StringBuffer(msg);
	    buf.append(" (" + result + ")");
	    throw new FTPException(buf.toString());
	}
    }
}
