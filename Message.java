import java.io.Serializable;

public class Message implements Serializable
{
    private String messageType;
    private String messageText;
    private String clientName;

    public Message(String messageType,String messageText,String clientName)
    {
        this.messageType = messageType;
        this.messageText = messageText;
        this.clientName = clientName;
    }

    //define getters methods
    public String getMessageType()
    {
        return messageType;
    }

    public String getMessageText()
    {
        return messageText;
    }

    public String getClientName()
    {
        return clientName;
    }

    //define setters methods
    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

}
