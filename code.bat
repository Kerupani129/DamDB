@echo off

rem ���C��
	
	for /F "tokens=2 delims=:" %%P in ('chcp') do set ACTIVE_CP=%%P
	
	if %ACTIVE_CP%==932 (
		call :utf8
	) else  (
		call :sjis
	)
	
	exit /b

rem UTF-8 �ɕύX
:utf8
	chcp 65001
	exit /b

rem Shift_JIS �ɕύX
:sjis
	chcp 932
	exit /b
