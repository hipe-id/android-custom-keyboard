package id.hipe.keyboard.calculator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;

public class EvaluateEngine {

    Double answer;
    private Context rhino;
    private Scriptable scope;

    public Double evaluate(String question) {

        Object[] functionParams = new Object[]{question};

        //The js function
        String script = "function evaluate(arithmetic){  return eval(arithmetic)    ;} ";

        Context rhino = Context.enter();

        //disabling the optimizer to better support Android.
        rhino.setOptimizationLevel(-1);

        try {

            Scriptable scope = rhino.initStandardObjects();

            /**
             * evaluateString(Scriptable scope, java.lang.String source, java.lang.String sourceName,
             * int lineno, java.lang.Object securityDomain)
             *
             */
            rhino.evaluateString(scope, script, "JavaScript", 1, null);


            Function function = (Function) scope.get("evaluate", scope);


            answer = (Double) function.call(rhino, scope, scope, functionParams);


        } catch (RhinoException e) {

            e.printStackTrace();

        } finally {
            Context.exit();
        }

        return answer;
    }
}
