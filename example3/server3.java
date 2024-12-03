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

int x=1023;
i=1;
int requestLength=0;
while(x>=0)
{
requestLength=requestLength+(header[x]*i);
i=i*10;
x--;
}

OutputStream os=socket.getOutputStream();
byte ack[]=new byte[1];
ack[0]=1;
os.write(ack,0,1);
os.flush();


byte request[]=new byte[requestLength];
int bytesToRecieve=requestLength;
j=0;
i=0;
while(j<bytesToRecieve)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
request[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}

ByteArrayInputStream bais=new ByteArrayInputStream(request);
ObjectInputStream ois=new ObjectInputStream(bais);
Student s=(Student)ois.readObject();
System.out.println("Roll number : "+s.rollNumber);
System.out.println("Name : "+s.name);
System.out.println("Gender : "+s.gender);
System.out.println("City code : "+s.city.code);
System.out.println("City : "+s.city.name);

String response="Data Saved";
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
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class Server3
{
private ServerSocket serverSocket;
Server3()
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
Server3 server=new Server3();
}
}