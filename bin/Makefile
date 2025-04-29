# Compiler
JAVAC = javac

# Java programs
SERVER = Server
CLIENT = TCPClient

# Java files
SOURCES = ClientHandler.java ClientRequest.java Message.java Server.java TCPClient.java

# The target to compile all Java files
all: $(SOURCES)
	$(JAVAC) $(SOURCES)

# The target to run the server
run-server: $(SOURCES)
	java $(SERVER)

# The target to run the client
run-client: $(SOURCES)
	java $(CLIENT)

# Clean up compiled class files
clean:
	rm -f *.class
