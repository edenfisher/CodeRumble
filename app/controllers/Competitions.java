package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Competition;
import models.Question;
import models.User;
import play.*;
import play.api.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Timer;

public class Competitions extends Controller {

    static Form<Competition> competitionForm = new Form(Competition.class);

    public Result index() {
        List<Competition> comps = Competition.find.all();
        return ok(createCompetition.render(comps, competitionForm));
    }

    public Result Create() {
        Form<Competition> form = competitionForm.bindFromRequest(request());
        form.get().setQuestions(Question.find.all());
        if(!form.hasErrors())
        {
            form.get().save();
            return (redirect(routes.Competitions.index()));
        }
        else
        {
            return (badRequest(createCompetition.render(Competition.find.all(), form)));
        }
    }

    public Result getLeaderboard(int nID) {
        Competition comp = Competition.find.byId((long) nID);
        ArrayNode array = Json.newArray();
        ObjectNode user = Json.newObject();
        for(User usr :comp.getUsers())
        {
            boolean bIsMe = false;
            user.put("name", usr.getName());
            user.put("points", usr.getPoints());
            if(usr.getId()== Long.parseLong(session("userId")))
            {
                bIsMe = true;
            }
            user.put("isMe",bIsMe);
            array.add(user);
            user = Json.newObject();
        }

        return ok(array);
    }

    public Result  Show(int nID) {
        return ok(competition.render(nID));
    }

    public Result  checkState(int nID) {
        Competition comp = Competition.find.byId((long) nID);
        ObjectNode result = Json.newObject();
        boolean bIsStarted = false;
        boolean bIsFinished = false;
        boolean bFinishCompetition = false;

        // Initlize the competition if this is the first time the method is called
        if(comp.getCurrent_question() == 0)
        {
            // Check if all of the users entered
            if(comp.getUsers().size()== comp.getNumber_of_players())
            {
                // Initilize leaderboard
                for(User usr : comp.getUsers())
                {
                    usr.setPoints(0);
                }
                comp.setCurrent_question(1);
                comp.setEnd_time(new Timestamp(new java.util.Date().getTime() + (1000 * comp.getQuestions().get(comp.getCurrent_question() - 1).getTime())));
                comp.save();
                bIsStarted = true;
            }
        }
        else
        {
            bIsStarted = true;
            boolean bAllFinished = true;
            for (User cur : comp.getUsers())
            {
                if(cur.getCurrent_question() != comp.getCurrent_question() +1)
                {
                    bAllFinished = false;
                    break;
                }
            }

            // Checks if the time for this question passed
            if(new java.util.Date().getTime() >= comp.getEnd_time().getTime() || bAllFinished)
            {
                bIsFinished = true;

                // Checks if this is the last question
                if(comp.getCurrent_question() == comp.getQuestions().size())
                {
                    bFinishCompetition = true;
                }
                else
                {
                    for (User cur : comp.getUsers())
                    {
                        cur.setCurrent_question(comp.getCurrent_question() + 1);
                        cur.save();
                    }

                    comp.setCurrent_question(comp.getCurrent_question() + 1);
                    comp.setEnd_time(new Timestamp(new java.util.Date().getTime() + (1000 * comp.getQuestions().get(comp.getCurrent_question() - 1).getTime())));
                    comp.save();
                }
            }
        }
        result.put("started", bIsStarted);
        result.put("finished", bIsFinished);
        result.put("finishCompetition", bFinishCompetition);
        result.put("currQuestion", comp.getCurrent_question());
        return ok(result);
    }

    public Result  finish(int nID) {
        return ok(finish.render());
    }


}
