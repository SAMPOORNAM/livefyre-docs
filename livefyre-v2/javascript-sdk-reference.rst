JavaScript SDK Reference
************************

.. role:: raw-html(raw)
   :format: html

LF
==

.. sourcecode:: javascript

    var conversation = LF(configuration_object, onReady_function);

``LF`` is the only global variable reserved for the JavaScript SDK. When called
as a function, it is used to initialize and configure the Livefyre Stream and return a ``conversation`` instance, which can be assigned to a
variable for later use. This documentation will use the ``conversation`` variable
as a convention, but any other name is equally valid.

The ``LF`` function accepts the following arguments:

.. _configuration_object:

* ``configuration_object``

    This Object specifically indicates which conversation should be loaded by the
    Livefyre Stream by providing a ``domain`` value, and then either a
    ``site_id``, ``article_id`` pair

    .. sourcecode:: javascript

        var conversation = LF({
            domain: '{domain}',
            site_id: '{site_id}',
            article_id: '{article_id}'
        });

    or the URL of a web page with an already-existent Livefyre conversation.

    .. sourcecode:: javascript

        var conversation = LF({
            domain: '{domain}',
            conv_meta: {
                source_url: '{url}'
            }
        });

* ``onReady_function``

    This function will be called when the conversation has been loaded.

Configuration options
---------------------

We allow the following configuration options:

* ``show_user_rating``: This option provides the ability to show or hide user's ratings on comments.

LF.changeConversation
---------------------

The ``LF.changeConversation`` method loads a new conversation in an already-initialized Livefyre Stream without requiring a page refresh. This can be useful on webpages where multiple pieces of 
content are found; for example, an image gallery or tabbed content section. This method accepts 
the same ``configuration_object`` `documented above`__. The provided ``configuration_object`` must 
have the ``conv_meta`` property described in the `Meta Data Best Practices`_ section.

.. __: configuration_object_

.. sourcecode:: javascript

    LF.changeConversation({
        domain: "mydomain.fyre.co",
        site_id: 987720,
        article_id: "101010",
        conv_meta: {
           source_url: 'http://articleurl.com',
           title: 'What an awesome headline!',
           sig: 'd95bc905d5722ccda6fb590c65ab8ef1'
        }
    });

LF.CommentCount
===============

For more control over how the comment counts are replaced, you can call ``LF.CommentCount()`` and pass in an object 
containing the configuration options. Make sure to call the function after all of the elements that need to be replaced
are in the DOM. The best place to call this method is in the footer, so it happens when the DOM is loaded, but prior to 
document and window ready events.

Configuration Options
---------------------

We allow the following configuration options:

- ``replacer``: Function or Regex used to replace the text of each comment count.

  - ``function``: Used to do the replacement on each element. The function's arguments are:

    - ``element``: The HTML element that is being updated.
    - ``count``: The comment count for this element.
  - ``regex``: Used to determine which part of the element's text should be replaced by the count.

Example
-------

.. sourcecode:: html

    <script type="text/javascript">
    LF.CommentCount({
        replacer: function(element, count) {
            element.innerHTML = count +' Comment'+ (count === 1 ? '' : 's');
        }
    });
    </script>


Livefyre Stream Events
======================

The Livefyre Stream uses a plugin-like delegate system so that partners and third-party applications 
can make use of the live event stream being pushed to the page. Custom delegates may implement methods 
named ``handle_{event}`` where ``{event}`` is the event type to handle.

The follow example code shows a list of events that delegates may handle: :raw-html:`<a href="https://gist.github.com/35fee11d9f5539716318" target="blank">https://gist.github.com/35fee11d9f5539716318</a>`


Meta Data Best Practices
========================

Livefyre accesses article URLs in order to determine their canonical URL as well as obtain the 
article title.  You can sidestep this behavior by providing the needed information in the JS config along with a special signature value.  This is considered a best practice for the 
sake of data integrity: it enables explicit setting of the title, tags and URL associated with a given 
conversation which gives the developer control over the (title/URL) information included in email notifications.  It also makes the Livefyre search APIs more useful - you can filter search results using the `tags` that are set here.  To avoid unauthorized creation of data, the data is signed as described below.

Example:

.. sourcecode:: html

    <script type="text/javascript">
        var conv = LF({
            domain: "example.fyre.co",
            site_id: 100,
            article_id: "12",
            conv_meta: {
                article_url: "http://example.com/blog/12",
                tags: "sports,shoes,blog_article",
                title: "Example Title",
                sig: "f7398c935b4d0d52ff02c0c561d60492"
            }
        });
    </script>

The ``sig`` value is calculated by creating a comma-separated string of values and taking 
the MD5 of that string (as UTF-8).  The values in the string are the ``article_id``, as well
as all of the values in conv_meta (except ``sig``), ordered alphabetically by their names, 
with the Site API Secret Key as the last value.  If the Site API Secret Key in the above 
example is "AAECAwQFBgcICQoLDA0ODxAREhM=", then the ``sig`` would be calculated as such 
(extra line breaks are for display purposes only):

.. sourcecode:: html

    sig = md5("12,http://example.com/blog/12/,sports,shoes,blog_article,Example Title,AAECAwQFBgcICQoLDA0ODxAREhM=")

You only need to construct conv_meta once for each new article or whenever an existing article's URL, title, or tags change.  It can also simply be calculated on page load so there are no storage requirements.

For a simple test, you can do this entirely in JS using the following example code.  This will 
get you up and working in your development environment right away, but is for development use only as it is insecure.

Note: extra line breaks are for display purposes only.

.. sourcecode:: html

    <script type="text/javascript" src="http://zor.livefyre.com/wjs/v1.0/
            javascripts/livefyre.js"></script>
    <script type="text/javascript" src="http://www.livefyre.com/wjs/v1.0/
            javascripts/Secure.js"></script>
    <script type="text/javascript">
        var article_id = "{article_id}";
        var conv_meta = {
            article_url: "{article url}",
            title: "{title}"
        };
        var sig = LF.Secure.createSig(article_id, "{site_api_secret}", conv_meta);
        conv_meta['sig'] = sig;

        var fyre = LF({
            domain: "{domain}",
            article_id: article_id,
            site_id: {site_id},
            conv_meta: conv_meta
        });
    </script>

Please note that this approach is only recommended in development environments that have publicly 
inaccessible pages.  A warning will be displayed in red print so you don't forget to remove this 
at production time.


.. _`Comment Counts`: /docs/getting-started/#comment-counts