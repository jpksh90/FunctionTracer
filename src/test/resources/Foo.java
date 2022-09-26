import java.io.BufferedReader;
import java.io.InputStreamReader;

class Foo {
  public boolean foo() {
    int x = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)));
    if (x % 2 == 0) { return true;}
    else { return false; }
  }
}
