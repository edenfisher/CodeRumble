package controllers;

import models.Competition;
import models.User;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import java.util.List;

public class Users extends Controller {
    static Form<User> usersForm = new Form(User.class);



    public Result index() {
        List<User> quests = User.find.all();
        return ok(login.render(quests, usersForm));
    }

    public Result Create()
    {
        Form<User> form = usersForm.bindFromRequest(request());
        if(!form.hasErrors())
        {
            form.get().save();
            session("userId", form.get().getId().toString());
            return (redirect(routes.Users.index()));
        }
        else
        {
            return (badRequest(login.render(User.find.all(), form)));
        }
    }
}
