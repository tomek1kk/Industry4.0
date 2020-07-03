package industry;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlanElement {
    public PlanMessage _messageContent;
    public ACLMessage _aclMessage;
    public long _productionEnd;
    public AID _receiver;
    public AID _requestingAgent;
    public boolean _bestOffer;
    public boolean _lastProcess;
    public boolean _obtained;
    public PlanMessage _upperMessageContent;
    public PlanElement(PlanMessage messageContent, ACLMessage aclMessage, AID receiver, AID requestingAgent, PlanMessage upperMessageContent){
        _messageContent = messageContent;
        _aclMessage = aclMessage;
        _productionEnd = 0;
        _receiver = receiver;
        _requestingAgent = requestingAgent;
        _bestOffer = false;
        _lastProcess = false;
        _obtained = false;
        _upperMessageContent = upperMessageContent;
    }
}
