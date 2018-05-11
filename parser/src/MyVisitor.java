/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.HashMap;
import java.util.Map;
import src.de.letsbuildacompiler.parser.AdditionrecursionBaseVisitor;
import src.de.letsbuildacompiler.parser.AdditionrecursionParser;
import src.de.letsbuildacompiler.parser.AdditionrecursionParser.ExpressionContext;
import Exceptions.UndeclaredVariableException;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author Mahika
 */
public class MyVisitor extends AdditionrecursionBaseVisitor<String> {

    private Map <String, Integer> variables = new HashMap <>();
    private JvmStack jvmStack = new JvmStack();
    private int branchCounter = 0;
    private int compareCount = 0;
    private int andCounter =0;
    private int orCounter = 0;
    private int loopCount = 0;              //number of loops reached in program
    private int loopNum = 0;                // loopID
    private int whileCount = 0;
    private int forCount = 0;
    
    
   LoopCount c = new LoopCount();
   Count_If a = new Count_If(0);
   Count_relational b = new Count_relational (0);
//    @Override
//    public String visitPrintln(AdditionrecursionParser.PrintlnContext ctx) {
//        String argumentInstructions = visit(ctx.argument);
//        DataType type = jvmStack.pop();
//        return "  getstatic java/lang/System/out Ljava/io/PrintStream;\n" + 
//				 argumentInstructions + "\n" + 
//				"  invokevirtual java/io/PrintStream/println(I)V\n";
//    }

    @Override
    public String visitSimplePrintStatement(AdditionrecursionParser.SimplePrintStatementContext ctx) {
        String argumentInstructions = visit(ctx.argument);
        DataType type = jvmStack.pop();
        return "  getstatic java/lang/System/out Ljava/io/PrintStream;\n" + 
				 argumentInstructions + "\n" + 
				"  invokevirtual java/io/PrintStream/println(I)V\n";
    }

    @Override
    public String visitPrintStatementAndVariable(AdditionrecursionParser.PrintStatementAndVariableContext ctx) {
        return " getstatic java/lang/System/out Ljava/io/PrintStream;" + "\n" +
                "new java/lang/StringBuilder" + "\n" + "dup" + "\n" +
                "invokespecial java/lang/StringBuilder/<init>()V" + "\n" +  "ldc " + ctx.txt.getText() + "\n" +
                " invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;" + "\n" +
                " iload " + requireVariableIndex(ctx.varName) + "\n" + 
                "invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;" + "\n" +
                " invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;" + "\n" +
                "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V" + "\n";           
    }
    
    @Override
    public String visitPlus(AdditionrecursionParser.PlusContext ctx) {
      /*  return visitChildren(ctx) + "\n" +  
       "iadd"; */
       String instructions = visitChildren(ctx) + "\n" +  
       "iadd";
       jvmStack.pop();
       jvmStack.pop();
      jvmStack.push(DataType.INT);
       return instructions;
    }

    @Override
    public String visitMinus(AdditionrecursionParser.MinusContext ctx) {
           /*    return visitChildren(ctx) + "\n" +  
                "isub";         */
           String instructions = visitChildren(ctx) + "\n" +  
                "isub";
           jvmStack.pop();
           jvmStack.pop();
           jvmStack.push(DataType.INT);
           return instructions;
    }

    @Override
    public String visitDiv(AdditionrecursionParser.DivContext ctx) {
        String instructions= visitChildren(ctx) + "\n" + 
                "idiv";
        jvmStack.pop();
        jvmStack.pop();
        jvmStack.push(DataType.INT);
        return instructions;
    }

    @Override
    public String visitMult(AdditionrecursionParser.MultContext ctx) {
        String instructions= visitChildren(ctx) + "\n" + 
                "imul";
        
        jvmStack.pop();
       jvmStack.pop();
        jvmStack.push(DataType.INT);
        return instructions;
    }
    
    
    @Override
    public String visitNumber(AdditionrecursionParser.NumberContext ctx) {
      jvmStack.push(DataType.INT);
       return "ldc " + ctx.number.getText();
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
       if(aggregate == null){
           return nextResult;
       } if (nextResult == null){
           return aggregate;
       }
       return aggregate + "\n" + nextResult;
    }

