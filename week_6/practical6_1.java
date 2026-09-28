import java.util.*;

interface Switchable {

    void on();

    void off();

    default void toggle() {
        System.out.println("Toggling device");
    }
}

class Fan implements Switchable {

    public void on() {
        System.out.println("Fan is ON");
    }

    public void off() {
        System.out.println("Fan is OFF");
    }
}

class Light implements Switchable {

    public void on() {
        System.out.println("Light is ON");
    }

    public void off() {
        System.out.println("Light is OFF");
    }
}


interface SwitchPermission {

    boolean maySwitchOn(Switchable device, int hour);
}

public class practical6_1 {

    public static void main(String[] args) {

        Switchable[] devices = {
            new Fan(),
            new Light()
        };

      
        for (Switchable device : devices) {
            device.on();
            device.off();
            device.toggle();
        }

        SwitchPermission permission1 = new SwitchPermission() {

            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };

        System.out.println(
            "Anonymous class: " +
            permission1.maySwitchOn(new Fan(), 10)
        );

        
        SwitchPermission permission2 = (device, hour) -> hour >= 6 && hour <= 22;

        System.out.println(
            "Lambda: " +
            permission2.maySwitchOn(new Light(), 23)
        );
    }
}
