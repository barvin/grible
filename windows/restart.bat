@ECHO OFF
IF EXIST grible_new.war (
	PING 192.168.2.2 -n 1 -w 1000 >NUL
	grible.exe stop
	DEL grible.war
	RENAME grible_new.war grible.war
	grible.exe start
)