package Events;

import Entities.Principle;

import java.util.List;

/* Get from the Server the Principle List in order to select Principle to send them the Extre Time Request*/
public class PrinciplesMessageEvent {
    private List<Principle> principlesList;
    public PrinciplesMessageEvent(List<Principle> principlesList) {
        this.principlesList = principlesList;
    }
    public List<Principle> getPrinciples() {
        return principlesList;
    }

    public void setSubjects(List<Principle> principles) {
        this.principlesList = principles;
    }
}
