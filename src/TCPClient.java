import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class TCPClient 
{
    public static void main(String argv[]) throws Exception
    {
       
        System.out.println("Client is running: " );

        //use try block to automatically close everything after existing try block without any manual calls
        //connect to the server using its destination IP address(127.0.0.1) and port number(6790)
       try(Socket clientSocket = new Socket("127.0.0.1", 6790);

        //set up output and input streams for sending/receiving message objects
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        Scanner scan = new Scanner(System.in))
        { 
          //prompt the client to enter their name
          String clientName;
          System.out.print("Enter your name: ");
          clientName = scan.nextLine(); //read client name 

          //send connection establishment request - send a message object with "connect" and client name
          Message connectMessage = new Message("connect","",clientName);
          out.writeObject(connectMessage);

          //wait for acknowledgement from the server - a message object with "connected"
          Message messageAck = (Message)in.readObject();
          if("connected".equalsIgnoreCase(messageAck.getMessageType()))
          {
            System.out.println("Connection Successful!");
          }
          else
          {
            System.out.println("Failed to connect");
            return;
          }

          //send math expressions at random intervals
          int numberofExpressions = 3; //number of expression send for each client
          for(int i=0;i<numberofExpressions;i++)
          {
            TimeUnit.SECONDS.sleep((int)(Math.random() * 5) + 1); //wait for 1-5 seconds before sending a new expression
            String expression = generateMathExpression(); //generate random math expressions

            //create a message object with "expression" and send the expression to the server
            Message expressionMessage = new Message("expression",expression,clientName);
            out.writeObject(expressionMessage);
            
            //receive a message object from server - either "result" or  "error"
            Message resultMessage = (Message) in.readObject();
            if("result".equalsIgnoreCase(resultMessage.getMessageType()))
            {
              System.out.println("Expression: " + expression);
              System.out.println("Result: " + resultMessage.getMessageText());
            } 
            else if("error".equalsIgnoreCase(resultMessage.getMessageType()))
            {
              System.out.println("Expression: " + expression);
              System.out.println("Error: " + resultMessage.getMessageText());
            }
          }

            //terminate connection with server once done sending all math expressions
            Message terminateMessage = new Message("terminate","",clientName);
            out.writeObject(terminateMessage);

            System.out.println("Connection terminated");
        } 
    }


    /**
     * A method to generate a random math expression of the form:
     * number1 operator number2
     */
    public static String generateMathExpression()
    {
      String[] operators = {"+","-","*","/"};

      //generate random numbers from 0-99
      int leftOperand  = (int)(Math.random() * 100); 
      int rightOperand = (int)(Math.random() * 100);

      //pick a random operator
      String operator = operators[(int)(Math.random() * operators.length)];
          
      return leftOperand + " " + operator + " " + rightOperand;
    }
}