import com.github.javaparser.JavaParser;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
  public static void main(String[] args) throws IOException {
    var directory = args[0];
    var file = args[1];
    Instrumentor inst = new Instrumentor(directory, file);
    inst.instrument();
    inst.save();
  }
}
