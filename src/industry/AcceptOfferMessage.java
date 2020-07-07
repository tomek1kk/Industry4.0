package industry;

public class AcceptOfferMessage {
    private String _id;
    public AcceptOfferMessage(String id){
        _id = id;
    }
    public String getId() {
        return _id;
    }
}