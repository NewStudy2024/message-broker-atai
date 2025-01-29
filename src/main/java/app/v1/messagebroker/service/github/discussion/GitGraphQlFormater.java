package app.v1.messagebroker.service.github.discussion;


import org.springframework.stereotype.Service;

@Service
public class GitGraphQlFormater {

    public GitGraphQlFormater() {}

    public static String escapeString(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("\\", "\\\\")  // Escape backslashes
                .replace("\"", "\\\"")  // Escape double quotes
                .replace("\r", "\\r")   // Escape carriage returns
                .replace("\n", "\\n")   // Escape newlines
                .replace("\t", "\\t");  // Escape tabs
    }

    public static String formatCreateDiscussionMutation(String repositoryId, String categoryId, String title, String body) {
        System.out.println(" -- Git discussions preparing query -- ");
        String escapedBody = escapeString(body);
        String escapedTitle = escapeString(title);

        String query =  String.format(
                "mutation { createDiscussion(input: { repositoryId: \\\"%s\\\", categoryId: \\\"%s\\\", title: \\\"%s\\\", body: \\\"%s\\\" }) { discussion { id title url } } }\"",
                repositoryId, categoryId, escapedTitle, escapedBody
        );
        return "{ \"query\": \"" + query + " }";
    }
}
