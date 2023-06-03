package Events;

import Entities.Principal;

import java.util.List;

/* Get from the Server the Principle List in order to select Principle to send them the Extre Time Request*/
public class PrinciplesMessageEvent {
    private List<Principal> principlesList;
    public PrinciplesMessageEvent(List<Principal> principlesList) {
        this.principlesList = principlesList;
    }
    public List<Principal> getPrinciples() {
        return principlesList;
    }

    public void setSubjects(List<Principal> principals) {
        this.principlesList = principals;
    }
}
