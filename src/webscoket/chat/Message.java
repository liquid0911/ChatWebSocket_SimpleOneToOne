package webscoket.chat;

public class Message
{
    private String to;
    private String content;
    	
    @Override
    public String toString() {
        return super.toString();
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
