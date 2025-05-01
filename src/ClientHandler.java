import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.logging.*;

/**
 * This class represents the handler thread for each new client 
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private BlockingQueue<ClientRequest> queue;

    /**
     * Constructs the clienthandler for each new client 
     * @param socket the socket where the client/server communicate through
     * @param queue The main queue that the worker thread uses to evaluate new expressions
     */
    public ClientHandler(Socket socket, BlockingQueue<ClientRequest> queue){
        this.socket = socket;
        this.queue = queue;
    }

    /**
     * Runs the client handler thread
     */
    public void run(){
        Logger logger = Logger.getLogger("MathServerLogger"); //creating a new logger for server to log new clients, new expresisons, and expression results 
         try {
            // Set up the logger 
            synchronized (logger) {
                if (logger.getHandlers().length == 0) {
                    FileHandler handler = new FileHandler("server.log", true);
                    handler.setFormatter(new SimpleFormatter());
                    logger.addHandler(handler);
                    logger.setUseParentHandlers(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Logging setup failed
        }


         try (
           ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
           ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
           //PrintWriter out1 =new PrintWriter(socket.getOutputStream(),true);
    
        ) {
            System.out.println("Handling: " + socket.getInetAddress()+ " socket: "+ socket.getPort());
            Message messageObject;
            while ((messageObject =(Message) in.readObject()) != null) {
                if(messageObject.getMessageType().equals("connect"))
                {
                    out.writeObject(new Message("connected","","Mathserver"));     // tell client that it has connected 
                    logger.info("Client "+socket.getInetAddress()+" Port: "+socket.getPort()+" has been connected at "+LocalDateTime.now());
                    
                }
                else if(messageObject.getMessageType().equals("terminate")){ //ends communication if client requests it
                    logger.info("Client "+socket.getInetAddress()+" Port: "+socket.getPort()+" has been terminated at "+LocalDateTime.now());
                    socket.close();
                    return;
                }
                else{ //means there is an expression to be solved, puts it in the main queue for worker thread 
                    queue.put(new ClientRequest(messageObject,out ));
                    logger.info("Client "+socket.getInetAddress()+" Port: "+socket.getPort()+" sent message: "+messageObject.getMessageText());
                }
    
            }
        } catch (EOFException e) {
            logger.info("Client disconnected (EOF): " + socket.getInetAddress());
        }catch (IOException e) {
            logger.warning("Client: "+ socket.getInetAddress()+ " socket: "+ socket.getPort()+" disconnected abruptly: " + e.getMessage() );
        } catch(InterruptedException e ){
            e.printStackTrace();

        }catch(ClassNotFoundException e1){
            e1.printStackTrace();
        }finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                logger.warning("Error while closing client socket: " + e.getMessage());
            }
        }





    }



    
}
