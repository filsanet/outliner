/** 
 * Copyright 1999 Hannes Wallnoefer
 * Implements a XML-RPC client. See http://www.xmlrpc.com/
 */
 
/** 
 * This class is a 99.9% copy of the the class helma.xmlrpc.XmlRpcClient.
 * I needed to make modifications that the API wasn't exposing, so I duplicated
 * the code as helma.xmlrpc.MyXmlRpcClient and made my minor modifications.
 * The changes allow a "raw" XMLRPC message to be sent with the client.
 */

package helma.xmlrpc;

import java.net.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;

public class MyXmlRpcClient implements XmlRpcHandler {
     
    URL url;
   
    /** 
     * Construct a XML-RPC client with this URL.
     */
    public MyXmlRpcClient (URL url) {
	this.url = url;
    }

    /** 
     * Construct a XML-RPC client for the URL represented by this String.
     */
    public MyXmlRpcClient (String url) throws MalformedURLException {
	this.url = new URL (url);
    }
   
    /** 
     * Construct a XML-RPC client for the specified hostname and port.
     */
    public MyXmlRpcClient (String hostname, int port) throws MalformedURLException {
	this.url = new URL ("http://"+hostname+":"+port+"/RPC2");
    }
   

    /** 
     * Generate an XML-RPC request and send it to the server. Parse the result and
     * return the corresponding Java object.
     * 
     * @exception XmlRpcException: If the remote host returned a fault message.
     * @exception IOException: If the call could not be made because of lower level problems.
     */
     
     public Object execute (String methodName, Vector params) {
     	return new Object();
     }
     
    public Object execute (String xmlrpcCall) throws XmlRpcException, IOException {
    	Worker worker = getWorker ();
	try {
    	    Object retval =  worker.execute (xmlrpcCall);
	    return retval;
	} finally {
	    if (workers < 20 && !worker.fault)
	        pool.push (worker);
	    else
	        workers -= 1;
	}
    }
    
    Stack pool = new Stack ();
    int workers = 0;

    private final Worker getWorker () throws IOException {
	try {
	    return (Worker) pool.pop ();
	} catch (EmptyStackException x) {
	    if (workers < 100) {
	        workers += 1;
	        return new Worker ();
	    }
	    throw new IOException ("XML-RPC System overload");
	}
    }
    

    class Worker extends XmlRpc {
    
    boolean fault = false;
    Object result = null; 
    HttpClient client = null;    
   
    public Worker () {
    	super ();
    }


    public Object execute (String xmlrpcCall) throws XmlRpcException, IOException {
	long now = System.currentTimeMillis ();
    	try {

 	    // and send it to the server
	    if (client == null) 
	        client = new HttpClient (url);
	    client.write (xmlrpcCall.getBytes());
	    
    	    InputStream in = client.getInputStream ();

	    // parse the response
    	    parse (in);

	    if (!client.keepalive)
	        client.closeConnection ();

	    if (debug) 
	        System.out.println ("result = "+result);
	    
	    // check for errors from the XML parser
	    if (errorLevel == FATAL) 
	        throw new Exception (errorMsg);
	} catch (IOException iox) {
	    // this is a lower level problem,  client could not talk to server for some reason.

	    throw iox;    

	} catch (Exception x) {
	    // same as above, but exception has to be converted to IOException. 
	    if (XmlRpc.debug)
	        x.printStackTrace ();
	        
	    String msg = x.getMessage ();
	    if (msg == null || msg.length () == 0)
	        msg = x.toString ();
	    throw new IOException (msg);
	}
	
	if (fault) { 
	    // this is an XML-RPC-level problem, i.e. the server reported an error.
	    // throw an XmlRpcException.
	    
	    XmlRpcException exception = null;
	    try {
	        Hashtable f = (Hashtable) result;
	        String faultString = (String) f.get ("faultString");
	        int faultCode = Integer.parseInt (f.get ("faultCode").toString ());
	        exception = new XmlRpcException (faultCode, faultString.trim ());
	    } catch (Exception x) {
	        throw new XmlRpcException (0, "Server returned an invalid fault response.");
	    }
	    throw exception;
	}
	if (debug)
	    System.out.println ("Spent "+(System.currentTimeMillis () - now)+" in request");
	return result;
    }


    /**
     * Called when the return value has been parsed. 
     */
    void objectParsed (Object what) {
	result = what;
    }
    
    
    /**
     * Generate an XML-RPC request from a method name and a parameter vector.
     */
    void writeRequest (RawXmlWriter writer, String method, Vector params) throws IOException {
	writer.startElement ("methodCall");

	writer.startElement ("methodName");
	writer.write (method);
	writer.endElement ("methodName");

	writer.startElement ("params");
	int l = params.size ();
	for (int i=0; i<l; i++) {
	    writer.startElement ("param");
	    writeObject (params.elementAt (i), writer);
	    writer.endElement ("param");
	}
	writer.endElement ("params");
	writer.endElement ("methodCall");
	writer.flush ();
    }

