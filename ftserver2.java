import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
String text="File Name : "+name+" ,Length : "+fileLength;
SwingUtilities.invokeLater(new Runnable()
{
public void run()
{
FTServer.appendToTextArea(text+"\n");
}
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
String text1="";
while(m<fileLength)
{
bytesReadCount=is.read(bytes);
if(bytesReadCount==-1) continue;
fos.write(bytes,0,bytesReadCount);
fos.flush();
/*
SwingUtilities.invokeLater(new Runnable()
{
public void run()
{
FTServer.appendToTextArea("file uploading....."+m+"/"+fileLength);
}
});
*/
m=m+bytesReadCount;
}
fos.close();
ack[0]=1;
os.write(ack,0,1);
os.flush();
System.out.println("File saved to : "+file.getAbsolutePath());

SwingUtilities.invokeLater(new Runnable()
{
public void run()
{
FTServer.appendToTextArea("File Saved to : "+file.getAbsolutePath()+"\n");
}
});


socket.close();
}catch(Exception e)
{
System.out.println(e);
}
}
}
class FTServer extends JFrame
{
private ServerSocket serverSocket;
private JLabel portNumberLabel;
private JTextField portNumberTextField;
private JButton startButton;
private JButton stopButton;
static private JTextArea textArea;
private boolean START_MODE,STOP_MODE;
private Container container;
private int portNumber;
FTServer()
{
portNumberLabel=new JLabel("Port No.");
portNumberTextField=new JTextField(10);
startButton=new JButton("Start");
stopButton=new JButton("Stop");
textArea=new JTextArea(40,100);
container=getContentPane();
container.setLayout(null);
portNumberLabel.setBounds(60,20,60,30);
portNumberTextField.setBounds(60+60,20,80,30);
startButton.setBounds(60+60+80+30,20,100,30);
stopButton.setBounds(60+60+80+30,20,100,30);
textArea.setBounds(20,30+20+20,545,470);
stopButton.setVisible(false);
container.add(portNumberLabel);
container.add(portNumberTextField);
container.add(startButton);
container.add(stopButton);
container.add(textArea);
Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
int width=600;
int heigth=600;
setSize(width,heigth);
int x=(d.width/2)-(width/2);
int y=(d.height/2)-(heigth/2);
setLocation(x,y);
setVisible(true);

startButton.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
String pns=portNumberTextField.getText().trim();
FTServer.this.portNumber=Integer.parseInt(pns);
portNumberTextField.setEnabled(false);
startButton.setVisible(false);
startButton.setEnabled(false);
stopButton.setVisible(true);
try
{
FTServer.this.serverSocket=new ServerSocket(portNumber);

new Thread(new Runnable(){
public void run()
{
FTServer.this.startListening();
}
}).start();

}catch(Exception e)
{
System.out.println(e);
}

}
});

stopButton.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent av)
{
stopButton.setEnabled(false);
stopButton.setVisible(false);
portNumberTextField.setEnabled(true);
startButton.setVisible(true);
startButton.setEnabled(true);
}
});
}

static public void appendToTextArea(String text)
{
textArea.append(text);
}


private void startListening()
{
try
{
Socket socket;
RequestProcessor requestProcessor;
while(true)
{
System.out.println("Server is ready to accept  on port "+portNumber);
String s="Server is ready to accept request on port "+portNumber;
System.out.println("Before invoking invokeLater");


SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    
textArea.append(s + "\n");
                
}
            
});
System.out.println("After invoking invokeLater");
socket=this.serverSocket.accept();
requestProcessor=new RequestProcessor(socket);
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