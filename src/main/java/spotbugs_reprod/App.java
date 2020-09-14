package spotbugs_reprod;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class App {
    private final Set<Integer> insertedValues = new HashSet<>();
    private final Map<Integer, String> someMap = Map.of(1, "1", 2, "2", 3, "3", 8, "1337");

    public void handleMessage(Message msg) {
        msg.match(new Message.Matcher<Void>() {
            @Override
            public Void is(Add a) {
                App.this.insertedValues.add(a.val);
                return null;
            }

            @Override
            public Void is(Calc c) {
                var sum = App.this.insertedValues.stream().reduce(0, (a, b) -> a + b);
                System.out.println(sum);
                System.out.println(App.this.someMap.get(sum));
                App.this.insertedValues.clear();
                return null;
            }
        });
    }

    public static void main(String[] args) {
        var app = new App();

        app.handleMessage(new Add(3));
        app.handleMessage(new Add(5));
        app.handleMessage(new Calc());

    }

    // public void doStuff() {
    //     this.insertedValues.add(1337);

    //     var sum = this.insertedValues.stream().reduce(0, (a, b) -> a + b);
    //     System.out.println(sum);
    //     System.out.println(this.someMap.get(sum));
    //     this.insertedValues.clear();
    // }

    interface Message {
        <T> T match(Matcher<T> matcher);

        interface Matcher<T> {
            T is(Add a);

            T is(Calc c);
        }
    }

    static final class Add implements Message {
        final int val;
        Add(int val) {
            this.val = val;
        }

        @Override
        public <T> T match(Matcher<T> matcher) {
            return matcher.is(this);
        }
    }
    static final class Calc implements Message {
        @Override
        public <T> T match(Matcher<T> matcher) {
            return matcher.is(this);
        }
    }
}
