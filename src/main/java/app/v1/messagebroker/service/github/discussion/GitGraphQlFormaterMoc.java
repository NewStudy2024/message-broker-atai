package app.v1.messagebroker.service.github.discussion;

import graphql.language.*;
import graphql.parser.Parser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitGraphQlFormaterMoc {

    public GitGraphQlFormaterMoc() {}

    public static String formatCreateDiscussionMutation(String repositoryId, String categoryId, String title, String body) {
        System.out.println(" -- GitHub discussions: preparing GraphQL AST query -- ");

        if (repositoryId == null || categoryId == null || title == null || body == null) {
            throw new IllegalArgumentException("All input values must be non-null");
        }

        // AST: mutation CreateDiscussion { createDiscussion(input: { ... }) { discussion { id title url } } }
        Field discussionField = Field.newField("discussion")
                .selectionSet(SelectionSet.newSelectionSet()
                        .selection(Field.newField("id").build())
                        .selection(Field.newField("title").build())
                        .selection(Field.newField("url").build())
                        .build())
                .build();

        Field createDiscussionField = Field.newField("createDiscussion")
                .arguments(List.of(
                        Argument.newArgument("input",
                                        ObjectValue.newObjectValue()
                                                .objectField("repositoryId", StringValue.newStringValue(repositoryId).build())
                                                .objectField("categoryId", StringValue.newStringValue(categoryId).build())
                                                .objectField("title", StringValue.newStringValue(title).build())
                                                .objectField("body", StringValue.newStringValue(body).build())
                                                .build())
                                .build()))
                .selectionSet(SelectionSet.newSelectionSet()
                        .selection(discussionField)
                        .build())
                .build();

        MutationDefinition mutation = MutationDefinition.newMutationDefinition()
                .name("CreateDiscussion")
                .selectionSet(SelectionSet.newSelectionSet()
                        .selection(createDiscussionField)
                        .build())
                .build();

        Document document = Document.newDocument()
                .definition(mutation)
                .build();

        String printedQuery = AstPrinter.printAst(document);
        return "{ \"query\": \"" + escapeForJson(printedQuery) + "\" }";
    }

    // Escapes a GraphQL query string for safe embedding into JSON
    private static String escapeForJson(String input) {
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> {} // skip
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
