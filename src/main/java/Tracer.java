import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

public class Tracer implements Instrumentor {

  private final CompilationUnit cu;
  private final String srcDir;
  private final String file;

  public Tracer(String srcDir, String file) throws IOException {
    this.srcDir = srcDir;
    this.file = file;
    var parser = new JavaParser().parse(Paths.get(srcDir, file));
    if (parser.isSuccessful() && parser.getResult().isPresent()) {
      this.cu = parser.getResult().get();
    } else {
      Logger.getAnonymousLogger().info("Parsing failed");
      throw new RuntimeException();
    }
  }

  private String generateMethodId(boolean isEntry, String className, String methodName) {
    String entryId = isEntry ? "ENTRY" : "EXIT";
    return "System.out.println(\"___%s___%s:%s___\");".formatted(entryId, className, methodName);
  }

  private Optional<Statement> entryStatement(String className, String methodName) {
    return new JavaParser().parseStatement(generateMethodId(true,className, methodName)).getResult();
  }

  private Optional<Statement> exitStatement(String className, String methodName) {
    return new JavaParser().parseStatement(generateMethodId(false, className, methodName)).getResult();
  }

  private BlockStmt instrumentReturnStmt(ReturnStmt retStmt, Statement instrumentStmt) {
    BlockStmt block = new BlockStmt();
    // move the return statement to a new block and add the statement
    block.addStatement(instrumentStmt);
    block.addStatement(retStmt.clone());
    return block;
  }


  public void instrumentMethod(MethodDeclaration method) {
    var parent = method.getParentNode();
    String className = "";
    String methodName = method.getNameAsString();
    if (parent.isPresent()) {
      var clazz = (ClassOrInterfaceDeclaration) parent.get();
      className = clazz.getNameAsString();
    }
    Statement entryStmt = entryStatement(className, methodName).orElse(null);
    Statement exitStmt = exitStatement(className, methodName).orElse(null);



    // Instrument function entry point

    method.getBody().ifPresent(body -> body.addStatement(0, entryStmt));

    // Instrument Return points
    var returnStmts = method.findAll(ReturnStmt.class).stream();
    returnStmts.forEach(
        returnStmt -> {
          System.out.println(returnStmt);
          var newBlkStmt = instrumentReturnStmt(returnStmt, exitStmt);
          returnStmt.getParentNode().ifPresent(node -> node.replace(returnStmt, newBlkStmt));
        });

    // Instrument function exit point
    method.getBody().ifPresent(body -> body.addStatement(exitStmt));
    //    System.out.println(this.cu.toString());
  }

  @Override
  public void instrument() {
    var methods = cu.findAll(MethodDeclaration.class).stream().filter(m -> !m.isAbstract());
    methods.forEach(this::instrumentMethod);
  }

  @Override
  public void save() {
    cu.setStorage(Paths.get(this.srcDir, "output", file));
    cu.getStorage().ifPresent(CompilationUnit.Storage::save);
  }
}
