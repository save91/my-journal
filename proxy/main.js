const express = require('express')
const axios = require('axios')
const he = require('he')
const sleep = require('./utils/sleep')
const buildBaseUrl = require('./utils/buildBaseUrl')
const firebaseAdmin = require('firebase-admin')
const environment = require('./environment')
const _ = require('lodash')
const PORT = 8080
const SLEEP_TIME = 0
const GTM = '000Z'
const TAG = 'main.js'

const serviceAccount = require('./myjournal-firebase.json')

firebaseAdmin.initializeApp({
    credential: firebaseAdmin.credential.cert(serviceAccount),
    databaseURL: 'https://myjournal-1e44c.firebaseio.com'
})

const app = express()
const { serverAddress, serverPort, serverProtocol } = environment

const baseUrl = buildBaseUrl(environment)

const errorHandler = (err, req, res, next) => {
    console.error(TAG, err.stack)
    res.status(500)
    res.send({
        error: 'Something went wrong'
    })
}

const parseWordPressAuthor = (wpAuthor) => {
    if (!wpAuthor) return {}

    return {
        id: wpAuthor.id,
        name: wpAuthor.name
    }
}

const parseWordPressMedia = (wpMedia) => {
    if (!wpMedia) return {}

    return {
        id: wpMedia.id,
        thumbnail_url: _.get(wpMedia, 'media_details.sizes.thumbnail.source_url', undefined),
        post_thumbnail_url: _.get(wpMedia, 'media_details.sizes.post-thumbnail.source_url', undefined),
        medium_url: _.get(wpMedia, 'media_details.sizes.medium.source_url', undefined),
    }
}

const parseWordPressPost = (wpPost) => {
    const data = new Date(`${wpPost.date_gmt}.${GTM}`)
    const modified = new Date(`${wpPost.modified_gmt}.${GTM}`)

    return {
        id: wpPost.id,
        author: parseWordPressAuthor(_.get(wpPost, '_embedded.author[0]', null)),
        body: wpPost.content.rendered,
        categories_id: wpPost.categories,
        date: data.getTime(),
        featured_media: parseWordPressMedia(_.get(wpPost, '_embedded.wp:featuredmedia[0]', null)),
        link: wpPost.link,
        modified: modified.getTime(),
        tags_id: wpPost.tags,
        title: he.decode(wpPost.title.rendered)
    }
}

app.get('/api/:version/posts', async (req, res, next) => {
    const page = req.query.page || 1
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts?_embed&page=${page}&per_page=10`
    try {
        const responseFromWp = await axios.get(postsUrl)
        const toReturn = responseFromWp.data.map(post => parseWordPressPost(post))
        await sleep(SLEEP_TIME)

        res.send(toReturn)
    } catch(e) {
        next(e)
    }
})

app.get('/api/:version/posts/:id', async (req, res, next) => {
    const id = req.params['id']
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts/${id}?_embed`
    try {
        const responseFromWp = await axios.get(postsUrl)
        const toReturn = parseWordPressPost(responseFromWp.data)
        await sleep(SLEEP_TIME)
    
        res.send(toReturn)
    } catch(e) {
        next(e)
    }
})

app.get('/api/:version/push', async (req, res, next) => {
    const dryRun = false
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts?_embed`
    const message = {
        android: {
            ttl: 3600 * 1000, // 1 hour in milliseconds
            priority: 'normal',
            notification: {
              title: 'News!',
              body: ''
            }
        },
        topic: 'news'
    }

    try {
        const responseFromWp = await axios.get(postsUrl)
        const posts = responseFromWp.data.map(post => parseWordPressPost(post))
        const post = posts[0]
        message.android.notification.body = post.title
        message.android.notification.icon = post.featured_media.thumbnail_url
        message.data = {
            id: post.id + ''
        }
        await firebaseAdmin.messaging().send(message, dryRun)

        res.status(200).send('success')
    } catch(e) {
        next(err)
    }
})

app.use(errorHandler);

app.listen(PORT, () => {
    console.log(`Server in ascolto sulla porta: ${PORT}`)
})