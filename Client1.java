import java.io.*;
import java.net.*;
class Client1
{
public static void main(String gg[])
{
int rollNumber=Integer.parseInt(gg[0]);
String name=gg[1];
String gender=gg[2];
try
{
String request=rollNumber+","+name+","+gender+"#";
String response;
Socket socket=new Socket("localhost",5500);
InputStream is;
InputStreamReader isr;
OutputStream os;
OutputStreamWriter osw;
StringBuffer sb;
int x;
os=socket.getOutputStream();
osw=new OutputStreamWriter(os);
osw.write(request);
osw.flush();
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
response=sb.toString();
System.out.println("Response is : "+response);
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}