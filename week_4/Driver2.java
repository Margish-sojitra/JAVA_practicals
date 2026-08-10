import java.util.Scanner;

public class Driver2 {
public static void main(String[] args) {
String[] logLines = {
"10:05 alice Hello there",
"10:06 bob How's it going?",
"10:07 malformed_line_without_enough_parts",
"10:08 charlie Hello, anyone here?"
};

Scanner scanner = new Scanner(System.in);
System.out.print("Enter keyword: ");
String keyword = scanner.nextLine();

String result = ChatFilter.filterLogs(logLines, keyword);
System.out.println("\n" + result);

scanner.close();
}
}

