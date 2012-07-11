Advanced Features
*****************

.. role:: raw-html(raw)
   :format: html

Publisher Domain Authentication
===============================

User authentication via the JavaScript method LF.login(), as well as Livefyre API calls, require Livefyre Authentication Tokens to be provided.  These tokens assert a user's identity using a digital signature.  With `Livefyre Publisher`, you are given a Domain API Secret Key which can be used to generate Authentication Tokens for any user within your domain.

How It Works
------------

An Authentication Token is a JSON Web Token (JWT) encoding an object with ``domain``, ``user_id``, ``expires``, and ``display_name`` keys:

.. sourcecode:: plain

    {
      domain: 'example.fyre.co',
      user_id: 'asd932i',
      expires: 1324453468.028148,
      display_name: 'test123'
    }

Parameters

* ``domain`` is the name of the `Livefyre Publisher` domain making the assertion
* ``user_id`` is the id of the user whose identity is being asserted
* ``expires`` is the date at which the token is not longer valid, stated as Epoch time
* ``display_name`` text to identify this user in the UI and in comments

The token is then signed with the Domain Secret Key to create a `JSON Web Token (JWT) <http://self-issued.info/docs/draft-jones-json-web-token.html>`_.

Creating a JWT Token out of the above example would create an string looking like:

.. sourcecode:: plain

    eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJkb21haW4iOiAiZXhhbXBsZS5meXJlLmNvIiwgInVzZXJfaWQiOiAiYXNkOTMyaSIsICJleHBpcmF0aW9uIjogMTMyNDQ1MzQ2OC4wMjgxNDh9.HqODFYgI5m6vPAWrm418zYTdw7OhLcEUgprExhlsHAE

JWT libraries exist for many languages, including `Ruby <https://github.com/progrium/ruby-jwt>`_, `Python <http://pypi.python.org/pypi/PyJWT>`_, `PHP <https://github.com/progrium/php-jwt>`_, `Java <http://code.google.com/p/openinfocard/source/browse/trunk/src/org/xmldap/json/WebToken.java>`_, and `.NET <https://github.com/johnsheehan/jwt>`_.

For instance, using Python and the PyJWT library, tokens can be easily created:

.. sourcecode:: python

    import jwt

    DOMAIN_KEY = "{domain API secret key}"
    token = {
        "domain": "{domain}",
        "user_id": "{user_id}",
        "expires": {expires},
        "display_name": "{display_name}"
    }

    signed_token = jwt.encode(token, DOMAIN_KEY)

Similarly, using Java, creating tokens is a snap:

.. sourcecode:: java

    // NOTE: In order to get all of the Java dependencies for this library, you should
    // download the source here: http://code.google.com/p/openinfocard/source/checkout

    import org.json.JSONException;
    import org.json.JSONObject;
    import org.xmldap.json.WebToken;

    public class JWTSample {

        public static String DOMAIN_KEY = "{domain API secret key}";

        public static void main(String[] args) {
            try {
                JSONObject tokenJSON = new JSONObject();
                tokenJSON.put("domain", "{domain}");
                tokenJSON.put("user_id", "{user_id}");
                tokenJSON.put("expires", {expires});
                tokenJSON.put("display_name", "{display_name}");

                JSONObject mHeader = new JSONObject();
                mHeader.put("alg", WebToken.SIGN_ALG_HS256);

                WebToken wToken = new WebToken(tokenJSON.toString(), mHeader);

                String signed_token = wToken.serialize(DOMAIN_KEY.getBytes());

            } catch(JSONException e) {
                System.err.println("There was a JSONException!" + e);
            } catch(Exception e) {
                System.err.println("There was an Exception!" + e);
            }
        }
    }

Livefyre also provides software libraries in that assist in commonly used API functions, such as generating Authentication tokens.

Using the `Livefyre PHP Client`_:

