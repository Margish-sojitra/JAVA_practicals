interface Notifier {

    void send(String message);

}


interface Urgent {

}

class EmailSender implements Notifier, Urgent {

    public void send(String message) {
        System.out.println("Email: " + message);
    }

}

class SMSSender implements Notifier {

    public void send(String message) {
        System.out.println("SMS: " + message);
    }

}

public class practical6_2 {

    public static void main(String[] args) {

        Notifier email = new EmailSender();
        Notifier sms = new SMSSender();

        Notifier[] senders = {email, sms};

        String message = "Your order has been shipped.";

        for (Notifier sender : senders) {

            sender.send(message);

           
            if (sender instanceof Urgent) {
                sender.send(message);
            }
        }
    }
}

