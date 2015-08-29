package controllers;

import models.Competition;
import models.Question;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.libs.Time;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.createCompetition;
import views.html.createQuestion;
import com.fasterxml.jackson.databind.node.*;
import play.api.cache.Cache;


import java.io.*;
import java.util.Date;
import java.util.List;

public class Questions extends Controller {

    public Result getCurrentQuestion()
    {
        int nCompID = Integer.parseInt(request().getQueryString("compId"));
        ObjectNode result = Json.newObject();
        Competition comp = Competition.find.byId((long) nCompID);
        result.put("questionContent", comp.getQuestions().get(comp.getCurrent_question()-1).getContent());
        result.put("index", comp.getCurrent_question());
        result.put("numOfQuestions", comp.getQuestions().size());
        result.put("precenteges", (comp.getQuestions().size() - comp.getCurrent_question()) * 100);
        result.put("time", comp.getQuestions().get(comp.getCurrent_question()-1).getTime());
        result.put("endtime", comp.getEnd_time().getTime());
        return ok(result);
    }

    public Result Check() throws IOException, InterruptedException {
        DynamicForm form = Form.form().bindFromRequest();
        int nID = Integer.parseInt(form.get("competitionId"));
        String strCode = form.get("code");
        ObjectNode result = Json.newObject();
        boolean bIsCompiled = true;
        boolean bIsSucceed = false;

        String strGeneratedName = String.valueOf(System.currentTimeMillis()) + Integer.toString(nID);
        PrintWriter writer = new PrintWriter("C:\\Borland\\BCC55\\Bin\\"+strGeneratedName + ".cpp");
        writer.println("#include <iostream>");
        writer.println("using namespace std;");
        writer.println("void main(){");
        writer.println(strCode);
        writer.println("}");
        writer.close();
        PrintWriter second = new PrintWriter("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".bat");
        second.println("cd C:\\Borland\\BCC55\\Bin");
        second.println("bcc32 " + strGeneratedName + ".cpp");
        second.close();
        String strError = "";
        //Process u = Runtime.getRuntime().exec("bcc32 test.cpp", null,new File("C:\\Borland\\BCC55\\Bin"));
        Process u = Runtime.getRuntime().exec("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".bat");
        u.waitFor();
        new File("C:\\Borland\\BCC55\\Bin\\" +strGeneratedName + ".cpp").delete();
        new File("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".bat").delete();
        BufferedReader stdError = new BufferedReader(new InputStreamReader(u.getInputStream()));
        String m;
        String output = "";
        while((m = stdError.readLine())!=null)
        {
            strError+=m;
        }
        if(strError.contains("Error"))
        {
            String[] strErrors = strError.split(":");
            for (int i=5 ; i< strErrors.length;i++)
            {
                output += strErrors[i].split("\\)")[0] +")\n";
            }

            //return ok();
            bIsCompiled = false;
            result.put("compileError", output + String.valueOf(strErrors.length-5) + " copmilation errors");
        }
        else
        {
            Process p = Runtime.getRuntime().exec("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".exe");
            p.waitFor();
            new File("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".tds").delete();
            new File("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".obj").delete();
            new File("C:\\Borland\\BCC55\\Bin\\" + strGeneratedName + ".exe").delete();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String strOutput = "";
            String s;
                while((s= stdInput.readLine())!=null)
            {
                // Add maybe /n
                strOutput+=s;
            }

            List<Question> lst = Competition.find.byId((long)nID).getQuestions();
            for(Question item : lst)
            {
                if(item.getId() == Competition.find.byId((long)nID).getCurrent_question())
                {
                    if(strOutput.equals(item.getResult()))
                    {
                        bIsSucceed = true;
                        User cur = User.find.byId(Long.parseLong(session("userId")));
                        int nPoints = (int)(item.getLevel() * (((double)System.currentTimeMillis()
                                / (double)Competition.find.byId((long)nID).getEnd_time().getTime()) *2.0));
                        cur.setPoints(cur.getPoints() + nPoints);
                        cur.setCurrent_question(cur.getCurrent_question() + 1);
                        cur.save();
                    }
                }
            }
            result.put("answer", strOutput);
        }



        result.put("success", bIsSucceed);
        result.put("compiled", bIsCompiled);

        return ok(result);

    }

    static Form<Question> questionForm = new Form(Question.class);

    public Result index()
    {
        List<Question> quests = Question.find.all();
        return ok(createQuestion.render(quests, questionForm));
    }

    public Result Create()
    {
        Form<Question> form = questionForm.bindFromRequest(request());
        if(!form.hasErrors())
        {
            form.get().save();
            return (redirect(routes.Questions.index()));
        }
        else
        {
            return (badRequest(createQuestion.render(Question.find.all(), form)));
        }
    }
}
