package Events;

import Entities.Person;

public class RefreshPerson {
    String message;
    private Person person;

    public RefreshPerson() {
    }

    public RefreshPerson(String messsage, Person person)
    {
        this.message = messsage;
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
    public String getMessage() {
        return message;
    }

    public void setPerson(Person student) {
        this.person = person;
    }
}
