# FTP
A simple FTP file transfer app from android to an ftp server

The android app uses [Apache Commons Net 3.6 FTPClient API](https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html).  
On the server side [FileZilla Windows Server](https://filezilla-project.org/) was used, other servers should work too but not tested.  
  
Features:
Multiple Connections can be added (but only 1 transfer will run at a time)  
Supports both IP and DDNS  
Source Path of files and folders have to be added manually(might add file viewer and selector in future)  
Destination path can also be added which would be relative to ftp servers home path  
Both source and destination paths are in [UNIX path format](https://en.wikipedia.org/wiki/Path_(computing)#Unix_style)  

[Screenshots](https://github.com/agnostic-apollo/FTP/tree/master/screenshots)  

Credits
 [Apache Commons Net 3.6 FTPClient API](https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html).  
![Icon](https://github.com/agnostic-apollo/FTP/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png =1000x100) [Source](http://www.egermeier.com/wp-content/uploads/2014/06/git-ftp-icon-150x150.png)   