.. sourcecode:: php

    <?php
        include('Livefyre.php');
        $DOMAIN = '{domain}';
        $DOMAIN_KEY = '{domain API secret key}';

        $my_domain = new \Livefyre\Domain($DOMAIN, $DOMAIN_KEY);
        $token = $my_domain->user('{user_id}')->token();

        // Output the token
        echo $token;
    ?>


Default Domain Owner: 'system'
------------------------------

A user with ``id='system'`` will be automatically created and made owner of your domain.  Every publisher domain has a default 'system' user id that can be used (via JWT) to make various API calls.  Other domain owners can be assigned using these instructions: `Managing Domain Owners`_.


Authenticating the Livefyre Stream
----------------------------------

When a user logs into your service or loads a page while already logged in, your service needs to generate an authentication token and profile object (with display_name only) and pass this information to the Livefyre Stream.  Somewhere in your JavaScript code, you should be doing this:

.. sourcecode:: javascript

    conv.login({token: "{token}", profile: {display_name: "{user display name}"}});

where ``token`` is a token generated according to the process described in the previous section.

For example, with PHP you might put the token right in the HTML served to the browser :raw-html:`(<a href="http://github.com/Livefyre/Livefyre-APIs/">uses the API Library</a>)`:

.. sourcecode:: php

    <?php
        include('Livefyre.php');
        $DOMAIN = '{domain}';
        $DOMAIN_KEY = '{domain API secret key}';

        $my_domain = new \Livefyre\Domain($DOMAIN, $DOMAIN_KEY);
        $token = $my_domain->user('{user_id}')->token();
    ?>
    conv.login({token:"<?php echo $token ?>", profile: {display_name: "<?php echo '{user display name}' ?>"}});

Authentication Events
-------------------------

Register a delegate object with the LF Dispatcher to handle actions that require login. For example, you will want to show a login box for your site when an anonymous user tries to like a comment. Your delegate can utilize ``auth_login`` and ``auth_logout`` Livefyre Stream Events:

.. sourcecode:: javascript

    var CustomAuthDelegate = {
        handle_auth_login: function(data) {
            // Do something to log the user in (e.g. open the auth window)
        },

        handle_auth_logout: function(data) {
            // Do something when the user logs out
        }
    };

You can register this delegate with the LF Dispatcher by using the ``ready`` function, which is executed once all LF modules have been initialized:

.. sourcecode:: javascript

    var conv = LF({
        domain: "example.fyre.co",
        site_id: 1,
        article_id: "{any_string}"
    }).ready(function() {
        LF.Dispatcher.addListener(CustomAuthDelegate);
    });

For other delegate events, see the `Livefyre Stream Events`_ section.

Optionally, you can add success and error callback functions as parameters on the ``login()`` function, the appropriate handler will be called once the login has completed or failed. For example:

.. sourcecode:: javascript

    conv.login({token: "{token}",
            profile: {"display_name": "JohnDoe"}, function() {
        // success
    }, function() {
        // error
    }});

When a user logs out of your service, tell the Livefyre JavaScript object:

.. sourcecode:: javascript

    conv.logout()


Remote Profiles
===============

Livefyre stores information about Livefyre Publisher profiles as they are created and updated. This record is used to display user data within the Livefyre Stream as well as throughout the platform. This record of a user is referred to as a `remote profile` and includes, at a minimum, a display name and a unique identifier from a remotely managed (non-Livefyre) profile system. A user's meta data, comments and affiliations (bans, whitelists, or moderator statuses) will also be associated with this record.

If you are integrating with an existing user profile system that already has registered users you will probably need to also see: `Importing Existing Profiles and Comments`_

There are three ways your system(s) shall provide remote profile information to Livefyre (you must implement all three methods for a robust profile integration with Livefyre):

1. In the JWT token payload that is passed to the JavaScript SDK's ``login()`` method. This should only include the display name of the user.
2. By pushing data to Livefyre using an HTTP API call whenever user profiles are created or updated in your system.
3. By registering a "pull" URL with the Livefyre service. Livefyre will use this in exceptional cases to request profile data that was missing for some reason.  It will be the responsibility of your web server to reply with the correct data (format described below).

