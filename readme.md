## TEAM PARTY PROJECT ##

# Business requirements:
# Problem definition

You have just joined a team that likes coding, joking and Dropwizard framework! Welcome!
:)
The team is having a party this weekend and asked you to create a backend API for getting jokes. One more thing: the
team doesn’t like telling similar jokes for a long time, so please make sure that the joke with the same keyword gets
rate limited.

# Tech spec

- Implement a single REST api endpoint for getting jokes for a given keyword. Keyword is sent by an API user;
- Returned jokes should contain only the full match of the keyword: not partial or a substring;
- If that key is being requested more than 5 times per minute, that request should be rate limited;
- As a web framework we suggest using Dropwizard: https://www.dropwizard.io/;
- For getting jokes we suggest using existing online APIs like
  https://api.chucknorris.io/. If you have other online sources, you are free to use it as well.

Please upload your code to any online repository of your choice: Github, Gitlab or Bitbucket.

## Project Overview ##

- Dropwizard framework is applied to run the project.
- The api limit rate is configured in `config.yml` as below:
  ```
  apiLimit:
     capacity: 5
     timeInSeconds: 60
  ```
  
## Prerequisite ###

- Jdk 11 installed.
- Maven installed.
- Make sure you have curl.

## Running application

- Build project:
  - `./build.sh`
- Run application:
  - `./run.sh`
- Check server status:
  - `curl localhost:8080/teamparty/healthcheck`
- Call endpoint to get jokes from Chuck Norris by replacing query param:
  - `curl localhost:8080/teamparty/getjokes?query={query}`

