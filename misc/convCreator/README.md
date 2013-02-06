# Conversation Creator
This sample CLI tool creates conversations on Livefyre.

## Preconditions
+ python (>= 2.7)

```
$ pip install jwt
$ pip install requests
$ pip install json
```

## Usage
+ Edit the sample file and populate the globals at the top of the file:

```
NETWORK = '' #'labs-t402.fyre.co'
SITE_ID = '' #303827
SITE_SECRET = u'' #u'1234ABCD'
URL_BASE = '' #u'http://demos.livefyre.com/labs-t402/'

SAVE = True
...

```
+ $ `./convCreator.py <title> <article_name>`

## Other Languages
+ node.js - [livefyre-streamhub-create-collection](https://github.com/yawetse/livefyre-streamhub-create-collection) (Thanks to [yawetse](https://github.com/yawetse) for the contribution!)