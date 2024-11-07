import java.io.*;
import java.net.*;
class City implements Serializable
{
public int code;
public String name;
}
class Student implements Serializable
{
public int rollNumber;
public String name;
public char gender;
public City city;
}
class Client3
{
public static void main(String gg[])
{
try
{
int rollNumber=Integer.parseInt(gg[0]);
String name=gg[1];
String gender=gg[2];
int cityCode=Integer.parseInt(gg[3]);
String cityName=gg[4];
City c=new City();
c.code=cityCode;
c.name=cityName;
Student s=new Student();
s.rollNumber=rollNumber;
s.name=name;
s.gender=gender.charAt(0);
s.city=c;


ByteArrayOutputStream baos=new ByteArrayOutputStream();
ObjectOutputStream oos=new ObjectOutputStream(baos);
oos.writeObject(s);
oos.flush();
byte objectBytes[];
objectBytes=baos.toByteArray();
int requestLength=objectBytes.length;
byte header[]=new byte[1024];
int x=requestLength;
int i=1023;
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
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
int bytesToSend=requestLength;
int j=0;
int chunkSize=1024;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("Data Sent");
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
}