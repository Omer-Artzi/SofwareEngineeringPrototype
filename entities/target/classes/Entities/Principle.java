package Entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Teachers")
@DiscriminatorValue("3")
public class Principle extends Person {
    private List<ExtraTime> extraTimeRequests;
    public void extraTimeRequest(ExtraTime data) {
        for(Principle principal:data.getPrincipals()) {
            if(principal.getFullName() == getFullName())
                //TODO: pop message that extra time is requested when conditions are met
                extraTimeRequests.add(data);
        }
    }

    @Override
    public void receiveExtraTime(ExtraTime data) {

    }

}