    /**
     * Overrides method in XmlRpc to handle fault repsonses.
     */
    public void startElement (String name, AttributeList atts) throws SAXException {
	if ("fault".equals (name))
	    fault = true;
	else
	    super.startElement (name, atts);
    }

    } // end of inner class Worker
    
    
    /** 
     * Just for testing.
     */
    public static void main (String args[]) throws Exception {
    	// XmlRpc.setDebug (true);
	try {
	    Vector v = new Vector ();
	    v.addElement (new Integer (Integer.parseInt (args[0])));
	    XmlRpcClient client = new XmlRpcClient ("http://betty.userland.com/RPC2");
	    try {
	        System.out.println (client.execute ("examples.getStateName", v));
	    } catch (Exception ex) {
	        System.out.println ("Error: "+ex);
	    }
	} catch (Exception x) {
	    System.err.println (x);
	    System.err.println ("Usage: java helma.xmlrpc.XmlRpcClient <statenumber>");
	    System.err.println ("Retrieves a state name from betty.userland.com.");
	}
    }
    

    // A replacement for java.net.URLConnection, which seems very slow on MS Java.
    class HttpClient {
    	
    	String hostname;
    	String host;
    	int port;
    	String uri;
    	Socket socket = null;
    	OutputStream output;
	InputStream input;
	boolean keepalive;
	boolean fresh;

    	
	public HttpClient (URL url) throws IOException {
	    hostname = url.getHost ();
	    port = url.getPort ();
	    if (port < 1) port = 80;
	    uri = url.getFile ();
	    host = port == 80 ? hostname : hostname+":"+port;
	    initConnection ();
	}

	protected void initConnection () throws IOException {
	    fresh = true;
	    socket = new Socket (hostname, port);
	    output = new BufferedOutputStream (socket.getOutputStream());
	    input = new BufferedInputStream (socket.getInputStream ());
	}
	
	protected void closeConnection () {
	    try {
	        output.close ();
	    } catch (Exception ignore) {}
	    try {
	        input.close ();
	    } catch (Exception ignore) {}
	    try {
	        socket.close ();
	    } catch (Exception ignore) {}
	}
	
	public void write (byte[] request) throws IOException {
	    try {
	        Writer writer = new OutputStreamWriter (output);
	        writer.write ("POST ");
	        writer.write (uri);
	        writer.write (" HTTP/1.0\r\n");
	        writer.write ("User-Agent: "+XmlRpc.version+"\r\n");
	        writer.write ("Host: ");
	        writer.write (host);
	        writer.write ("\r\nConnection: keep-alive\r\n");
	        writer.write ("Content-Type: text/xml\r\n");
	        writer.write ("Content-Length: " + request.length);
	        writer.write ("\r\n\r\n");
	        writer.flush ();
	        output.write (request);
	        output.flush ();
	        // writer.write ("\r\n");
	        // writer.flush();
	        fresh = false;
	    } catch (IOException iox) {
	        // if the connection is not "fresh" (unused), the exception may have occurred
	        // because the server timed the connection out. Give it another try.
	        if (!fresh) {
	            initConnection ();
	            write (request);
	        } else {
	            throw (iox);
	        }
	    }
	}
	
	
	public InputStream getInputStream () throws IOException {
	    String line = readLine ();
	    if (XmlRpc.debug) 
	        System.out.println (line);
	    int contentLength = 0;
	    try {
	        StringTokenizer tokens = new StringTokenizer (line);
	        String httpversion = tokens.nextToken ();
	        String statusCode = tokens.nextToken();
	        String statusMsg = tokens.nextToken ("\n\r");
	        keepalive = "HTTP/1.1".equals (httpversion);
	        if (!"200".equals (statusCode))
	            throw new IOException ("Unexpected Response from Server: "+statusMsg);
	    } catch (IOException iox) {
	        throw iox;
	    } catch (Exception x) {
	        x.printStackTrace ();
	    	  throw new IOException ("Server returned invalid Response.");
	    }
	    do {	 
	        line = readLine ();
	        if (line != null) {
	            if (XmlRpc.debug) 
	                System.out.println (line);
	            line = line.toLowerCase ();
	            if (line.startsWith ("content-length:"))
	                contentLength = Integer.parseInt (line.substring (15).trim ()); 
	            if (line.startsWith ("connection:"))
                      keepalive = line.indexOf ("keep-alive") > -1;
	        }
	    } while (line != null && ! line.equals(""));
	    return new ServerInputStream (input, contentLength);
	}


	byte[] buffer;
	private String readLine () throws IOException {
	    if (buffer == null)
	        buffer = new byte[512];
	    int next;
	    int count = 0;
	    for (;;) {
	        next = input.read();	
	        if (next < 0 || next == '\n')
	           break;
	        if (next != '\r')
	            buffer[count++] = (byte) next;
	        if (count >= 512)
	            throw new IOException ("HTTP Header too long");
	    }
	    return new String (buffer, 0, count);
	}

	protected void finalize () throws Throwable {
	    super.finalize ();
	    this.closeConnection ();
	}
	
  }


    
}

// this is borrowed from Apache JServ
class ServerInputStream extends InputStream {
    // bytes remaining to be read from the input stream. This is
    // initialized from CONTENT_LENGTH (or getContentLength()).
    // This is used in order to correctly return a -1 when all the
    // data POSTed was read.
    long available = -1;

    private InputStream in;

    public ServerInputStream(InputStream in, int available) {
        this.in = in;
        this.available = available;
    }

    public int read() throws IOException {
        if (available > 0) {
            available--;
            return in.read();
        }
        return -1;
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (available > 0) {
            if (len > available) {
                // shrink len
                len = (int) available;
            }
            int read = in.read(b, off, len);
            if (read != -1) {
                available -= read;
            } else {
                available = -1;
            }
            return read;
        }
        return -1;
    }

    public long skip(long n) throws IOException {
        long skip = in.skip(n);
        available -= skip;
        return skip;
    }


}