    @Override
    public String visitVarDeclarationAndAssignment(AdditionrecursionParser.VarDeclarationAndAssignmentContext ctx) {
        variables.put(ctx.varName.getText(), variables.size());
        String instructions = visit(ctx.exp) + "\n" + 
              "istore " + requireVariableIndex(ctx.varName);
      return instructions;
    }

    

    @Override
    public String visitSimpleVarDeclaration(AdditionrecursionParser.SimpleVarDeclarationContext ctx) {
         variables.put(ctx.varName.getText(), variables.size());                  
         return ""; 
    }

    @Override
    public String visitListDeclaration(AdditionrecursionParser.ListDeclarationContext ctx) {
        variables.put(ctx.varName.getText(), variables.size());                  
         return "";
    }

    

    

  //  @Override
  //  public String visitAssignment(AdditionrecursionParser.AssignmentContext ctx) {
     /*   return visit(ctx.expr) + "\n" + */
     //   "istore " + variables.get(ctx.varName.getText());           //takes top value on top of stack and save it in variable table
      /*      "istore " + requireVariableIndex(ctx.varName);  */
  //    String instructions = visit(ctx.expr) + "\n" + 
  //            "istore " + requireVariableIndex(ctx.varName);
  //    jvmStack.pop();
  //    return instructions;
 //     }

    @Override
    public String visitDirectVarAssignment(AdditionrecursionParser.DirectVarAssignmentContext ctx) {
      String instructions = visit(ctx.expr) + "\n" + 
              "istore " + requireVariableIndex(ctx.varName);
      return instructions;
  
    }  
    
    

    @Override
    public String visitInputVar(AdditionrecursionParser.InputVarContext ctx) {
       String instructions = "new java/util/Scanner" + "\n" + "dup" + "\n" +"getstatic java/lang/System/in Ljava/io/InputStream;" + 
               "\n" + "invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V" + "\n" + "astore " + requireVariableIndex(ctx.varName) +
               "\n" + "aload " + requireVariableIndex(ctx.varName) + "\n" + "invokevirtual java/util/Scanner/nextInt()I" +"\n" + "istore " + requireVariableIndex(ctx.varName) + "\n";
       return instructions;
    }
    
    

    @Override
    public String visitVariable(AdditionrecursionParser.VariableContext ctx) {
        /*
        Integer varIndex = variables.get(ctx.varName.getText());
        if (varIndex == null){
        throw new UndeclaredVariableException(ctx.varName);
        }
        return varIndex;
        */
       jvmStack.push(DataType.INT);
         return  "iload " + requireVariableIndex(ctx.varName);    //take the value of the variable from the variable table and put it on top of the stack
    }
    
    private int requireVariableIndex(Token varNameToken){
        Integer varIndex = variables.get(varNameToken.getText());
        if (varIndex == null){
        throw new UndeclaredVariableException(varNameToken);
        }
        return varIndex;
    }

    @Override
    public String visitBranch(AdditionrecursionParser.BranchContext ctx) {
        String conditionInstructions = visit(ctx.condition);
        jvmStack.pop();
        String onTrueInstructions = visit(ctx.onTrue);
        String onFalseInstructions = visit(ctx.onFalse);
      //  int branchNum = branchCounter;
      //  ++ branchCounter;
       int branchNum = a.getNum();
       ++branchNum;
       c.incrementLoopCount();
       b.set_Count(c.getloopNum());
      //  System.out.println( "\n\tTTTTTT " + conditionInstructions);
      //  System.out.println("\n\tTTTTTT " + onTrueInstructions);
      //  System.out.println("\n\tTTTTTT " + onFalseInstructions);
        
       /* System.out.println( "****" +conditionInstructions + "\n" +
                "ifne ifTrue" + branchNum + "\n" +
                onFalseInstructions + "\n"+
                "goto endIf" + branchNum + "\n" +
                "ifTrue" + branchNum + ":\n" +
                onTrueInstructions + "\n" +
                "endIf" + branchNum +":\n"); */
       
        return conditionInstructions + "\n" +
                "ifne onTrue" + branchNum + "\n" + "onFalse" + branchNum +":" + "\n" +
                  onFalseInstructions + "\n"+
                "goto endIf" + branchNum + "\n" +
                "onTrue" + branchNum + ":\n" +
                onTrueInstructions + "\n" + 
                "endIf" + branchNum +":\n";
         
    }

