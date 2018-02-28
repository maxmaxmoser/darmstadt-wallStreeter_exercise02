import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {
    final static String KV_DELIMITER = "=";
    final static String FIELD_DELIMITER = "\n";
    final static String HEADER_DELIMITER = "---------------\n";
    private Map<MessageHeaderField, String> header = new HashMap<MessageHeaderField, String>();
    private Map<String, String> body = new HashMap<String, String>();

    public Message(){ }

    public Message(String strRepresentation) {
        String messageParts[] = strRepresentation.split(HEADER_DELIMITER);
        if (messageParts.length >= 2) parseFields(messageParts[1], true);
        if(messageParts.length >= 3) parseFields(messageParts[2], false);
    }

    private void parseFields(String str, boolean header){
        String fieldAssignements[] = str.split(FIELD_DELIMITER);
        String assignementParts[] = null;
        String fieldName = null;
        String fieldValue = null;

        for (String fieldAssignement: fieldAssignements) {
            assignementParts = fieldAssignement.split(KV_DELIMITER);
            if(assignementParts.length != 2) break;
            fieldName = assignementParts[0];
            fieldValue = assignementParts[1];

            if(header) {
                setHeaderField(MessageHeaderField.valueOf(fieldName), fieldValue);
            } else {
                setBodyParam(fieldName, fieldValue);
            }
        }
    }

    public String getBodyString(){
        StringBuilder b = new StringBuilder();

        for (Object o : body.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            b.append(pair.getKey() + KV_DELIMITER + pair.getValue() + FIELD_DELIMITER);
        }

        return b.toString();
    }

    public String getHeaderString() {
        StringBuilder h = new StringBuilder();

        for (Object o : header.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            h.append(((MessageHeaderField) pair.getKey()).name() + KV_DELIMITER + pair.getValue() + FIELD_DELIMITER);
        }

        return h.toString();
    }

    public int getContentLength() {
        return this.toString().length();
    }

    @Override
    public String toString() {
        return HEADER_DELIMITER + getHeaderString() + HEADER_DELIMITER + getBodyString();
    }

    public String getEncapsulated() {
        return getContentLength() + "\n" + this.toString();
    }

    public Message setHeaderField(MessageHeaderField k, String v){
        header.put(k, v);
        return this;
    }

    public Message setBodyParam(String key, String value) {
        body.put(key, value);
        return this;
    }

    public Map<MessageHeaderField, String> getHeader() {return header;}
    public Map<String, String> getBody() {return body;}
}
