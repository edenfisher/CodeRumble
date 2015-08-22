package models;


import com.avaje.ebean.Model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "t_competitions")
public class Competition extends Model {
    @Id
    @Column(name = "id")
    Long id;
    String name;
    int number_of_questions;
    int number_of_players;
    int current_question;
    Timestamp end_time;
    @ManyToMany
    List<Question> questions;
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    List<User> users;

    public static Finder<Long, Competition> find =
            new Finder<Long, Competition>(Long.class, Competition.class);

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber_of_questions() {
        return number_of_questions;
    }

    public void setNumber_of_questions(int number_of_questions) {
        this.number_of_questions = number_of_questions;
    }

    public int getCurrent_question() {
        return current_question;
    }

    public void setCurrent_question(int current_question) {
        this.current_question = current_question;
    }

    public int getNumber_of_players() {
        return number_of_players;
    }

    public void setNumber_of_players(int number_of_players) {
        this.number_of_players = number_of_players;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
