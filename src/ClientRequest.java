
import java.io.ObjectOutputStream;

public class ClientRequest {
    private Message message;
    private ObjectOutputStream out; // where result of the server will be attached to 

    ClientRequest( Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    public Message getMessage(){
        return message;
    }

    public ObjectOutputStream getOutput(){
        return out;
    } 

    
    
}
