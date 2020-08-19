import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Chat {

    public static void main(String[] args) {

        // Declarations
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //Scanner in = new Scanner(System.in);
        HashMap<Integer, ClientConnection> clientConnections = new HashMap<Integer, ClientConnection>();
        int connections = 0;

        // Read the port number from input
        System.out.println("Which port would you like to run the server on?");
        int portNum = acceptPortNumber(in);

        // Give the user some direction if they are unfamiliar with the program
        System.out.println("Type 'help' for a list of commands");

        // Start the server thread to accept incoming messages
        try {
            Server server = new Server(portNum);
            Thread serverThread = new Thread(server);
            serverThread.start();
        } catch (IOException e) {
            System.out.println("Failed to start server! Closing program!");
            exitProgram();
        }

        //Loop while the program is running
        while (true) {

            try {

                //Read input and tokenize on space to read command arguments if any
                String x = in.readLine();
                StringTokenizer st = new StringTokenizer(x, " ");
                String switchLine;

                if (st.countTokens() == 0) { switchLine = x; }
                else { switchLine = st.nextToken(); }

                switch(switchLine) {

                    // Prints a list of commands
                    case "help":
                        printCommands();
                        break;

                    // Prints the current computers IP on the local network
                    case "myip":
                        System.out.println(InetAddress.getLocalHost());
                        break;

                    // Reiterates the initial input port number
                    case "myport":
                        System.out.println(portNum);
                        break;

                    // Creates a socket connection to the input IP on the local network on the input port
                    // Use help command to see usage syntax
                    case "connect":
                        String inputIP = st.nextToken();
                        int inputPort = Integer.parseInt(st.nextToken());
                        ClientConnection newClient = new ClientConnection(inputIP, inputPort, connections);
                        clientConnections.put(connections++, newClient);
                        break;

                    // Lists all connections currently active
                    case "list":
                        printConnections(clientConnections);
                        break;

                    // Closes the active connection in the list
                    // Use help command to see usage syntax
                    case "terminate":
                        int connectionId = Integer.parseInt(st.nextToken());
                        ClientConnection termClient = clientConnections.get(connectionId);
                        termClient.closeConnection();
                        clientConnections.remove(connectionId);
                        break;

                    // Sends a message to the input connection ID
                    // Use help command to see usage syntax
                    case "send":
                        ClientConnection client = clientConnections.get(Integer.parseInt(st.nextToken()));
                        client.sendMessage(st.nextToken());
                        break;

                    // Terminate program execution
                    case "exit":
                        in.close();
                        exitProgram();
                        break;

                    // Input was not a command, let them try again and suggest 'help' command for guidance
                    default:
                        System.out.println("Sorry, that is not a proper command.");
                        System.out.println("Please type 'help' to see available commands and their syntax.");
                }

                // Handle errors elegantly
            } catch (IOException e) {
                System.out.println("Encountered IOException in command loop!");
                System.out.println(e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Improper use of command. Please type 'help' to see command syntax");
            }
        }
    }

    // Prints the available commands and their usage syntax
    private static void printCommands() {
        System.out.println("-----Commands-----\n");
        System.out.println("'myip'                                  : Display IP address");
        System.out.println("'myport'                                : Display port number");
        System.out.println("'connect <Destination IP> <Port Number>': Establish a new TCP connection");
        System.out.println("'list'                                  : Display list of all connections");
        System.out.println("'terminate <Connection ID>'             : terminate connection from list");
        System.out.println("'send <Connection ID> <Message>'        : Send message");
        System.out.println("'exit'                                  : Close program");
        return;
    }

    // Elegantly closes the program
    private static void exitProgram() {
        System.out.println("Thank you for using the Chatroom! Goodbye.");
        System.exit(0);
    }

    // Iterates over the hash map of active connections, printing the connection ID, connected IP, and port
    @SuppressWarnings("rawtypes")
    private static void printConnections(HashMap<Integer, ClientConnection> clientConnections) {
        Iterator it = clientConnections.entrySet().iterator();
        System.out.println("id: IP address | Port No.");
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ClientConnection client = (ClientConnection) pair.getValue();
            System.out.print(pair.getKey() + " : " + client.getInputIP() + " | " + client.getInputPort() + "\n");
        }
    }

    private static int acceptPortNumber(BufferedReader in) {
        boolean done = false;
        int portNum = 65556;

        while(!done) {
            try {
                portNum = Integer.parseInt(in.readLine());
                done = true;
            } catch (NumberFormatException e1) {
                System.out.println("Please enter a proper port number");
            } catch (IOException e1) {
                System.out.println("Failed to read port number! Closing Program!");
                e1.printStackTrace();
                exitProgram();
            }
        }

        return portNum;
    }
}

// A class to open a connection to a given IP and port on the local network to send a message
class ClientConnection {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    int connectionId;
    String inputIP;
    int inputPort;

    // Create socket, data input/output streams, and keep track of IP and port for later use
    public ClientConnection (String inputIP, int port, int connectionId) throws IOException {
        s = new Socket(inputIP, port);
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
        this.connectionId = connectionId;
        this.inputIP = inputIP;
        this.inputPort = port;
    }

    // Attempt to send a message to the connected IP and port
    public void sendMessage(String message) {
        try {
            // write on the output stream
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    // Cleanup and close the connection
    public void closeConnection() throws IOException {
        s.close();
        dis.close();
        dos.close();
        return;
    }

    // Getters
    public String getInputIP() {
        return inputIP;
    }

    public int getInputPort() {
        return inputPort;
    }
}

// A class to accept any incoming messages to this machine on the initial given port
// And display it on screen
class Server implements Runnable {
    ServerSocket serverSocket;
    Socket boundSocket;
    DataInputStream dis;
    DataOutputStream dos;

    // Create the server socket on instantiation
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        return;
    }

    @Override
    public void run() {

        // Loop to infinitely accept incoming messages
        while(true) {
            try {
                // Bind the socket and input/output streams
                boundSocket = serverSocket.accept();

                dis = new DataInputStream(boundSocket.getInputStream());
                dos = new DataOutputStream(boundSocket.getOutputStream());

                // read the message sent to this client and display it in proper format
                String msg = dis.readUTF();
                String senderIP = boundSocket.getInetAddress().toString();
                String senderPort = Integer.toString(boundSocket.getPort());
                System.out.println("Message received from " + senderIP);
                System.out.println("Sender's Port: " + senderPort);
                System.out.println("Message: \"" + msg + "\"");

            } catch (IOException e) {
                e.getMessage();
                return;
            }
        }
    }
}
