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
    public  Principle(){}
    public Principle( String firstName, String lastName, Gender gender, String email, String password) {
        super(firstName, lastName, gender, email, password);
    }
    @Override
    public void receiveExtraTime(ExtraTime data) {

    }

}
