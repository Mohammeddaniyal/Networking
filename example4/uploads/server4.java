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
InputStream is=socket.getInputStream();
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
byte header[]=new byte[1024];
int bytesReadCount;
int i,j,k;
j=0;
i=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
System.out.println("Header recieved");
int x=1023;
i=1;
int requestLength=0;
char c;
while(x>=0)
{
c=(char)header[x];
if(c=='?') break;
requestLength=requestLength+(header[x]*i);
i=i*10;
x--;
}
System.out.println("Request length : "+requestLength);
i=0;
String fileName="";
char cc[]=new char[22];

while(true)
{
c=(char)header[i];
if(c=='?') break;
cc[i]=c;
i++;
}
fileName=new String(cc).trim();
System.out.println("File Name : ("+fileName+")");
OutputStream os=socket.getOutputStream();
byte ack[]=new byte[1];
ack[0]=1;
os.write(ack,0,1);
os.flush();


byte request[]=new byte[requestLength];
int bytesToRecieve=requestLength;
j=0;
i=0;
File file=new File("uploads/"+fileName);
if(file.exists()) file.delete();
FileOutputStream fos=new FileOutputStream("uploads/"+fileName);
while(j<bytesToRecieve)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
fos.write(tmp);
j=j+bytesReadCount;
}
System.out.println("File Uploaded");
String response="File Uploaded";
ByteArrayOutputStream baos=new ByteArrayOutputStream();
ObjectOutputStream oos=new ObjectOutputStream(baos);
oos.writeObject(response);
oos.flush();
byte []objectBytes=baos.toByteArray();
int responseLength=objectBytes.length;
header=new byte[1024];
x=responseLength;
i=1023;
while(x>0)
{
header[i]=(byte)(x%10);
i--;
x=x/10; 
}


os.write(header,0,1024);
os.flush();
System.out.println("Response Header Sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}

int bytesToSend=responseLength;
j=0;
i=0;
int chunkSize=1024;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("Response Sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Acknoledgement Recieved");
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class Server4
{
private ServerSocket serverSocket;
Server4()
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
Server4 server=new Server4();
}
}sponseLength;
j=0;
i=0;
int chunkSize=1024;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("Response Sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Acknoledgement Recieved");
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class Server4
{
private ServerSocket serverSocket;
Server4()
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
pu