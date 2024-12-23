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
int x=0;
i=1;
long fileLength=0;
while(header[x]!=',')
{
fileLength=fileLength+(header[x]*i);
i=i*10;
x++;
}
x++;
StringBuffer sb=new StringBuffer();
while(x<1024)
{
sb.append((char)header[x]);
x++;
}
String name=sb.toString().trim();
System.out.println("File Name : "+name+" ,Length : "+fileLength);

OutputStream os=socket.getOutputStream();
byte ack[]=new byte[1];
ack[0]=1;
os.write(ack,0,1);
os.flush();

int chunkSize=4096;
byte bytes[]=new byte[chunkSize];
File file=new File("uploads"+File.separator+name);
if(file.exists()) file.delete();
FileOutputStream fos=new FileOutputStream(file);
j=0;
i=0;
long m=0;
while(m<fileLength)
{
bytesReadCount=is.read(bytes);
if(bytesReadCount==-1) continue;
fos.write(bytes,0,bytesReadCount);
fos.flush();
m=m+bytesReadCount;
}
fos.close();
ack[0]=1;
os.write(ack,0,1);
os.flush();
System.out.println("File save to : "+file.getAbsolutePath());
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class FTServer
{
private ServerSocket serverSocket;
FTServer()
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
FTServer server=new FTServer();
}
}