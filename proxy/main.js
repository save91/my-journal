const express = require('express')
const axios = require('axios')
const he = require('he')
const environment = require('./environment')
const _ = require('lodash')
const PORT = 8080
const GTM = '000Z'

const app = express()
const { serverAddress, serverPort, serverProtocol } = environment;

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
    }
}

const parseWordPressPost = (wpPost) => {
    const data = new Date(`${wpPost.date_gmt}.${GTM}`);
    const modified = new Date(`${wpPost.modified_gmt}.${GTM}`);

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
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts?_embed`;
    const responseFromWp = await axios.get(postsUrl)
    const toReturn = responseFromWp.data.map(post => parseWordPressPost(post))
    res.send(toReturn)
})

app.listen(PORT, () => {
    console.log(`Server in ascolto sulla porta: ${PORT}`)
})