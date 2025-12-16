#!/bin/bash
echo "Starting Chess Server..."
java -jar server/target/chess-server-executable.jar &

sleep 2

echo "Starting Client 1..."
java -jar client/target/chess-client-executable.jar &

echo "Starting Client 2..."
java -jar client/target/chess-client-executable.jar &

echo "Game is ready!"
wait