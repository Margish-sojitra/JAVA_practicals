abstract class Media {

    protected String title;
    protected int lateDays;

    Media(String title, int lateDays) {
        this.title = title;
        this.lateDays = lateDays;
    }

    abstract double lateFee();
}

class Book extends Media {

    Book(String title, int lateDays) {
        super(title, lateDays);
    }

    @Override
    double lateFee() {
        return lateDays * 2;
    }
}

class DVD extends Media {

    DVD(String title, int lateDays) {
        super(title, lateDays);
    }

    @Override
    double lateFee() {
        return lateDays * 5;
    }
}

class Magazine extends Media {

    Magazine(String title, int lateDays) {
        super(title, lateDays);
    }

    @Override
    double lateFee() {
        return lateDays * 1;
    }
}

public class MediaDemo {

    public static void main(String[] args) {

        Media[] returnedMedia = {
            new Book("Java Programming", 4),
            new DVD("Inception", 3),
            new Magazine("Technology Today", 5),
            new Book("Data Structures", 2)
        };

        double totalFee = 0;

        for (Media media : returnedMedia) {

            double fee = media.lateFee();

            System.out.printf(
                "Title: %s | Late Days: %d | Late Fee: %.2f%n",
                media.title,
                media.lateDays,
                fee
            );

            totalFee += fee;
        }

        System.out.printf("%nTotal Late Fee = %.2f%n", totalFee);
    }
}