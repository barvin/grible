Windows Installer Creation Process
=============================================================

1. Install Inno Setup: http://www.jrsoftware.org/isdl.php

2. Copy all the files from this folder to some folder on you system (i.e. D:\Tools\Grible)

3. Download JDK and copy all its content to jdk folder (i.e. from C:\Program Files\Java\jdk1.7.0_45 to D:\Tools\Grible\jdk)

4. Open Grible.iss from D:\Tools\Grible with Inno Setup

5. Edit row
	#define BaseDir "D:\Tools\Grible"
	according to location of your folder

6. Edit row
	#define MyAppVersion "0.9.0"
	with the current version

7. Compile.