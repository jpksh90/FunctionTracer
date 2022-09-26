import java.io.BufferedReader;
import java.io.InputStreamReader;

class Foo {

    public boolean foo() {
        System.out.println("___ENTRY___Foo:foo___");
        int x = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)));
        if (x % 2 == 0) {
            {
                System.out.println("___EXIT___Foo:foo___");
                return true;
            }
        } else {
            {
                System.out.println("___EXIT___Foo:foo___");
                return false;
            }
        }
        System.out.println("___EXIT___Foo:foo___");
    }
}