Whenever user profiles are created or updated in your system, or if the user's profile information ever changes; it should be pushed to Livefyre to ensure it stays current. Generally, pressing the 'save' button on an Edit Profile page (or similar registration form) in your user profile system would cause a push to Livefyre. Sometimes systems fail: a profile's data may not always be made available to Livefyre before the user attempts to login for the first time.  In this case two things happen to ensure a smooth user experience:

* By passing ``display_name`` within the JWT payload passed to the JavaScript SDK's ``login()``, you ensure users in this state can see they're logged-in the first time the Livefyre Stream is loaded.
* Livefyre's servers will "pull" profile data from the URL that was registered for this purpose. This happens asynchronously, and is triggered first time the authenticated user interacts with Livefyre Stream.

The remote profile is represented as a JSON object in the case of both the "push" and "pull" mechanisms.  Here is an example:

.. sourcecode:: javascript

    {
        "id": "_u1",
        "display_name": "Bob Dole",
        "nickname": "bdole",
        "name": {
            "formatted": "Bob Joseph Dole",
            "first": "Bob",
            "middle": "Joseph",
            "last": "Dole"
        },
        "email": "bob@dole.com",
        "image_url": "http://dole.com/images/bob.jpg",
        "profile_url": "http://site.com/bobdole",
        "settings_url": "http://site.com/settings",
        "websites": ["http://dole.com/blog/", "http://bobdolerocks.com"],
        "location": "Washington D.C., USA",
        "bio": "Bob Dole talks in the third person",
        "email_notifications": {
            "comments": "never",
            "moderator_comments": "immediately",
            "moderator_flags": "immediately",
            "replies": "immediately",
            "likes": "often"
        },
        "autofollow_conversations": true
    }

The only required field is ``display_name``. The rest are optional. The ``image_url`` field should contain a URL to the largest resolution square image you have for the user's avatar. Other images of various sizes will be automatically generated and distributed to our CDN for display within the Livefyre Stream.

In order to receive email notifications, both ``email`` and ``email_notifications`` property must be defined (the default is to not send any mail). On most email notification settings, the valid options are: "immediately", "often", and "never". The ``moderator_flags`` setting is unique in that only the "immediately" and "never" options are allowed.


Push Interface
--------------

Use this to update Livefyre when a user on your domain changes his or her profile settings.

.. sourcecode:: plain

    POST http://{domain}/profiles/?actor_token={token}&id={id}

Query-string parameters:

* ``actor_token``: Authentication token of a domain owner account (such as ``system@{domain}``)
* ``id``: the ID of the user being updated

POST data: 

* ``data``: A JSON profile object, as defined above, encoded as a string.  Use Content-Type: application/json.

Response: 201 Created, no data.

Here's an example of the push mechanism in PHP :raw-html:`(<a href="http://github.com/Livefyre/Livefyre-APIs/">uses the API Library</a>): <a href="https://gist.github.com/cb1ad1bad672e23f5bd8">https://gist.github.com/cb1ad1bad672e23f5bd8</a>`


Pull Interface
--------------

The "pull" interface is used by Livefyre to retrieve the full profile information of a user.  To support it, you need to register a URL that pulls the profile. You can also register a url that Livefyre will use to push user affiliation changes associated with that user.  These are done with the following call:

.. sourcecode:: plain

    POST http://{domain}/?actor_token={token}&pull_profile_url={url}

Query-string parameters:

* ``actor_token``: Authentication token of a domain owner account (such as ``system@{domain}``)
* ``pull_profile_url``: URL of profile service

POST data: no data.

Response: 204, no data.

