import java.io.*;
import java.net.*;
class Client4
{
public static void main(String gg[])
{
try
{
String filePath=gg[0];
File file=new File(filePath);
if(file.exists()==false)
{
System.out.println("File not found");
return;
}
int m=filePath.length()-1;
while(m>=0)
{
if(filePath.charAt(m)=='/') break;
m--;
}
m++;
String fileName=filePath.substring(m);
System.out.println("File Name : "+fileName);
int requestLength=(int)file.length();
System.out.println(requestLength);
int x=requestLength;
byte header[]=new byte[1024];
int i=1023;
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
header[i]=(byte)'?';
i--;
i=0;
int u=fileName.length();
while(i<u)
{
header[i]=(byte)fileName.charAt(i);
i++;
}
header[i]='?';

System.out.println("Header Done");
Socket socket=new Socket("localhost",5500);
OutputStream os=socket.getOutputStream();
os.write(header,0,1024);
os.flush();

System.out.println("Header sent");

InputStream is;
is=socket.getInputStream();
byte ack[];
ack=new byte[1];
int bytesReadCount;
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1)continue;
break;
}
System.out.println("Acknowledgement recieved");

FileInputStream fis=new FileInputStream(file);

int bytesToSend=requestLength;
int j=0;
int chunkSize=1024;
byte bytes[]=new byte[1024];
byte _byte;
int b;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
b=0;
while( b<chunkSize )
{
_byte=(byte)(fis.read());
bytes[b]=_byte;
b++;
}

os.write(bytes,0,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("File Uploaded Sent");
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
int k;
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
x=1023;
i=1;
int responseLength=0;
while(x>=0)
{
responseLength=responseLength+(header[x]*i);
i=i*10;
x--;
}
System.out.println("Response Length : "+responseLength);
ack[0]=1;
os.write(ack,0,1);
os.flush();
System.out.println("Acknowledgement Sent");
byte response[]=new byte[responseLength];
int bytesToRecieve=responseLength;
j=0;
i=0;
while(j<bytesToRecieve)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
response[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
System.out.println("Response Recieved");
os.write(ack,0,1);
os.flush();
socket.close();
ByteArrayInputStream bais=new ByteArrayInputStream(response);
ObjectInputStream ois=new ObjectInputStream(bais);
String responseString=(String)ois.readObject();
System.out.println("Response : "+responseString);
}catch(Exception e)
{
System.out.println(e);
}
}
}r(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}

System.out.println("Header recieved");
x=1023;
i=1;
int responseLength=0;
while(x>=0)
{
responseLength=responseLength+(header[x]*i);
i=i*10;
x--;
}