import java.io.Serializable;

/**
 * This class represents the message object that the clients and server will use to communicate 
 */
public class Message implements Serializable
{
    private String messageType;//connect, connected, expression, result, error, terminate
    private String messageText;//expression Strings, results, error
    private String clientName;//name of the user sending the math expression

    /**
     * Instantiates the message object 
     * @param messageType The type of message (expression, connect, terminate)
     * @param messageText The content of the message 
     * @param clientName The name of the sender 
     */
    public Message(String messageType,String messageText,String clientName)
    {
        this.messageType = messageType; 
        this.messageText = messageText; 
        this.clientName = clientName; 
    }

    //define getters methods
    /**
     * Gets the type of the message 
     * @return The message type 
     */
    public String getMessageType()
    {
        return messageType;
    }

    /**
     * gets the text in the message 
     * @return the text in the message 
     */
    public String getMessageText()
    {
        return messageText;
    }

    /**
     * Gets the name of the client sending the message
     * @return
     */
    public String getClientName()
    {
        return clientName;
    }

    //define setters methods

    /**
     * sets the type of the message
     * @param messageType the message type
     */
    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    /**
     * Sets the text of the message 
     * @param messageText the message text 
     */
    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    /**
     * stes the client name of the message 
     * @param clientName the clientname 
     */
    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

}