The pull profile URL registered should contain the string "{id}" which will be replaced with the ID of the user when the call is made. For example, if your URL is "``http://example.com/users/get_remote_profile?id={id}``", and the ID of interest is "123", then Livefyre will perform an HTTP GET request to "``http://example.com/users/get_remote_profile?id=123``". The response from your service is expected to be a JSON object (Content-Type: application/json).

Affiliation Push Interface
--------------------------

The affiliation push interface is used by Livefyre to send an external system information about changes to user affiliations.  This includes the following affiliated states:

* ``admin``: This user can moderate conversations & comments.
* ``member``: This user is whitelisted - doesn't require approval and can post realtime in pre-moderated conversations
* ``none``: This user is a typical user (this is the implicit default state of a user's affiliation).
* ``outcast``: This user has been banned from participating in any conversations.
* ``owner``: This user is an owner which means they can both moderate conversations, comments and assign new moderators.

To support this, you need to register a URL that receives affiliation data as POST reqeusts.  The base URL for setting the affiliation push URL is the same as for setting the profile pull url.  You could set both with a single request, or set them separately:

.. sourcecode:: plain

    POST http://{domain}/?actor_token={token}&push_affiliation_url={url}

Query-string parameters:

* ``actor_token``: Authentication token of a domain owner account (such as ``system@{domain}``)
* ``push_affiliation_url``: URL to post user affiliation changes to

POST data: no data.

Response: 204, no data.

The push user affiliation URL registered should be a URL that Livefyre can post to with the following data as content-type: application/x-www-form-urlencoded:

* ``jid``: JID of the user whose affiliation is changed. A JID is a string of the form user_id@domain.
* ``affiliation``: Name of the affiliation assigned, should be one of the following: {admin | member | none | outcast | owner}


Request Security
----------------

The request to both the "push_affiliation_url" and "pull_profile_url" urls will include an HTTP parameter ``server_token`` which uses a shared key to validate that the request is coming from Livefyre's server.  Here's an example of the validation in PHP :raw-html:`(<a href="http://github.com/Livefyre/Livefyre-APIs/">uses the API Library</a>): <a href="https://gist.github.com/cbb0ad3645db1dd524f0">https://gist.github.com/cbb0ad3645db1dd524f0</a>`


Site Management
===============

Adding a Site
-------------

With `Livefyre Publisher`, you can have multiple sites within the domain.  If you haven't added a site within your domain yet, or you want to add another site to your domain, make the following API call:

.. sourcecode:: plain

    POST http://{domain}/sites/?actor_token={token}&url={url}

Query-string parameters:

* ``actor_token``: Authentication token of a domain owner account (such as ``system@{domain}``)
* ``url``: URL of website to add

POST data: no data.

Response: 201 Created, with a JSON payload:

.. sourcecode:: javascript

    {
        "id": 1,
        "api_secret": "M5X4rTeKsGNRsV2RekagqSIE9UM="
    }

Don't lose the ``id`` or ``api_secret`` values!  You'll need them for future interaction with the site object.  The ``api_secret`` is known as the `Site API Secret Key`.  As the name implies, it should be kept private.

Now that you have a site, you can embed the conversation Livefyre Stream on it (see `Embed Livefyre`_).

Changing Moderators of a Site
-----------------------------

Livefyre has two privilege levels: `owner` and `admin`.  Both levels qualify as `moderators`, in that owners and admins can each moderate comments on a site.  However, only owners can add and remove other owners and admins, or make configuration changes to a site.  In this way, it is possible for owners of a site to delegate moderation responsibility to other users by making them admins, without giving those users total control of the site.

Here is how to add a user as an admin:

.. sourcecode:: plain

    POST http://{domain}/site/{site_id}/admins/?actor_token={token}&jid={user}

Query-string parameters:

* ``actor_token``: Authentication token of a domain owner account (such as ``system@{domain}``)
* ``jid``: JID of the user to add as an admin.  A JID is a string of the form ``user_id@domain``.

POST data: no data.

Response: 201 Created, no data.

Please consult the `HTTP API Reference`_ for further site management functionality.


Accessing Site Comment Data
===========================

Whenever a user comments on any conversation that is part of a site, the action is appended to that site's activity stream.  Livefyre provides two interfaces for obtaining a site's activity stream in real time:

* Fetch - At any time, past activity data for a site may be fetched via HTTP GET.  Events can be fetched starting from the very beginning of the stream, or since some last known activity.  An application can then ship this to a data warehouse, aggregator, indexes, etc.
* Sync Update - Activity on a site triggers a POST to your specified site "postback URL", an event you can use to schedule/queue/initiate a fetch.

These two methods combined can provide data to an external system in real time.  If an application stores the most recent known Livefyre activity id, it can "push play" by hitting the sync URL (described below) with that id.  It should do this whenever it receives a Sync Update.  Livefyre will respond by "playing back" up to 200 comments' worth of data in the response to a fetch request.

Fetch Site Data
--------------------

Because a site's data can contain sensitive user information, access is restricted by API Tokens. Information on how to generate API Tokens can be found in the `Authorization API Token Reference`_.

Fetch a site's data, starting from the beginning, with the following HTTP request:

.. sourcecode:: plain

    GET http://{domain}/api/v1.1/private/feed/site/{site_id}/sync/

    GET http://{domain}/api/v1.1/private/feed/site/{site_id}/sync/{since_id}/

Query-string parameters

* ``sig_created``: A timestamp indicating when the request was signed (Livefyre will verify that it is within a few minutes of receipt of the request.)
* ``sig``: An HMAC-SHA1 calculation of "sig_created={sig_created_value}" using your Site's Key.

Response: 200 OK, with a JSON payload.

An example of the signature calculation in PHP: https://gist.github.com/db7ba7a595fe480781e4

A JSON blob is returned, with an array of dictionary items.  Each item contains a field called ``message_type``.  On success, one or more items of type ``lf-activity`` will be returned, optionally followed by an item of type ``more-data``.  The ``more-data`` item indicates there is more activity that can be fetched, by requesting again since the last id.  On error, there will be a single item of type ``error``.

An ``lf-activity`` item contains the following fields

===========================    ===================================================================================
Field                                        Description
===========================    ===================================================================================
``activity_id``                id of this activity
``author``                     name of comment author
``author_email``               email of comment author
``author_url``                 website of comment author
``article_identifier``         article this conversation is associated with, often CMS-generated
``site_id``                    id of your site
``lf_conv_id``                 id of the conversation
``lf_comment_id``              id of the comment
``lf_parent_comment_id``       id of the comment's parent (may be null if none)
``lf_jid``                     JID (Livefyre id) of the author of the comment
``body_text``                  text of comment
``author_ip``                  IP address of author
``created``                    time the comment was created, as seconds since January 1st, 1970, in UTC timezone
``state``                      "active", "unapproved", "minimized", or "hidden" (described below)
``activity_type``              type of activity (described below)
``message_type``               always ``lf-activity``
===========================    ===================================================================================

If you store stateful Livefyre comment data, its usually most clear to look at the 'state' property of the comment record and insert/update data as appropriate.

======================    ===================================================================================
Comment State                           Description
======================    ===================================================================================
``hidden``                user's comment was "deleted" (soft delete) or declined approval by a moderator
``active``                user's comment is active/approved/undeleted
``deleted``               user's comment is hard-deleted, i.e. by a system admin (rare)
``unapproved``            user's comment was filtered out due to spam, profanity, or the site was set to pre-moderated
======================    ===================================================================================

You can inspect 'activity_type' for additional data, as follows.

===================================    ===================================================================================
Activity Type                                        Description
===================================    ===================================================================================
``comment-add``                        a new comment was added (it might not be approved, check the 'state')
``comment-moderate:mod-approve``       a comment was approved or unmarked as spam (see 'state' for specifics)
``comment-moderate:mod-hide``          a comment was deleted (soft delete) by a moderator
===================================    ===================================================================================

:raw-html:`<br/>`

Fetching Comment Data Based on Livefyre Sync Updates
----------------------------------------------------

If a "postback URL" is registered for the site, sync updates are posted to that URL as comments are added/approved/etc to the site's conversations.  To implement the most robust real time solution possible, this HTTP endpoint can be the trigger for your "sync" mechanism as described in the previous section.  Here are a couple different ways to execute this:

* Immediately, synchronously fetch the data using the above "Retrieving Site Data" approach, and insert data into a data store. This is the easiest approach, but may not work in high-traffic & high-volume systems with specific performance requirements.
* Queue a task or job, where some worker handles the syncing out-of-band.  In this case the worker process does the task of fetching and inserting data that Livefyre responds with, instead of your web server.

You can register a postback URL with the following API call:

.. sourcecode:: plain

    POST http://{your_assigned_domain}/site/{site_id}/

Query-string parameters:

* ``actor_token``: Authentication token of a site owner account
* ``postback_url``: The full URL of the HTTP endpoint that should receive activity updates

Response: 204, no data.

The postback URL must support this interface:

.. sourcecode:: plain

    POST {postback_url}

Query-string parameters: See table below.

Response: 200 OK, with a JSON payload that is one of:

* Success response:

.. sourcecode:: javascript

    {"status":"ok"}

* Error response for any other reason:

.. sourcecode:: javascript

    {"status":"error","message":"general-error"}

Note: the ``general-error`` type may be used by Livefyre to indicate that it should attempt to send the Sync Update again at a later time.


Importing Existing Profiles and Comments
========================================

Process and Data Format
-----------------------

Loading any existing data from a legacy comment system (or user profile system) into our production environment consists of 3 steps:
    1. Provide a sample (up to 1000 records) of your :raw-html:`<a href="https://gist.github.com/cb866b5ca0d95e8de5c5" target="blank">conversation data</a>` and  :raw-html:`<a href="https://gist.github.com/78a4ffb99a77090b8900" target="blank">user profile data</a>`.  The samples should be sent to `integration@livefyre.com <mailto:integration@livefyre.com>`_
    2. Livefyre will perform a test import of the sample data in one of our integration environments.
    3. Any issues encountered in step #2 are resolved iteratively through collaboration of the customer and Livefyre integration teams.
    4. Livefyre will work closely with your team to plan a start time and approximate duration of the data load, and decide on the necessity of providing a "diff" (see #5).
    5. If necessary, an additional dump of the "diff" for conversation data will be created to capture comments/conversations that were added after the initial dump, but during the data load.

Notes on the format of the import files:
    * Each JSON record (Profile or Conversation) should not include any endlines, and each appears on a single line.
    * Each record is followed by a single endline, then the next record's JSON.
    * This implies that the files themselves are not valid JSON, but each line of the file is a single valid JSON string.

Importing Comments Only (w/o User Profiles)
-------------------------------------------

In some cases a customer may wish to import comment data, but not the user profiles that originally authored those comments.  This might happen, for example, if you intend to create an entirely new profile system or if you are migrating from a closed user profile system.  The above link to the conversation data JSON format specifies fields that begin with ``imported_`` which should be used in this case.  Only ``imported_display_name`` is required.  When using these fields, you should omit the ``author_id`` field.


.. _`Embed Livefyre`: /docs/getting-started/#embed-the-livefyre-stream
.. _`Livefyre Stream Events`: /docs/javascript-sdk-reference/#livefyre-stream-events
.. _`HTTP API Reference`: /docs/http-api-reference/
.. _`Managing Domain Owners`: /docs/http-api-reference/#http-domain-owners
.. _`Authorization API Token Reference`: /docs/http-api-reference/#authorization-api-token
.. _`Livefyre PHP Client`: https://github.com/Livefyre/Livefyre-APIs
