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

class AkismetComment {

  final static String BLOG = "blog"
  final static String USER_IP = "user_ip"
  final static String USER_AGENT = "user_agent"
  final static String COMMENT_CONTENT = "comment_content"
  final static String REFERRER = "referrer"
  final static String COMMENT_TYPE = "comment_type"
  final static String COMMENT_AUTHOR = "comment_author"
  final static String COMMENT_AUTHOR_EMAIL = "comment_author_email"
  final static String COMMENT_AUTHOR_URL = "comment_author_url"

  String blog             // Required
  String user_ip          // Required
  String user_agent       // Required
  String comment_content  // Required
  String referrer
  String comment_type = "comment" // default value, also valid: 'trackback', 'pingback' or other custom value
  String comment_author
  String comment_author_email
  String comment_author_url

  Map otherData = [:]

  def addOtherData(String key, String value){
    otherData."$key" = value
  }

}
