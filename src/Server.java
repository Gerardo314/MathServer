import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

/**
 * This class represents the Server that handles mathematical epxressions requests from clients 
 */
public class Server {
    public static void main(String[] args) throws Exception {

        BlockingQueue<ClientRequest> mainQueue = new  LinkedBlockingQueue<ClientRequest>(); //queue for solving expressions (shared by all clients)
        ServerSocket welcomeSocket = null;
        
        Logger logger =  Logger.getLogger("MathServerLogger");//instantiates logger to log everything
        try
        { 
            welcomeSocket = new ServerSocket(6790);
            FileHandler handler = new FileHandler("server.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            


            while (true) { 
                System.out.println("running ");
                Socket clientSocket =  welcomeSocket.accept();
                //PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream());
            
                new Thread(new ClientHandler(clientSocket,mainQueue)).start(); // create a new thread to handle new clients
                new Thread(() -> { // worker thread to evaluate equations from the main queue
                    while (true) {
                        try {
                            ClientRequest req = mainQueue.take();  // blocks until available
                            double result = evaluate(req.getMessage().getMessageText(),req.getOutput()); // evaluate expression
                            req.getOutput().writeObject(new Message("result",Double.toString(result),"Mathserver"));     // send back to client
                            logger.info("Client "+clientSocket.getInetAddress()+" Port: "+clientSocket.getPort()+" has been given result: "+ result+ " at: " +LocalDateTime.now());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }    
        }  catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (welcomeSocket != null && !welcomeSocket.isClosed()) {
                try {
                    welcomeSocket.close();
                    System.out.println("Server socket closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Method to evaluate a simple math expression 
     * @param expression The expression to be solved 
     * @param out the output stream for the respective client where the result is sent to 
     * @return the result of the expression 
     * @throws IOException An exception can be thrown if there is a divide by zero 
     */
    public static double evaluate(String expression, ObjectOutputStream out) throws IOException{
        expression = expression.replaceAll("\\s+", "");
        char[] operators = {'+', '-', '*', '/'};
        int opIndex = -1;
        char operator = 0;

        // Find which operator is in the expression
        for (char op : operators) {
            opIndex = expression.indexOf(op);
            if (opIndex != -1) {
                operator = op;
                break;
            }
        }

        double left = Double.parseDouble(expression.substring(0, opIndex));
        double right = Double.parseDouble(expression.substring(opIndex + 1));
        double result = -1;
        switch (operator) {
            case '+':
                 result =  left + right;
                 break;
            case '-': 
                result =  left - right;
                break;
            case '*': 
                result =  left * right;
                break;
            case '/': 
                if(right ==0){
                    out.writeObject(new Message("error", "Divide by zero", "MathServer"));//throw new IllegalStateException("divide by zero");
                    return Double.NaN;
                }
                else result=  left / right;
                break;
            default: 
                out.writeObject(new Message("error", "Unknown operator", "MathServer"));
                return Double.NaN;
        }
        return result;
    }



}
