package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Competition;
import models.Question;
import models.User;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import java.sql.Date;
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

    /*public Result GetLeaderboard() {
        return ok(index.render("Your new application is ready."));
    }
    */
    public Result  Show(int nID) {
        return ok(competition.render(nID));
    }

    public Result  checkState(int nID) {
        Competition comp = Competition.find.byId((long) nID);
        ObjectNode result = Json.newObject();
        boolean bIsStarted = false;
        boolean bIsFinished = false;
        if(comp.getCurrent_question()==0)
        {
            if(comp.getUsers().size()== comp.getNumber_of_players())
            {
                comp.setCurrent_question(1);
                comp.setEnd_date(new Date(new java.util.Date().getTime() + comp.getQuestions().get(comp.getCurrent_question()-1).getTime()));
                bIsStarted = true;
            }
        }
        else
        {
            bIsStarted = true;
            if(new java.util.Date().getTime() == Competition.find.byId((long)nID).getEnd_date().getTime())
            {
                bIsFinished = true;
                if(comp.getCurrent_question() == comp.getQuestions().size())
                {
                    // Add redirect to finish page
                }
                else
                {
                    comp.setCurrent_question(User.find.byId(Long.parseLong(session("userId"))).getCurrent_question() + 1);
                }
            }
        }
        result.put("started", bIsStarted);
        result.put("finished", bIsFinished);
        return ok(result);
    }
    /*
    public Result  Finish(int nID) {
        return ok(index.render("Your new application is ready."));
    }
    */

}
