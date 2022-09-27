import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    var directory = args[0];
    var file = args[1];
    Instrumentor tracer = new Tracer(directory, file);
    tracer.instrument();
    tracer.save();
  }
}
