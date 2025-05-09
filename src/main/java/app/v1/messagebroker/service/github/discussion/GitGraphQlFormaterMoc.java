package app.v1.messagebroker.service.github.discussion;

import graphql.language.*;
import graphql.parser.Parser;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitGraphQlFormaterMoc {

    public GitGraphQlFormaterMoc() {}

    public static String formatCreateDiscussionMutation(String repositoryId, String categoryId, String title, String body) {
        System.out.println(" -- GitHub discussions: preparing GraphQL AST query -- ");

        // Build the mutation programmatically
        MutationDefinition mutation = MutationDefinition.newMutationDefinition()
                .name("CreateDiscussion")
                .selectionSet(SelectionSet.newSelectionSet()
                        .selection(
                                Field.newField("createDiscussion")
                                        .arguments(List.of(
                                                Argument.newArgument("input", ObjectValue.newObjectValue()
                                                                .objectField("repositoryId", StringValue.newStringValue(repositoryId).build())
                                                                .objectField("categoryId", StringValue.newStringValue(categoryId).build())
                                                                .objectField("title", StringValue.newStringValue(title).build())
                                                                .objectField("body", StringValue.newStringValue(body).build())
                                                                .build())
                                                        .build()))
                                        .selectionSet(SelectionSet.newSelectionSet()
                                                .selection(Field.newField("discussion")
                                                        .selectionSet(SelectionSet.newSelectionSet()
                                                                .selection(Field.newField("id").build())
                                                                .selection(Field.newField("title").build())
                                                                .selection(Field.newField("url").build())
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                        .build())
                .build();

        Document document = Document.newDocument()
                .definition(mutation)
                .build();

        // Convert AST back to string
        String printedQuery = AstPrinter.printAst(document);

        // Wrap into JSON
        return "{ \"query\": \"" + escapeForJson(printedQuery) + "\" }";
    }

    private static String escapeForJson(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
