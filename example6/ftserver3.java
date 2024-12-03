import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
class RequestProcessor extends Thread
{
private Socket socket;
private String id;
private FTServerFrame fsf;
RequestProcessor(Socket socket,String id,FTServerFrame fsf)
{
this.socket=socket;
this.id=id;
this.fsf=fsf;
start();
}
public void run()
{
try
{
SwingUtilities.invokeLater(()->{
fsf.updateLog("Client Connected and alotted id is :"+id);
});
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
long fol=fileLength;
SwingUtilities.invokeLater(()->{
fsf.updateLog("Receiving file : "+name+" of length : "+fol);
});

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
SwingUtilities.invokeLater(()->{
fsf.updateLog("File save to : "+file.getAbsolutePath());
fsf.updateLog("Connection with client whose id is : "+id+" closed.");
});
socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class FTServer extends Thread
{
private ServerSocket serverSocket;
private FTServerFrame fsf;
FTServer(FTServerFrame fsf)
{
this.fsf=fsf;
}
public void run()
{
try
{
this.serverSocket=new ServerSocket(5500);
}catch(Exception e)
{
System.out.println(e);
}
this.startListening();
}
private void startListening()
{
try
{
Socket socket;
while(true)
{
SwingUtilities.invokeLater(()->{
fsf.updateLog("Server is ready to accept request on port 5500");
});
socket=this.serverSocket.accept();
RequestProcessor requestProcessor=new RequestProcessor(socket,UUID.randomUUID().toString(),fsf);
}
}catch(Exception e)
{
System.out.println(e);
}
}
}
class FTServerFrame extends JFrame implements ActionListener
{
private FTServer server;
private JTextArea jta;
private JScrollPane jsp;
private JButton button;
private Container container;
private boolean serverState=false;
FTServerFrame()
{
jta=new JTextArea();
jsp=new JScrollPane(jta,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
button=new JButton("Start");
container=getContentPane();
container.setLayout(new BorderLayout());
container.add(jsp,BorderLayout.CENTER);
container.add(button,BorderLayout.SOUTH);
setLocation(100,100);
setSize(500,500);
setVisible(true);
button.addActionListener(this);
}
public void updateLog(String message)
{
jta.append(message+"\n");
}
public void actionPerformed(ActionEvent ev)
{
if(serverState==false)
{
serverState=true;
button.setText("Stop");
server=new FTServer(this);
server.start();
}
else
{
serverState=false;
button.setText("Start");
jta.append("Server stopped\n");
server.stop();
}
}
public static void main(String gg[])
{
FTServerFrame fsf=new FTServerFrame();
}

}