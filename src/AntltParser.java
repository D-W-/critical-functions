import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

//import org.antlr.v4.runtime.Token;

/**
 * Created by Han Wang at 6/27/18.
 */


class MyListener extends CBaseListener {

  public void enterPrimaryExpression(CParser.PrimaryExpressionContext ctx) {
    if (ctx != null && ctx.children.size() == 1) {
      String func = ctx.children.get(0).toString();
      if (AntltParser.store1.containsKey(func)) {
        AntltParser.calledFuncs.add(func);
      }
    }
  }
}

class CppListener extends CPP14BaseListener {
  public void enterUnqualifiedid(CPP14Parser.UnqualifiedidContext ctx) {
    if (ctx != null && ctx.children.size() == 1) {
      String func = ctx.children.get(0).toString();
      if (AntltParser.store1.containsKey(func)) {
        AntltParser.calledFuncs.add(func);
      }
    }
  }

  public void visitErrorNode(ErrorNode node) {
    if (node != null && AntltParser.store1.containsKey(node.toString())) {
      AntltParser.calledFuncs.add(node.toString());
    }
  }

  public void enterClassname(CPP14Parser.ClassnameContext ctx) {
    if (ctx != null && ctx.children.size() == 1) {
      String func = ctx.children.get(0).toString();
      if (AntltParser.store1.containsKey(func)) {
        AntltParser.calledFuncs.add(func);
      }
    }
  }
}


public class AntltParser {
  public static Set<String> calledFuncs = new HashSet<>();
  public static Map<String, Integer> store1 = new HashMap();
  public static Map<String, Integer> store0 = new HashMap();
  public static Integer errorCount = 0;
  public static Integer trueCount = 0;
  public static Integer falseCount = 0;
  public static boolean DEBUG = false;

  public static void initStoreFromFile(String filename) {
    BufferedReader br = IO.getReader(filename);
    try {
      String temp;
      while ((temp = br.readLine()) != null) {
        store1.put(temp, 0);
        store0.put(temp, 0);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void parsecpp(String line) throws IOException {
    InputStream inputStream = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
    CharStream charStream = CharStreams.fromStream(inputStream);
    CPP14Lexer cpp14Lexer = new CPP14Lexer(charStream);
    cpp14Lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
    CommonTokenStream commonTokenStream = new CommonTokenStream(cpp14Lexer);
    CPP14Parser cpp14Parser = new CPP14Parser(commonTokenStream);
    cpp14Parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    ParseTree parseTree = cpp14Parser.translationunit();
    CppListener listener = new CppListener();
    ParseTreeWalker.DEFAULT.walk(listener, parseTree);
  }

  public static void parse(String line) throws IOException{
    InputStream inputStream = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
    CharStream charStream = CharStreams.fromStream(inputStream);
    CLexer cLexer = new CLexer(charStream);
    CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);
    CParser cParser = new CParser(commonTokenStream);

//    ParseTree parseTree = cParser.translationUnit();
//    deal each line with primary expression to bypass if statement without brackets
    ParseTree parseTree = cParser.primaryExpression();
    MyListener listener = new MyListener();
    ParseTreeWalker.DEFAULT.walk(listener, parseTree);
  }

  public static void collect(Map<String, Integer> store, String buffer,
      String line)  throws RuntimeException{
    if (calledFuncs.isEmpty()) {
      if (!DEBUG) {
        errorCount++;
      }
      else {
        System.out.println(buffer);
        System.out.println(line);
        throw new RuntimeException();
      }
    }
    for (String fun : calledFuncs) {
      store.put(fun, store.get(fun) + 1);
    }
  }

  public static void run() throws IOException {
    BufferedReader bufferedReader = IO.getReader("res/in119");
    String buffer = "";
    String line = null, prev = null;
//    ignore 1st line
    bufferedReader.readLine();
    while ((line = bufferedReader.readLine()) != null) {
      if (line.equals("---------------------------------")) {
        line =  bufferedReader.readLine();
        if (prev.equals("0")) {
          falseCount++;
          collect(store0, buffer, line);
        }
        else if (prev.equals("1")){
          trueCount++;
          collect(store1, buffer, line);
        }
        //        clear set
        calledFuncs.clear();
        buffer = "";
      }
      else {
        prev = line;
        buffer = buffer + line + "\n";
        //        parse(line);
        parsecpp(line);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    initStoreFromFile("res/f119");
    run();

    Gson gson = new Gson();

    System.out.println("Unhandled cases:" + errorCount.toString());
    System.out.println("True cases:" + trueCount.toString());
    System.out.println(store1);
    System.out.println(gson.toJson(store1));

    System.out.println("--------------------");

    System.out.println("False cases:" + falseCount.toString());
    System.out.println(store0);
    System.out.println(gson.toJson(store0));
  }

}
