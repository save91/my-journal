const express = require('express')
const axios = require('axios')
const he = require('he')
const firebaseAdmin = require('firebase-admin')
const environment = require('./environment')
const _ = require('lodash')
const PORT = 8080
const GTM = '000Z'

const serviceAccount = require('./myjournal-firebase.json')

firebaseAdmin.initializeApp({
    credential: firebaseAdmin.credential.cert(serviceAccount),
    databaseURL: "https://myjournal-1e44c.firebaseio.com"
})

const app = express()
const { serverAddress, serverPort, serverProtocol } = environment

const baseUrl = `${serverProtocol}://${serverAddress}`
if (serverPort) {
    baseUrl.concat(':', serverPort)
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

app.get('/api/:version/posts', async (req, res) => {
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts?_embed`
    const responseFromWp = await axios.get(postsUrl)
    const toReturn = responseFromWp.data.map(post => parseWordPressPost(post))
    res.send(toReturn)
})

app.get('/api/:version/posts/:id', async (req, res) => {
    const id = req.params['id']
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts/${id}?_embed`
    const responseFromWp = await axios.get(postsUrl)
    const toReturn = parseWordPressPost(responseFromWp.data)
    res.send(toReturn)
})

app.get('/api/:version/push', async (req, res) => {
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
        topic: "news"
    }
    try {
        const responseFromWp = await axios.get(postsUrl)
        const posts = responseFromWp.data.map(post => parseWordPressPost(post))
        const post = posts[0]
        message.android.notification.body = post.title
        message.android.notification.icon = post.featured_media.thumbnail_url
        message.data = {
            id: post.id + ""
        }
        const response = await firebaseAdmin.messaging().send(message, dryRun)
        res.status(200).send('success')
    } catch(error) {
        console.log("Error: ", error)
        res.status(500).send('error')
    }
})

app.listen(PORT, () => {
    console.log(`Server in ascolto sulla porta: ${PORT}`)
})