    @Override
    public String visitRelational(AdditionrecursionParser.RelationalContext ctx) {
       // int compareNum = compareCount;
       // ++compareCount;
     // loopNum = loopCount;
       int number = b.getCount();
        String jumpInstructions;
        switch(ctx.operator.getText()){
            case "<":
                jumpInstructions = "if_icmplt";
                break;
            case "<=":
                jumpInstructions = "if_icmple";
                break;
            case ">":
                jumpInstructions = "if_icmpgt";
                break;
            case ">=":
                jumpInstructions= "if_icmpge";
                break;
            default: 
                throw new IllegalArgumentException("Unknown operator: " + ctx.operator.getText());
        
        }
        /*
        String instructions= visitChildren(ctx) + "\n" + 
                jumpInstructions + " onTrue" + compareNum + "\n" +
                "ldc 0\n" + "goto onFalse" + compareNum + "\n"+
                "onTrue" + compareNum + ":\n" + 
                "ldc 1\n" + 
                "onFalse" + compareNum + ":";
        */
        String instructions = visitChildren(ctx) + "\n" + jumpInstructions + " onTrue" + number
                + "\n" + "goto onFalse" + number;
        
        
        jvmStack.pop();
        jvmStack.pop();
        jvmStack.push(DataType.INT);
        return instructions;
    }

    @Override
    public String visitAnd(AdditionrecursionParser.AndContext ctx) {
        String left= visit(ctx.lft);
        String right = visit(ctx.rght);
        int andNum = andCounter; 
        ++andCounter;
        
        jvmStack.pop();
        jvmStack.pop();
        jvmStack.push(DataType.INT);
        
        return left + "\n" + 
                "ifeq onAndFalse" + andNum + "\n" +
                right + "\n" + 
                "ifeq onAndFalse" + andNum + "\n" + 
                "ldc 1 \n" + 
                "goto andEnd" + andNum + "\n"+
                "onAndFalse" + andNum + ":\n" + 
                "ldc 0\n" + 
                "andEnd" + andNum + ":";
    }

    @Override
    public String visitOr(AdditionrecursionParser.OrContext ctx) {
        String left = visit(ctx.lft);
        String right = visit(ctx.rght);
        int orNum = orCounter;
        ++orCounter;
        
        jvmStack.pop();
        jvmStack.pop();
        jvmStack.push(DataType.INT);
        
        return left + "\n" + 
                "ifne onOrTrue" + orNum + "\n"+
                right + "\n" + 
                "ifne onOrTrue" + orNum + "\n" + 
                "ldc 0\n" + 
                "goto orEnd" + orNum + "\n" + 
                "onOrTrue" + orNum + ":\n" + 
                "ldc 1\n" + 
                "orEnd" + orNum + ":";
    }


    @Override
    public String visitPrint(AdditionrecursionParser.PrintContext ctx) {
        String argumentInstructions = visit(ctx.argument); 
        DataType type = jvmStack.pop();
    
   // System.out.println(argumentInstructions);
      return "  getstatic java/lang/System/out Ljava/io/PrintStream;\n" + 
				argumentInstructions + "\n" + 
				"  invokevirtual java/io/PrintStream/print("+ type.getJvmType()+ ")V\n";
    }


    @Override
    public String visitString(AdditionrecursionParser.StringContext ctx) {
        jvmStack.push(DataType.STRING);
       return "ldc " + ctx.txt.getText();
    }

    @Override
    public String visitWhile_statement(AdditionrecursionParser.While_statementContext ctx) {
        int whileNum = c.getloopNum();
        if(whileNum != 0){
            c.incrementLoopCount();
            a.setValue(c.getloopNum());
            b.set_Count(c.getloopNum());
        }
       String loopInstructions = visit (ctx.condition);
       String onTrue = visit(ctx.loopCondition);
       
       String instructions = " while" + whileNum + ":" + "\n" + loopInstructions + "\n" + "onTrue"+ c.getloopNum()+ ":" + "\n"+ onTrue + "\n"+ "goto while" + whileNum +
               "\n" + "onFalse" + c.getloopNum() + ":" + "\n";
      if(whileNum == 0){
       c.incrementLoopCount();
       a.setValue(c.getloopNum());
       b.set_Count(c.getloopNum());
      }
       return instructions;
    }

