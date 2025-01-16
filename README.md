# GitHub Comment Bot

## Overview
The GitHub Comment Bot is an automated service that generates eloquent and insightful comments based on your GitHub commits and pushes. Using the GitHub API to fetch repository data and CloudFlare Ai to create meaningful content, this bot can save time and enhance your development workflow.

### Features
- Fetches commit and push data from your GitHub repositories.
- Processes commit messages and extracts relevant information.
- Uses CloudFlare Ai to generate context-aware comments.
- Integrates seamlessly into discussions or other GitHub activity.
- Written in Java using the Spring framework.

## How It Works
1. **Fetch Commit Data**: The bot retrieves recent commits and pushes using the GitHub API.
2. **Process Data**: Filters and organizes commit data into structured DTOs.
3. **Send to GPT**: Passes the processed data to the GPT API via the Cloudflare middleware.
4. **Generate Comments**: GPT generates comments based on the provided data.
5. **Post Comments**: The bot prepares comments for publishing on GitHub Discussions or other areas.

## Future Enhancements
- Add scheduling for automated triggers.
- Improve error handling and stability.
- Support additional platforms like Bitbucket or GitLab.
- Include customizable comment templates.