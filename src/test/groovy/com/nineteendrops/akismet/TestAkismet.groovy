package com.nineteendrops.akismet

/**
 * Created www.19drops.com
 * User: 19drops
 * Date: jan-2009
  * <p/>
 * This material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, you can find it at: http://www.gnu.org/licenses/lgpl-3.0.txt
 */

class TestAkismet {


  final static String API_KEY = "YOUR API KEY"  // "your API-KEY"
  final static String APP_NAME = "TestAkismet"
  final static String APP_VERSION = "0.01"

  final static String BLOG = "http://YOUR BLOG" // "your BLOG"


  static void main(args){


    // First thing always is verify the Akismet apiKey
    // The only mandatory parameter is the API_KEY
    Akismet akismet = new Akismet(apiKey: API_KEY, appName: APP_NAME, appVersion: APP_VERSION, blog: BLOG)

    // Check if the apiKey is Valid
    if(akismet.isApiKeyValid()){
      println "The apiKey is valid"
    } else {
      println "The apiKey is not valid"
    }


    // Check if a comment is spam or not
    def commentInfo = [:]
    commentInfo.put(AkismetComment.USER_IP, '127.0.0.1')
    commentInfo.put(AkismetComment.USER_AGENT, 'Mozilla 4.0')
    commentInfo.put(AkismetComment.COMMENT_CONTENT, 'IS THIS COMMENT SPAM OR NOT???')

    AkismetComment akismetComment = new AkismetComment(commentInfo);

    def result = akismet.isThisCommentSpam(akismetComment)
    if(result){
      println "The comment is SPAM"
    } else {
      println "The comment is NOT SPAM"
    }


    // Tell Akismet a comment not marked as spam should be spam
    akismetComment."${AkismetComment.COMMENT_CONTENT}" = "Ohhh yesssss this is spam"
    akismet.thisCommentShouldBeSpam(akismetComment)
    
    // Tell Akismet a comment should be marked as spam
    akismetComment."${AkismetComment.COMMENT_CONTENT}" = "this is not spam"
    akismet.thisCommentIsFalsePositive(akismetComment)

  }

}
