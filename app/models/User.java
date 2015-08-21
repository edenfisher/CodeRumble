package models;


import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
@Table(name = "t_users")
public class User extends Model {
    @Id
    Long id;
    String name;
    @ManyToOne
    @JoinColumn(name = "competition_id")
    Competition competition;
    int current_question;
    int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public static Finder<Long, User> find =
            new Finder<Long, User>(Long.class, User.class);


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public int getCurrent_question() {
        return current_question;
    }

    public void setCurrent_question(int current_question) {
        this.current_question = current_question;
    }
}