    @Override
    public String visitDo_while(AdditionrecursionParser.Do_whileContext ctx) {
        int do_while = c.getloopNum();
        if (do_while != 0){
             c.incrementLoopCount();
             a.setValue(c.getloopNum());
             b.set_Count(c.getloopNum());
        }
        String onTrue = visit(ctx.loop);
        String conditions = visit(ctx.condition);
        
        String instructions= "onTrue" + c.getloopNum() + ":" + "\n" + onTrue + "\n" + conditions +"\n" + "onFalse" + c.getloopNum() + ":"; 
        if(do_while == 0){
            c.incrementLoopCount();
            a.setValue(c.getloopNum());
            b.set_Count(c.getloopNum());
        }
        return instructions;
    }

    @Override
    public String visitIncrementation(AdditionrecursionParser.IncrementationContext ctx) {
         return  "iload " + requireVariableIndex(ctx.varName) + "\n" + "ldc 1" + "\n" + "iadd" + 
                 "\n" +  "istore " + requireVariableIndex(ctx.varName) ; 
    }

    @Override
    public String visitDecrement(AdditionrecursionParser.DecrementContext ctx) {
        return  "iload " + requireVariableIndex(ctx.varName) + "\n" + "ldc 1" + "\n" + "isub" + 
                 "\n" +  "istore " + requireVariableIndex(ctx.varName) ;
    }

    @Override
    public String visitForLoop(AdditionrecursionParser.ForLoopContext ctx) {
        int for_Num = c.getloopNum();
        if(for_Num != 0){
            c.incrementLoopCount();
            a.setValue(c.getloopNum());
            b.set_Count(c.getloopNum());
        }
        String initialization = visit(ctx.initialization);
        String condition = visit(ctx.condition);
        String change = visit(ctx.change);
        String conditionStillValid = visit(ctx.loopCondition);
        
        String for_loop = initialization + "\n" + "for" + for_Num + ":" +"\n"  + condition + "\n" + "onTrue" + c.getloopNum() + ":" + "\n" +conditionStillValid + "\n" +
                change +"\n" + "goto for" + for_Num + "\n" + "onFalse" + c.getloopNum() + ":" + "\n"; 
        if(for_Num == 0){
            c.incrementLoopCount();
            a.setValue(c.getloopNum());
            b.set_Count(c.getloopNum());
        }
        return for_loop;
    }

    @Override
    public String visitAssignVariable_For(AdditionrecursionParser.AssignVariable_ForContext ctx) {
        String instructions = visit(ctx.assignment());
        return instructions;
    }

    @Override
    public String visitDeclareVariable_For(AdditionrecursionParser.DeclareVariable_ForContext ctx) {
        String instructions = visit(ctx.varDeclaration());
        return instructions;
    }

    @Override
    public String visitSimpleBooleanVariableDeclaration(AdditionrecursionParser.SimpleBooleanVariableDeclarationContext ctx) {
        variables.put(ctx.variableName.getText(), variables.size());                  
         return ""; 
    }

    @Override
    public String visitBooleanVariableDeclarationAndAssignment(AdditionrecursionParser.BooleanVariableDeclarationAndAssignmentContext ctx) {
         variables.put(ctx.variableName.getText(), variables.size());
        String instructions = visit(ctx.value) + "\n" + 
              "istore " + requireVariableIndex(ctx.variableName);
      return instructions;
    }

    @Override
    public String visitTrue(AdditionrecursionParser.TrueContext ctx) {
        return "ldc 1";
    }

    @Override
    public String visitFalse(AdditionrecursionParser.FalseContext ctx) {
        return "ldc 0";
    }

    @Override
    public String visitBooleanVarAssignment(AdditionrecursionParser.BooleanVarAssignmentContext ctx) {
            String instructions = visit(ctx.value) + "\n" + 
              "istore " + requireVariableIndex(ctx.variableName);
      return instructions;
    
    }

    @Override
    public String visitInverseBoolean(AdditionrecursionParser.InverseBooleanContext ctx) {
       // int inverseNum = whileCount -1;
      int inverseNum=  c.getloopNum();
     //   ++whileCount;
       return  "iload " + requireVariableIndex(ctx.variableName) + "\n" + "ifeq " + "onTrue" +inverseNum +
               "\n" + "ldc 0" + "\n" + "goto onFalse" + inverseNum+ "\n";
       
    }

    @Override
    public String visitInverseBool(AdditionrecursionParser.InverseBoolContext ctx) {
        String instructions = visit(ctx.inverseBoolean());
       return instructions;
    }

    
}
