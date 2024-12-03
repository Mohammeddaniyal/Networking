import java.net.*;
import java.io.*;
class RequestProcessor extends Thread
{
private Socket socket;
RequestProcessor(Socket socket)
{
this.socket=socket;
start();
}
public void run()
{
try
{
OutputStream os;
OutputStreamWriter osw;
InputStream is;
InputStreamReader isr;
StringBuffer sb;
String pc1,pc2,pc3;
int x;
int rollNumber;
String name,gender;
int c1,c2;
String request,response;
is=socket.getInputStream();
isr=new InputStreamReader(is);
sb=new StringBuffer();
while(true)
{
x=isr.read();
if(x==-1) break;
if(x=='#') break;
sb.append((char)x);
}
request=sb.toString();
c1=request.indexOf(",");
c2=request.indexOf(",",c1+1);
pc1=request.substring(0,c1);
pc2=request.substring(c1+1,c2);
pc3=request.substring(c2+1);
rollNumber=Integer.parseInt(pc1);
name=pc2;
gender=pc3;
System.out.printf("Roll number %d, Name %s, Gender %s\n",rollNumber,name,gender);
response="Data Saved#";
os=socket.getOutputStream();
osw=new OutputStreamWriter(os);
osw.write(response);
osw.flush();
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class Server2
{
private ServerSocket serverSocket;
Server2()
{
try
{
this.serverSocket=new ServerSocket(5500);
this.startListening();
}catch(Exception e)
{
System.out.println(e);
}
}
private void startListening()
{
try
{
Socket socket;
while(true)
{
System.out.println("Server is ready to accept request on port 5500");
socket=this.serverSocket.accept();
RequestProcessor requestProcessor=new RequestProcessor(socket);
}
}catch(Exception e)
{
System.out.println(e);
}
}
public static void main(String gg[])
{
Server2 server=new Server2();
}
}