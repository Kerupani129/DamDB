@echo off

	pushd %~dp0
	java -jar damdb.jar %*
	popd
