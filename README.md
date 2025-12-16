# 🔥 Hell Yeah Chess 🔥

A full-stack Chess system featuring a custom-built communication protocol, multi-threaded networking, and integration with the Stockfish AI engine. 



## 📺 Live Demo
https://github.com/user-attachments/assets/38c68655-172d-4127-83dc-273cf7fb7281


## 🛠️ Engineering Highlights

### 📡 Custom Networking Protocol
Unlike simple REST APIs, this project uses **low-level TCP/IP Sockets** to manage real-time game state. I designed a **custom communication protocol** to handle:
- Piece movement and synchronization.
- Player authentication and matchmaking.
- Real-time clock synchronization.
- Game state persistence across the socket stream.

### 🧵 Advanced Multithreading
To ensure a responsive user experience and high server throughput, the project utilizes a complex threading model:
- **Server-Side:** Features a main **Server Thread** to accept new connections, which hands off each session to dedicated **Handler Threads** for concurrent gameplay.
- **Client-Side:** Employs a background **Listener Thread** to intercept server messages without blocking the **JavaFX Application Thread (UI)**,enabling responsiveness.

### 🤖 Stockfish AI Integration
The server interfaces directly with the **Stockfish Engine**. Users can challenge the AI with 5 distinct difficulty levels, managed by passing UCI commands to the engine process.

### ⚙️ Technical Stack
- **Language**: Java 23
- **GUI**: JavaFX (FXML)
- **Build Tool**: Maven (Multi-module)
- **Networking**: Java Sockets (TCP/IP)
- **AI**: Stockfish (UCI Protocol)


## 🏗️ Architecture
- **`chess-server`**: Mediator for player communication and host platform for AI engines.
- **`chess-client`**: Interactive GUI with game logic.


## 🚀 Getting Started
### Prerequisites
- Java 23 or higher
- Maven 3.9+

### Download the Engine
Download the Stockfish bot engine from https://stockfishchess.org/. Rename the executable to *stockfish.exe* and place it the root folder.


### Build the Project  
Run the following command in the root directory to compile both modules and generate executable JARs:
`mvn clean install`

### Launch (One Server + Two Clients)  
I have included a helper script to launch the server and two clients simultaneously for local testing:
- WIndows:
```
./start.bat
```
- MacOS/Linux:
```
chmod +x start.sh
./start.sh
```
