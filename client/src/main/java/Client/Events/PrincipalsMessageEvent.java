package Client.Events;

import Entities.Users.Principal;

import java.util.List;

/* Get from the Server the Principal List in order to select Principal to send them the Extre Time Request*/
public class PrincipalsMessageEvent {
    private List<Principal> principalsList;
    public PrincipalsMessageEvent(List<Principal> principalsList) {
        this.principalsList = principalsList;
    }
    public List<Principal> getPrincipals() {
        return principalsList;
    }

    public void setSubjects(List<Principal> principals) {
        this.principalsList = principals;
    }
}
