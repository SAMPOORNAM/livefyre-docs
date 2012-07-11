Getting Started
***************


Concepts
========

Data Model
----------

Livefyre recognizes the following data types

* Domain - Indicated by a unique hostname-like string (e.g. ``livefyre.com``, ``example.fyre.co``, etc)
* Site - Belongs to a domain. Has a unique integer site_id
* Article - Belongs to a site. Indicated by a string article_id chosen by the site owner
* Conversation - One per article. Has a unique integer conversation_id
* User - Belongs to a domain. Indicated by a JID (e.g. ``user_id@example.fyre.co``)

The `Livefyre Blogger` product uses the ``livefyre.com`` domain, allowing you to set up a site and accept comments by regular Livefyre.com users.  `Livefyre Publisher` gives you an entire domain of your own, allowing you to set up multiple sites within that domain as well as leverage your existing user database.  Publisher domains are assigned as subdomains of fyre.co (e.g. ``your-company.fyre.co``).  Please note that these domain names are used for API interactions only and are not anything a user would actually browse to.


Template Strings
----------------

This documentation makes frequent use of template strings to decrease verbosity.  Keep an eye out for curly bracket characters, which indicate that the wrapped value is intended to be substituted with something else. For example, absolute URL paths for some HTTP API calls take the form ``http://{domain}/resource``, indicating that when using the call, ``{domain}`` should be replaced with the relevant domain (such as ``livefyre.com`` or ``example.fyre.co``).


Environments for Livefyre Publisher
===================================

Your Integration Server
-----------------------

Typically Livefyre will identify to your developer(s) which integration environment to use when you begin development, QA, or any other kind of testing.  Once assigned to a particular dev/QA integration server environment, you can use the domain name, site ID(s) specified by Livefyre with that environment.  Upon deployment to production, you will need to change the following parameters that were used during dev/QA: 

* JavaScript Source URL
* Livefyre domain name
* Livefyre domain Secret Key
* Livefyre site ID (0..N)
* Livefyre site Secret Key (0..N)

It is a good idea to put all of these parameters into a configuration file or database that is used to dynamically set the values for different environments.


JavaScript Source URLs
----------------------

During development and/or QA, you should always use the following domain to source JavaScript files: ``zor.{domain}``

Upon deployment to production, all JavaScript should be sourced from: ``zor.livefyre.com``


Non-Public Environments
-----------------------

By default, Livefyre tries to "scrape" pages as part of the process of creating (i.e. discovering) new conversations.  Your dev/QA or other environments may not be visible to the public internet, in that case you should use the conv_meta configuration attribute described here: `Meta Data Best Practices`_


Embed the Livefyre Stream
=========================

To embed the Livefyre Stream: 

* Include the Livefyre JavaScript file in your HTML page.
* Include an HTML &lt;div&gt; element with id="livefyre", where you want the Livefyre Stream to appear.

.. sourcecode:: html

    <div id="livefyre"></div>
    ...other content...
    <script
            type="text/javascript"
            src="http://zor.livefyre.com/wjs/v1.0/javascripts/livefyre_init.js">
    </script>

Note: If you are integrating Livefyre Publisher for the first time you may need to replace the above URL with the JavaScript source URL for your specific integration environment - see `Environments for Livefyre Publisher`_.

Embedding ``livefyre_init.js`` on your page will make the JavaScript SDK's ``LF`` function available, which is used to embed the Livefyre Stream. So, immediately after the ``<script>`` tag you've just inserted, initialize and configure the Livefyre Stream.

.. sourcecode:: html

    <script type="text/javascript">
        var conv = LF({
            domain: '{domain}',
            site_id: '{site_id}',
            article_id: '{article_id}'
        });
    </script>

This function accepts a configuration object as its first argument.  The ``domain``, ``site_id``, and ``article_id`` values indicate the correct conversation to load, but the object can be used to configure other options as well. A full reference can be found in the LF_ section of the `JavaScript SDK Reference`_ document.  At a minimum, this object must contain the ``domain`` and ``site_id`` values.  If the ``article_id`` is unspecified, then it is assumed to be the URL of the page that the Livefyre Stream is embedded on.  However, it is strongly recommended that ``article_id`` be specified, using the fixed article identifiers provided by your CMS or blog.

Embedding the Livefyre Stream is the only required integration point for `Livefyre Blogger` users.  For `Livefyre Publisher` it is additionally necessary to provide authentication (see `Publisher Domain Authentication`_).


Comment Counts
==============

Livefyre offers a JavaScript client, ``LF.CommentCount``, to fetch the latest comment counts for the conversations on your site. This can be useful for integrations where your website does not have a local database of comments, or where your CMS' database is not being synced with Livefyre. ``LF.CommentCount``'s automatic functionality is described here, but a detailed reference of its programmatic interface can be found in the `JavaScript SDK Reference`_.

To make use of ``LF.CommentCount``, first embed the JavaScript file in the ``<head>`` section of the page or template where you'd like to make use of it.

.. sourcecode:: html

    <script
            type="text/javascript"
            src="http://zor.livefyre.com/wjs/v1.0/javascripts/CommentCount.js">
    </script>

For `Livefyre Publisher`, you should also add a ``data-lf-domain`` attribute to the script tag indicating your domain:

.. sourcecode:: html

    <script
            type="text/javascript"
            data-lf-domain="{domain}"
            src="http://zor.livefyre.com/wjs/v1.0/javascripts/CommentCount.js">
    </script>

Once the script is loaded, it will attempt to find other elements on the page with a class name of ``livefyre-commentcount``. These elements should be the elements that contain comment counts for an article.  For each of these elements, the script will look for ``data-lf-site-id`` and ``data-lf-article-id`` HTML attributes, and will use these to fetch comments from Livefyre and update each element with the latest value. The following markup is an example of an element that would be updated:

.. sourcecode:: html

    <span
          class="livefyre-commentcount"
          data-lf-site-id="{site_id}"
          data-lf-article-id="{article_id}">0 Comments</span>


HTML Fragments for SEO
======================

In order for search engines and other robots to see comments on your website, the comments need to be sent down in the HTML responses from your web server.  Embedding the JavaScript is not enough, as with that alone the comments can only seen by browsers.  To solve this problem, Livefyre is able to provide a complete HTML representation of the Livefyre Stream that your web server can obtain and serve in its responses.  How this is integrated with your website is specific to your CMS or blog platform.

The following API call can be used to retrieve the HTML (extra line breaks are for display purposes only):

.. sourcecode:: plain

    GET http://bootstrap.{domain}/api/v1.1/public/bootstrap/html/{site_id}/
            {Base64(article_id)}.html

The result of this call should be placed in your page where you want the Livefyre Stream to appear.

For example, in PHP you might include this code in your template (extra line breaks are for display purposes only):

.. sourcecode:: php

    <?php file_get_contents(
            'http://bootstrap.{domain}/api/v1.1/public/bootstrap/html/
            /{site_id}/' . urlencode(base64_encode('{article_id}')) . '.html');
            ?>


Help
====

If you need further assistance, do not hesitate to contact Livefyre by email at support@livefyre.com


.. _LF: /docs/javascript-sdk-reference/#lf
.. _`JavaScript SDK Reference`: /docs/javascript-sdk-reference/
.. _`Publisher Domain Authentication`: /docs/advanced-features/#publisher-domain-authentication
.. _`Livefyre PHP Client`: https://github.com/Livefyre/Livefyre-APIs
.. _`Meta Data Best Practices`: /docs/javascript-sdk-reference/#meta-data-best-practices