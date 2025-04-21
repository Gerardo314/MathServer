import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

class TCPClient 
{

    public static void main(String argv[]) throws Exception
    {
        String clientName;
        System.out.println("Client is running: " );

        Socket clientSocket = new Socket("127.0.0.1", 6789);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        Scanner scan = new Scanner(System.in);

        //prompt the clients to their name
        System.out.print("Enter your name: ");
        clientName = scan.nextLine(); //read client name 

        //send connection request
        Message connectMessage = new Message("connect","",clientName);
        out.writeObject(connectMessage);

        //wait for acknowledgement from the server
        Message messageAck = (Message)in.readObject();
        if("connected".equalsIgnoreCase(messageAck.getMessageType()))
        {
           System.out.println("Connection Successful!");
        }
        else
        {
          System.out.println("Failed to connect!");
          return;
        }

        //send math expression at random intervals
        String[] expressions =
        {
          "2 * (3 + 4)",
          "10 / 5",
          "10 / 0",
        };

        for(String expressionString: expressions)
        {
          TimeUnit.SECONDS.sleep((int)(Math.random() * 3) + 1); //wait for 1-3 seconds
          Message expressionMessage = new Message("expression",expressionString,clientName);
          out.writeObject(expressionMessage);
          
          //get results from server 
          Message resultMessage = (Message) in.readObject();
          if("result".equalsIgnoreCase(resultMessage.getMessageType()))
          {
            System.out.println("Result: " + resultMessage.getMessageText());
          } 
          else if("error".equalsIgnoreCase(resultMessage.getMessageType()))
          {
            System.out.println("Error: " + resultMessage.getMessageText());
          }
        }
          
          //terminate connection with server once done sending all math expressions
          Message terminateMessage = new Message("terminate","",clientName);
          out.writeObject(terminateMessage);

          System.out.println("Connection terminated");
        clientSocket.close();
    }
}
