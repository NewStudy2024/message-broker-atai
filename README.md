# GitHub Comment Bot

## Overview
The GitHub Discussion Bot is an automated service that generates eloquent, context-aware discussions based on your GitHub commits and push events. By leveraging the GitHub API to gather repository data and Gemini AI to create insightful comments, this bot can save you time and enhance your development workflow.

### Features
- Fetches commit data from your GitHub repositories.
- Processes commits messages and extracts relevant information.
- Uses Gemini Ai to generate context-aware comments.
- Integrates seamlessly into GitHub discussions.
- Written in Java using the Spring framework.

## How It Works
1.  **Trigger Execution:** The bot is triggered by GitHub Actions, gathering data about the latest and previous commits, as well as user information.
2.  **API Requests:** The bot uses the GitHub API to determine which changes have been made in the latest commit.
3.  **Data Filtering:** Relevant commit details are extracted and refined, ensuring only meaningful information is sent to the AI.
4.  **AI Processing:** Gemini AI processes the filtered data and returns insightful comments or discussion points.
5.  **Discussion Creation:** The bot uses GitHub GraphQL to create new discussions with the AI-generated content.

## Future Enhancements
- Scheduled Triggers: Automate repetitive tasks with customizable schedules.
- Robust Error Handling: Improve stability and error reporting to manage edge cases more effectively.
- Configuration UI: Introduce a web-based interface for easier bot configuration.
- User Dashboard: Provide a dashboard for tracking and managing AI-generated discussions.
- Customizable Templates: Allow users to define their own comment styles and templates to suit various project needs.

How to execute bot???

Well, you need next properties:

    - API_TOKEN "Token form GitHub with permissions"
    - API_URL "https://api.github.com/repos/"
    - GIT_GRAPHQL "https://api.github.com/graphql"
    - API_GEMINI "End point of your Ai service"
    - SERVER_PORT "The Port of your service"
    - SERVER_ADDRESS "The Address of your service"
    - SERVER_CONTEXT_PATH "The path for your end ponts, you can use main path " 

The environment variables should be provided via a .env file. You can use Docker or just create a .env file manually.

You definitely need Docker on your machine. Using docker-compose, you can start the service with the following command:
    
    docker compose up 

After running it, you need to start your AI service (check this repository for details: https://github.com/NewStudy2024/gem-ai-client-atai).

Once your container is successfully up and running, you need to find out your IP address:

    - If you're using a VPS, you can check it in your hosting provider's dashboard.
    - If it's a local machine, you'll need to open ports between your router and your machine (this is a separate topic—you can Google it or ask me for help).
    - You can check your IP address here: https://whatismyipaddress.com/.

If you've made it this far—congratulations, you're halfway there! (Hahahaha, just kidding 😆).

Now, you need to set up a GitHub repository and add a workflow using GitHub Actions.
For the workflow file, simply create a .yml file that will be executed whenever you push to the main branch.

The .yml file: 

````
name: Notify Server

on:
push:
branches:
- main

jobs:
notify:
runs-on: ubuntu-latest
steps:
- name: Checkout code
uses: actions/checkout@v3

      - name: Notify Server with Retries
        env:
          SERVER_URL: ${{ secrets.SERVER_URL }}
        run: |
          MAX_RETRIES=5
          RETRY_DELAY=5
          COUNTER=0

          while [ $COUNTER -lt $MAX_RETRIES ]; do
            RESPONSE=$(curl -s -o response.txt -w "%{http_code}" -X POST "$SERVER_URL" \
                -H "Content-Type: application/json" \
                --data-raw '{
                  "repository": "'${{ github.repository }}'",
                  "ref": "'${{ github.ref }}'",
                  "commit": "'${{ github.sha }}'",
                  "previous_commit": "'${{ github.event.before }}'",
                  "pusher": "'${{ github.actor }}'"
                }')

            if [ "$RESPONSE" -eq 200 ]; then
              echo "Notification sent successfully!"
              exit 0
            else
              echo "Failed to notify server (HTTP $RESPONSE), retrying in $RETRY_DELAY seconds..."
              sleep $RETRY_DELAY
              COUNTER=$((COUNTER + 1))
            fi
          done

          echo "Failed to notify server after $MAX_RETRIES attempts."
          exit 1
````

And as you can see we need also set up secrets, please add for your repo SERVER_URL, which is address of your machine!

So, have fun with this bot, Yes I know there are a lot of hardcore shity-stuff but for 1st semester project it is quite enough 

Thanks for reading till end 😆