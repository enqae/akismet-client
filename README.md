
# Akismet-Client

## Is related AkismetDrops to Akismet?
There is NO relation between Akismet and AkismetDrops.
AkismetDrops just uses the Akismet REST API.


## What do I need to start using AkismetDrops?
First thing you need to start using AkismetDrops is an api-key from Akismet.

You can get a free one at http://akismet.com/personal/


## What can I do with AkismetDrops?
AkismetDrops offers four methods to interact with Akismet.

There is one method for each api call in the Akismet REST API, they are the following:

  * isApiKeyValid (verify-key)

    Verifies if the key is valid

  * isThisCommentSpam (comment-check)

    Checks if the comment is spam or not

  * thisCommentShouldBeSpam (submit-spam)

    Notifies to Akismet a comment should be spam even if Akismet did not identified it as spam

  * thisCommentIsFalsePositive (submit-ham)

    Notifies to Akismet a false positive


## How can I use AkismetDrops

### Take the sources

You can take them from the Source Section.

### Make the apropriate calls in your program

#### (1) Create the AkismetDrops object

  ```
  Akismet akismet = new Akismet(apiKey: API_KEY)
  ```

There are up to four parameters, but only the apiKey is required.

  ```
  Akismet akismet = new Akismet(apiKey: API_KEY, blog: BLOG, appName: APP_NAME, appVersion: APP_VERSION)
  ```

This four parameters are:

  * apiKey: the api key provided by Akismet
  * blog: the url of your blog or the application using Akismet
  * appName: the name of your application
  * appVersion: the version of your application


#### (2) The AkismetComment object

This object keeps the information needed to send to Akismet so it can decide if the comment is span or not, only 4 parameters are required, but the result will be more precise if more info is provided. The parameters are the following:

  * blog(required): the url of the service using Akismet (not needed if blog is provided in step 1)

  * user_ip (required): the ip of the user who made the comment

  * user_agent (required): the user-agent of the program the user used to make the comment

  * comment_content (required): the comment itself

  * referrer: the referer header of the user request

  * comment_type: the type of comment ('comment','trackback', 'pingback' or other custom value)

  * comment_author: the name of the user who made the comment

  * comment_author_email: the email of the user who made the comment

  * comment_author_url: the url of the user who made the comment


#### (3) Verify a comment

Build an AkismetComment object and call _isThisCommentSpam_

```
    def commentInfo = [:]
    commentInfo.put(AkismetComment.USER_IP, '195.178.99.xx')
    commentInfo.put(AkismetComment.USER_AGENT, 'Mozilla 4.0')
    commentInfo.put(AkismetComment.COMMENT_CONTENT, 'IS THIS COMMENT SPAM OR NOT???')

    AkismetComment akismetComment = new AkismetComment(commentInfo);

    def result = akismet.isThisCommentSpam(akismetComment)
    if(result){
      println "The comment is SPAM"
    } else {
      println "The comment is NOT SPAM"
    }
```

#### (4) Notify Akismet a comment is spam

Build an AkismetComment and call _thisCommentShouldBeSpam_

```
    akismetComment."${AkismetComment.COMMENT_CONTENT}" = "Ohhh yesssss this is spam"
    akismet.thisCommentShouldBeSpam(akismetComment)
```

#### (5) Notify Akismet a false positive

Build an AkismetComment and call _thisCommentIsFalsePositive_

```
    akismetComment."${AkismetComment.COMMENT_CONTENT}" = "this is not spam"
    akismet.thisCommentIsFalsePositive(akismetComment)
```
