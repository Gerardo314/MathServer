import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

public class Server {
    public static void main(String[] args) throws Exception {

        BlockingQueue<ClientRequest> mainQueue = new  LinkedBlockingQueue<ClientRequest>(); 
        ServerSocket welcomSocket = new ServerSocket(6790);
        
        Logger logger =  Logger.getLogger("MathServerLogger");
         
        FileHandler handler = new FileHandler("server.log", true);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
           


        while (true) { 
            System.out.println("running ");
            Socket clientSocket =  welcomSocket.accept();
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
        
    }
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
