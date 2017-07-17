# Java JWT Demo

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

A tiny java app authenticating with a JWT. Everything but the home page requires authentication for access (try /users).

Call `UserContext.get().getUserInfo()` anywhere in the scope of a request to get info about the current user (may be a sentinel object); call `userInfo.isValid()` to see if the user's logged in or not. 

## Caveats: 
- UserContext should be torn down at the end of each request
- This has been tested using `login-with`, so it expects a certain payload structure (see below)
- Needs to be deployed to a subdomain of the domain protected by the `login-with` server
- You need to ensure end-to-end SSL to get the JWT
- Heroku terminates SSL at the router, but correctly injects the standard HTTP proxy [headers](https://devcenter.heroku.com/articles/http-routing#heroku-headers)  so for this to work you need to ensure your server "trusts" the heroku proxy. Spring boot _just works_. If you use the code in this example but deploy on a different server, YMMV. 

 
## Required environment vars: 
- COOKIE_NAME: the name of the jwt cookie to parse for auth
- COOKIE_SECRET: the HMAC key for the cookie
- LOGIN_SERVICE_URL: your auth service (e.g. https://login.YOUR_DOMAIN.biz)

## Sample JWT 
```json
HEADER:
{
  "alg": "HS256",
  "typ": "JWT"
}
PAYLOAD:
{
  "accessToken": "....",
  "profile": {
    "username": "Pallav Kothari",
    "provider": "facebook",
    "name": "Pallav Kothari",
    "photo": "https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/photo.jpg"
  },
  "iat": 1500139105
}

```

## Heroku setup 
Setup notes on heroku (assuming auth server is running on YOUR_DOMAIN.biz). 

First, use the deploy button above and set up the required environment vars. 

```bash
heroku domains:add jwt-demo.YOUR_DOMAIN.biz
heroku domains:wait 'jwt-demo.YOUR_DOMAIN.biz'

# tell your dns provider about the new domain
hover -u hoverusername -p passw0rd add:cname -d YOUR_DOMAIN.biz -s jwt-demo -t jwt-demo.YOUR_DOMAIN.biz.herokussl.com
hover -u hoverusername -p passw0rd ls:cnames -d YOUR_DOMAIN.biz

# $7/mo gets your a managed cert
heroku ps:resize hobby
heroku certs:auto:enable

// wait for the cert to kick in
heroku certs:auto 

// you should get something like this: 

	=== Automatic Certificate Management is enabled on jwt-demo

	Certificate details:
	Common Name(s): jwt-demo.YOUR_DOMAIN.biz
	Expires At:     2017-10-13 16:18 UTC
	Issuer:         /C=US/O=Let's Encrypt/CN=Let's Encrypt Authority X3
	Starts At:      2017-07-15 16:18 UTC
	Subject:        /CN=jwt-demo.YOUR_DOMAIN.biz
	SSL certificate is verified by a root authority.

	Domain                 Status
	─────────────────────  ──────
	jwt-demo.YOUR_DOMAIN.biz  OK

``` 
