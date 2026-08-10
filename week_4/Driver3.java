
public class Driver3 {
    public static void main(String[] args) {
        String template = "Dear {name}, order {id} ships {date}.";
        String[] keys = { "name", "id" };
        String[] values = { "Riya", "A07" };

        String output = TemplateFiller.fillTemplate(template, keys, values);

        System.out.println("Original: " + template);
        System.out.println("Filled: " + output);
    }
}
