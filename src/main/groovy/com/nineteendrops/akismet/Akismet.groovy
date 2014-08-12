package com.nineteendrops.akismet

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

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


class Akismet implements GroovyInterceptable {

  final static String AKISMET_DROP_NAME = "AkismetDrop"
  final static String AKISMET_DROP_VERSION = "0.01"

  final static String AKISMET_BASE = "rest.akismet.com"
  final static String AKISMET_SERVICE_VERSION = "1.1"

  String apiKey = ""
  String appName = ""
  String appVersion = ""
  String blog = ""

  Log logger = LogFactory.getLog(this.class.name)

  // Api Methods

  Boolean isApiKeyValid() {}

  Boolean isThisCommentSpam(AkismetComment comment) {}

  Boolean thisCommentShouldBeSpam(AkismetComment comment) {}

  def thisCommentIsFalsePositive(AkismetComment comment) {}

  // Utility methods
  def requiredParameters = [AkismetComment.BLOG,
                            AkismetComment.USER_IP, AkismetComment.USER_AGENT,
                            AkismetComment.COMMENT_CONTENT]
  def nonRequiredParameters = [AkismetComment.REFERRER, AkismetComment.COMMENT_TYPE,
                               AkismetComment.COMMENT_AUTHOR, AkismetComment.COMMENT_AUTHOR_EMAIL, AkismetComment.COMMENT_AUTHOR_URL ]

  def validMethods = ['isThisCommentSpam': "comment-check",
                      'thisCommentShouldBeSpam': "submit-spam",
                      'thisCommentIsFalsePositive': "submit-ham"]

  Closure response200 = {text ->
                            Boolean result = (text == 'true') ;
                            if (logger.isDebugEnabled()) {
                              if (!result) {
                                if (text != 'false') {
                                  logger.debug("Message from Akismet: ${text}")
                                }
                              }
                            }
                            return result
                        }

  Closure response200Log = {text ->
                              println "Message from Akismet: ${text}"
                              if (logger.isDebugEnabled()) {
                                logger.debug("Message from Akismet: ${text}")
                              }
                           }

  def validMethodsActions = ['isThisCommentSpam': response200,
                             'thisCommentShouldBeSpam': response200Log,
                             'thisCommentIsFalsePositive': response200Log]


  def invokeMethod(String name, args) {

    if (validMethods[name]) {

      def dataToPost = buildDataToPost(args[0])

      if (!dataToPost.blog) {
        throw new IllegalStateException("No blog provided!")
      }

      return doRequest("${apiKey}.${AKISMET_BASE}",
                      "${AKISMET_SERVICE_VERSION}/${validMethods[name]}",
                      dataToPost,
                      validMethodsActions[name])

    } else if (name == 'isApiKeyValid') {

      if (!apiKey) {
        throw new IllegalStateException("No apiKey provided!")
      }
      if (!blog) {
        throw new IllegalStateException("No blog provided!")
      }

      return doRequest(AKISMET_BASE,
                      "${AKISMET_SERVICE_VERSION}/verify-key",
                      [key: "$apiKey", blog: "${blog}"],
                      {text -> (text == 'valid')})

    } else {

      def otherMethod = Akismet.metaClass.getMetaMethod(name, args)
      if (otherMethod) {

        return otherMethod.invoke(this, args)

      } else {

        Akismet.metaClass.invokeMissingMethod(this, name, args)

      }
    }
  }

  private Map buildDataToPost(AkismetComment comment) {

    def dataToPost = [:]

    comment.properties.each {property ->

      switch (property.key) {
        case "class":
        case "metaClass":
          break

        case "otherData":
          property.value?.each {k, v ->
            if (v)
              dataToPost.put(k, v?.toString())
          }
          break

        case requiredParameters:
          if (property.value?.trim()) {
            dataToPost."${property.key}" = property.value.toString()
          } else {
            if (property.key == 'blog') {
              dataToPost.blog = blog
            } else {
              throw new IllegalArgumentException("Required parameter: ${property.key}")
            }
          }

          break

        case nonRequiredParameters:
          if (property.value?.trim()) {
            dataToPost."${property.key}" = property.value
          }
      }
    }

    return dataToPost
  }

  private doRequest(service, path, parameters, Closure response200
  ) {

    def result = false

    def url = new URL("http://${service}/${path}")
    HttpURLConnection connection = (HttpURLConnection) url.openConnection()
    connection.with {

      setRequestMethod("POST")
      setRequestProperty "User-Agent", buildUserAgent()
      doOutput = true

      Writer writer = new OutputStreamWriter(connection.outputStream)
      writer.with {

        def queryString = []
        parameters.each {k, v ->
          queryString << "${k}=${URLEncoder.encode(v)}"
        }

        write queryString.join('&')
        flush()
        close()
      }

      try {
        connect()

        switch (responseCode) {
          case "200":
            result = response200(connection.content.text)

            break;

          default:
            StringBuilder sb = new StringBuilder()
              sb << "Error Response: ${connection.responseCode}\n"
              connection.properties.each {
                sb << it.key + "..." + it.value + "\n"
              }

            throw new RuntimeException("Wrong response: " + sb.toString())
        }

      } catch (ex){

        logger.error("Error on connection: " + ex.message)
        throw new RuntimeException("Error on connection to Akismet", ex)

      } finally {

        disconnect()

      }
    }

    return result
  }

  private String buildUserAgent() {

    if (!appName?.trim()) {
      appName = AKISMET_DROP_NAME
    }

    if (!appVersion?.trim()) {
      appVersion = AKISMET_DROP_VERSION
    }

    "${appName}/${appVersion}|${AKISMET_DROP_NAME}/${AKISMET_DROP_VERSION}"
  }

}