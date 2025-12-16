@echo off
echo Starting Chess Server...
start java -jar server/target/chess-server-executable.jar

echo Waiting for server to initialize...
timeout /t 2

echo Starting Client 1...
start java -jar client/target/chess-client-executable.jar

echo Starting Client 2...
start java -jar client/target/chess-client-executable.jar

echo Game is ready!
pause