package Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExtraTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
    joinColumns = @JoinColumn(name = "ExtraTime_ID"),
    inverseJoinColumns = @JoinColumn(name ="principles_ID"))
    private List<Principle> principles = new ArrayList<>();
    public List<Principle> getPrincipals() {
        return this.principles;
    }
}
