@echo off

	set CLASSPATH=.;lib/sqlite-jdbc-3.8.10.1.jar;lib/jsoup-1.8.2.jar
	
	if not exist classes (
		mkdir classes
	)
	if not exist bin (
		mkdir bin
	)
	
	javac -d classes -sourcepath src src/net/kerupani129/damdb/Main.java
	if ERRORLEVEL 1 (
		exit /b
	)
	
	jar cfm bin/damdb.jar MANIFEST.MF META-INF -C classes .
	if ERRORLEVEL 1 (
		exit /b
	)
	
	rem bin\damdb http://www.clubdam.com/app/damtomo/member/info/Profile.do?damtomoId=MzA4MzY0NjQ
