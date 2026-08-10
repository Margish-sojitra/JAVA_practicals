public class ChatFilter {

    public static String filterLogs(String[] logs, String keyword) {
        int matches = 0;
        StringBuilder report = new StringBuilder();
        String lowerKeyword = keyword.toLowerCase();

        for (String line : logs) {
            // Split into at most 3 parts: time, user, message
            String[] parts = line.split(" ", 3);
            if (parts.length < 3) {
                continue; // Skip malformed lines
            }

            String time = parts[0];
            String user = parts[1];
            String message = parts[2];

            if (message.toLowerCase().contains(lowerKeyword)) {
                matches++;
                report.append(time).append(" ").append(user).append(": ").append(message).append("\n");
            }
        }

        return "Matches: " + matches + "\n" + report.toString();
    }
}
