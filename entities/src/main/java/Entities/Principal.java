package Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Principals")
@DiscriminatorValue("3")
public class Principal extends Person {

    @ManyToMany(mappedBy = "principals")
    private List<ExtraTime> extraTimeRequests=new ArrayList<>();
    public void extraTimeRequest(ExtraTime data) {
        for(Principal principal:data.getPrincipals()) {
            if(principal.getFullName() == getFullName())

            //TODO: pop message that extra time is requested when conditions are met
            extraTimeRequests.add(data);
        }
    }
    public Principal(){}
    public Principal(String firstName, String lastName, HSTS_Enums.Gender gender, String email, String password) {
        super(firstName, lastName, gender, email, password);
    }
    /*
    public Principal(String principleFirstName, String principleLastName, HSTS_Enums.Gender male, String firstName, String lastName){
        super(firstName,lastName);
    }
    */

    @Override
    public void receiveExtraTime(ExtraTime data) {

    }

    @Override
    public String toString() {
        return super.toString();
    }
    public List<ExtraTime> getExtraTimeRequests() {
        return extraTimeRequests;
    }

    public void setExtraTimeRequests(List<ExtraTime> extraTimeRequests) {
        this.extraTimeRequests = extraTimeRequests;
    }

}
