import java.io.ObjectOutputStream;

/**
 * This class represents a request to the Main Server to handle 
 * The request contains a message that contains the math expression to  be solved
 * The request also contains an output stream for the result of the expression to be printed to. 
 */
public class ClientRequest {
    private Message message;
    private ObjectOutputStream out; // where result of the server will be attached to 

    /**
     * Constructs the Client Request Class
     * @param message The message the client sent which is to be handled by the main server's worker thread
     * @param out the output stream corresponding to the client where the result of the expression will be written to
     */
    ClientRequest( Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    /**
     * Gets the message from the ClientRequest object
     * @return The message from the ClientRequest
     */
    public Message getMessage(){
        return message;
    }

    /**
     * The client's corresponding output stream 
     * @return the Client's output stream 
     */
    public ObjectOutputStream getOutput(){
        return out;
    } 

    
    
}
