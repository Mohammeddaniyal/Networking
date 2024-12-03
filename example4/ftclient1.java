import java.io.*;
import java.net.*;
class FTClient
{
public static void main(String gg[])
{
try
{
String fileName=gg[0];
File file=new File(fileName);
if(file.exists()==false) 
{
System.out.println("Invalid File");
return;
}
if(file.isDirectory())
{
System.out.println("File is directory");
return;
}
String name=file.getName();

long requestLength=file.length();


byte header[]=new byte[1024];
long x=requestLength;
int i=0;
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i++;
}
header[i]=',';
i++;
int r=0;
int y=name.length();
while(r<y)
{
header[i]=(byte)name.charAt(r);
r++;
i++;
}

while(i<1024)
{
header[i]=(byte)32;
i++;
}



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
long fileLength=requestLength;
FileInputStream fis=new FileInputStream(file);
int j=0;
int chunkSize=4096;
byte []bytes=new byte[chunkSize];
while(j<fileLength)
{
bytesReadCount=fis.read(bytes);
os.write(bytes,0,bytesReadCount);
os.flush();
j=j+bytesReadCount;
}
fis.close();
System.out.println("Data Sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("File Uploaded");
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}