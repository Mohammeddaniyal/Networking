import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
class ClientRequestProcessor extends Thread
{
private Socket socket;
private String filePath;
private JProgressBar progressBar;
public ClientRequestProcessor(Socket socket,JProgressBar progressBar,String filePath)
{
this.socket=socket;
this.progressBar=progressBar;
this.filePath=filePath;
start();
}
public void run()
{
try
{
File file=new File(filePath);
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


int u=0;
while(j<fileLength)
{
bytesReadCount=fis.read(bytes);
os.write(bytes,0,bytesReadCount);
os.flush();
j=j+bytesReadCount;
final int l=j;
SwingUtilities.invokeLater(()->
{
progressBar.setValue(l);

progressBar.revalidate();   // Force layout refresh
    
progressBar.repaint();      // Force UI to repaint
}); 
}
try
{
Thread.sleep(50);
}catch(InterruptedException ie)
{
System.out.println(ie.getMessage());
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
class FileTableModel extends AbstractTableModel
{
private Object [][]data;
private String []title;
private int index;
FileTableModel()
{
index=0;
title=new String[2];
title[0]="S.No";
title[1]="File Name";
data=new Object[10][2];
}
public int getRowCount()
{
return data.length;
}
public int getColumnCount()
{
return title.length;
}
public String getColumnName(int columnIndex)
{
return title[columnIndex];
}
public Object getValueAt(int rowIndex,int columnIndex)
{
return data[rowIndex][columnIndex];
}
public Class getColumnClass(int columnIndex)
{
Class c=null;
try
{
if(columnIndex==0)
{
c=Class.forName("java.lang.Integer");
}
if(columnIndex==1)
{
c=Class.forName("java.lang.String");
}
}catch(Exception e)
{
System.out.println(e.getMessage());
}
return c;
}
public void add(String fileName)
{
data[index][0]=index+1;
data[index][1]=fileName;
index++;
fireTableDataChanged();
}
}
class FTClient extends JFrame
{
private JLabel serverLabel;
private JLabel portNumberLabel;
private JTextField serverTextField;
private JTextField portNumberTextField;
private JButton selectFileButton;
private FileTableModel fileTableModel;
private JTable table;
private JButton startButton;
private JLabel statusLabel;
private JPanel filePanel;
private JPanel progressPanel;
private Container container;
private JScrollPane jsp;
int totalFilesCount;
String files[];
int portNumber;
String server;
public FTClient()
{
files=new String[10];
totalFilesCount=0;
serverLabel=new JLabel("Server");
serverTextField=new JTextField(10);
portNumberLabel=new JLabel("Port No.");
portNumberTextField=new JTextField(10);
selectFileButton=new JButton("Select File");
fileTableModel=new FileTableModel();
table=new JTable(fileTableModel);
Font font=new Font("Times New Roman",Font.PLAIN,24);
table.setFont(font);

table.setRowHeight(30);
jsp=new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
startButton=new JButton("Start");
filePanel=new JPanel();
filePanel.setLayout(null);

serverLabel.setBounds(30,20,80,30);
serverTextField.setBounds(30+80+10,20,80,30);
portNumberLabel.setBounds(30+170+10,20,80,30);
portNumberTextField.setBounds(30+260+10,20,80,30);

selectFileButton.setBounds(30,20+30+10,150,30);

jsp.setBounds(15,20+50+10+30,550,290);
startButton.setBounds(40,485,100,40);

filePanel.add(serverLabel);
filePanel.add(serverTextField);
filePanel.add(portNumberLabel);
filePanel.add(portNumberTextField);
filePanel.add(selectFileButton);
filePanel.add(jsp);
filePanel.add(startButton);

progressPanel=new JPanel();
progressPanel.setLayout(new FlowLayout());
progressPanel.add(new JLabel("Progress Bar"));
container=getContentPane();
container.setLayout(new GridLayout(1,2));
container.add(filePanel);
container.add(progressPanel);
Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
int width=1200;
int heigth=600;
setSize(width,heigth);
int x=(d.width/2)-(width/2);
int y=(d.height/2)-(heigth/2);
setLocation(x,y);
setVisible(true);
startButton.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent av)
{
FTClient.this.server=serverTextField.getText().trim();
FTClient.this.portNumber=Integer.parseInt(portNumberTextField.getText());
Socket socket=null;
JProgressBar progressBar=null;
for(int i=0;i<FTClient.this.totalFilesCount;i++)
{
final String name=files[i];
File file=new File(name);
try
{
progressBar = new JProgressBar(0, (int)file.length());
JPanel panel=new JPanel();
panel.setLayout(new GridLayout(2,1));
final int fileIndex = i;  // For lambda capture

        
final JProgressBar jsp=progressBar;
final JPanel jp=panel;
// Update the UI on the EDT
        
SwingUtilities.invokeLater(() -> 
{
jsp.setValue(0);
jsp.setStringPainted(true);
 
jp.add(new JLabel(name));           
jp.add(jsp);
            
progressPanel.add(jp);
progressPanel.revalidate();  // To refresh the layout
            
progressPanel.repaint();     // To ensure it redraws
        
});
socket=new Socket(FTClient.this.server,FTClient.this.portNumber);
try
{
ClientRequestProcessor clientRequestProcessor=new ClientRequestProcessor(socket,jsp,name);
}catch(Exception e)
{
System.out.println(e.getMessage());
}

}catch(Exception e)
{
System.out.println(e.getMessage());
}
}
}
});
selectFileButton.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent av)
{
JFileChooser jFileChooser=new JFileChooser();
jFileChooser.setCurrentDirectory(new File("."));
int selectedOption=jFileChooser.showSaveDialog(FTClient.this);
if(selectedOption==jFileChooser.APPROVE_OPTION)
{
File selectedFile=jFileChooser.getSelectedFile();
String filePath=selectedFile.getAbsolutePath();
File file=new File(filePath);
System.out.println(filePath);
FTClient.this.fileTableModel.add(file.getName());
System.out.println("File added");
FTClient.this.files[FTClient.this.totalFilesCount]=filePath;
System.out.println("File name : "+FTClient.this.files[FTClient.this.totalFilesCount]);
FTClient.this.totalFilesCount++;
}
}
});
}
public static void main(String gg[])
{
FTClient ftClient=new FTClient();
}
}