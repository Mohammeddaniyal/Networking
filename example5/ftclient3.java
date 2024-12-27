import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
class FileUploadEvent
{
private String uploaderId;
private File file;
private long numberOfBytesUploaded;
public FileUploadEvent()
{
this.uploaderId=null;
this.file=null;
numberOfBytesUploaded=0;
}
public void setUploaderId(String uploaderId)
{
this.uploaderId=uploaderId;
}
public String getUploaderId()
{
return this.uploaderId;
}
public void setFile(File file)
{
this.file=file;
}
public File getFile()
{
return this.file;
}
public void setNumberOfBytesUploaded(long numberOfBytesUploaded)
{
this.numberOfBytesUploaded=numberOfBytesUploaded;
}
public long getNumberOfBytesUploaded()
{
return numberOfBytesUploaded;
}
}
interface FileUploadListener
{
public void fileUploadStatusChanged(FileUploadEvent fileUploadEvent);
}
class FileModel extends AbstractTableModel
{
private ArrayList<File> files;
FileModel()
{
files=new ArrayList<>();
}
public int getRowCount()
{
return files.size();
}
public int getColumnCount()
{
return 2;
}
public String getColumnName(int c)
{
if(c==0) return "S.No";
return "File";
}
public Class getColumnClass(int c)
{
if(c==0) return Integer.class;
return String.class;
}
public boolean isCellEditable(int r,int c)
{
return false;
}
public Object getValueAt(int r,int c)
{
if(c==0) return r+1;
return files.get(r).getAbsolutePath();
}
public void add(File file)
{
files.add(file);
fireTableDataChanged();
}
public ArrayList<File> getFiles()
{
return this.files;
}
}
class FileUploadThread extends Thread
{
private FileUploadListener fileUploadListener;
private String id;
private File file;
private String host;
private int portNumber;
public FileUploadThread(FileUploadListener fileUploadListener,File file,String id,String host,int portNumber)
{
this.fileUploadListener=fileUploadListener;
this.file=file;
this.id=id;
this.host=host;
this.portNumber=portNumber;
}
public void run()
{
try
{
String name=file.getName();
long lengthOfFile=file.length();
byte header[]=new byte[1024];
long x=lengthOfFile;
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

Socket socket=new Socket(host,portNumber);
OutputStream os=socket.getOutputStream();
os.write(header,0,1024);
os.flush();



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

long fileLength=lengthOfFile;
FileInputStream fis=new FileInputStream(file);
long j=0;
int chunkSize=4096;
byte []bytes=new byte[chunkSize];
while(j<fileLength)
{
bytesReadCount=fis.read(bytes);
os.write(bytes,0,bytesReadCount);
os.flush();
j=j+bytesReadCount;
long brc=j;
SwingUtilities.invokeLater(()->{
FileUploadEvent fileUploadEvent=new FileUploadEvent();
fileUploadEvent.setUploaderId(id);
fileUploadEvent.setFile(file);
fileUploadEvent.setNumberOfBytesUploaded(brc);
fileUploadListener.fileUploadStatusChanged(fileUploadEvent);
});
}
fis.close();
System.out.println("Data Sent");
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
class FTClientFrame extends JFrame
{
private String host;
private int portNumber;
private FileSelectionPanel fileSelectionPanel;
private FileUploadViewPanel fileUploadViewPanel;
private Container container;
public FTClientFrame(String host,int portNumber)
{
this.host=host;
this.portNumber=portNumber;
fileSelectionPanel=new FileSelectionPanel();
fileUploadViewPanel=new FileUploadViewPanel();
container=getContentPane();
container.setLayout(new GridLayout(1,2));
container.add(fileSelectionPanel);
container.add(fileUploadViewPanel);
setSize(1000,600);
setLocation(10,20);
setVisible(true);
}
class FileSelectionPanel extends JPanel implements ActionListener
{
private JLabel titleLabel;
private JButton addFileButton;
private FileModel fileModel;
private JTable table;
private JScrollPane jsp;
public FileSelectionPanel()
{
titleLabel=new JLabel("Selected Files");
fileModel=new FileModel();
table=new JTable(fileModel);
jsp=new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
addFileButton=new JButton("Add file");
setLayout(new BorderLayout());
add(titleLabel,BorderLayout.NORTH);
add(jsp,BorderLayout.CENTER);
add(addFileButton,BorderLayout.SOUTH);
addFileButton.addActionListener(this);
}
public void actionPerformed(ActionEvent ev)
{
JFileChooser jfc=new JFileChooser();
jfc.setCurrentDirectory(new File("."));
int selectedOption=jfc.showOpenDialog(this);
if(selectedOption==jfc.APPROVE_OPTION)
{
File file=jfc.getSelectedFile();
ArrayList<File> files=fileModel.getFiles();
if(files!=null && files.contains(file))
{
JOptionPane.showMessageDialog(this,"File already selected");
return;
}
fileModel.add(file);
}
}
public ArrayList<File> getFiles()
{
return fileModel.getFiles();
}
}//inner class end's here
class FileUploadViewPanel extends JPanel implements ActionListener,FileUploadListener
{
private JButton uploadFileButton;
private ArrayList<File> files;
private JPanel progressPanelContainer;
private ProgressPanel progressPanel;
private ArrayList<ProgressPanel> progressPanels;
private JScrollPane jsp;
private ArrayList<FileUploadThread> fileUploaders;
public FileUploadViewPanel()
{
uploadFileButton=new JButton("Upload File");
setLayout(new BorderLayout());
add(uploadFileButton,BorderLayout.NORTH);
uploadFileButton.addActionListener(this);
}
public void actionPerformed(ActionEvent ev)
{
files=fileSelectionPanel.getFiles();
if(files.size()==0)
{
JOptionPane.showMessageDialog(FTClientFrame.this,"No files selected to upload");
return;
}
progressPanelContainer=new JPanel();
progressPanelContainer.setLayout(new GridLayout(files.size(),1));
jsp=new JScrollPane(progressPanelContainer,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
progressPanels=new ArrayList<ProgressPanel>();
fileUploaders=new ArrayList<>();
FileUploadThread fileUploadThread;
String uploaderId;
for(File file:files)
{
uploaderId=UUID.randomUUID().toString();
progressPanel=new ProgressPanel(uploaderId,file);
progressPanels.add(progressPanel);
progressPanelContainer.add(progressPanel);
fileUploadThread=new FileUploadThread(this,file,uploaderId,host,portNumber);
fileUploaders.add(fileUploadThread);
}
add(jsp,BorderLayout.CENTER);
repaint();
revalidate();
for(FileUploadThread fileUpload:fileUploaders)
{
fileUpload.start();
}
}
public void fileUploadStatusChanged(FileUploadEvent fileUploadEvent)
{
String uploaderId=fileUploadEvent.getUploaderId();
long numberOfBytesUploaded=fileUploadEvent.getNumberOfBytesUploaded();
File file=fileUploadEvent.getFile();
for(ProgressPanel progressPanel:progressPanels)
{
if(progressPanel.getId().equals(uploaderId))
{
progressPanel.updateProgressBar(numberOfBytesUploaded);
break;
}
}
}
class ProgressPanel extends JPanel
{
private JProgressBar progressBar;
private String id;
private File file;
private long fileLength;
private JLabel fileNameLabel;
ProgressPanel(String id,File file)
{
progressBar=new JProgressBar(0,100);
this.id=id;
this.file=file;
fileNameLabel=new JLabel("Uploading : "+file.getAbsolutePath());
fileLength=file.length();
setLayout(new GridLayout(2,1));
add(fileNameLabel);
add(progressBar);
}
public String getId()
{
return this.id;
}
public void updateProgressBar(long bytesUploaded)
{
int percentage;
if(fileLength==bytesUploaded) percentage=100;
else percentage=(int)((bytesUploaded*100)/fileLength);
progressBar.setValue(percentage);
if(percentage==100)
{
fileNameLabel.setText("File Uploaded : "+file.getAbsolutePath());
}
}
}//inner inner's class ends
}//inner class ends
public static void main(String gg[])
{
FTClientFrame fcf=new FTClientFrame("localhost",5500);
}
}