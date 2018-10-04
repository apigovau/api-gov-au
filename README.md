# api.gov.au 
Makes it easier to discover electronic government services

## Current Status
api.gov.ai is under development. It is currently at the alpha stage, a live version can be viewed at [https://api.gov.au/](https://api.gov.au/)

[![Build Status](https://travis-ci.org/ausdto/apigovau.svg?branch=master)](https://travis-ci.org/dxa/apigovau)

## Technology
This service uses Kotlin, Spring Boot and is deployed to Heroku.

For a basic introduction to Kotlin, Spring Boot and Heroku, read the [Getting Started with Kotlin on Heroku](https://devcenter.heroku.com/articles/getting-started-with-kotlin) article.

## Running Locally

Install the [Heroku CLI](https://cli.heroku.com/).

```sh
$ git clone https://github.com/ausdto/apigovau.git
$ cd apigovau
$ gradle build
$ heroku local:start
```

Your app should now be running on [localhost:5000](http://localhost:5000/).

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

## Documentation

For more information about using Java and Kotlin on Heroku, see these Dev Center articles:

- [Java on Heroku](https://devcenter.heroku.com/categories/java)

## Reusing government tools
The prototype also uses [Gov.au UI-Kit](https://github.com/govau/uikit). If you want to make any significant changes to the styling, you'll need [Node](https://nodejs.org/en/), [NPM](https://www.npmjs.com/) and [Yarn](https://yarnpkg.com).


## Making contributions
To propose a change, you first need to [create a GitHub account](https://github.com/join).

Once you're signed in, you can browse through the folders above and choose the content you're looking for. You should then see the content in Markdown form. Click the Edit icon in the top-right corner to start editing the content.

The content is written in the Markdown format. [There's a guide here on how to get started with it](https://guides.github.com/features/mastering-markdown/).

You can preview your changes using the tabs at the top of the editor.

When you're happy with your change, make sure to create a pull request for it using the options at the bottom of the page. You'll need to write a short description of the changes you've made.

A pull request is a proposal for a change to the content. Other people can comment on the change and make suggestions. When your change has been reviewed, it will be "merged" - and it will appear immediately in the published content.




Take a look at [this guide on GitHub about pull requests](https://help.github.com/articles/using-pull-requests/).
