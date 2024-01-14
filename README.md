# media-fetch

## Usages

### Twitter (X)

1. open network tab of browser's DevTools
2. open X website and login
3. find `auth_token` in request header `Cookie` and bearer token in request header `Authorization`
4. putting the tokens as parameters in the constructor

```java
TwitterMediaFetch twitterMediaFetch = new TwitterMediaFetch(
        /* proxy */, /* auth_token */, /* bearer token */);
```
